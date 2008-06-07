/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.shortestpath;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.graph.Graph;

/**
 * Statistics relating to vertex-vertex distances in a graph.
 * 
 * <p>Formerly known as <code>GraphStatistics</code> in JUNG 1.x.</p>
 * 
 * @author Scott White
 * @author Joshua O'Madadhain
 */
public class DistanceStatistics 
{
    /**
     * For each vertex <code>v</code> in <code>graph</code>, 
     * calculates the average shortest path length from <code>v</code> 
     * to all other vertices in <code>graph</code> using the metric 
     * specified by <code>d</code>, and returns the results in a
     * <code>Map</code> from vertices to <code>Double</code> values.
     * If there exists an ordered pair <code>&lt;u,v&gt;</code>
     * for which <code>d.getDistance(u,v)</code> returns <code>null</code>,
     * then the average distance value for <code>u</code> will be stored
     * as <code>Double.POSITIVE_INFINITY</code>).
     * 
     * <p>Does not include self-distances (path lengths from <code>v</code>
     * to <code>v</code>).
     * 
     * <p>To calculate the average distances, ignoring edge weights if any:
     * <pre>
     * Map distances = GraphStatistics.averageDistances(g, new UnweightedShortestPath(g));
     * </pre>
     * To calculate the average distances respecting edge weights:
     * <pre>
     * DijkstraShortestPath dsp = new DijkstraShortestPath(g, nev);
     * Map distances = GraphStatistics.averageDistances(g, dsp);
     * </pre>
     * where <code>nev</code> is an instance of <code>Transformer</code> that
     * is used to fetch the weight for each edge.
     * 
     * @see edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath
     * @see edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance
     */
    public static <V,E> Transformer<V,Double> averageDistances(Graph<V,E> graph, Distance<V> d)
    {
    	ClosenessCentrality<V,E> cc = new ClosenessCentrality<V,E>(graph, d);
    	cc.evaluate();
    	return cc.getVertexScores();
    }
    
    /**
     * For each vertex <code>v</code> in <code>g</code>, 
     * calculates the average shortest path length from <code>v</code> 
     * to all other vertices in <code>g</code>, ignoring edge weights.
     * @see #diameter(ArchetypeGraph, Distance)
     */
    public static <V,E> Transformer<V, Double> averageDistances(Graph<V,E> g)
    {
    	ClosenessCentrality<V,E> cc = new ClosenessCentrality<V,E>(g, 
    			new UnweightedShortestPath<V,E>(g));
    	cc.evaluate();
    	return cc.getVertexScores();
    }
    
    /**
     * Returns the diameter of <code>g</code> using the metric 
     * specified by <code>d</code>.  The diameter is defined to be
     * the maximum, over all pairs of vertices <code>u,v</code>,
     * of the length of the shortest path from <code>u</code> to 
     * <code>v</code>.  If the graph is disconnected (that is, not 
     * all pairs of vertices are reachable from one another), the
     * value returned will depend on <code>use_max</code>:  
     * if <code>use_max == true</code>, the value returned
     * will be the the maximum shortest path length over all pairs of <b>connected</b> 
     * vertices; otherwise it will be <code>Double.POSITIVE_INFINITY</code>.
     */
    public static <V, E> double diameter(Graph<V,E> g, Distance<V> d, boolean use_max)
    {
        double diameter = 0;
        Collection<V> vertices = g.getVertices();
        for(V v : vertices) {
            for(V w : vertices) {

                if (v.equals(w) == false) // don't include self-distances
                {
                    Number dist = d.getDistance(v, w);
                    if (dist == null)
                    {
                        if (!use_max)
                            return Double.POSITIVE_INFINITY;
                    }
                    else
                        diameter = Math.max(diameter, dist.doubleValue());
                }
            }
        }
        return diameter;
    }
    
    /**
     * Returns the diameter of <code>g</code> using the metric 
     * specified by <code>d</code>.  The diameter is defined to be
     * the maximum, over all pairs of vertices <code>u,v</code>,
     * of the length of the shortest path from <code>u</code> to 
     * <code>v</code>, or <code>Double.POSITIVE_INFINITY</code>
     * if any of these distances do not exist.
     * @see #diameter(Graph, Distance, boolean)
     */
    public static <V, E> double diameter(Graph<V,E> g, Distance<V> d)
    {
        return diameter(g, d, false);
    }
    
    /**
     * Returns the diameter of <code>g</code>, ignoring edge weights.
     * @see #diameter(Graph, Distance, boolean)
     */
    public static <V, E> double diameter(Graph<V,E> g)
    {
        return diameter(g, new UnweightedShortestPath<V,E>(g));
    }
    
}
