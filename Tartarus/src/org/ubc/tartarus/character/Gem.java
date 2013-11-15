package org.ubc.tartarus.character;

import org.ubc.tartarus.graphics.BitmapImg;
import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

import android.R;
import android.util.Log;

public class Gem {
	public enum AnimTypes{ ROTATE, NUM_ANIM }
	
	public enum GemType { BLUE, ORANGE, GREEN, YELLOW, NUM_TYPES }
	GemType type;
	
	public static final String GEM_INTENT = "GemType";
	
	private BitmapImg mGemImg;
	private Point mPosition;
	private float mWidth, mHeight;
	private float[] scaleMat;
	private float[] modelMat;
	
	// bottom left and top right in that order
	public static final Rectangle[] blue = {
		new Rectangle(15,453-39,33,453-7),
		new Rectangle(63,453-39,81,453-7),
		new Rectangle(112,453-39,128,453-7),
		new Rectangle(161,453-39,175,453-7),
		new Rectangle(208,453-39,224,453-7),
		new Rectangle(15,453-87,33,453-55)
	};
	
	public static final Rectangle[] orange = {
		new Rectangle(16,453-190,34,453-158),
		new Rectangle(64,453-190,82,453-158),
		new Rectangle(113,453-190,129,453-158),
		new Rectangle(162,453-190,176,453-158),
		new Rectangle(209,453-190,225,453-158),
		new Rectangle(16, 453-238,34, 453-206)
	};
	
	public static final Rectangle[] green = {
		new Rectangle(272,453-190,290,453-158),
		new Rectangle(320,453-190,338,453-158),
		new Rectangle(369,453-190,385,453-158),
		new Rectangle(418,453-190,432,453-158),
		new Rectangle(465,453-190,481,453-158),
		new Rectangle(272, 453-238, 296, 453-206)
	};

	public static final Rectangle[] yellow = {
		new Rectangle(16,453-341, 34, 453-309),
		new Rectangle(65,453-341, 82, 453-309),
		new Rectangle(113,453-341, 129, 453-309),
		new Rectangle(161,453-341,176, 453-309),
		new Rectangle(209,453-341, 225, 453-309),
		new Rectangle(16, 453-389, 34, 453-357)
	};
	
		
	
	// GEM
	int currentAnimation = 0;
	Animation[] animList;
	Point refFrame;
	
	
	private int resId;
	
	public Animation getCurrentAnimation(){
		return animList[currentAnimation];
	}
	
	// Modified to take in only a rotation animation list
	public Gem(GemType g){
				
		this.animList = new Animation[AnimTypes.NUM_ANIM.ordinal()];
		
		for(int i = 0; i < animList.length; ++i)
		{
			animList[i] = new Animation();
		}
		
		this.currentAnimation = 0;
		this.refFrame = new Point(0,0);
		//this.resId(R.drawable.gems);
		
		type = g;
		switch(g) {
		case BLUE:
			populateAnimList(blue);
			break;
		case GREEN:
			populateAnimList(green);
			break;
		case YELLOW:
			populateAnimList(yellow);
			break;
		case ORANGE:
			populateAnimList(orange);
			break;
		default:
			Log.i("Gem","Not a valid gem colour, defaulting to Blue.");
			type = g;
			populateAnimList(blue);
				break;
		}
		
	}
	
	public void setCurrentAnimation(AnimTypes anim){
		this.currentAnimation = anim.ordinal();
	}
	
	public void setRefFrame(int width, int height){
		this.refFrame = new Point(width, height);
	}
	
	public Point getRefFrame(){
		return this.refFrame;
	}
	
	public void addAnim(int anim, Rectangle[] frames){
		animList[anim] = new Animation();
		for(int i = 0; i < frames.length; i++) {
			animList[anim].addFrame(frames[i]);
		}
	}
	
	public void populateAnimList(Rectangle[] rotate){
		for(int i = 0; i < rotate.length; i++){
			animList[0].addFrame(rotate[i]);
		}
	}
	
	public int getResourceId() {
		return resId;
	}
		
	protected void setResource(int resourceId){
			resId = resourceId;
	}
}
	
	
