package org.ubc.tartarus;

public class Rectangle {
	
	public Point bottomLeft;
	public Point topRight;
	
	public Rectangle(float x1, float y1, float x2, float y2) {
		this.bottomLeft = new Point(x1,y1);
		this.topRight = new Point(x2,y2);
	}
}
