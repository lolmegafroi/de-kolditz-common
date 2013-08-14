/*
 * numaviz.cpp
 *
 *  Created on: 12.08.2013
 *      Author: tk4
 */
#include "numaviz.h"

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <iostream>
#include <fstream>
#include <sstream>

namespace numaviz {

const char* Numaviz::basename_ = "numaviz";
const char* Numaviz::baseext_dot = ".dot";
const char* Numaviz::baseext_neato = ".neato";
const char* Numaviz::baseext_fdp = ".fdp";
const char* Numaviz::baseext_sfdp = ".sfdp";
const char* Numaviz::baseext_twopi = ".twopi";
const char* Numaviz::baseext_circo = ".circo";

const int Numaviz::NOT_IMPLEMENTED = -1;
const unsigned Numaviz::UNKNOWN = -1;
const unsigned Numaviz::DOT = 1;
const unsigned Numaviz::NEATO = 2;
const unsigned Numaviz::FDP = 3;
const unsigned Numaviz::SFDP = 4;
const unsigned Numaviz::TWOPI = 5;
const unsigned Numaviz::CIRCO = 6;
const unsigned Numaviz::DEFAULT_PRINT_TYPE = Numaviz::DOT;
const char* Numaviz::DEFAULT_OUTPUT_FORMAT = "pdf";

PrintTypeMap PRINTTYPE_MAP { { "dot", Numaviz::DOT }, { "neato", Numaviz::NEATO }, { "fdp", Numaviz::FDP }, { "sfdp", Numaviz::SFDP }, { "twopi", Numaviz::TWOPI }, { "circo", Numaviz::CIRCO } };

const char* test1 = R"(available: 2 nodes (0 1)
node 0 cpus: 0 1 2 3
node 0 size: 5119 MB
node 0 free: 2602 MB
node 1 cpus: 4 5 6 7
node 1 size: 5119 MB
node 1 free: 2602 MB
node distances:
node   0  1 
  0:  10 50 
  1:  50 10 )";

const char* test2 = R"(available: 7 nodes (0 1 2 3 4 5 6 7)
node 0 cpus: 0 1 2 3 4 5 6 7
node 0 size: 5119 MB
node 0 free: 2602 MB
node 1 cpus: 8 9 10 11 12 13 14 15
node 1 size: 5119 MB
node 1 free: 2602 MB
node 2 cpus: 32 33 34 35 36 37 38 39
node 2 size: 5119 MB
node 2 free: 2602 MB
node 3 cpus: 40 41 42 43 44 45 46 47
node 3 size: 5119 MB
node 3 free: 2602 MB
node 4 cpus: 48 49 50 51 52 53 54 55
node 4 size: 5119 MB
node 4 free: 2602 MB
node 5 cpus: 56 57 58 59 60 61 62 63
node 5 size: 5119 MB
node 5 free: 2602 MB
node 6 cpus: 16 17 18 19 20 21 22 23
node 6 size: 5119 MB
node 6 free: 2602 MB
node 7 cpus: 24 25 26 27 28 29 30 31
node 7 size: 5119 MB
node 7 free: 2602 MB
node distances:
node   0   1   2   3   4   5   6   7 
  0:  10  20 100 100 100 100  50  50 
  1:  20  10 100 100 100 100  50  50 
  2: 100 100  10  20 100 100  50  50 
  3: 100 100  20  10 100 100  50  50 
  4: 100 100 100 100  10  20 100 100 
  5: 100 100 100 100  20  10 100 100 
  6:  50  50  50  50 100 100  10  20 
  7:  50  50  50  50 100 100  20  10 )";

Numaviz::Numaviz() {
	filename_graphviz = NULL;
	filename_output = NULL;
	convertCMD = NULL;
	outputFormat = NULL;
	setType(Numaviz::DEFAULT_PRINT_TYPE);
	setOutputFormat(Numaviz::DEFAULT_OUTPUT_FORMAT);
	update();
}

Numaviz::~Numaviz() {
	if (filename_graphviz)
		delete[] filename_graphviz;
	if (filename_output)
		delete[] filename_output;
	if (outputFormat) {
		delete[] outputFormat;
	}
}

