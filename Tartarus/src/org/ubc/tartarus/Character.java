package org.ubc.tartarus;

import java.util.Vector;

public class Character {
	
	public enum AnimTypes{
		WALK_LEFT, WALK_RIGHT, WALK_UP, WALK_DOWN, ATTACK_LEFT, ATTACK_RIGHT, ATTACK_UP,
		ATTACK_DOWN, NUM_ANIM
	}
	
	public class Animation{
		Vector<Rectangle> frameList;
		public float animSpeed = 1;
		int currentFrame = 0;
		public boolean flip = false;
		
		public Animation(){
			this.flip = false;
			this.currentFrame = 0;
			this.animSpeed = 1;
		}
		
		public void addFrame(Rectangle rect){
			frameList.add(rect);
		}
		
		public void setFrameSpeed(int speed){
			this.animSpeed = speed;
		}
		
		public void reset(){
			this.currentFrame = 0;
		}
		
		public Rectangle getCurrentFrame(){
			return this.frameList.elementAt(currentFrame);
		}
		
		public float getAnimSpeed(){
			return this.animSpeed;
		}
		
	}
	
	// CHARACTER
	protected int currentAnimation = 0;
	Animation[] animList;
	
	public Character(Rectangle[] walkLeft, Rectangle[] walkRight, 
			Rectangle[] walkUp, Rectangle[] walkDown) {
		
		animList = new Animation[AnimTypes.NUM_ANIM.ordinal()];
		populateAnimList(walkLeft, walkRight, walkUp, walkDown);
	}
	
	public void addAnim(int anim, Rectangle[] frames) {
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
