#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "prefix_tree.h"

#define NUMELEMENTS 10000000
#define NUMELEMENTSTXT 1000000

uint32_t i, k;
volatile void* pVal = NULL;
time_t t;

void test1 ( );
void test2 ( );
void test3 ( );
void test4 ( );

int main ( int argc, char* argv[] ) {
	// test1 ( );
	test2 ( );
	test3 ( );
	test4 ( );
}

void test1 ( ) {
	pt_index tree;
	pt_index_init ( &tree, 32, 32 );
	uint32_t keys[4], values[4];
	for ( i = 0; i < 4 ; ++i ) {
		keys[i] = i * 2;
		values[i] = i * 2 + 1;
		pt_add ( &tree, &keys[i], &values[i] );
		pVal = pt_find ( &tree, &keys[i] );
		printf ( "  keys[%u] = %u\n"
				" value[%u] = %u\n"
				"&value[%u] = %lx\n"
				"  pVal[%u] = %lx\n", i, keys[i], i, values[i], i, (uint64_t) &values[i], i, (uint64_t) pVal );
		if ( pVal != NULL ) {
			printf ( " *pVal = %u\n", *(uint32_t*) pVal );
		}
	}
}

void test2 ( ) {
	pt_index tree2;
	pt_index_init ( &tree2, 32, 32 );
	uint32_t *keys2 = malloc ( sizeof(uint32_t) * NUMELEMENTS );
	uint32_t *vals2 = malloc ( sizeof(uint32_t) * NUMELEMENTS );
	t = clock ( );
	for ( i = 0; i < NUMELEMENTS ; ++i ) {
		keys2[i] = i * 2;
		vals2[i] = i * 2 + 1;
		pt_add ( &tree2, &keys2[i], &vals2[i] );
	}
	t = clock ( ) - t;
	printf ( "Tree2 : Inserting " TOSTR(NUMELEMENTS)" keys took %f seconds.\n", (float) t / CLOCKS_PER_SEC );

	t = clock ( );
	for ( i = 0; i < NUMELEMENTS ; ++i ) {
		pVal = pt_find ( &tree2, &keys2[i] );
	}
	t = clock ( ) - t;
	printf ( "Tree2 : Finding " TOSTR(NUMELEMENTS)" keys took %f seconds.\n", (float) t / CLOCKS_PER_SEC );

	free ( keys2 );
	free ( vals2 );
	keys2 = NULL;
	vals2 = NULL;
}

void test3 ( ) {
	uint32_t key32, val32;
	pt_index tree3;
	pt_index_init ( &tree3, 32, 32 );
	t = clock ( );
	for ( i = 0; i < NUMELEMENTS ; ++i ) {
		key32 = i * 2;
		val32 = i * 2 + 1;
		pt_add ( &tree3, &key32, &val32 );
	}
	t = clock ( ) - t;
	printf ( "Tree3 : Inserting " TOSTR(NUMELEMENTS)" keys took %f seconds.\n", (float) t / CLOCKS_PER_SEC );

	t = clock ( );
	for ( i = 0; i < NUMELEMENTS ; ++i ) {
		key32 = i * 2;
		pVal = pt_find ( &tree3, &key32 );
	}
	t = clock ( ) - t;
	printf ( "Tree3 : Finding " TOSTR(NUMELEMENTS)" keys took %f seconds.\n", (float) t / CLOCKS_PER_SEC );

	t = clock ( );
	uint32_t numBadValues = 0;
	uint32_t numNullPointers = 0;
	for ( i = 0; i < NUMELEMENTS ; ++i ) {
		key32 = i * 2;
		pVal = pt_find ( &tree3, &key32 );
		numNullPointers += ( pVal == NULL );
		numBadValues += ( pVal == NULL ) || ( ( *(uint32_t*) pVal ) != ( i * 2 + 1 ) );
	}
	numBadValues -= numNullPointers;
	t = clock ( ) - t;
	printf ( "Tree3 : Verifying " TOSTR(NUMELEMENTS)" keys took %f seconds. %u bad values found. %u null pointers.\n", (float) t / CLOCKS_PER_SEC, numBadValues, numNullPointers );
}

