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

#ifndef FS_BASE_H_
# define FS_BASE_H_

/*************************************/
/* INCLUDES                          */
/*************************************/
#include <inttypes.h>

/*************************************/
/* PUBLIC DEFINES                    */
/*************************************/
#ifndef false
# define false 0
# define true (!false)
#endif

#ifndef NULL
# define NULL ((void*)0)
#endif

/*************************************/
/* PUBLIC TYPES                      */
/*************************************/
typedef uint8_t bool;

/*************************************/
/* PUBLIC FUNCTIONS                  */
/*************************************/
#define ALIGN(x,a)              __ALIGN_MASK((x),(a)-1)
#define __ALIGN_MASK(x,mask)    ((typeof(x))((((typeof(mask))(x))+(mask))&~(mask)))

#define TOSTR0(x) #x
#define TOSTR(x) TOSTR0(x)

uint32_t xorshift32 ( );
uint64_t xorshift64 ( );

/*************************************/
/* PRIVATE TYPES                     */
/*************************************/

/*************************************/
/* PRIVATE FUNCTIONS                 */
/*************************************/

#endif /* FS_BASE_H_ */
