package org.ubc.tartarus.character;

import java.util.Vector;

import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

public class Character {
	
	public enum AnimTypes{
		WALK_LEFT, WALK_RIGHT, WALK_UP, WALK_DOWN, ATTACK_LEFT, ATTACK_RIGHT, ATTACK_UP,
		ATTACK_DOWN, NUM_ANIM
	}
	
	public enum CharacterType {
		MAGUS, LOCK, MONSTER, ROOSTER, SERDIC, STRIDER, NEKU, BEAT, NUM_TYPES
	}
	
	public static final String TYPE_INTENT = "CharType";
	
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
	
	public Point getDimensions(){
		float w = this.getCurrentAnimation().getCurrentFrame().topRight.x - this.getCurrentAnimation().getCurrentFrame().bottomLeft.x;
		float h = this.getCurrentAnimation().getCurrentFrame().bottomLeft.y - this.getCurrentAnimation().getCurrentFrame().topRight.y;
		return new Point(w,h);
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
		//MAGUS, LOCK, MONSTER, ROOSTER, SERDIC, STRIDER, NEKU, BEAT
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
			newChar = new CharSerdic();
			break;
		}
		
		return newChar;
	}
	
}
