/*
 * prefix_tree.h
 *
 * A node pointer with flag REDUCED set is actually a pointer to an extent list!
 *
 *  Created on: 24.07.2015
 *      Author: tk4
 */

#ifndef PREFIX_TREE_H_
#define PREFIX_TREE_H_

/*************************************/
/* INCLUDES                          */
/*************************************/
#include "fs_base.h"

/*************************************/
/* PUBLIC DEFINES                    */
/*************************************/

#define PT_BITS_PER_BYTE (8)
#define PT_PREFIX_BITLEN (4)
#define PT_NUM_CHILDREN (1 << PT_PREFIX_BITLEN)
#define PT_BYTE_SHIFT (sizeof(key_unit) - PT_PREFIX_BITLEN)
#define PT_BITMASK_LOW (0x0F)
#define PT_BITMASK_HIGH (0xF0)
#define PT_BITLEN(type) (sizeof(type) * PT_BITS_PER_BYTE)
#define PT_BITLEN_TO_BYTELEN(bits) (bits / PT_BITS_PER_BYTE)
#define PT_BYTELEN_TO_BITLEN(bytes) (bytes * PT_BITS_PER_BYTE)
#define PT_MAXSIZE (1024 * 1024 * 1024)                                  /* maximum 1 GB trees just for now */
#define PT_MAXNUMTREES ((1 << (PT_BITS_PER_BYTE * sizeof(char*) - 1)) / PT_MAXSIZE - 1)     /* keep at least 1 GB */

#define PT_POINTER_BITMASK (sizeof(pt_node*) - 1)    /* Let's use always-zero bits due to alignment */

#define PT_POINTER_FLAG_VALUE   0x0001 /* currently not really used */
#define PT_POINTER_FLAG_REDUCED 0x0002 /* because all key/value (pointer) pairs are stored in extent lists anyways */

#define pt_pointer_extract(child) ((pt_node*)(((pt_key_ptr_len_t)child) & ~PT_POINTER_BITMASK))
#define pt_pointer_is_value(child) ((pt_node*)(((pt_key_ptr_len_t)child) & PT_POINTER_FLAG_VALUE))
#define pt_pointer_is_reduced(child) ((pt_node*)(((pt_key_ptr_len_t)child) & PT_POINTER_FLAG_REDUCED))
#define pt_pointer_set_flag(child, flag) do { child = (pt_node*)(((pt_key_ptr_len_t)child) | (flag & PT_POINTER_BITMASK)); } while(0)
#define pt_pointer_clear_flag(child, flag) do { child = (pt_node*)(((pt_key_ptr_len_t)child) & ~(flag & PT_POINTER_BITMASK)); } while(0)

/*************************************/
/* PUBLIC TYPES                      */
/*************************************/
struct pt_index;
struct pt_node;
struct pt_extent_list;
struct pt_find_result;

typedef uint8_t pt_key_unit_t;
typedef uint16_t pt_key_bitlen_t;
typedef uint64_t pt_value_maxbitlen_t;
typedef uint16_t pt_fill_t;

#ifdef __x86_64__
typedef uint64_t pt_key_ptr_len_t;
typedef uint64_t* pt_key_ptr_t;
typedef uint64_t** pt_key_ptr_ptr_t;
#elif defined __i386__
typedef uint32_t pt_key_ptr_len_t;
typedef uint32_t* pt_key_ptr_t;
typedef uint32_t** pt_key_ptr_ptr_t;
#endif

typedef struct pt_node {
	void *children[PT_NUM_CHILDREN];
} pt_node;

typedef struct pt_index {
	pt_node *root;
	pt_key_bitlen_t keyBitLen;
	pt_value_maxbitlen_t valueMaxBitLen;
	pt_fill_t maxDepth;
	// void             *virtualRangeStart;
	// void             *virtualRangeLength;
} pt_index, prefix_tree;

#define PT_EXTENT_LIST_MAX_FILL 16

typedef struct pt_extent_list {
	pt_fill_t fill;
	/* flag bit fields */
	uint8_t isKeyInline :1;
	uint8_t isValueInline :1;
	uint8_t unused2 :1;
	uint8_t unused3 :1;
	uint8_t unused4 :1;
	uint8_t unused5 :1;
	uint8_t unused6 :1;
	uint8_t unused7 :1;
	void *next;
	void *values[PT_EXTENT_LIST_MAX_FILL];
	uint8_t keys[0];
} pt_extent_list;

typedef struct pt_find_result {
	void *valueStart;
	void *valueEnd;
} pt_find_result;

/*************************************/
/* PUBLIC FUNCTIONS                  */
/*************************************/
void pt_index_init ( pt_index *tree, pt_key_bitlen_t keyBitLen, pt_value_maxbitlen_t valueMaxBitLen );

void pt_node_create ( pt_node **node );

bool pt_node_add ( pt_key_bitlen_t depth, pt_node* node, void* key, pt_key_bitlen_t keyBitLen, void* value, pt_value_maxbitlen_t valueBitLen );

bool pt_add ( pt_index* tree, void* key, void* value );

void* pt_find ( pt_index* tree, void* key );

void* pt_update ( pt_index* tree, void* key, void* value );

void* pt_delete ( pt_index* tree, void* key );

pt_extent_list* pt_extent_list_create ( pt_key_bitlen_t keyBitLen, uint64_t valueBitLen );

bool pt_extent_list_add ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen, void* value, pt_value_maxbitlen_t valueBitLen );

void* pt_extent_list_find ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen );

void* pt_extent_list_update ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen, void* value );

void* pt_extent_list_delete ( pt_extent_list* list, void* key, pt_key_bitlen_t keyBitLen, pt_value_maxbitlen_t valueBitLen );

/* Adds its key/value (pointer) pairs to the given nodes */
bool pt_extent_list_move ( pt_key_bitlen_t depth, pt_node* node, pt_extent_list* list, pt_key_bitlen_t keyBitLen, pt_value_maxbitlen_t valueBitLen );

void pt_extent_list_free ( pt_extent_list* list );

/*************************************/
/* PRIVATE TYPES                     */
/*************************************/

/*************************************/
/* PRIVATE FUNCTIONS                 */
/*************************************/

#endif /* PREFIX_TREE_H_ */
