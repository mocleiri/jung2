/*
 * Created on Jul 8, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * An edge weight function that assigns weights as uniform transition 
 * probabilities to all incident edges of a vertex.
 * This is useful for random-walk-based scoring algorithms on undirected 
 * graphs, since each edge may need to take
 * on different weights depending on the direction in which it's being
 * traversed.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
public class UniformIncident<V, E> implements VertexEdgeWeight<V,E,Double>
{
    private Hypergraph<V,E> graph;
    
    public UniformIncident(Hypergraph<V,E> graph)
    {
        this.graph = graph;
    }
    
    public Double transform(VEPair<V,E> ve_pair)
    {
        V v = ve_pair.getV();
        return 1.0 / graph.degree(v);
    }

}
