/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Dec 26, 2001
 *
 */
package edu.uci.ics.jung.algorithms.filters.impl;
import static edu.uci.ics.jung.algorithms.filters.impl.KNeighborhoodFilter.EdgeType.IN;
import static edu.uci.ics.jung.algorithms.filters.impl.KNeighborhoodFilter.EdgeType.IN_OUT;
import static edu.uci.ics.jung.algorithms.filters.impl.KNeighborhoodFilter.EdgeType.OUT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A filter used to extract the k-neighborhood around one or more root node(s)
 * @author Danyel Fisher
 *
 */
public class KNeighborhoodFilter<V,E> implements Filter<V,E> {

	public static enum EdgeType { IN_OUT, IN, OUT };
	private Set<V> rootNodes;
	private int radiusK;
	private EdgeType edgeType;
	
	/**
	 * Constructs a new instance of the filter
	 * @param rootNodes the set of root nodes
	 * @param radiusK the neighborhood radius around the root set
	 * @param edgeType 0 for in/out edges, 1 for in-edges, 2  for out-edges
	 */
	public KNeighborhoodFilter(Set<V> rootNodes, int radiusK, EdgeType edgeType) {
		this.rootNodes = rootNodes;
		this.radiusK = radiusK;
		this.edgeType = edgeType;
	}
	/**
	 * Constructs a new instance of the filter
	 * @param rootNode the root node
	 * @param radiusK the neighborhood radius around the root set
	 * @param edgeType 0 for in/out edges, 1 for in-edges, 2  for out-edges
	 */
	public KNeighborhoodFilter(V rootNode, int radiusK, EdgeType edgeType) {
		this.rootNodes = new HashSet<V>();
		this.rootNodes.add(rootNode);
		this.radiusK = radiusK;
		this.edgeType = edgeType;
	}
	
	public String getName() {
		return "KNeighborhood(" + radiusK + "," + edgeType + ")";
	}
	
	/**
	 * Constructs an unassembled graph containing the k-neighbhood around the root node(s)
	 */
	public Graph<V,E> filter(Graph<V,E> graph) {
		// generate a Set of Vertices we want
		// add all to the UG
		int currentDepth = 0;
		List<V> currentVertices = new ArrayList<V>();
		Set<V> visitedVertices = new HashSet<V>();
		Set<E> visitedEdges = new HashSet<E>();
		Set<V> acceptedVertices = new HashSet<V>();
		//Copy, mark, and add all the root nodes to the new subgraph
		for (V currentRoot : rootNodes) {

			visitedVertices.add(currentRoot);
			acceptedVertices.add(currentRoot);
			currentVertices.add(currentRoot);
		}
		ArrayList<V> newVertices = null;
		//Use BFS to locate the neighborhood around the root nodes within distance k
		while (currentDepth < radiusK) {
			newVertices = new ArrayList<V>();
			for (V currentVertex : currentVertices) {

				Collection<E> edges = null;
				switch (edgeType) {
					case IN_OUT :
						edges = graph.getIncidentEdges(currentVertex);
						break;
					case IN :
						edges = graph.getInEdges(currentVertex);
						break;
					case OUT :
						edges = graph.getOutEdges(currentVertex);
						break;
				}
				for (E currentEdge : edges) {

					V currentNeighbor =
						graph.getOpposite(currentVertex, currentEdge);
					if (!visitedEdges.contains(currentEdge)) {
						visitedEdges.add(currentEdge);
						if (!visitedVertices.contains(currentNeighbor)) {
							visitedVertices.add(currentNeighbor);
							acceptedVertices.add(currentNeighbor);
							newVertices.add(currentNeighbor);
						}
					}
				}
			}
			currentVertices = newVertices;
			currentDepth++;
		}
		Graph<V,E> ug = null;
		try {
			ug = (Graph<V,E>)graph.getClass().newInstance();
			for(E edge : graph.getEdges()) {
				Pair<V> endpoints = graph.getEndpoints(edge);
				if(acceptedVertices.containsAll(endpoints)) {
					ug.addEdge(edge, endpoints.getFirst(), endpoints.getSecond());
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return ug;
	}
}
