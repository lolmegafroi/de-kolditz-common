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

#include <sylmain.h>
#include <plugin.h>
#include <folder.h>
#include <procmsg.h>

#include <glib.h>
#include <gtk/gtk.h>

#include "fs_main.h"
#include "fs_infodialog.h"
#include "fs_indexer.h"

extern SylPluginInfo fs_plugin_info;

static gchar* fs_infodialog_text = NULL;
static const gchar* DIALOG_TEST_LABEL_TEXT = "Hi, this is just a test.\nPlug-in info:\nName: %s\nVersion: %s\nAuthor: %s\nDescription: %s\n# HW-Threads: %ld\n# Worker-Threads: %ld";

void fs_infodialog_load ( ) {
}

void fs_infodialog_unload ( ) {
	if ( fs_infodialog_text != NULL ) {
		free ( fs_infodialog_text );
	}
}

void fs_infodialog_button_clicked ( GtkWidget *widget, gpointer data ) {
	if ( data != NULL ) {
		GtkWidget* w = (GtkWidget*) data;
		gtk_widget_hide_all ( w );
		gtk_widget_destroy ( w );
	}
}

static void fs_infodialog_create ( widgets_fsid * widgets ) {
	widgets_fsid * const w = widgets;

	w->window = gtk_window_new ( GTK_WINDOW_TOPLEVEL );
	gtk_window_set_title ( GTK_WINDOW( w->window ), "Full Search plug-in" );

	w->layout = gtk_table_new ( 2, 3, FALSE );
	gtk_table_set_row_spacings ( GTK_TABLE( w->layout ), 2 );
	gtk_container_add ( GTK_CONTAINER( w->window ), w->layout );

	if ( fs_infodialog_text == NULL ) {
		long numHWThreads = sysconf ( _SC_NPROCESSORS_ONLN );
		const fs_isg* stIdxGl = fs_indexer_get_global_state ( );
		asprintf ( &fs_infodialog_text, DIALOG_TEST_LABEL_TEXT, fs_plugin_info.name, fs_plugin_info.version, fs_plugin_info.author, fs_plugin_info.description, numHWThreads, stIdxGl->nWorkers );
	}
	w->label = gtk_label_new ( fs_infodialog_text );
	gtk_table_attach_defaults ( GTK_TABLE( w->layout ), w->label, 0, 3, 0, 1 );
	w->button = gtk_button_new_with_mnemonic ( "_Close" );
	gtk_table_attach_defaults ( GTK_TABLE( w->layout ), w->button, 1, 2, 1, 2 );

	gtk_window_set_default_size ( GTK_WINDOW( w->window ), 400, 200 );

	g_signal_connect( G_OBJECT(w->button), "clicked", G_CALLBACK(fs_infodialog_button_clicked), w->window );
}

void fs_infodialog_open ( void ) {
	widgets_fsid w;
	fs_infodialog_create ( &w );
	gtk_widget_show_all ( w.window );
}
