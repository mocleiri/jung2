/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package edu.uci.ics.jung.visualization.jai;

import javax.media.jai.PerspectiveTransform;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.transform.shape.TransformingGraphics;

/**
 * Creates a PerspectiveShapeTransformer to use in the view
 * transform. This one will distort Vertex shapes.
 * 
 * @author Tom Nelson
 *
 *
 */
public class PerspectiveViewTransformSupport<V,E> extends AbstractPerspectiveTransformSupport<V,E>
    implements PerspectiveTransformSupport {
    
	protected RenderContext renderContext;
    protected Renderer<V,E> renderer;
    protected GraphicsDecorator lensGraphicsDecorator;
    protected GraphicsDecorator savedGraphicsDecorator;
    
    public PerspectiveViewTransformSupport(VisualizationViewer<V,E> vv) {
        super(vv);
        this.renderer = vv.getRenderer();
        this.renderContext = vv.getRenderContext();
        perspectiveTransformer = 
            new PerspectiveShapeTransformer(new PerspectiveTransform(), vv.getViewTransformer());
        savedGraphicsDecorator = renderContext.getGraphicsContext();
        this.lensGraphicsDecorator = new TransformingGraphics(perspectiveTransformer);
    }
    
    public void activate() {
        lens = new Lens(perspectiveTransformer, vv.getSize());
        vv.setViewTransformer(perspectiveTransformer);
        vv.getRenderContext().setGraphicsContext(lensGraphicsDecorator);
        vv.addPreRenderPaintable(lens);
        vv.setToolTipText(instructions);
        vv.repaint();
    }

    public void deactivate() {
        vv.setViewTransformer(savedViewTransformer);
        vv.removePreRenderPaintable(lens);
        vv.getRenderContext().setGraphicsContext(savedGraphicsDecorator);
        vv.setToolTipText(defaultToolTipText);
        vv.repaint();
    }

}