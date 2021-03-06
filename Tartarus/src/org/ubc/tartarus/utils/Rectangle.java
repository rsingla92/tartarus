package org.ubc.tartarus.utils;

public class Rectangle {
	
	public Point bottomLeft;
	public Point topRight;
	
	public Rectangle(float x1, float y1, float x2, float y2) {
		this.bottomLeft = new Point(x1,y1);
		this.topRight = new Point(x2,y2);
	}
	
	@Override
	public String toString() {
		return "{" + bottomLeft.toString() + ", " + topRight.toString() + "}";
	}
}
