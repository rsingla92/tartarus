package org.ubc.tartarus.character;

import java.util.Vector;

import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

import android.util.Log;

public class Character {
	
	public enum AnimTypes{
		WALK_LEFT, WALK_RIGHT, WALK_UP, WALK_DOWN, ATTACK_LEFT, ATTACK_RIGHT, ATTACK_UP,
		ATTACK_DOWN, NUM_ANIM
	}
	
	public enum CharacterType {
		MAGUS, LOCK, MONSTER, ROOSTER, SERDIC, STRIDER, NEKU, BEAT, NUM_TYPES
	}
	
	public static final String TYPE_INTENT = "CharType";
	
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
	
	private int resId;
	
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
	
	public int getResourceId() {
		return resId;
	}
	
	protected void setResource(int resourceId) {
		resId = resourceId;
	}
	
	public static Character getCharFromType(Character.CharacterType c) {
		Character newChar = null; 
		//MAGUS, LOCK, MONSTER, ROOSTER, SERDIC, STRIDER
		switch(c) {
			case MAGUS:
			newChar = new CharMagus();
			break;
			
			case LOCK:
			newChar = new CharLock();
			break;
			
			case MONSTER:
			newChar = new CharMonster();
			break;
			
			case ROOSTER:
			newChar = new CharRooster();
			break;
			
			case SERDIC:
			newChar = new CharSerdic();
			break;
			
			case STRIDER:
			newChar = new CharStrider();
			break;
		
			case NEKU: 
			newChar = new CharNeku();
			break;
			
			case BEAT:
			newChar = new CharBeat();
			break;
			default:
			newChar = new CharRooster();
			Log.i("Player", "No character found, defaulting to Rooster.");
			break;
		}
		
		return newChar;
	}
	
}
