/*
 * Created on Jul 10, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring;

import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to vertices based on their distances to each other vertex 
 * in the graph.
 * 
 * NOTE: This class optionally normalizes its results based on the value of its
 * 'averaging' constructor parameter.  If it is <code>true</code>, 
 * then the value returned for vertex v is the
 * _average_ distance from v to all other vertices; 
 * this is sometimes called <i>closeness centrality</i>.
 * If it is <code>false</code>, then the value returned is the _total_ distance from
 * v to all other vertices; this is sometimes referred to as <i>barycenter centrality</i>.
 * 
 * @see BarycenterScorer
 * @see ClosenessCentrality
 */
public class DistanceCentralityScorer<V,E> implements VertexScorer<V, Double>
{
    /**
     * The graph on which the vertex scores are to be calculated.
     */
    protected Hypergraph<V, E> graph;
    
    /**
     * The metric to use for specifying the distance between pairs of vertices.
     */
    protected Distance<V> distance;
    
    /**
     * The storage for the output results.
     */
    protected Map<V, Double> output;
    
    /**
     * Specifies whether the values returned are the sum of the v-distances
     * or the mean v-distance.
     */
    protected boolean averaging;
    
    /**
     * Specifies whether, for a vertex <code>v</code> with missing (null) distances, 
     * <code>v</code>'s score should ignore the missing values or be set to 'null'.
     * Defaults to 'true'.
     */
    protected boolean ignore_missing;

    /**
     * Specifies whether the values returned should ignore self-distances 
     * (distances from <code>v</code> to itself).
     * Defaults to 'true'.
     */
    protected boolean ignore_self_distances;
    
    /**
     * Creates an instance with the specified graph, distance metric, and 
     * averaging behavior.
     * 
     * @param graph     The graph on which the vertex scores are to be calculated.
     * @param distance  The metric to use for specifying the distance between 
     * pairs of vertices.
     * @param averaging Specifies whether the values returned is the sum of all 
     * v-distances or the mean v-distance.
     * @param ignore_missing	Specifies whether scores for missing distances 
     * are to ignore missing distances or be set to null.
     * @param ignore_self_distances	Specifies whether distances from a vertex
     * to itself should be included in its score.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, Distance<V> distance, 
    		boolean averaging, boolean ignore_missing, 
    		boolean ignore_self_distances)
    {
        this.graph = graph;
        this.distance = distance;
        this.averaging = averaging;
        this.ignore_missing = ignore_missing;
        this.ignore_self_distances = ignore_self_distances;
    }

    /**
     * Equivalent to <code>this(graph, distance, averaging, true, true)</code>.
     * 
     * @param graph     The graph on which the vertex scores are to be calculated.
     * @param distance  The metric to use for specifying the distance between 
     * pairs of vertices.
     * @param averaging Specifies whether the values returned is the sum of all 
     * v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, Distance<V> distance, 
    		boolean averaging)
    {
    	this(graph, distance, averaging, true, true);
    }
    
    /**
     * Creates an instance with the specified graph and averaging behavior
     * whose vertex distances are calculated based on the specified edge
     * weights.  
     * 
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param edge_weights  The edge weights to use for specifying the distance 
     * between pairs of vertices.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     * @param ignore_missing	Specifies whether scores for missing distances 
     * are to ignore missing distances or be set to null.
     * @param ignore_self_distances	Specifies whether distances from a vertex
     * to itself should be included in its score.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, 
            Transformer<E, ? extends Number> edge_weights, boolean averaging,
            boolean ignore_missing, boolean ignore_self_distances)
    {
        this(graph, new DijkstraDistance<V,E>(graph, edge_weights), averaging,
        	ignore_missing, ignore_self_distances);
    }
    
    /**
     * Equivalent to <code>this(graph, edge_weights, averaging, true, true)</code>.
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param edge_weights  The edge weights to use for specifying the distance 
     * between pairs of vertices.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, 
            Transformer<E, ? extends Number> edge_weights, boolean averaging)
    {
        this(graph, new DijkstraDistance<V,E>(graph, edge_weights), averaging,
        	true, true);
    }
    
    /**
     * Creates an instance with the specified graph and averaging behavior
     * whose vertex distances are calculated on the unweighted graph.  
     * 
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     * @param ignore_missing	Specifies whether scores for missing distances 
     * are to ignore missing distances or be set to null.
     * @param ignore_self_distances	Specifies whether distances from a vertex
     * to itself should be included in its score.
     */
    public DistanceCentralityScorer(Graph<V,E> graph, boolean averaging,
            boolean ignore_missing, boolean ignore_self_distances)
    {
        this(graph, new UnweightedShortestPath<V,E>(graph), averaging, 
        	ignore_missing, ignore_self_distances);
    }

    /**
     * Equivalent to <code>this(graph, averaging, true, true)</code>.
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Graph<V,E> graph, boolean averaging)
    {
        this(graph, new UnweightedShortestPath<V,E>(graph), averaging, true,
        	true);
    }

    /**
     * Calculates the score for all vertices.
     */
    public void evaluate()
    {
        for (V v : graph.getVertices())
        	calculate(v);
    }

	/**
	 * Calculates the score for the specified vertex.
	 */
	public void calculate(V v) 
	{
		// if we don't ignore missing distances and there aren't enough
		// distances, output null
		if (!ignore_missing)
		{
			int n = graph.getVertexCount();
			n -= ignore_self_distances ? 1 : 0;
			if (distance.getDistanceMap(v).size() < n) 
			{
				output.put(v, null);
				return;
			}
		}		

		
		Double sum = 0.0;
		for (V w : graph.getVertices())
		{
			if (w.equals(v) && ignore_self_distances)
				continue;
			Number w_distance = distance.getDistance(v, w);
			if (w_distance == null)
				if (ignore_missing)
					continue;
				else
				{
					output.put(v, null);
					return;
				}
			else
				sum += w_distance.doubleValue();
		}
		if (averaging)
		    output.put(v, sum / graph.getVertexCount());
		else
		    output.put(v, sum);
	}
    
	/**
	 * Returns the transformer that assigns a score to each vertex.
	 * @return the transformer that assigns a score to each vertex
	 */
    public Transformer<V, Double> getVertexScores()
    {
        return MapTransformer.getInstance(output);
    }
}
