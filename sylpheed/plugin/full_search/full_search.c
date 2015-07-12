/*
 * Sylpheed -- a GTK+ based, lightweight, and fast e-mail client
 * Copyright (C) 1999-2011 Hiroyuki Yamamoto
 *
 * Sylpheed Full Mail Search Plugin  -- a Sylpheed plug-in to enable
 * arbitrary search for any mail (header) field
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

#include <glib.h>
#include <gtk/gtk.h>

#include <pthread.h>

#include "sylmain.h"
#include "plugin.h"
#include "full_search.h"
#include "folder.h"
#include "procmsg.h"

static SylPluginInfo info = { "Full Search Plugin", "0.0.1", "Till Kolditz", "Index-based search plug-in for any (header) field in mails" };

static gchar* info_text = NULL;
static const gchar* DIALOG_TEST_LABEL_TEXT = "Hi, this is just a test.\nPlug-in info:\nName: %s\nVersion: %s\nAuthor: %s\nDescription: %s";

static gulong app_exit_handler_id = 0;

static void init_done_cb ( GObject *obj, gpointer data );
static void app_exit_cb ( GObject *obj, gpointer data );

static void dialog_info_button_clicked ( GtkWidget *widget, gpointer data );
static void dialog_info_create ( void );

void plugin_load ( void ) {
	GList *list, *cur;
	const gchar *ver;
	gpointer mainwin;

	debug_print ( "Full search plug-in loading.\n" );

	syl_plugin_add_menuitem ( "/Tools", NULL, NULL, NULL );
	syl_plugin_add_menuitem ( "/Tools", "Test Full search", dialog_info_create, NULL );

	g_signal_connect_after( syl_app_get ( ), "init-done", G_CALLBACK(init_done_cb), NULL );
	app_exit_handler_id = g_signal_connect( syl_app_get ( ), "app-exit", G_CALLBACK(app_exit_cb), NULL );

	debug_print ( "Full search plug-in loaded.\n" );
}

void plugin_unload ( void ) {
	if ( info_text != NULL ) {
		free ( info_text );
	}
	debug_print ( "Full search plug-in unloaded.\n" );
}

SylPluginInfo* plugin_info ( ) {
	return &info;
}

gint plugin_interface_version ( void ) {
	return SYL_PLUGIN_INTERFACE_VERSION;
}

static void init_done_cb ( GObject *obj, gpointer data ) {
	// syl_plugin_update_check_set_check_url("http://localhost/version_pro.txt?");
	// syl_plugin_update_check_set_download_url("http://localhost/download.php?sno=123&ver=VER&os=win");
	// syl_plugin_update_check_set_jump_url("http://localhost/index.html");
	// syl_plugin_update_check_set_check_plugin_url("http://localhost/plugin_version.txt");
	// syl_plugin_update_check_set_jump_plugin_url("http://localhost/plugin.html");

	syl_plugin_notification_window_open ( "Sylpheed app init done", "Full Search plug-in doing background initialization now...", 5 );

	g_print ( "test: %p: app init done\n", obj );
}

static void app_exit_cb ( GObject *obj, gpointer data ) {
	g_print ( "test: %p: app will exit\n", obj );
}

static void dialog_info_button_clicked ( GtkWidget *widget, gpointer data ) {
	g_print ( "button_clicked\n" );
}

static void dialog_info_create ( void ) {
	GtkWidget *window;
	GtkWidget *layout;
	GtkWidget *button;
	GtkWidget *label;

	g_print ( "creating window\n" );

	window = gtk_window_new ( GTK_WINDOW_TOPLEVEL );
	gtk_window_set_title ( GTK_WINDOW( window ), "Full Search plug-in" );

	layout = gtk_table_new ( 2, 3, FALSE );
	gtk_table_set_row_spacings ( GTK_TABLE( layout ), 2 );
	gtk_container_add ( GTK_CONTAINER( window ), layout );

	if ( info_text == NULL ) {
		int len = strlen ( DIALOG_TEST_LABEL_TEXT );
		len += strlen ( info.name );
		len += strlen ( info.version );
		len += strlen ( info.author );
		len += strlen ( info.description );
		info_text = (gchar*) malloc ( len );
		// since the format string already has some "%s" format substrings inside there is already enough space to accomodate the trailing \0
		sprintf ( info_text, DIALOG_TEST_LABEL_TEXT, info.name, info.version, info.author, info.description );
	}
	label = gtk_label_new ( info_text );
	gtk_table_attach_defaults ( GTK_TABLE( layout ), label, 0, 3, 0, 1 );
	button = gtk_button_new_with_mnemonic ( "_Close" );
	gtk_table_attach_defaults ( GTK_TABLE( layout ), button, 1, 2, 1, 2 );

	gtk_window_set_default_size ( GTK_WINDOW( window ), 400, 200 );

	g_signal_connect( G_OBJECT(button), "clicked", G_CALLBACK(dialog_info_button_clicked), NULL );

	gtk_widget_show_all ( window );
}

