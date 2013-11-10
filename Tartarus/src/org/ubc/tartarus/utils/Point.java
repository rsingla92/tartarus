package org.ubc.tartarus.utils;

public class Point {
	public float x, y; 
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		String str = "(" + String.valueOf(x) + ", " + String.valueOf(y) + ")";
		return str;
	}
}
