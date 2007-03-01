package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using a variation of Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class PrimMinimumSpanningTree<V,E> implements Transformer<Graph<V,E>,Tree<V,E>> {
	
	protected Factory<Tree<V,E>> treeFactory;
	protected Transformer<E,Double> weights = 
		(Transformer<E,Double>)new ConstantTransformer(1.0);
	
	public PrimMinimumSpanningTree(Factory<Tree<V,E>> factory) {
		this(factory, null);
	}

	/**
	 * @param graph
	 * @param factory
	 * @param root
	 * @param weights
	 */
	public PrimMinimumSpanningTree(Factory<Tree<V,E>> factory, 
			Transformer<E, Double> weights) {
		this.treeFactory = factory;
		if(weights != null) {
			this.weights = weights;
		}
	}
	
	/**
	 * @param graph the Graph to find MST in
	 */
    public Tree<V,E> transform(Graph<V,E> graph) {
		Set<E> unfinishedEdges = new HashSet<E>(graph.getEdges());
		Tree<V,E> tree = treeFactory.create();
		V root = findRoot(graph);
		if(graph.getVertices().contains(root)) {
			tree.addVertex(root);
		} else if(graph.getVertexCount() > 0) {
			// pick an arbitrary vertex to make root
			tree.addVertex(graph.getVertices().iterator().next());
		}
		updateTree(tree, graph, unfinishedEdges);
		
		return tree;
	}
    
    protected V findRoot(Graph<V,E> graph) {
    	for(V v : graph.getVertices()) {
    		if(graph.getInEdges(v).size() == 0) {
    			return v;
    		}
    	}
    	return null;
    }
	
	protected void updateTree(Tree<V,E> tree, Graph<V,E> graph, Collection<E> unfinishedEdges) {
		Collection<V> tv = tree.getVertices();
		double minCost = Double.MAX_VALUE;
		E nextEdge = null;
		V nextVertex = null;
		V currentVertex = null;
		for(E e : unfinishedEdges) {
			
			if(tree.getEdges().contains(e)) continue;
			// find the lowest cost edge, get its opposite endpoint,
			// and then update forest from its Successors
			Pair<V> endpoints = graph.getEndpoints(e);
			V first = endpoints.getFirst();
			V second = endpoints.getSecond();
			if((tv.contains(first) == true && tv.contains(second) == false)) {
				if(weights.transform(e) < minCost) {
					minCost = weights.transform(e);
					nextEdge = e;
					currentVertex = first;
					nextVertex = second;
				}
			} else if((tv.contains(second) == true && tv.contains(first) == false)) {
				if(weights.transform(e) < minCost) {
					minCost = weights.transform(e);
					nextEdge = e;
					currentVertex = second;
					nextVertex = first;
				}
			}
		}
		
		if(nextVertex != null && nextEdge != null) {
			unfinishedEdges.remove(nextEdge);
			tree.addEdge(nextEdge, currentVertex, nextVertex);
			updateTree(tree, graph, unfinishedEdges);
		}
	}
}
