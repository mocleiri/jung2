/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.connectivity;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * @author Scott White, adapted to jung2 by Tom Nelson
 */
public class TestBFSDistanceLabeler extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBFSDistanceLabeler.class);
	}

	protected void setUp() {

	}

	public void test() {
        Graph<Number,Number> graph = new UndirectedSparseGraph<Number,Number>();
        for(int i=0; i<6; i++) {
        	graph.addVertex(i);
        }
        int j = 0;
        graph.addEdge(j++,0,1);
        graph.addEdge(j++,0,5);
        graph.addEdge(j++,0,3);
        graph.addEdge(j++,0,4);
        graph.addEdge(j++,1,5);
        graph.addEdge(j++,3,4);
        graph.addEdge(j++,3,2);
        graph.addEdge(j++,5,2);
        Number root = 0;

		BFSDistanceLabeler<Number,Number> labeler = new BFSDistanceLabeler<Number,Number>();
		labeler.labelDistances(graph,root);

		Assert.assertEquals(labeler.getPredecessors(root).size(),0);
        Assert.assertEquals(labeler.getPredecessors(1).size(),1);
        Assert.assertEquals(labeler.getPredecessors(2).size(),2);
        Assert.assertEquals(labeler.getPredecessors(3).size(),1);
        Assert.assertEquals(labeler.getPredecessors(4).size(),1);
        Assert.assertEquals(labeler.getPredecessors(5).size(),1);

        Assert.assertEquals(labeler.getDistance(graph,0),0);
        Assert.assertEquals(labeler.getDistance(graph,1),1);
        Assert.assertEquals(labeler.getDistance(graph,2),2);
        Assert.assertEquals(labeler.getDistance(graph,3),1);
        Assert.assertEquals(labeler.getDistance(graph,4),1);
        Assert.assertEquals(labeler.getDistance(graph,5),1);

	}
}