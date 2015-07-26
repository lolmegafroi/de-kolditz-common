/*
 * Sylpheed -- a GTK+ based, lightweight, and fast e-mail client
 * Copyright (C) 1999-2011 Hiroyuki Yamamoto
 *
 * Sylpheed Full Mail Search Plugin  -- a Sylpheed plug-in to enable
 * arbitrary search for any mail content and header fields
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

#include <string.h>

#include <folderview.h>

#include "fs_indexer.h"

/* PRIVATE MEMBERS */
static fs_isg stGlobal;
static const char* const fs_indexer_threads_template = "fs_indexer_%d";

/* PUBLIC FUNCTIONS */
int fs_indexer_start ( ) {
	g_debug ( "[Full Search] Starting Full Search Indexer." );
	pthread_t i;
	GError *pError;

	// TODO stGloval.nThreads = sysconf ( _SC_NPROCESSORS_ONLN );
	fs_indexer_init_global_state ( 1 );

	// create worker states
	for ( i = 0; i < stGlobal.nWorkers ; ++i ) {
		pError = NULL;
		fs_indexer_init_worker_state ( i, &stGlobal, &stGlobal.stWorkers[i], i );
		stGlobal.pThreads[i] = g_thread_try_new ( stGlobal.stWorkers[i].name, fs_indexer_worker, &stGlobal.stWorkers[i], &pError );
		if ( stGlobal.pThreads[i] != NULL ) {
			g_debug ( "[Full Search] Created worker thread %ld", i );
		} else {
			--i;
			--stGlobal.nWorkers;
			if ( pError ) {
				g_debug ( "[Full Search] Error creating worker thread %ld: %s", i, pError->message );
			} else {
				g_debug ( "[Full Search] Error creating worker thread %ld", i );
			}
		}
	}

	if ( stGlobal.nWorkers > 1 ) {
		g_debug ( "[Full Search] Building network." );
		// build network
		if ( stGlobal.nWorkers == 2 ) {
			stGlobal.stWorkers[0].left = NULL;
			stGlobal.stWorkers[0].right = &stGlobal.stWorkers[1];
			stGlobal.stWorkers[1].left = &stGlobal.stWorkers[0];
			stGlobal.stWorkers[1].right = NULL;
		} else {
			stGlobal.stWorkers[0].left = &stGlobal.stWorkers[stGlobal.nWorkers - 1];
			stGlobal.stWorkers[stGlobal.nWorkers - 1].right = &stGlobal.stWorkers[0];
			fs_isw *pW = &stGlobal.stWorkers[0];
			for ( i = 1; i < stGlobal.nWorkers ; ++i ) {
				pW->right = &stGlobal.stWorkers[i];
				pW->right->left = pW;
				pW = pW->right;
			}
		}
	} else {
		g_debug ( "[Full Search] Not building network." );
	}

	// send init messages
	fs_imb msgInit;
	msgInit.type = fs_imt_init;
	msgInit.lenMsg = sizeof(fs_imb);
	for ( i = 0; i < stGlobal.nWorkers ; ++i ) {
		g_debug ( "[Full Search] Sending init message to workder %lu", i );
		fs_indexer_worker_send_message ( &stGlobal.stWorkers[i], &stGlobal.state, &msgInit );
	}

	g_debug ( "[Full Search] Started Full Search Indexer." );
	return 0;
}

int fs_indexer_stop ( ) {
	g_debug ( "[Full Search] Stopping Full Search Indexer." );
	size_t i;
	if ( stGlobal.pThreads ) {
		if ( stGlobal.nWorkers ) {
			for ( i = 0; i < stGlobal.nWorkers ; ++i ) {
				stGlobal.stWorkers[i].doTerminate = true;
				g_cond_signal ( &stGlobal.stWorkers[i].condMsgIn );
				g_thread_join ( stGlobal.pThreads[i] );
				fs_indexer_destroy_worker_state ( &stGlobal.stWorkers[i] );
			}
		}
		g_free ( stGlobal.pThreads );
		stGlobal.nWorkers = 0;
	}
	g_debug ( "[Full Search] Stopped Full Search Indexer." );
	return 0;
}

