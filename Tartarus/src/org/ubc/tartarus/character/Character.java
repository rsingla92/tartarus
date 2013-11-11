package org.ubc.tartarus.character;

import java.util.Vector;

import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

public class Character {
	
	public enum AnimTypes{
		WALK_LEFT, WALK_RIGHT, WALK_UP, WALK_DOWN, ATTACK_LEFT, ATTACK_RIGHT, ATTACK_UP,
		ATTACK_DOWN, NUM_ANIM
	}
	
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
		
		public double getAnimSpeed(){
			return this.animSpeed;
		}
		
	}
	
	// CHARACTER
    int currentAnimation = 0;
	Animation[] animList;
	Point refFrame;
	
	public Animation getCurrentAnimation(){
		return animList[currentAnimation];
	}
	
	public Character(Rectangle[] walkLeft, Rectangle[] walkRight, 
			Rectangle[] walkUp, Rectangle[] walkDown) {
		
		this.animList = new Animation[AnimTypes.NUM_ANIM.ordinal()];
        for (int i = 0; i < animList.length; i++){
			animList[i] = new Animation();
		}
		this.currentAnimation = 0;
		this.refFrame = new Point(0,0);
		populateAnimList(walkLeft, walkRight, walkUp, walkDown);
	}
	
	public void setCurrentAnimation (AnimTypes anim){
		this.currentAnimation = anim.ordinal();
	}
	
	public void setRefFrame(int width, int height){
		this.refFrame = new Point(width, height); 
	}
	
	public Point getRefFrame(){
		return this.refFrame;
	}
	
	public void addAnim(int anim, Rectangle[] frames) {
		animList[anim] = new Animation();
		for(int i = 0; i < frames.length; i++) {
			animList[anim].addFrame(frames[i]);
		}
	}
	
	public void populateAnimList(Rectangle[] walkLeft, Rectangle[] walkRight, 
			Rectangle[] walkUp, Rectangle[] walkDown) {
		
		for(int i = 0; i < walkLeft.length; i++) {
			animList[0].addFrame(walkLeft[i]);
		}
		
		for(int i = 0; i < walkRight.length; i++) {
			animList[1].addFrame(walkRight[i]);
		}
		
		for(int i = 0; i < walkUp.length; i++) {
			animList[2].addFrame(walkUp[i]);
		}
		
		for(int i = 0; i < walkDown.length; i++) {
			animList[3].addFrame(walkDown[i]);
		}
		
		for (int j = AnimTypes.ATTACK_LEFT.ordinal(); j < AnimTypes.NUM_ANIM.ordinal(); j++) {
				animList[j] = null;
		}
	}
	
}