bool Numaviz::setType(char* type) {
	return setType(string(type));
}

bool Numaviz::setType(string type) {
	PrintTypeMap::const_iterator iter = PRINTTYPE_MAP.find(type);
	if (iter != PRINTTYPE_MAP.end()) {
		return setType(iter->second);
	} else {
		return setType(Numaviz::UNKNOWN);
	}
}

bool Numaviz::setType(unsigned type) {
	switch (type) {
	case Numaviz::DOT:
		printFunc = &Numaviz::printDOT;
		deleteFunc = &Numaviz::deleteDOT;
		convertFunc = &Numaviz::convertDOT;
		break;
	case Numaviz::NEATO:
		printFunc = &Numaviz::printNEATO;
		deleteFunc = &Numaviz::deleteNEATO;
		convertFunc = &Numaviz::convertNEATO;
		break;
	case Numaviz::FDP:
		printFunc = &Numaviz::printFDP;
		deleteFunc = &Numaviz::deleteFDP;
		convertFunc = &Numaviz::convertFDP;
		break;
	case Numaviz::SFDP:
		printFunc = &Numaviz::printSFDP;
		deleteFunc = &Numaviz::deleteSFDP;
		convertFunc = &Numaviz::convertSFDP;
		break;
	case Numaviz::TWOPI:
		printFunc = &Numaviz::printTWOPI;
		deleteFunc = &Numaviz::deleteTWOPI;
		convertFunc = &Numaviz::convertTWOPI;
		break;
	case Numaviz::CIRCO:
		printFunc = &Numaviz::printCIRCO;
		deleteFunc = &Numaviz::deleteCIRCO;
		convertFunc = &Numaviz::convertCIRCO;
		break;
	default:
		printFunc = NULL;
		deleteFunc = NULL;
		convertFunc = NULL;
		return false;
	}
	return true;
}

bool Numaviz::setOutputFormat(char* outputFormat) {
	return setOutputFormat(string(outputFormat));
}

bool Numaviz::setOutputFormat(string outputFormat) {
	if (this->outputFormat && this->outputFormat != DEFAULT_OUTPUT_FORMAT) {
		delete[] this->outputFormat;
	}
	this->outputFormat = new char[outputFormat.length()];
	strcpy(this->outputFormat, outputFormat.c_str());
	return true;
}

