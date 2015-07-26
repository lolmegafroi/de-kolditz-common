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

#ifndef __FS_INFODIALOG__
#define __FS_INFODIALOG__

/*************************************/
/* INCLUDES                          */
/*************************************/
#include "fs_base.h"

/*************************************/
/* PUBLIC DEFINES                    */
/*************************************/

/*************************************/
/* PUBLIC TYPES                      */
/*************************************/

/*************************************/
/* PUBLIC FUNCTIONS                  */
/*************************************/
void fs_infodialog_load ( );
void fs_infodialog_unload ( );
void fs_infodialog_open ( void );

/*************************************/
/* PRIVATE TYPES                     */
/*************************************/
typedef struct fs_infodialog_widgets {
	GtkWidget *window;
	GtkWidget *layout;
	GtkWidget *button;
	GtkWidget *label;
} fs_infodialog_widgets, widgets_fsid;

/*************************************/
/* PRIVATE FUNCTIONS                 */
/*************************************/
static void fs_infodialog_button_clicked ( GtkWidget *widget, gpointer data );

#endif /* __FS_INFODIALOG__ */
