package org.ubc.tartarus.character;

import org.ubc.tartarus.graphics.BitmapImg;
import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

import org.ubc.tartarus.R;

import android.app.Activity;
import android.opengl.Matrix;
import android.util.Log;

public class Gem {
	public enum AnimTypes{ ROTATE, NUM_ANIM }
	
	public enum GemType { BLUE, ORANGE, GREEN, YELLOW, NUM_TYPES }
	GemType type;
	
	public static final String GEM_INTENT = "GemType";
	private BitmapImg mGemImg;
	private Point mPosition;
	private int resId;
	private float mWidth, mHeight;
	private float[] scaleMat;
	private float[] modelMat;
	private float[] mMVPMat;
	
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
	
	public Animation getCurrentAnimation(){
		return animList[currentAnimation];
	}
	
	// Modified to take in only a rotation animation list
	public Gem(Activity activity, GemType g,float height,float width){
				
		this.animList = new Animation[AnimTypes.NUM_ANIM.ordinal()];
		mMVPMat = new float[16];
		animList[0] = new Animation();
		modelMat = new float[16];
		scaleMat = new float[16];
		mHeight = height;
		mWidth = width;
		
		this.currentAnimation = 0;
		this.setRefFrame((int)(yellow[3].topRight.x - yellow[3].bottomLeft.x), 
				(int)(yellow[3].topRight.y - yellow[3].bottomLeft.y));
		setResource(R.drawable.gems);
		mGemImg = new BitmapImg(activity, this.getResourceId());
		this.mPosition = new Point (32,32);
	
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
	
	public Point getScaleDimensions(){
		Point bottomLeft = this.getCurrentAnimation().getCurrentFrame().bottomLeft;
		Point topRight = this.getCurrentAnimation().getCurrentFrame().topRight;
			 
		float width = (refFrame.x/refFrame.y)*mHeight;
		float scaleWidth = width * ((topRight.x - bottomLeft.x)/refFrame.x);
		float scaleHeight = mHeight * ((topRight.y - bottomLeft.y)/refFrame.y);
			
		Point scaleDimensions = new Point(scaleWidth, scaleHeight);
		return scaleDimensions;
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
	
	public void setPosition(Point p){
		// Need to randomize this
		mPosition.x = p.x;
		mPosition.y = p.y;
	}
	
	public Point getPosition(){
		return mPosition;
	}
	
	public void drawGems(float[] modelViewMatrix, float ViewPortX, float ViewPortY, 
			float viewportWidth, float viewportHeight, float viewWidth, float viewHeight){
		
		if (mPosition.x < ViewPortX || mPosition.x > ViewPortX + viewportWidth || mPosition.y < ViewPortY 
				|| mPosition.y > ViewPortY + viewportHeight) return;

		float convertedX = (-(mPosition.x - ViewPortX)/ viewportWidth) * viewWidth + viewWidth/2;
		float convertedY = (-(mPosition.y - ViewPortY) / viewportHeight) * viewHeight + viewHeight/2;
		Point bottomLeft = this.getCurrentAnimation().getCurrentFrame().bottomLeft;
		Point topRight = this.getCurrentAnimation().getCurrentFrame().topRight;
		Point refFrame = this.getRefFrame();
		 
		float width = (refFrame.x/refFrame.y)*mHeight;
		float scaleWidth = width * ((topRight.x - bottomLeft.x)/refFrame.x);
		float scaleHeight = mHeight * ((topRight.y - bottomLeft.y)/refFrame.y);
		Matrix.setIdentityM(modelMat, 0);
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.translateM(modelMat, 0, convertedX, convertedY, 0.2f);

		/*
		 * We must flip in the opposite direction expected... Because the view matrix is set up to look
		 * down the negative z-axis, which means that the x-axis is positive going left (opposite that we 
		 * would expect).  
		 */
		int flipVal = (this.getCurrentAnimation().getFlip()? -1 : 1);
		Matrix.scaleM(scaleMat, 0, flipVal*scaleWidth, scaleHeight, 1);
		Matrix.multiplyMM(modelMat, 0, modelMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(mMVPMat, 0, modelViewMatrix, 0, modelMat, 0);
		
		mGemImg.setTexturePortion(this.getCurrentAnimation().getCurrentFrame().bottomLeft, this.getCurrentAnimation().getCurrentFrame().topRight);
		mGemImg.draw(mMVPMat);
	}
}
	
	
