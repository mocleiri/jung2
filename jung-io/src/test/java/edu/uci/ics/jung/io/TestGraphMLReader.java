/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.util.SettableTransformer;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * @author Scott White
 * @author Tom Nelson - converted to jung2
 */
public class TestGraphMLReader extends TestCase {
	
	Factory<Graph<Number,Number>> graphFactory;
	Factory<Number> vertexFactory;
	Factory<Number> edgeFactory;
    GraphMLReader<Graph<Number,Number>,Number,Number> gmlreader;

    public static Test suite() {
        return new TestSuite(TestGraphMLReader.class);
    }

    @Override
    protected void setUp() throws ParserConfigurationException, SAXException {
    	graphFactory = new Factory<Graph<Number,Number>>() {
    		public Graph<Number,Number> create() {
    			return new DirectedSparseMultigraph<Number,Number>();
    		}
    	};
    	vertexFactory = new Factory<Number>() {
    		int n = 0;
    		public Number create() { return n++; }
    	};
    	edgeFactory = new Factory<Number>() {
    		int n = 0;
    		public Number create() { return n++; }
    	};
        gmlreader = new GraphMLReader<Graph<Number, Number>, Number,Number>(vertexFactory,edgeFactory);
    }

    public void testLoad() throws IOException {
        String testFilename = "toy_graph.ml";

        Graph<Number,Number> graph = loadGraph(testFilename);

        Assert.assertEquals(graph.getVertexCount(),3);
        Assert.assertEquals(graph.getEdgeCount(),3);

        BidiMap<Number, String> vertex_ids = gmlreader.getVertexIDs();

        Number joe = vertex_ids.getKey("1");
        Number bob = vertex_ids.getKey("2");
        Number sue = vertex_ids.getKey("3");

        Assert.assertEquals(gmlreader.getVertexData().get("name").transform(joe),"Joe");
        Assert.assertEquals(gmlreader.getVertexData().get("name").transform(bob),"Bob");
        Assert.assertEquals(gmlreader.getVertexData().get("name").transform(sue),"Sue");

        Assert.assertTrue(graph.isPredecessor(joe, bob));
        Assert.assertTrue(graph.isPredecessor(bob, joe));
        Assert.assertTrue(graph.isPredecessor(sue, joe));
        Assert.assertFalse(graph.isPredecessor(joe, sue));
        Assert.assertFalse(graph.isPredecessor(sue, bob));
        Assert.assertFalse(graph.isPredecessor(bob, sue));


        File testFile = new File(testFilename);
        testFile.delete();
    }

    public void testAttributes() throws IOException 
    {
        Graph<Number, Number> graph = new UndirectedSparseGraph<Number, Number>();
        gmlreader.load("src/test/resources/attributes.graphml", graph);

        Assert.assertEquals(graph.getVertexCount(),6);
        Assert.assertEquals(graph.getEdgeCount(),7);
        
        // test vertex IDs
        BidiMap<Number, String> vertex_ids = gmlreader.getVertexIDs();
        for (Map.Entry<Number, String> entry : vertex_ids.entrySet())
        {
            Assert.assertEquals(entry.getValue().charAt(0), 'n');
            Assert.assertEquals(Integer.parseInt(entry.getValue().substring(1)), entry.getKey().intValue());
        }

        // test edge IDs
        BidiMap<Number, String> edge_ids = gmlreader.getEdgeIDs();
        for (Map.Entry<Number, String> entry : edge_ids.entrySet())
        {
            Assert.assertEquals(entry.getValue().charAt(0), 'e');
            Assert.assertEquals(Integer.parseInt(entry.getValue().substring(1)), entry.getKey().intValue());
        }
        
        // test data
        Map<String, SettableTransformer<Number, String>> vertex_data = gmlreader.getVertexData();
        Map<String, SettableTransformer<Number, String>> edge_data = gmlreader.getEdgeData();
        
        // test vertex colors
        Transformer<Number, String> vertex_color = vertex_data.get("d0");
        Assert.assertEquals(vertex_color.transform(0), "green");
        Assert.assertEquals(vertex_color.transform(1), "yellow");
        Assert.assertEquals(vertex_color.transform(2), "blue");
        Assert.assertEquals(vertex_color.transform(3), "red");
        Assert.assertEquals(vertex_color.transform(4), "yellow");
        Assert.assertEquals(vertex_color.transform(5), "turquoise");
        
        // test edge weights
        Transformer<Number, String> edge_weight = edge_data.get("d1");
        Assert.assertEquals(edge_weight.transform(0), "1.0");
        Assert.assertEquals(edge_weight.transform(1), "1.0");
        Assert.assertEquals(edge_weight.transform(2), "2.0");
        Assert.assertEquals(edge_weight.transform(3), null);
        Assert.assertEquals(edge_weight.transform(4), null);
        Assert.assertEquals(edge_weight.transform(5), null);
        Assert.assertEquals(edge_weight.transform(6), "1.1");
        
    }

