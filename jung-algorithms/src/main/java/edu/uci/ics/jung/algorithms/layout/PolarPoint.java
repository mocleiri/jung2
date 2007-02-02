package edu.uci.ics.jung.algorithms.layout;

import java.awt.geom.Point2D;


public class PolarPoint extends Point2D.Double {
	
	public PolarPoint() {
		this(0,0);
	}
	public PolarPoint(double theta, double radius) {
		super(theta, radius);
	}
	public double getTheta() { return getX(); }
	public double getRadius() { return getY(); }
	public void setTheta(double theta) { setLocation(theta, getRadius()); }
	public void setRadius(double radius) { setLocation(getTheta(), radius); }

	/**
	 * Returns the result of converting <code>polar</code> to Cartesian coordinates.
	 */
	public static Point2D polarToCartesian(PolarPoint polar) {
		return polarToCartesian(polar.getTheta(), polar.getRadius());
	}

	/**
	 * Returns the result of converting <code>(theta, radius)</code> to Cartesian coordinates.
	 */
	public static Point2D polarToCartesian(double theta, double radius) {
		return new Point2D.Double(radius*Math.cos(theta), radius*Math.sin(theta));
	}

	/**
	 * Returns the result of converting <code>point</code> to polar coordinates.
	 */
	public static PolarPoint cartesianToPolar(Point2D point) {
		return cartesianToPolar(point.getX(), point.getY());
	}

	/**
	 * Returns the result of converting <code>(x, y)</code> to polar coordinates.
	 */
	public static PolarPoint cartesianToPolar(double x, double y) {
		double theta = Math.atan2(y,x);
		double radius = Math.sqrt(x*x+y*y);
		return new PolarPoint(theta, radius);
	}

}