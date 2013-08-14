/*
 * main.cpp
 *
 *  Created on: 12.08.2013
 *      Author: tk4
 */

#include "main.h"
#include "numaviz.h"

#include <string.h>

#include <iostream>

using namespace std;
using namespace numaviz;

void usage();

int main(int argc, char* argv[]) {
	unsigned type = Numaviz::DEFAULT_PRINT_TYPE;
	char* outputFormat = NULL;
	for (int i = 1; i < argc; ++i) {
		if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0 || strcmp(argv[i], "-?") == 0) {
			usage();
			return 0;
		} else if (strncmp(argv[i], "-t", 2) == 0) {
			PrintTypeMap::const_iterator iter = PRINTTYPE_MAP.find(string(argv[i] + 2));
			if (iter != PRINTTYPE_MAP.end()) {
				type = iter->second;
			} else {
				usage();
				return 1;
			}
		} else if (strncmp(argv[i], "-f", 2) == 0) {
			string s(argv[i] + 2);
			outputFormat = new char[s.length()];
			strcpy(outputFormat, s.c_str());
		} else {
			cerr << "Unknown command line parameter \"" << argv[i] << "\"! Aborting." << endl;
			usage();
			return 2;
		}
	}
	Numaviz nv;
	nv.setType(type);
	if (outputFormat)
		nv.setOutputFormat(outputFormat);
	return nv.print();
}

void usage() {
	cout << R"(NUMA nodes visualizer depending on "numactl" and "graphviz". © Till Kolditz 2013)" << "\n\n";
	cout << "Usage: numaviz [-t<type>] [-f<format>] [-o<file>]\n\n";
	cout << R"(    -t<type>          Print type: <type> may be one of "dot", "neato", "fdp", "sfdp", "twopi", or "circo". See the graphviz documentation for more information under http://graphviz.org.)" << endl;
	cout << R"(    -f<format>        Output format: <format> may be one of those recognized by graphviz (bmp, fig, gif, gtk, ico, imap, jpg, jpeg, jpe, pdf, plain, png, ps, ps2, svg, svgz, tif, tiff, vml, vmlz, vrml, xlib, ...))" << endl;
	cout << "    -o<file>          Output file name.\n" << endl;
}