    public void testLoadHypergraph() throws IOException, ParserConfigurationException, SAXException
    {
        Hypergraph<Number, Number> graph = new SetHypergraph<Number, Number>();
        GraphMLReader<Hypergraph<Number, Number>, Number, Number> hyperreader = 
            new GraphMLReader<Hypergraph<Number, Number>, Number, Number>(vertexFactory, edgeFactory);
        hyperreader.load("src/test/resources/hyper.graphml", graph);

        Assert.assertEquals(graph.getVertexCount(),7);
        Assert.assertEquals(graph.getEdgeCount(),4);
        
        // n0
        Set<Number> incident = new HashSet<Number>();
        incident.add(0);
        incident.add(3);
        Assert.assertEquals(graph.getIncidentEdges(0), incident);

        // n1
        incident.clear();
        incident.add(0);
        incident.add(2);
        Assert.assertEquals(graph.getIncidentEdges(1), incident);

        // n2
        incident.clear();
        incident.add(0);
        Assert.assertEquals(graph.getIncidentEdges(2), incident);

        // n3
        incident.clear();
        incident.add(1);
        incident.add(2);
        Assert.assertEquals(graph.getIncidentEdges(3), incident);

        // n4
        incident.clear();
        incident.add(1);
        incident.add(3);
        Assert.assertEquals(graph.getIncidentEdges(4), incident);
        
        // n5
        incident.clear();
        incident.add(1);
        Assert.assertEquals(graph.getIncidentEdges(5), incident);

        // n6
        incident.clear();
        incident.add(1);
        Assert.assertEquals(graph.getIncidentEdges(6), incident);
    }
    
    private Graph<Number,Number> loadGraph(String testFilename) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFilename));
            writer.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
             writer.write("<?meta name=\"GENERATOR\" content=\"XML::Smart 1.3.1\" ?>\n");
            writer.write("<graph edgedefault=\"directed\">\n");
            writer.write("<node id=\"1\" name=\"Joe\"/>\n");
            writer.write("<node id=\"2\" name=\"Bob\"/>\n");
            writer.write("<node id=\"3\" name=\"Sue\"/>\n");
            writer.write("<edge source=\"1\" target=\"2\"/>\n");
            writer.write("<edge source=\"2\" target=\"1\"/>\n");
            writer.write("<edge source=\"1\" target=\"3\"/>\n");
             writer.write("</graph>\n");
            writer.close();

        } catch (IOException ioe) {

        }

        Graph<Number,Number> graph = graphFactory.create();
        gmlreader.load(testFilename, graph);
        return graph;
    }

//    public void testSave() {
//        String testFilename = "toy_graph.ml";
//        Graph<Number,Number> oldGraph = loadGraph(testFilename);
////        GraphMLFile<Number,Number> graphmlFile = new GraphMLFile();
//        String newFilename = testFilename + "_save";
//        gmlreader.save(oldGraph,newFilename);
//		Graph<Number,Number> newGraph = gmlreader.load(newFilename);
//        Assert.assertEquals(oldGraph.getVertexCount(),newGraph.getVertexCount());
//        Assert.assertEquals(oldGraph.getEdgeCount(),newGraph.getEdgeCount());
//        File testFile = new File(testFilename);
//        testFile.delete();
//        File newFile = new File(newFilename);
//        newFile.delete();
//
//
//    }
}
