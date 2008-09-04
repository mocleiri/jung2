/*
 * Created on Jun 22, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.TestGraphs;

public class TestGraphMLWriter extends TestCase
{
    public void testBasicWrite() throws IOException, ParserConfigurationException, SAXException
    {
        Graph<String, Number> g = TestGraphs.createTestGraph(true);
        GraphMLWriter<String, Number> gmlw = new GraphMLWriter<String, Number>();
        Transformer<Number, String> edge_weight = new Transformer<Number, String>() 
		{ 
			public String transform(Number n) 
			{ 
				return String.valueOf(n.intValue()); 
			} 
		};

		Transformer<String, String> vertex_name = TransformerUtils.nopTransformer();
		
        gmlw.addEdgeData("weight", "integer value for the edge", 
        		Integer.toString(-1), edge_weight);
        gmlw.addVertexData("name", "identifier for the vertex", null, vertex_name);
        gmlw.setEdgeIDs(edge_weight);
        gmlw.setVertexIDs(vertex_name);
        gmlw.save(g, new FileWriter("src/test/resources/testbasicwrite.graphml"));
        
        // TODO: now read it back in and compare the graph connectivity 
        // and other metadata with what's in TestGraphs.pairs[], etc.
//        Factory<String> vertex_factory = null;
//        Factory<Object> edge_factory = FactoryUtils.instantiateFactory(Object.class);
//        GraphMLReader<Graph<String, Object>, String, Object> gmlr = 
//        	new GraphMLReader<Graph<String, Object>, String, Object>(
//        			vertex_factory, edge_factory);
        GraphMLReader<Graph<String, Object>, String, Object> gmlr = 
            new GraphMLReader<Graph<String, Object>, String, Object>();
        Graph<String, Object> g2 = new DirectedSparseGraph<String, Object>();
        gmlr.load("src/test/resources/testbasicwrite.graphml", g2);
        Map<String, GraphMLMetadata<Object>> edge_metadata = 
        	gmlr.getEdgeMetadata();
        Transformer<Object, String> edge_weight2 = 
        	(Transformer<Object, String>)edge_metadata.get("weight").transformer;
        validateTopology(g, g2, edge_weight, edge_weight2);
        
        // TODO: delete graph file when done
        File f = new File("src/test/resources/testbasicwrite.graphml");
        f.delete();
    }
    
    public void testMixedGraph() throws IOException, ParserConfigurationException, SAXException
    {
        Graph<String, Number> g = TestGraphs.getSmallGraph();
        GraphMLWriter<String, Number> gmlw = new GraphMLWriter<String, Number>();
        Transformer<Number, String> edge_weight = new Transformer<Number, String>() 
        { 
            public String transform(Number n) 
            { 
                return String.valueOf(n.doubleValue()); 
            } 
        };

        gmlw.addEdgeData("weight", "integer value for the edge", 
                Integer.toString(-1), edge_weight);
        gmlw.setEdgeIDs(edge_weight);
        gmlw.save(g, new FileWriter("src/test/resources/testmixedgraph.graphml"));

        // TODO: now read it back in and compare the graph connectivity 
        // and other metadata with what's in TestGraphs, etc.
        GraphMLReader<Graph<String,Object>,String,Object> gmlr = 
            new GraphMLReader<Graph<String,Object>,String,Object>();
        Graph<String,Object> g2 = new SparseMultigraph<String,Object>();
        gmlr.load("src/test/resources/testmixedgraph.graphml", g2);
        Map<String, GraphMLMetadata<Object>> edge_metadata = 
            gmlr.getEdgeMetadata();
        Transformer<Object, String> edge_weight2 = 
            (Transformer<Object, String>)edge_metadata.get("weight").transformer;
        validateTopology(g, g2, edge_weight, edge_weight2);
        
        // TODO: delete graph file when done
        File f = new File("src/test/resources/testmixedgraph.graphml");
        f.delete();
    }

    public <T extends Comparable<T>> void validateTopology(Graph<T,Number> g, Graph<T,Object> g2,
            Transformer<Number,String> edge_weight, Transformer<Object,String> edge_weight2)
    {
        Assert.assertEquals(g2.getEdgeCount(), g.getEdgeCount());
        List<T> g_vertices = new ArrayList<T>(g.getVertices());
        List<T> g2_vertices = new ArrayList<T>(g2.getVertices());
        Collections.sort(g_vertices); 
        Collections.sort(g2_vertices);
        Assert.assertEquals(g_vertices, g2_vertices);

        List g_edges = new ArrayList();
        for (Number n : g.getEdges())
            g_edges.add(String.valueOf(n));
        List g2_edges = new ArrayList(g2.getEdges());
        Collections.sort(g_edges); 
        Collections.sort(g2_edges);
        Assert.assertEquals(g_edges, g2_edges);
        
        for (T v : g2.getVertices())
        {
            for (T w : g2.getVertices())
            {
                Assert.assertEquals(g.isNeighbor(v, w), 
                        g2.isNeighbor(v, w));
                List e = new ArrayList();
                for (Number n : g.findEdgeSet(v, w))
                    e.add(String.valueOf(n));
                List e2 = new ArrayList(g2.findEdgeSet(v, w));
                Assert.assertEquals(e.size(), e2.size());
                Collections.sort(e);
                Collections.sort(e2);
                Assert.assertEquals(e, e2);
            }
        }
        
        for (Object o : g2.getEdges())
        {
            String weight = edge_weight.transform(new Double((String)o));
            String weight2 = edge_weight2.transform(o);
            Assert.assertEquals(weight2, weight);
        }        
//                Number n = g.findEdge(v, w);
//                Object o = g2.findEdge(v, w);
//                if (n != null)
//                {
//                    String weight = edge_weight.transform(n);
//                    String weight2 = edge_weight2.transform(o);
//                    Assert.assertEquals(weight2, weight);
//                }
//            }
//        }
        
    }
    
}
