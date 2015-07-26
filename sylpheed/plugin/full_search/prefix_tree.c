/*
 * prefix_tree.c
 *
 *  Created on: 24.07.2015
 *      Author: tk4
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "prefix_tree.h"

static void* nextTreeLocation = NULL;

void pt_index_init ( pt_index *tree, pt_key_bitlen_t keyBitLen, pt_value_maxbitlen_t valueMaxBitLen ) {
	if ( tree == NULL ) {
		return;
	}
	if ( nextTreeLocation == NULL ) {
		switch ( sizeof(char*) ) {
		case 4:
		case 8:
			break;

		default:
			fprintf ( stderr, "ERROR: unsupported pointer width %lu!\n", sizeof(char*) );
			exit ( 1 );
		}
	}
	tree->keyBitLen = keyBitLen;
	tree->valueMaxBitLen = valueMaxBitLen;
	tree->maxDepth = keyBitLen / PT_PREFIX_BITLEN;
	pt_node_create ( &tree->root );
}

void pt_node_create ( pt_node **node ) {
	*node = calloc ( sizeof(pt_node), 1 );
}

bool pt_add ( pt_index* tree, void* key, void* value ) {
	bool result = false;
	if ( tree != NULL && key != NULL ) {
		pt_node_add ( 0, tree->root, key, tree->keyBitLen, value, tree->keyBitLen );
	}
	return result;
}

bool pt_node_add ( pt_key_bitlen_t depth, pt_node* node, void* key, pt_key_bitlen_t keyBitLen, void* value, pt_value_maxbitlen_t valueBitLen ) {
	bool result = false;
	pt_node* next = NULL;
	pt_node** target = NULL;
	pt_extent_list* list = NULL;
	void *cValue = NULL;
	pt_key_unit_t k;
	// the current byte index of the key that we look at, so only even numbers
	pt_key_bitlen_t curByte = ( depth & ~0x1 ) >> 1;
	bool doIt = true;

	// an uneven depth, so do the second part of the loop here
	if ( depth & 0x1 ) {
		// check LSBs
		k = ( (pt_key_unit_t*) key )[curByte] & PT_BITMASK_LOW;
		next = node->children[k];
		if ( next == NULL ) {
			target = (pt_node**) &node->children[k];
			doIt = false;
		} else if ( pt_pointer_is_reduced( next ) ) {
			target = (pt_node**) &node->children[k];
			list = (pt_extent_list*) pt_pointer_extract( next );
			doIt = false;
		} else if ( pt_pointer_is_value( next ) ) {
			cValue = (void*) pt_pointer_extract( next );
			doIt = false;
		}
		if ( doIt ) {
			node = next;
			++depth;
			++curByte; // next byte of the key
		}
	}

	if ( doIt ) {
		// we must break out of that loop at least at the leaf level
		while ( true ) {
			// check MSBs  -> move them right
			k = ( ( (pt_key_unit_t*) key )[curByte] & PT_BITMASK_HIGH ) >> PT_PREFIX_BITLEN;
			next = node->children[k];
			if ( next == NULL ) {
				target = (pt_node**) &node->children[k];
				break;
			} else if ( pt_pointer_is_reduced( next ) ) {
				target = (pt_node**) &node->children[k];
				list = (pt_extent_list*) pt_pointer_extract( next );
				break;
			} else if ( pt_pointer_is_value( next ) ) {
				cValue = (void*) pt_pointer_extract( next );
				break;
			}
			node = next;
			++depth;

			// check LSBs
			k = ( (pt_key_unit_t*) key )[curByte] & PT_BITMASK_LOW;
			next = node->children[k];
			if ( next == NULL ) {
				target = (pt_node**) &node->children[k];
				break;
			} else if ( pt_pointer_is_reduced( next ) ) {
				target = (pt_node**) &node->children[k];
				list = (pt_extent_list*) pt_pointer_extract( next );
				break;
			} else if ( pt_pointer_is_value( next ) ) {
				cValue = (void*) pt_pointer_extract( next );
				break;
			}
			node = next;
			++depth;
			++curByte; // next byte of the key
		}
	}

	// we need that here because we broke out of the while loop before and did not update it
	++depth;
	// now, if we arrived at an even depth we advanced one byte
	if ( depth & 0x1 == 0 ) {
		++curByte;
	}
	bool isLeafLevel = PT_BYTELEN_TO_BITLEN ( curByte ) >= keyBitLen;

	if ( list != NULL ) {
		if ( isLeafLevel || list->fill < PT_EXTENT_LIST_MAX_FILL ) {
			result = pt_extent_list_add ( list, key, keyBitLen, value, valueBitLen );
		} else {
			pt_node_create ( target );
			pt_extent_list_move ( depth, *target, list, keyBitLen, valueBitLen );
			// we do not reuse the list but the space should be reused the next time a list is requested
			pt_extent_list_free ( list );
			// don't forget to add the key...
			result = pt_node_add ( depth, *target, key, keyBitLen, value, valueBitLen );
		}
	} else if ( target != NULL ) {
		*target = (pt_node*) pt_extent_list_create ( keyBitLen, valueBitLen );
		list = (pt_extent_list*) *target;
		pt_pointer_set_flag( *target, PT_POINTER_FLAG_REDUCED );
		result = pt_extent_list_add ( list, key, keyBitLen, value, valueBitLen );
	} else if ( cValue != NULL ) {
		static uint32_t niy = 0;
		printf ( "not implemented yet #%d.\n", ++niy );
	}
	return result;
}

void* pt_find ( pt_index* tree, void* key ) {
	void *pValue = NULL;
	if ( tree != NULL && key != NULL ) {
		pt_node* node = tree->root;
		pt_key_bitlen_t cUnit = 0;
		pt_key_unit_t k;
		pt_extent_list *list;
		while ( node != NULL && ( cUnit << 3 ) <= tree->keyBitLen ) {
			k = ( ( (pt_key_unit_t*) key )[cUnit] & PT_BITMASK_HIGH ) >> PT_PREFIX_BITLEN;
			node = node->children[k];
			if ( node == NULL ) {
				break;
			}
			if ( pt_pointer_is_reduced( node ) ) {
				list = (pt_extent_list*) pt_pointer_extract( node );
				pValue = pt_extent_list_find ( list, key, tree->keyBitLen );
				break;
			}
			k = ( (pt_key_unit_t*) key )[cUnit] & PT_BITMASK_LOW;
			node = node->children[k];
			if ( pt_pointer_is_value( node ) ) {
				pValue = (void*) pt_pointer_extract( node );
			} else if ( pt_pointer_is_reduced( node ) ) {
				list = (pt_extent_list*) pt_pointer_extract( node );
				pValue = pt_extent_list_find ( list, key, tree->keyBitLen );
				break;
			}
			++cUnit;
		}
	}
	return pValue;
}

void* pt_update ( pt_index* tree, void* key, void* value ) {
	return NULL;
}

void* pt_delete ( pt_index* tree, void* key ) {
	return NULL;
}

pt_extent_list* pt_extent_list_create ( pt_key_bitlen_t keyBitLen, uint64_t valueBitLen ) {
	size_t size = sizeof(pt_extent_list) + ( PT_EXTENT_LIST_MAX_FILL * PT_BITLEN_TO_BYTELEN( keyBitLen ) );
	pt_extent_list* list = calloc ( ALIGN( size, 64 ), 1 );
	if ( keyBitLen <= PT_BITLEN( void* ) ) {
		list->isKeyInline = true;
	}
	if ( valueBitLen <= PT_BITLEN( void* ) ) {
		list->isValueInline = true;
	}
	return list;
}

bool pt_extent_list_add ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen, void* value, pt_value_maxbitlen_t valueBitLen ) {
	while ( list->fill >= PT_EXTENT_LIST_MAX_FILL && list->next != NULL ) {
		list = list->next;
	}
	if ( list->fill < PT_EXTENT_LIST_MAX_FILL ) {
		if ( list->isKeyInline ) {
			memcpy ( &list->keys[list->fill * PT_BITLEN_TO_BYTELEN( keyBitLen )], key, PT_BITLEN_TO_BYTELEN( keyBitLen ) );
		} else {
			memcpy ( &list->keys[list->fill * PT_BITLEN_TO_BYTELEN( keyBitLen )], &key, PT_BITLEN_TO_BYTELEN( keyBitLen ) );
		}
		if ( list->isValueInline ) {
			memcpy ( &list->values[list->fill], value, PT_BITLEN_TO_BYTELEN( valueBitLen ) );
		} else {
			list->values[list->fill] = value;
		}
		++list->fill;
		return true;
	} else {
		if ( list->next == NULL ) {
			list->next = pt_extent_list_create ( keyBitLen, valueBitLen );
			return pt_extent_list_add ( list->next, key, keyBitLen, value, valueBitLen );
		} else {
			printf ( "ERROR [" __FILE__ "@" TOSTR(__LINE__) "] list->next must not be null" );
			exit ( 1 );
		}
	}
}

void* pt_extent_list_find ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen ) {
	void* value = NULL;
	pt_fill_t i;
	do {
		if ( list->isKeyInline ) {
			for ( i = 0; i < list->fill && i < PT_EXTENT_LIST_MAX_FILL ; ++i ) {
				if ( 0 == memcmp ( (void*) &list->keys[i * PT_BITLEN_TO_BYTELEN( keyBitLen )], key, PT_BITLEN_TO_BYTELEN( keyBitLen ) ) ) {
					value = &list->values[i];
					break;
				}
			}
		} else {
			for ( i = 0; i < list->fill && i < PT_EXTENT_LIST_MAX_FILL ; ++i ) {
				if ( 0 == memcmp ( *(void**) &list->keys[i * PT_BITLEN_TO_BYTELEN( keyBitLen )], key, PT_BITLEN_TO_BYTELEN( keyBitLen ) ) ) {
					value = &list->values[i];
					break;
				}
			}
		}
	} while ( ( value == NULL ) && ( list = list->next ) != NULL );
	if ( value != NULL ) {
		if ( !list->isValueInline ) {
			value = *(void**) value;
		}
	}
	return value;
}

void* pt_extent_list_update ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen, void* value ) {
	return NULL;
}

void* pt_extent_list_delete ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen, pt_value_maxbitlen_t valueBitLen ) {
	return NULL;
}

bool pt_extent_list_move ( pt_key_bitlen_t depth, pt_node* node, pt_extent_list* list, pt_key_bitlen_t keyBitLen, pt_value_maxbitlen_t valueBitLen ) {
	pt_fill_t i;
	// each variant hands over pointers according to whether inlining is used or not
	if ( list->isKeyInline && list->isValueInline ) {
		for ( i = 0; i < list->fill && i < PT_EXTENT_LIST_MAX_FILL ; ++i ) {
			pt_node_add ( depth, node, &list->keys[i * PT_BITLEN_TO_BYTELEN( keyBitLen )], keyBitLen, &list->values[i], valueBitLen );
		}
	} else if ( list->isKeyInline ) {
		for ( i = 0; i < list->fill && i < PT_EXTENT_LIST_MAX_FILL ; ++i ) {
			pt_node_add ( depth, node, &list->keys[i * PT_BITLEN_TO_BYTELEN( keyBitLen )], keyBitLen, list->values[i], valueBitLen );
		}
	} else if ( list->isValueInline ) {
		for ( i = 0; i < list->fill && i < PT_EXTENT_LIST_MAX_FILL ; ++i ) {
			pt_node_add ( depth, node, *(pt_key_ptr_ptr_t) &list->keys[i * PT_BITLEN_TO_BYTELEN( keyBitLen )], keyBitLen, &list->values[i], valueBitLen );
		}
	} else {
		for ( i = 0; i < list->fill && i < PT_EXTENT_LIST_MAX_FILL ; ++i ) {
			pt_node_add ( depth, node, *(pt_key_ptr_ptr_t) &list->keys[i * PT_BITLEN_TO_BYTELEN( keyBitLen )], keyBitLen, list->values[i], valueBitLen );
		}
	}
	return false;
}

void pt_extent_list_free ( pt_extent_list* list ) {
	if ( list->next ) {
		pt_extent_list_free ( list->next );
	}
	free ( list );
}
