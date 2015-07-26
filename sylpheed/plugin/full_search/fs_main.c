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

SylPluginInfo fs_plugin_info = { "Full Search Plugin", "0.0.1", "Till Kolditz", "Index-based search plug-in for any (header) field in mails" };

static gulong app_exit_handler_id = 0;

void plugin_load ( void ) {
	GList *list, *cur;
	const gchar *ver;
	gpointer mainwin;

	g_debug ( "[Full Search] Plug-in loading." );

	syl_plugin_add_menuitem ( "/Tools", NULL, NULL, NULL );
	syl_plugin_add_menuitem ( "/Tools", "Full Search", fs_infodialog_open, NULL );

	g_signal_connect_after( syl_app_get ( ), "init-done", G_CALLBACK(init_done_cb), NULL );
	app_exit_handler_id = g_signal_connect( syl_app_get ( ), "app-exit", G_CALLBACK(app_exit_cb), NULL );

	fs_infodialog_load ( );
	fs_indexer_start ( );

	g_debug ( "[Full Search] Plug-in loaded." );
}

void plugin_unload ( void ) {
	g_debug ( "[Full Search] Plug-in unloading." );
	fs_indexer_stop ( );
	fs_infodialog_unload ( );
	g_debug ( "[Full Search] Plug-in unloaded." );
}

SylPluginInfo* plugin_info ( ) {
	return &fs_plugin_info;
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

	syl_plugin_notification_window_open ( "Full Search Plugin", "Doing background initialization now...", 3 );
}

static void app_exit_cb ( GObject *obj, gpointer data ) {
}
