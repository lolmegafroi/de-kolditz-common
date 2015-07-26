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

#include "fs_base.h"

uint32_t xorshift32 ( ) {
	static uint32_t x = 314159265UL;
	x ^= x << 13;
	x ^= x >> 17;
	x ^= x << 5;
	return x;
}

uint64_t xorshift64 ( ) {
	static uint64_t x = 88172645463325252ull;
	x ^= x << 13;
	x ^= x >> 7;
	x ^= x << 17;
	return x;
}
