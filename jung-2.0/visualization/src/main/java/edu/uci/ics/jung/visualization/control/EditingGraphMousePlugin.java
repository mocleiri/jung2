/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package edu.uci.ics.jung.visualization.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedEdge;
import edu.uci.ics.jung.graph.DirectedSparseEdge;
import edu.uci.ics.jung.graph.UndirectedSparseEdge;
import edu.uci.ics.jung.visualization.ArrowFactory;
import edu.uci.ics.jung.visualization.PickSupport;
import edu.uci.ics.jung.visualization.SettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationViewer.Paintable;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.util.GraphElementFactory;

/**
 * A plugin that can create vertices, undirected edges, and directed edges
 * using mouse gestures.
 * 
 * @author Tom Nelson - RABA Technologies
 *
 */
public class EditingGraphMousePlugin<V, E extends Edge<V>> extends AbstractGraphMousePlugin implements
    MouseListener, MouseMotionListener {
    
    SettableVertexLocationFunction<V> vertexLocations;
    V startVertex;
    Point2D down;
    GraphElementFactory<V,E> factory;
    
    CubicCurve2D rawEdge = new CubicCurve2D.Float();
    Shape edgeShape;
    Shape rawArrowShape;
    Shape arrowShape;
    Paintable edgePaintable;
    Paintable arrowPaintable;
    boolean edgeIsDirected;
    
    public void setFactory(GraphElementFactory<V,E> factory) {
        this.factory = factory;
    }

    public EditingGraphMousePlugin() {
        this(MouseEvent.BUTTON1_MASK);
    }

    /**
     * create instance and prepare shapes for visual effects
     * @param modifiers
     */
    public EditingGraphMousePlugin(int modifiers) {
        super(modifiers);
        rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50,
                1.0f, 0.0f);
        rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
        edgePaintable = new EdgePaintable();
        arrowPaintable = new ArrowPaintable();
    }
    
    /**
     * sets the vertex locations. Needed to place new vertices
     * @param vertexLocations
     */
    public void setVertexLocations(SettableVertexLocationFunction<V> vertexLocations) {
        this.vertexLocations = vertexLocations;
    }
    
    /**
     * overrided to be more flexible, and pass events with
     * key combinations. The default responds to both ButtonOne
     * and ButtonOne+Shift
     */
    public boolean checkModifiers(MouseEvent e) {
        return (e.getModifiers() & modifiers) != 0;
    }

    /**
     * If the mouse is pressed in an empty area, create a new vertex there.
     * If the mouse is pressed on an existing vertex, prepare to create
     * an edge from that vertex to another
     */
    public void mousePressed(MouseEvent e) {
        if(checkModifiers(e)) {
            final VisualizationViewer<V,E> vv =
                (VisualizationViewer<V,E>)e.getSource();
            final Point2D p = vv.inverseViewTransform(e.getPoint());
            PickSupport<V,E> pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                final V vertex = pickSupport.getVertex(p.getX(), p.getY());
                if(vertex != null) { // get ready to make an edge
                    startVertex = vertex;
                    down = e.getPoint();
                    transformEdgeShape(down, down);
                    vv.addPostRenderPaintable(edgePaintable);
                    if((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) {
                        edgeIsDirected = true;
                        transformArrowShape(down, e.getPoint());
                        vv.addPostRenderPaintable(arrowPaintable);
                    } 
                } else { // make a new vertex
                    Graph<V,E> graph = vv.getGraphLayout().getGraph();
                    V newVertex = factory.createVertex();
                    vertexLocations.setLocation((V)newVertex, vv.inverseTransform(e.getPoint()));
                    Layout<V,E> layout = vv.getGraphLayout();
                    for(V v : graph.getVertices()) {
//                    for(Iterator iterator=graph.getVertices().iterator(); iterator.hasNext(); ) {
                        layout.lockVertex(v);
                    }
                    graph.addVertex(newVertex);
                    vv.getModel().restart();
                    for(V v : graph.getVertices()) {
//                    for(Iterator iterator=graph.getVertices().iterator(); iterator.hasNext(); ) {
                        layout.unlockVertex(v);
                    }
                    vv.repaint();
                }
            }
        }
    }
    
    /**
     * If startVertex is non-null, and the mouse is released over an
     * existing vertex, create an undirected edge from startVertex to
     * the vertex under the mouse pointer. If shift was also pressed,
     * create a directed edge instead.
     */
    public void mouseReleased(MouseEvent e) {
        if(checkModifiers(e)) {
            final VisualizationViewer<V,E> vv =
                (VisualizationViewer)e.getSource();
            final Point2D p = vv.inverseViewTransform(e.getPoint());
            PickSupport<V,E> pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                final V vertex = pickSupport.getVertex(p.getX(), p.getY());
                if(vertex != null && startVertex != null) {
                    Graph<V,E> graph = vv.getGraphLayout().getGraph();
                    if(edgeIsDirected) {
                        graph.addEdge(factory.createDirectedEdge(startVertex, vertex));
//                        graph.addEdge(new DirectedSparseEdge(startVertex, vertex));
                    } else {
                        graph.addEdge(factory.createUndirectedEdge(startVertex, vertex));
//                      
//                        graph.addEdge(factory.<String,UndirectedSparseEdge<String>>createEdge(UndirectedEdge.class, startVertex, vertex));
//                        graph.addEdge(new UndirectedSparseEdge(startVertex, vertex));
                    }
                    vv.repaint();
                }
            }
            startVertex = null;
            down = null;
            edgeIsDirected = false;
            vv.removePostRenderPaintable(edgePaintable);
            vv.removePostRenderPaintable(arrowPaintable);
        }
    }

    /**
     * If startVertex is non-null, stretch an edge shape between
     * startVertex and the mouse pointer to simulate edge creation
     */
    public void mouseDragged(MouseEvent e) {
        if(checkModifiers(e)) {
            if(startVertex != null) {
                transformEdgeShape(down, e.getPoint());
                if(edgeIsDirected) {
                    transformArrowShape(down, e.getPoint());
                }
            }
        }
    }
    
    /**
     * code lifted from PluggableRenderer to move an edge shape into an
     * arbitrary position
     */
    private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
        
        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        xform.scale(dist / rawEdge.getBounds().getWidth(), 1.0);
        edgeShape = xform.createTransformedShape(rawEdge);
    }
    
    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);
        
        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        arrowShape = xform.createTransformedShape(rawArrowShape);
    }
    
    /**
     * Used for the edge creation visual effect during mouse drag
     */
    class EdgePaintable implements Paintable {
        
        public void paint(Graphics g) {
            if(edgeShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D)g).draw(edgeShape);
                g.setColor(oldColor);
            }
        }
        
        public boolean useTransform() {
            return false;
        }
    }
    
    /**
     * Used for the directed edge creation visual effect during mouse drag
     */
    class ArrowPaintable implements Paintable {
        
        public void paint(Graphics g) {
            if(arrowShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D)g).fill(arrowShape);
                g.setColor(oldColor);
            }
        }
        
        public boolean useTransform() {
            return false;
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    
//    static interface GraphElementFactory<V,E extends Edge<V>> {
//        V createVertex();
//        E createEdge(Class<E> c, V v, V u);
//    }
}
