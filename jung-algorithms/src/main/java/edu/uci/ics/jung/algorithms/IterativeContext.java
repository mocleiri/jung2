/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms;



/**

 */
public interface IterativeContext {
    

	/**
	 * Advances one step
	 */
	void step();

	/**
	 * The iteration is finished
	 */
	boolean done();
	
}
