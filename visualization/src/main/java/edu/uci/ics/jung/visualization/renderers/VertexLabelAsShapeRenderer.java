package edu.uci.ics.jung.visualization.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.decorators.VertexShapeFunction;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;


public class VertexLabelAsShapeRenderer<V,E> implements Renderer.Vertex<V,E>, VertexShapeFunction<V> {

	protected Map<V,Shape> shapes = new HashMap<V,Shape>();
	
	public void paintVertex(RenderContext<V,E> rc, V v, int x, int y) {
		labelVertex(rc, v, rc.getVertexStringer().getLabel(v), x, y);
	}

	public Component prepareRenderer(RenderContext<V,E> rc, VertexLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, V vertex) {
		return rc.getVertexLabelRenderer().<V>getGraphLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getVertexFontFunction().getFont(vertex), isSelected, vertex);
	}

	/**
	 * Labels the specified vertex with the specified label.  
	 * Uses the font specified by this instance's 
	 * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
	 * font for the graphics context is used.)  If vertex label centering
	 * is active, the label is centered on the position of the vertex; otherwise
     * the label is offset slightly.
     */
    protected void labelVertex(RenderContext<V,E> rc, V v, String label, int x, int y) {
        GraphicsDecorator g = rc.getGraphicsContext();
        Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), label,
        		rc.getPickedVertexState().isPicked(v), v);
        ((JComponent)component).setBorder(BorderFactory.createLineBorder(Color.black,1));
        component.setBackground(Color.pink);
        Dimension d = component.getPreferredSize();
        
        int h_offset = -d.width / 2;
        int v_offset = -d.height / 2;
        
        rc.getRendererPane().paintComponent(g.getDelegate(), component, rc.getScreenDevice(), x+h_offset, y+v_offset,
                d.width, d.height, true);

        Dimension size = component.getPreferredSize();
        Rectangle bounds = new Rectangle(-size.width/2, -size.height/2, size.width, size.height);
        shapes.put(v, bounds);
    }

	public Shape getShape(V v) {
		Shape shape = shapes.get(v);
		if(shape == null) return new Rectangle(-20,-20,40,40);
		else return shape;
	}
}