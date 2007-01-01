/*
 * Created on Jan 2, 2004
 */
package samples.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * @author danyelf
 */
public class ShortestPathDemo extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7526217664458188502L;

	/**
	 * Starting vertex
	 */
	private String mFrom;

	/**
	 * Ending vertex
	 */	
	private String mTo;
	private Graph<String,Number> mGraph;
	private Set<String> mPred;
	
	/**
	 * @param g
	 */
	public ShortestPathDemo() {
	
		this.mGraph = getGraph();
		setBackground(Color.WHITE);
		// show graph
        final Layout<String,Number> layout = new FRLayout<String,Number>(mGraph);
        final VisualizationViewer<String,Number> vv = new VisualizationViewer<String,Number>(layout);
        vv.setBackground(Color.WHITE);

        vv.getRenderContext().setVertexDrawPaintFunction(new MyVertexDrawPaintFunction<String>());
        vv.getRenderContext().setVertexFillPaintFunction(new MyVertexFillPaintFunction<String>());
        vv.getRenderContext().setEdgeDrawPaintFunction(new MyEdgePaintFunction());
        vv.getRenderContext().setEdgeStrokeFunction(new MyEdgeStrokeFunction());
        vv.getRenderContext().setVertexStringer(new ToStringLabeller<String>());
        vv.setGraphMouse(new DefaultModalGraphMouse());
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable(){
            
            public boolean useTransform() {
                return true;
            }
            public void paint(Graphics g) {
                if(mPred == null) return;
                
                // for all edges, paint edges that are in shortest path
                for (Number e : layout.getGraph().getEdges()) {
                    
                    if(isBlessed(e)) {
                        String v1 = mGraph.getEndpoints(e).getFirst();
                        String v2 = mGraph.getEndpoints(e).getSecond();
                        Point2D p1 = layout.transform(v1);
                        Point2D p2 = layout.transform(v2);
                        p1 = vv.layoutTransform(p1);
                        p2 = vv.layoutTransform(p2);
                        Renderer<String,Number> renderer = vv.getRenderer();
                        renderer.renderEdge(
                                vv.getRenderContext(),
                                mGraph,
                                e,
                                (int) p1.getX(),
                                (int) p1.getY(),
                                (int) p2.getX(),
                                (int) p2.getY());
                    }
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(vv, BorderLayout.CENTER);
        // set up controls
        add(setUpControls(), BorderLayout.SOUTH);
	}

    boolean isBlessed( Number e ) {
    	Pair<String> endpoints = mGraph.getEndpoints(e);
		String v1= endpoints.getFirst()	;
		String v2= endpoints.getSecond() ;
		return v1.equals(v2) == false && mPred.contains(v1) && mPred.contains(v2);
    }
    
	/**
	 * @author danyelf
	 */
	public class MyEdgePaintFunction implements Transformer<Number,Paint> {
	    
		/**
		 * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getDrawPaint(edu.uci.ics.jung.graph.Edge)
		 */
		public Paint transform(Number e) {
			if ( mPred == null || mPred.size() == 0) return Color.BLACK;
			if( isBlessed( e )) {
				return new Color(0.0f, 0.0f, 1.0f, 0.5f);//Color.BLUE;
			} else {
				return Color.LIGHT_GRAY;
			}
		}
	}
	
	public class MyEdgeStrokeFunction implements Transformer<Number,Stroke> {
        protected final Stroke THIN = new BasicStroke(1);
        protected final Stroke THICK = new BasicStroke(1);

        public Stroke transform(Number e) {
			if ( mPred == null || mPred.size() == 0) return THIN;
			if (isBlessed( e ) ) {
			    return THICK;
			} else 
			    return THIN;
        }
	    
	}
	
	/**
	 * @author danyelf
	 */
	public class MyVertexDrawPaintFunction<V> implements Transformer<V,Paint> {

		public Paint transform(V v) {
			return Color.black;
		}

	}

	public class MyVertexFillPaintFunction<V> implements Transformer<V,Paint> {

		public Paint transform( V v ) {
			if ( v == mFrom) {
				return Color.BLUE;
			}
			if ( v == mTo ) {
				return Color.BLUE;
			}
			if ( mPred == null ) {
				return Color.LIGHT_GRAY;
			} else {
				if ( mPred.contains(v)) {
					return Color.RED;
				} else {
					return Color.LIGHT_GRAY;
				}
			}
		}

	}

	/**
	 *  
	 */
	private JPanel setUpControls() {
		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
		jp.setBorder(BorderFactory.createLineBorder(Color.black, 3));		
		jp.add(
			new JLabel("Select a pair of vertices for which a shortest path will be displayed"));
		JPanel jp2 = new JPanel();
		jp2.add(new JLabel("vertex from", SwingConstants.LEFT));
		jp2.add(getSelectionBox(true));
		jp2.setBackground(Color.white);
		JPanel jp3 = new JPanel();
		jp3.add(new JLabel("vertex to", SwingConstants.LEFT));
		jp3.add(getSelectionBox(false));
		jp3.setBackground(Color.white);
		jp.add( jp2 );
		jp.add( jp3 );
		return jp;
	}

	/**
	 * @param g
	 * @param from
	 * @return
	 */
	private Component getSelectionBox(final boolean from) {

		Set<String> s = new TreeSet<String>();
		
		for (String v : mGraph.getVertices()) {
			s.add(v);
		}
		final JComboBox choices = new JComboBox(s.toArray());
		choices.setSelectedIndex(-1);
		choices.setBackground(Color.WHITE);
		choices.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String v = (String)choices.getSelectedItem();
					
				if (from) {
					mFrom = v;
				} else {
					mTo = v;
				}
				drawShortest();
				repaint();				
			}
		});
		return choices;
	}

	/**
	 *  
	 */
	protected void drawShortest() {
		if (mFrom == null || mTo == null) {
			return;
		}
		BFSDistanceLabeler<String,Number> bdl = new BFSDistanceLabeler<String,Number>();
		bdl.labelDistances(mGraph, mFrom);
		mPred = new HashSet<String>();
		
		// grab a predecessor
		String v = mTo;
		Set<String> prd = bdl.getPredecessors(v);
		mPred.add( mTo );
		while( prd != null && prd.size() > 0) {
			v = prd.iterator().next();
			mPred.add( v );
			if ( v == mFrom ) return;
			prd = bdl.getPredecessors(v);
		}
	}

	public static void main(String[] s) {
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new ShortestPathDemo());
		jf.pack();
		jf.setVisible(true);
	}

	/**
	 * @return
	 */
	Graph<String,Number> getGraph() {

		Graph<String,Number> g =
			new EppsteinPowerLawGenerator<String,Number>(new VertexFactory(), new EdgeFactory(), 26, 50, 50).generateGraph();
		Set<String> removeMe = new HashSet<String>();
		for (String v : g.getVertices()) {
            if ( g.degree(v) == 0 ) {
                removeMe.add( v );
            }
        }
		for(String v : removeMe) {
			g.removeVertex(v);
		}
		return g;
	}
	
	static class VertexFactory implements Factory<String> {
		char a = 'a';
		public String create() {
			return Character.toString(a++);
		}
		
	}
	static class EdgeFactory implements Factory<Number> {
		int count;
		public Number create() {
			return count++;
		}
		
	}

}
