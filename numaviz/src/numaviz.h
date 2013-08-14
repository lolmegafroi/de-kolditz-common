/*
 * numaviz.h
 *
 *  Created on: 12.08.2013
 *      Author: tk4
 */

#ifndef NUMAVIZ_H_
#define NUMAVIZ_H_
#include <string>
#include <map>
#include <vector>

using namespace std;

namespace numaviz {

typedef map<unsigned, unsigned> NumaDistances;

struct NumaInfo {
	unsigned nodeID;
	vector<unsigned> cpuIDs;
	unsigned memTotal;
	unsigned memFree;
	NumaDistances distances;
};

typedef map<unsigned, NumaInfo> NumaMap;

typedef const map<string, unsigned> PrintTypeMap;
extern PrintTypeMap PRINTTYPE_MAP;

class Numaviz {
	typedef int (*ptVizFunc)(Numaviz* instance);

	NumaMap numamap;
	static const char* basename_;
	static const char* baseext_dot;
	static const char* baseext_neato;
	static const char* baseext_fdp;
	static const char* baseext_sfdp;
	static const char* baseext_twopi;
	static const char* baseext_circo;

	char* filename_graphviz;
	char* filename_output;
	char* outputFormat;
	char* convertCMD;

	ptVizFunc printFunc;
	ptVizFunc deleteFunc;
	ptVizFunc convertFunc;

	static const int NOT_IMPLEMENTED;
	static const unsigned UNKNOWN;

public:
	Numaviz();
	virtual ~Numaviz();

	int update();

	bool setType(char* type);
	bool setType(string type);
	bool setType(unsigned type);

	bool setOutputFormat(char* outputFormat);
	bool setOutputFormat(string outputFormat);

	int print(bool deleteDependantFiles = false);

	static const unsigned DOT;
	static const unsigned NEATO;
	static const unsigned FDP;
	static const unsigned SFDP;
	static const unsigned TWOPI;
	static const unsigned CIRCO;
	static const unsigned DEFAULT_PRINT_TYPE;

	static const char* DEFAULT_OUTPUT_FORMAT;

	static int printDOT(Numaviz* instance);
	static int convertDOT(Numaviz* instance);
	static int deleteDOT(Numaviz* instance);

	static int printNEATO(Numaviz* instance);
	static int convertNEATO(Numaviz* instance);
	static int deleteNEATO(Numaviz* instance);

	static int printFDP(Numaviz* instance);
	static int convertFDP(Numaviz* instance);
	static int deleteFDP(Numaviz* instance);

	static int printSFDP(Numaviz* instance);
	static int convertSFDP(Numaviz* instance);
	static int deleteSFDP(Numaviz* instance);

	static int printTWOPI(Numaviz* instance);
	static int convertTWOPI(Numaviz* instance);
	static int deleteTWOPI(Numaviz* instance);

	static int printCIRCO(Numaviz* instance);
	static int convertCIRCO(Numaviz* instance);
	static int deleteCIRCO(Numaviz* instance);
};

}

#endif /* NUMAVIZ_H_ */