const fs_isg* fs_indexer_get_global_state ( ) {
	return &stGlobal;
}

/* PRIVATE FUNCTIONS */
static void fs_indexer_init_global_state ( size_t nThreads ) {
	fs_indexer_init_worker_state ( -1, &stGlobal, &stGlobal.state, -1 );
	stGlobal.nWorkers = nThreads;
	stGlobal.pThreads = g_new0( GThread*, stGlobal.nWorkers );
	stGlobal.stWorkersUnaligned = g_new0( fs_isw, stGlobal.nWorkers + 1 );
	stGlobal.stWorkers = ALIGN( stGlobal.stWorkersUnaligned, 64ULL );

	// one index per  indexed field, but only one for now
	stGlobal.indexes = calloc ( 1, sizeof(prefix_tree*) );
	do {
		--nThreads;
		stGlobal.indexes[nThreads] = malloc ( sizeof(pt_index) );
		pt_index_init ( stGlobal.indexes[nThreads], 64, 64 );
	} while ( nThreads );
}

static void fs_indexer_init_worker_state ( const pthread_t threadID, const fs_isg *stGlobal, fs_isw *stWorker, size_t iWorker ) {
	size_t i;
	*( (pthread_t*) &stWorker->threadID ) = threadID;
	stWorker->stGlobal = stGlobal;
	if ( asprintf ( &stWorker->name, fs_indexer_threads_template, iWorker ) < 0 ) {
		g_debug ( "[Full Search] Could not allocate memory for name (worker %ld)", iWorker );
	}
	stWorker->doTerminate = false;
	g_cond_init ( &stWorker->condMsgIn );
	g_mutex_init ( &stWorker->mutMsgIn );
	stWorker->msgIn.size = (size_t) sysconf ( _SC_PAGESIZE );
	stWorker->msgIn.data = g_new0( gchar, stWorker->msgIn.size );
	stWorker->msgIn.pos = 0;
	stWorker->msgInWork.size = stWorker->msgIn.size;
	stWorker->msgInWork.data = g_new0( gchar, stWorker->msgIn.size );
	stWorker->msgInWork.pos = 0;
	stWorker->nWorkers = stGlobal->nWorkers;
	stWorker->msgsOut = g_new( fs_imsg, stWorker->nWorkers );
	for ( i = 0; i < stWorker->nWorkers ; ++i ) {
		stWorker->msgsOut[i].size = (size_t) sysconf ( _SC_PAGESIZE );
		stWorker->msgsOut[i].data = g_new0( gchar, stWorker->msgsOut[i].size );
		stWorker->msgsOut[i].pos = 0;
	}
	g_cond_init ( &stWorker->condPauseResume );
	g_mutex_init ( &stWorker->mutPauseResume );

	stWorker->doTerminate = false;
	stWorker->isPaused = false;
	stWorker->isWaiting = false;

	stWorker->left = NULL;
	stWorker->right = NULL;

	g_debug ( "[Full Search] Initialized worker state for worker %ld", iWorker );
}

static void fs_indexer_destroy_worker_state ( fs_isw *stWorker ) {
	size_t i;
	g_free ( stWorker->name );
	g_cond_clear ( &stWorker->condMsgIn );
	g_mutex_clear ( &stWorker->mutMsgIn );
	g_free ( stWorker->msgIn.data );
	for ( i = 0; i < stWorker->nWorkers ; ++i ) {
		g_free ( stWorker->msgsOut[i].data );
	}
	g_free ( stWorker->msgsOut );
	g_cond_clear ( &stWorker->condPauseResume );
	g_mutex_clear ( &stWorker->mutPauseResume );
}

