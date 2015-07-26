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

#ifndef FS_INDEXER_H_
#define FS_INDEXER_H_

/*************************************/
/* INCLUDES                          */
/*************************************/
#include <glib.h>
#include <gtk/gtk.h>
#include "fs_base.h"
#include "prefix_tree.h"

/*************************************/
/* PUBLIC DEFINES                    */
/*************************************/
#define FS_NUM_INDEXED_FIELDS 1 /* maybe I'll make that dynamic later */

/*************************************/
/* PUBLIC TYPES                      */
/*************************************/
struct fs_indexer_state_global;
struct fs_indexer_state_worker;
struct fs_indexer_message;

typedef struct fs_indexer_state_global fs_indexer_state_global, fs_isg;
typedef struct fs_indexer_state_worker fs_indexer_state_worker, fs_isw;
typedef struct fs_indexer_message fs_indexer_message, fs_imsg;

struct fs_indexer_message {
	gchar* data;
	size_t pos;
	size_t size;
};

struct fs_indexer_state_worker {
	const pthread_t threadID;
	const fs_isg *stGlobal;
	gchar* name;
	GCond condMsgIn;
	GMutex mutMsgIn;
	fs_imsg msgIn;
	fs_imsg msgInWork;
	size_t nWorkers;
	fs_imsg *msgsOut;
	GCond condPauseResume;
	GMutex mutPauseResume;

	// flags
	volatile uint8_t doTerminate :1; /* Should be terminated */
	volatile uint8_t isPaused :1; /* Is paused and waits for resume */
	volatile uint8_t isWaiting :1; /* Is paused and waits for new message */
	volatile uint8_t unused3 :1;
	volatile uint8_t unused4 :1;
	volatile uint8_t unused5 :1;
	volatile uint8_t unused6 :1;

	// let's build a ring network of workers
	fs_isw *left;
	fs_isw *right;
}__attribute__ ((aligned (64)));

struct fs_indexer_state_global {
	fs_isw state;
	size_t nWorkers;
	GThread **pThreads;
	fs_isw *stWorkersUnaligned;
	fs_isw *stWorkers;
	pt_index **indexes;
};

/*************************************/
/* PUBLIC FUNCTIONS                  */
/*************************************/
int fs_indexer_start ( );
int fs_indexer_stop ( );
const fs_isg* fs_indexer_get_global_state ( );

/*************************************/
/* PRIVATE TYPES                     */
/*************************************/
typedef enum fs_indexer_message_type {
	fs_imt_start, fs_imt_stop_now, fs_imt_stop_asap, fs_imt_pause, fs_imt_init, fs_imt_add_messages, fs_imt_remove_messages
} fs_indexer_message_type, fs_imt, *p_fs_imt;

typedef struct fs_indexer_message_body {
	uint8_t type;
	uint16_t lenMsg;
} fs_indexer_message_body, fs_imb, *p_fs_imb;

typedef struct fs_indexer_message_body_start {
	fs_imb base;
	char data[0];
} fs_indexer_message_body_start, fs_imb_start, *p_fs_imb_start;

typedef struct fs_indexer_message_body_stop_now {
	fs_imb base;
} fs_indexer_message_body_stop_now, fs_imb_stop_now, *p_fs_imb_stop_now;

typedef struct fs_indexer_message_body_stop_asap {
	fs_imb base;
} fs_indexer_message_body_stop_asap, fs_imb_stop_asap, *p_fs_imb_stop_asap;

typedef struct fs_indexer_message_body_pause {
	fs_imb base;
} fs_indexer_message_body_pause, fs_imb_pause, *p_fs_imb_pause;

typedef struct fs_indexer_message_body_resume {
	fs_imb base;
	GThread *pThread;

} fs_indexer_message_body_resume, fs_imb_resume, *p_fs_imb_resume;

#define FS_MSG_TYPE(pByte) ((fs_imt)((p_fs_imb)(pByte))->type)
#define FS_MSG_LENGTH(pByte) ((uint16_t)((p_fs_imb)(pByte))->lenMsg)

/*************************************/
/* PRIVATE FUNCTIONS                 */
/*************************************/
static void fs_indexer_init_global_state ( size_t nThreads );
static void fs_indexer_init_worker_state ( const pthread_t threadID, const fs_isg *stGlobal, fs_isw *stWorker, size_t iWorker );
static void fs_indexer_destroy_worker_state ( fs_isw *stWorker );
static gpointer fs_indexer_worker ( gpointer data );
static void fs_indexer_worker_send_message ( fs_isw* stDest, fs_isw* stSrc, fs_imb* msg );

#endif /* FS_INDEXER_H_ */
