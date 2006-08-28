/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package edu.uci.ics.jung.visualization.transform.shape;

import java.awt.Dimension;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.transform.AbstractLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.LensTransformer;

/**
 * Uses a LensTransformer to use in the view
 * transform. This one will distort Vertex shapes.
 * 
 * @author Tom Nelson - RABA Technologies
 *
 *
 */
public class ViewLensSupport<V,E> extends AbstractLensSupport<V,E>
    implements LensSupport {
    
    protected RenderContext<V,E> renderContext;
    GraphicsDecorator lensGraphicsDecorator;
    GraphicsDecorator savedGraphicsDecorator;
    
    public ViewLensSupport(VisualizationViewer<V,E> vv) {
        this(vv, new HyperbolicShapeTransformer(vv),
                new ModalLensGraphMouse());
    }
    public ViewLensSupport(VisualizationViewer<V,E> vv, LensTransformer lensTransformer,
            ModalGraphMouse lensGraphMouse) {
        super(vv, lensGraphMouse);
        this.renderContext = vv.getRenderContext();
        this.savedGraphicsDecorator = renderContext.getGraphicsContext();
        this.lensTransformer = lensTransformer;
        Dimension d = vv.getSize();
        if(d.width == 0 || d.height == 0) {
            d = vv.getPreferredSize();
        }
        lensTransformer.setViewRadius(d.width/5);
        this.lensGraphicsDecorator = new TransformingGraphics(lensTransformer);

    }
    public void activate() {
        if(lens == null) {
            lens = new Lens(lensTransformer);
        }
        if(lensControls == null) {
            lensControls = new LensControls(lensTransformer);
        }
        vv.setViewTransformer(lensTransformer);
        this.renderContext.setGraphicsContext(lensGraphicsDecorator);
        vv.addPreRenderPaintable(lens);
        vv.addPostRenderPaintable(lensControls);
        vv.setGraphMouse(lensGraphMouse);
        vv.setToolTipText(instructions);
        vv.repaint();
    }

    public void deactivate() {
        vv.setViewTransformer(savedViewTransformer);
        vv.removePreRenderPaintable(lens);
        vv.removePostRenderPaintable(lensControls);
        this.renderContext.setGraphicsContext(savedGraphicsDecorator);
        vv.setRenderContext(renderContext);
        vv.setToolTipText(defaultToolTipText);
        vv.setGraphMouse(graphMouse);
        vv.repaint();
    }
}