int Numaviz::update() {
	numamap.clear();

	FILE* cmd = fopen("test", "r");
	if (cmd == NULL) {
		cmd = popen("numactl --hardware", "r");
	}
	// FILE* cmd = popen("numactl --hardware", "r");
	if (cmd == NULL) {
		cerr << R"(Could not executy "numactl --hardware"!)" << endl;
		return (__LINE__);
	}
	size_t size = 1024;
	char* buf = new char[size];
	string word;
	unsigned nNodes = -1;
	int* nodesIDs = NULL;

	// read "available" line
	if (getline(&buf, &size, cmd) > 0) {
		istringstream line(buf);
		line >> word;
		if (strcmp(word.c_str(), "available:") == 0) {
			line >> nNodes;
			line >> word;
			if (strcmp(word.c_str(), "nodes") != 0) {
				cerr << R"(wrong numactl version?!)" << endl;
				delete[] buf;
				if (nodesIDs)
					delete[] nodesIDs;
				return (__LINE__);
			}
		}
		if (nNodes < 1) {
			cerr << R"(Could not acquire the amount of nodes!)" << endl;
			delete[] buf;
			if (nodesIDs)
				delete[] nodesIDs;
			return (__LINE__);
		}
		nodesIDs = new int[nNodes];
		for (unsigned idx = 0; idx < nNodes; ++idx) {
			line >> word;
			if (word.at(0) == '(') {
				word.erase(word.begin());
			}
			istringstream(word) >> nodesIDs[idx];
			if (word.at(word.length() - 1) == ')') {
				break;
			}
		}
		cout << "numactl reports " << nNodes << (nNodes > 1 ? " nodes: " : " node: ");
		for (unsigned i = 0; i < nNodes; ++i) {
			cout << nodesIDs[i];
			if (i + 1 < nNodes) {
				cout << ", ";
			}
		}
		cout << endl;
	}

	bool checkDistances = false;

	unsigned lineNr = 1;
	unsigned currentNodeID;
	unsigned cpuID, nodeID;
	// node lines
	while (getline(&buf, &size, cmd) > 0) {
		currentNodeID = -1;
		++lineNr;
		istringstream line(buf);
		line >> word;
		if (word.compare("node") != 0) {
			cerr << "Unexpected token \"" << word << "\" in line " << lineNr << "! Skipping. Whole line contents was: " << buf << endl;
			continue;
		}
		line >> currentNodeID;
		if (line.fail()) {
			line.clear();
			line.seekg(word.size());
			line >> word >> word;
			if (word.compare("distances:") == 0) {
				checkDistances = true;
				break;
			} else {
				cerr << "Unexpected token \"" << word << "\" in line " << lineNr << "! Skipping. Whole line contents was: " << buf << endl;
				continue;
			}
			break;
		}
		if (currentNodeID < 0) {
			cerr << "Unexpected token, the node id was not found!" << endl;
			return (__LINE__);
		}
		line >> word;
		if (word.compare("cpus:") == 0) {
			do {
				cpuID = -1;
				line >> cpuID;
				if (line.fail()) {
					break;
				}
				numamap[currentNodeID].cpuIDs.push_back(cpuID);
			} while (true);
		}
		// other "node" lines are currently not used
	}

	// check distances
	if (checkDistances) {
		++lineNr;
		vector<unsigned> distanceIDs;
		if (getline(&buf, &size, cmd) > 0) {
			istringstream line(buf);
			line >> word;
			if (word.compare("node") != 0) {
				cerr << "Unexpected token \"" << word << "\"! Wrong version?" << endl;
				delete[] buf;
				if (nodesIDs)
					delete[] nodesIDs;
				return __LINE__;
			}
			do {
				nodeID = -1;
				line >> nodeID;
				if (line.fail()) {
					break;
				}
				distanceIDs.push_back(nodeID);
			} while (true);
		}
		while (getline(&buf, &size, cmd) > 0) {
			++lineNr;
			currentNodeID = -1;
			istringstream line(buf);
			line >> currentNodeID;
			if (line.fail()) {
				cerr << "Error on line " << lineNr << ". Expected a node ID. Continuing. Contents is: " << buf << endl;
				continue;
			}
			line >> word;
			if (word.compare(":") != 0) {
				cerr << "Unexpected token \"" << word << "\". Expected ':'. Skipping line (Contents: \"" << buf << '"' << endl;
				continue;
			}
			unsigned distance, idx = 0;
			do {
				distance = -1;
				line >> distance;
				if (line.fail()) {
					break;
				}
				numamap[currentNodeID].distances[distanceIDs[idx++]] = distance;
			} while (true);
		}
	}

	unsigned id;
	for (NumaMap::iterator iter = numamap.begin(); iter != numamap.end(); ++iter) {
		cout << "node " << iter->first << " contains " << iter->second.cpuIDs.size() << " CPUs ";
		for (size_t i = 0; i < iter->second.cpuIDs.size(); ++i) {
			id = iter->second.cpuIDs[i];
			cout << id;
			if ((i + 1) < iter->second.cpuIDs.size()) {
				cout << ", ";
			}
		}
		cout << endl;
	}
	cout << "node distances:" << endl;
	for (NumaMap::iterator iter = numamap.begin(); iter != numamap.end(); ++iter) {
		cout << "node " << iter->first << ": ";
		bool first = true;
		for (NumaDistances::iterator iter2 = iter->second.distances.begin(); iter2 != iter->second.distances.end(); ++iter2) {
			if (first)
				first = false;
			else
				cout << ' ';
			cout << iter2->first << ":" << iter2->second;
		}
		cout << endl;
	}

	delete[] buf;
	if (nodesIDs)
		delete[] nodesIDs;
	return 0;
}