static gpointer fs_indexer_worker ( gpointer data ) {
	fs_isw* stLocal = (fs_isw*) data;

	if ( stLocal && stLocal->name ) {
		g_debug ( "[Full Search] [Worker %lu] Started indexer worker thread \"%s\".", stLocal->threadID, stLocal->name );
	} else {
		g_debug ( "[Full Search] [Worker %lu] Started some indexer worker thread.", stLocal->threadID );
	}

	do {
		// first, save the message data we received so far
		g_mutex_lock ( &stLocal->mutMsgIn );
		const bool isPosCorrect = stLocal->msgIn.pos < stLocal->msgIn.size;
		const size_t actPos = isPosCorrect ? stLocal->msgIn.pos : stLocal->msgIn.size;
		if ( !isPosCorrect ) {
			// gdk_threads_enter ( );
			g_debug ( "[Full Search] [Worker %lu] Warning: Incoming message position is after its maximum size! (%ld >= %ld)", stLocal->threadID, stLocal->msgIn.pos, stLocal->msgIn.size );
			// gdk_threads_leave ( );
		}
		memcpy ( stLocal->msgInWork.data, stLocal->msgIn.data, actPos );
		stLocal->msgInWork.pos = actPos;
		// should not be necessary here, but we'll clean the incoming data anyway...
		memset ( stLocal->msgIn.data, 0, stLocal->msgIn.size );
		stLocal->msgIn.pos = 0;
		g_mutex_unlock ( &stLocal->mutMsgIn );

		// second, process messages
		gchar* pData = stLocal->msgInWork.data;
		gchar* pEnd = stLocal->msgInWork.data + stLocal->msgInWork.pos;
		// skip immediately if there is nothing inside
		while ( !stLocal->doTerminate && pData < pEnd ) {
			switch ( FS_MSG_TYPE( pData ) ) {
			case fs_imt_start:
				break;

			case fs_imt_stop_asap:
				// fall-through
			case fs_imt_stop_now:
				stLocal->doTerminate = true;
				break;

			case fs_imt_pause:
				g_mutex_lock ( &stLocal->mutPauseResume );
				stLocal->isPaused = true;
				g_cond_wait ( &stLocal->condPauseResume, &stLocal->mutPauseResume );
				stLocal->isPaused = false;
				g_mutex_unlock ( &stLocal->mutPauseResume );
				break;

			case fs_imt_init:
				g_debug ( "[Full Search] [Worker %lu] Initializing.", stLocal->threadID );
				// TODO do work
				g_debug ( "[Full Search] [Worker %lu] Initialized.", stLocal->threadID );
				break;

			case fs_imt_add_messages:
				// TODO do work
				break;

			case fs_imt_remove_messages:
				// TODO do work
				break;

			default:
				break;
			}

			pData += FS_MSG_LENGTH( pData );
		}

		// finally some synchronization
		if ( stLocal->doTerminate ) {
			break;
		}
		g_mutex_lock ( &stLocal->mutMsgIn );
		if ( stLocal->msgIn.pos == 0 ) {
			stLocal->isWaiting = true;
			g_debug ( "[Full Search] [Worker %lu] Waiting for more messages.", stLocal->threadID );
			g_cond_wait ( &stLocal->condMsgIn, &stLocal->mutMsgIn );
			stLocal->isWaiting = false;
		}
		g_mutex_unlock ( &stLocal->mutMsgIn );
	} while ( !stLocal->doTerminate );

	g_debug ( "[Full Search] [Worker %lu] Terminating.", stLocal->threadID );
	return NULL;
}

static void fs_indexer_worker_send_message ( fs_isw* stDest, fs_isw* stSrc, fs_imb* msg ) {
	bool isSent = false;
	size_t pos, msgLen;
	switch ( msg->type ) {
	case fs_imt_init:
		msgLen = sizeof(fs_imb);
		break;
	}
	while ( !isSent && !stDest->doTerminate && !stSrc->doTerminate ) {
		g_mutex_lock ( &stDest->mutMsgIn );
		if ( stDest->msgIn.pos + msgLen <= stDest->msgIn.size ) {
			pos = stDest->msgIn.pos;
			stDest->msgIn.pos += msgLen;
			memcpy ( stDest->msgIn.data + pos, msg, msgLen );
			if ( stDest->isPaused ) {
				g_mutex_lock ( &stDest->mutPauseResume );
				g_cond_signal ( &stDest->condPauseResume );
				g_mutex_unlock ( &stDest->mutPauseResume );
			} else if ( stDest->isWaiting ) {
				g_cond_signal ( &stDest->condMsgIn );
			}
			isSent = true;
		} else {
			// TODO wait until space is free
		}
		g_mutex_unlock ( &stDest->mutMsgIn );
	}
}