void test4 ( ) {
#define TXT_NUMBITS (128)
#define VAL_MINBYTES (PT_BITLEN_TO_BYTELEN(16))
#define VAL_MAXBYTES (PT_BITLEN_TO_BYTELEN(TXT_NUMBITS))
#define VAL_VARBYTES (VAL_MAXBYTES - VAL_MINBYTES)

	char* keysTxt = malloc ( PT_BITLEN_TO_BYTELEN(TXT_NUMBITS) * NUMELEMENTSTXT );
	uint64_t *valsTxt = malloc ( PT_BITLEN_TO_BYTELEN(64) * NUMELEMENTSTXT );

	t = clock ( );
	uint32_t lenBytes;
	char* pKey = keysTxt;
	for ( i = 0; i < NUMELEMENTSTXT ; ++i ) {
		// at least 16 bits, at most 128 bits of non-null bytes
		lenBytes = ( xorshift32 ( ) % ( VAL_VARBYTES + 1 ) ) + VAL_MINBYTES;
		for ( k = 0; k < lenBytes ; ++k ) {
			*pKey = (char) ( ( ( xorshift32 ( ) & ( TXT_NUMBITS - 1 ) ) + 32 ) & ( 0xFF ) );
			pKey++;
		}
		for ( ; k < PT_BITLEN_TO_BYTELEN( TXT_NUMBITS ) ; ++k ) {
			*pKey++ = 0;
		}
	}
	t = clock ( ) - t;
	printf ( "TreeTxt : Creating " TOSTR(NUMELEMENTSTXT)" random keys took %f seconds\n", (float) t / CLOCKS_PER_SEC );
	if ( pKey != ( keysTxt + PT_BITLEN_TO_BYTELEN(TXT_NUMBITS) * NUMELEMENTSTXT ) ) {
		printf ( "TreeTxt: Overwrote key array!" );
	}
	/*
	 char* ptKey2 = keysTxt;
	 for ( i = 0; i < 10 ; ++i ) {
	 printf ( "\t%u: %s\n", i, ptKey2 );
	 ptKey2 += PREFIX_TREE_BITLEN_TO_BYTELEN( 128 );
	 }
	 */

	pt_index treeTxt;
	pt_index_init ( &treeTxt, TXT_NUMBITS, 64 );

	t = clock ( );
	pKey = keysTxt;
	uint64_t *pValOrg = valsTxt;
	for ( i = 0; i < NUMELEMENTSTXT ; ++i ) {
		*pValOrg = xorshift64 ( );
		pt_add ( &treeTxt, pKey, pValOrg ); // I know that this will be stored inline

		pKey += PT_BITLEN_TO_BYTELEN( TXT_NUMBITS );
		++pValOrg;
	}
	t = clock ( ) - t;
	printf ( "TreeTxt : Inserting " TOSTR(NUMELEMENTSTXT)" keys took %f seconds\n", (float) t / CLOCKS_PER_SEC );

	t = clock ( );
	uint32_t numBadValues = 0;
	uint32_t numNullPointers = 0;
	pKey = keysTxt;
	pValOrg = valsTxt;
	for ( i = 0; i < NUMELEMENTSTXT ; ++i ) {
		pVal = pt_find ( &treeTxt, pKey );
		numNullPointers += ( pVal == NULL );
		numBadValues += ( pVal == NULL ) || ( memcmp ( pVal, pValOrg, PT_BITLEN_TO_BYTELEN( TXT_NUMBITS ) ) != 0 );

		pKey += PT_BITLEN_TO_BYTELEN( TXT_NUMBITS );
		++pValOrg;
	}
	numBadValues -= numNullPointers;
	t = clock ( ) - t;
	printf ( "TreeTxt : Verifying " TOSTR(NUMELEMENTS)" keys took %f seconds. %u bad values found. %u null pointers.\n", (float) t / CLOCKS_PER_SEC, numBadValues, numNullPointers );

	free ( keysTxt );
	free ( valsTxt );
	keysTxt = NULL;
	valsTxt = NULL;
}