int Numaviz::printDOT(Numaviz* instance) {
	if (instance->filename_graphviz == NULL) {
		string s;
		s += basename_;
		s += baseext_dot;
		instance->filename_graphviz = new char[s.length() + 1];
		strcpy(instance->filename_graphviz, s.c_str());
	}
	ofstream fout(instance->filename_graphviz);
	if (!fout) {
		cerr << "Could not open file \"" << instance->filename_graphviz << "\" for writing!" << endl;
		return (__LINE__);
	}
	fout << "digraph numaviz {\n";
	fout << "node [shape=record];\n";
	unsigned cpuID, nodeID, otherNodeID, distance;
	for (NumaMap::iterator iter = instance->numamap.begin(); iter != instance->numamap.end(); ++iter) {
		nodeID = iter->first;
		// a subgraph for each numa node
		// fout << "subgraph numa" << nodeID << "{\n";
		// fout << "\tnode" << nodeID << R"([shape=box,color=black,label="NUMA Node )" << nodeID << "\"];\n";
		fout << "node" << nodeID << "[label=\"{<node" << nodeID << ">NUMA node " << nodeID << "|{";
		bool first = true;
		for (size_t i = 0; i < iter->second.cpuIDs.size(); ++i) {
			cpuID = iter->second.cpuIDs[i];
			// fout << "\tcpu_" << nodeID << '_' << cpuID << R"([label="CPU )" << cpuID << "\"];\n";
			if (first)
				first = false;
			else
				fout << "|";
			fout << "<cpu_" << nodeID << '_' << cpuID << ">CPU " << cpuID;
		}
		fout << "}}\"]\n";
		for (size_t i = 0; i < iter->second.cpuIDs.size(); ++i) {
			/*
			 cpuID = iter->second.cpuIDs[i];
			 // links from CPUs to their NUMA nodes
			 // fout << "cpu_" << nodeID << '_' << id << " -> node" << nodeID << " [arrowhead=none,label=\"" << iter->second.distances[nodeID] << "\"];\n";
			 fout << "\tcpu_" << nodeID << '_' << cpuID << " -> node" << nodeID << " [arrowhead=normal,arrowtail=normal];\n";
			 */
			// links between all CPUs of the same NUMA node
			/*
			 for (size_t j = 0; j < iter->second.cpuIDs.size(); ++j) {
			 if (j != i) {
			 fout << "cpu" << iter->first << '_' << id << " -> " << "cpu" << iter->first << '_' << iter->second.cpuIDs[j] << ";\n";
			 }
			 }
			 */
		}
		// end subgraph
		// fout << "}\n";
	}
	fout << '\n';
	for (NumaMap::iterator iter = instance->numamap.begin(); iter != instance->numamap.end(); ++iter) {
		nodeID = iter->first;
		// links between NUMA nodes (subgraphs)
		for (NumaDistances::iterator iter2 = iter->second.distances.begin(); iter2 != iter->second.distances.end(); ++iter2) {
			otherNodeID = iter2->first;
			distance = iter2->second;
			if (iter2->second == instance->numamap.find(otherNodeID)->second.distances.find(nodeID)->second) {
				// same distances so we need only one undirected link
				if (nodeID < otherNodeID) {
					fout << "node" << nodeID << " -> node" << otherNodeID << " [arrowhead=none,style=bold,label=\"" << distance << "\",len=" << ((double) iter2->second) / 10.0 << "];\n";
				}
			} else {
				fout << "node" << nodeID << " -> node" << otherNodeID << " [style=bold,label=\"" << distance << "\",len=" << ((double) iter2->second) / 10.0 << "];\n";
			}
		}
	}
	fout << "}" << endl;
	fout.close();

	cout << "Created dot-file \"" << instance->filename_graphviz << "\"" << endl;
	return 0;
}

int Numaviz::convertDOT(Numaviz* instance) {
	string s("dot -T");
	s += instance->outputFormat;
	s += " -o";
	s += instance->filename_output;
	s += " ";
	s += instance->filename_graphviz;
	instance->convertCMD = new char[s.length()];
	strcpy(instance->convertCMD, s.c_str());
	return system(instance->convertCMD);
}

int Numaviz::deleteDOT(Numaviz* instance) {
	if (instance->filename_graphviz != NULL) {
		return unlink(instance->filename_graphviz);
	}
	return 0;
}

int Numaviz::printNEATO(Numaviz* instance) {
	return printDOT(instance);
}

