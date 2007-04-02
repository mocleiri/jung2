/*
 * Created on Oct 18, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings("serial")
public class UndirectedOrderedSparseMultigraph<V,E> 
    extends UndirectedSparseMultigraph<V,E>
    implements UndirectedGraph<V,E>, Serializable {
	
	public static <V,E> Factory<UndirectedGraph<V,E>> getFactory() {
		return new Factory<UndirectedGraph<V,E>> () {
			public UndirectedGraph<V,E> create() {
				return new UndirectedOrderedSparseMultigraph<V,E>();
			}
		};
	}

    public UndirectedOrderedSparseMultigraph() {
        vertices = new LinkedHashMap<V, Set<E>>();
        edges = new LinkedHashMap<E, Pair<V>>();
    }

    public boolean addVertex(V vertex) {
    	if(vertex == null) {
    		throw new IllegalArgumentException("vertex may not be null");
    	}
        if (!vertices.containsKey(vertex))
        {
            vertices.put(vertex, new LinkedHashSet<E>());
            return true;
        } else {
            return false;
        }
    }

    public Collection<V> getNeighbors(V vertex) {
        Set<V> neighbors = new LinkedHashSet<V>();
        for (E edge : getIncident_internal(vertex))
        {
            Pair<V> endpoints = this.getEndpoints(edge);
            V e_a = endpoints.getFirst();
            V e_b = endpoints.getSecond();
            if (vertex.equals(e_a))
                neighbors.add(e_b);
            else
                neighbors.add(e_a);
        }
        
        return Collections.unmodifiableCollection(neighbors);
    }
}
