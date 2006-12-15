/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */
package edu.uci.ics.jung.visualization.renderers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Renders Vertex Labels, but can also supply Shapes for vertices.
 * This has the effect of making the vertex label the actual vertex
 * shape. The user will probably want to center the vertex label
 * on the vertex location.
 * 
 * @author Tom Nelson
 *
 * @param <V>
 * @param <E>
 */
public class VertexLabelAsShapeRenderer<V,E> 
	implements Renderer.VertexLabel<V,E>, Transformer<V,Shape> {

	protected Map<V,Shape> shapes = new HashMap<V,Shape>();
	
	public Component prepareRenderer(RenderContext<V,E> rc, VertexLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, V vertex) {
		return rc.getVertexLabelRenderer().<V>getVertexLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getVertexFontFunction().transform(vertex), isSelected, vertex);
	}

	/**
	 * Labels the specified vertex with the specified label.  
	 * Uses the font specified by this instance's 
	 * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
	 * font for the graphics context is used.)  If vertex label centering
	 * is active, the label is centered on the position of the vertex; otherwise
     * the label is offset slightly.
     */
    public void labelVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, String label, int x, int y) {
        if (rc.getVertexIncludePredicate().evaluateVertex(graph, v) == false) {
        	return;
        }
        GraphicsDecorator g = rc.getGraphicsContext();
        Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), label,
        		rc.getPickedVertexState().isPicked(v), v);
        Dimension d = component.getPreferredSize();
        
        int h_offset = -d.width / 2;
        int v_offset = -d.height / 2;
        
        rc.getRendererPane().paintComponent(g.getDelegate(), component, rc.getScreenDevice(), x+h_offset, y+v_offset,
                d.width, d.height, true);

        Dimension size = component.getPreferredSize();
        Rectangle bounds = new Rectangle(-size.width/2, -size.height/2, size.width, size.height);
        shapes.put(v, bounds);
    }

	public Shape transform(V v) {
		Shape shape = shapes.get(v);
		if(shape == null) return new Rectangle(-20,-20,40,40);
		else return shape;
	}

	public Renderer.VertexLabel.Position getPosition() {
		return Renderer.VertexLabel.Position.CNTR;
	}

	public void setPosition(Renderer.VertexLabel.Position position) {
		// TODO Auto-generated method stub
		
	}
}
