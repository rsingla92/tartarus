package org.ubc.tartarus.character;

import java.util.Vector;

import org.ubc.tartarus.utils.Rectangle;

public class Animation{
	private Vector<Rectangle> frameList;
	private float animSpeed = 0.1f;
	private float currentFrame = 0;
	private boolean flip = false;
	
	public Animation(){
		this.flip = false;
		this.currentFrame = 0;
		this.animSpeed = 0.1f;
        this.frameList = new Vector<Rectangle>();
	}
	
	public void animate(){
		this.currentFrame += this.animSpeed;
		// not sure if this is wat we need to animate or if we need
		// to set the currentFrame back to 0 
		if (this.currentFrame >= frameList.size()-1)
			//this.animSpeed = -this.animSpeed;
			this.currentFrame = 0;
	}
	
	public void setFlip(boolean bool){
		this.flip = bool;
	}
	
	public boolean getFlip() {
		return flip;
	}
	
	public void addFrame(Rectangle rect){
		Rectangle tmpRect = new Rectangle(rect.bottomLeft.x, rect.bottomLeft.y, 
				rect.topRight.x, rect.topRight.y);
		
		this.frameList.add(tmpRect);
	}
	
	public void setFrameSpeed(int speed){
		this.animSpeed = speed;
	}
	
	public void reset(){
		this.currentFrame = 0;
	}
	
	public Rectangle getCurrentFrame(){
		return this.frameList.elementAt((int)Math.round(currentFrame));
	}
	
	public int getFrameNumber(){
		return (int)Math.round(currentFrame);
	}
	
	public double getAnimSpeed(){
		return this.animSpeed;
	}
	
}