int Numaviz::convertNEATO(Numaviz* instance) {
	string s("neato -T");
	s += instance->outputFormat;
	s += " -o";
	s += instance->filename_output;
	s += " ";
	s += instance->filename_graphviz;
	instance->convertCMD = new char[s.length()];
	strcpy(instance->convertCMD, s.c_str());
	return system(instance->convertCMD);
}

int Numaviz::deleteNEATO(Numaviz* instance) {
	return deleteDOT(instance);
}

int Numaviz::printFDP(Numaviz* instance) {
	return printDOT(instance);
}

int Numaviz::convertFDP(Numaviz* instance) {
	string s("fdp -T");
	s += instance->outputFormat;
	s += " -o";
	s += instance->filename_output;
	s += " ";
	s += instance->filename_graphviz;
	instance->convertCMD = new char[s.length()];
	strcpy(instance->convertCMD, s.c_str());
	return system(instance->convertCMD);
}

int Numaviz::deleteFDP(Numaviz* instance) {
	return deleteDOT(instance);
}

int Numaviz::printSFDP(Numaviz* instance) {
	return printDOT(instance);
}

int Numaviz::convertSFDP(Numaviz* instance) {
	string s("sfdp -T");
	s += instance->outputFormat;
	s += " -o";
	s += instance->filename_output;
	s += " ";
	s += instance->filename_graphviz;
	instance->convertCMD = new char[s.length()];
	strcpy(instance->convertCMD, s.c_str());
	return system(instance->convertCMD);
}

int Numaviz::deleteSFDP(Numaviz* instance) {
	return deleteDOT(instance);
}

int Numaviz::printTWOPI(Numaviz* instance) {
	return printDOT(instance);
}

int Numaviz::convertTWOPI(Numaviz* instance) {
	string s("twopi -T");
	s += instance->outputFormat;
	s += " -o";
	s += instance->filename_output;
	s += " ";
	s += instance->filename_graphviz;
	instance->convertCMD = new char[s.length()];
	strcpy(instance->convertCMD, s.c_str());
	return system(instance->convertCMD);
}

int Numaviz::deleteTWOPI(Numaviz* instance) {
	return deleteDOT(instance);
}

int Numaviz::printCIRCO(Numaviz* instance) {
	return printDOT(instance);
}

int Numaviz::convertCIRCO(Numaviz* instance) {
	string s("circo -T");
	s += instance->outputFormat;
	s += " -o";
	s += instance->filename_output;
	s += " ";
	s += instance->filename_graphviz;
	instance->convertCMD = new char[s.length()];
	strcpy(instance->convertCMD, s.c_str());
	return system(instance->convertCMD);
}

int Numaviz::deleteCIRCO(Numaviz* instance) {
	return deleteDOT(instance);
}

int Numaviz::print(bool deleteDependantFiles) {
	// check functions
	if (!printFunc) {
		cerr << "No correct print function set! Aborting." << endl;
		return __LINE__;
	}
	if (deleteDependantFiles && !deleteFunc) {
		cerr << "No delete function set although required! Aborting." << endl;
		return __LINE__;
	}
	if (convertFunc == NULL) {
		cerr << "No converter function set! Aborting." << endl;
		return __LINE__;
	}

	// create dot / neato / ... file
	int result = printFunc(this);
	if (result == Numaviz::NOT_IMPLEMENTED) {
		cerr << "This print type is not implemented yet! Aborting." << endl;
		return __LINE__;
	} else if (result > 0) {
		cerr << "Could not create the graphviz file! Aborting." << endl;
		return result;
	}

	// set filename
	if (filename_output == NULL) {
		string s;
		s += basename_;
		s += '.';
		s += outputFormat;
		filename_output = new char[s.length() + 1];
		strcpy(filename_output, s.c_str());
	}

	// convert from original file to .ps file
	if ((result = convertFunc(this)) != 0) {
		if (result == NOT_IMPLEMENTED) {
			cerr << "This conversion method is not yet implemented!" << endl;
		} else {
			cerr << "Could not execute command \"" << convertCMD << "\"!" << endl;
		}
	} else {
		cout << "Created " << outputFormat << "-file \"" << filename_output << '"' << endl;
		// unlink original file if desired
		if (deleteDependantFiles) {
			deleteFunc(this);
		}
	}
	return result;
}

}
