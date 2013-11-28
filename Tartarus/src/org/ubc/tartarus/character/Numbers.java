package org.ubc.tartarus.character;

import org.ubc.tartarus.R;
import org.ubc.tartarus.character.Gem.AnimTypes;
import org.ubc.tartarus.graphics.BitmapImg;
import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

import android.app.Activity;
import android.content.Context;
import android.opengl.Matrix;

public class Numbers {
	private static BitmapImg mNumImg;
	private Point mPosition;
	private static int resId;
	private float mWidth, mHeight;
	private float[] scaleMat;
	private float[] modelMat;
	private float[] mMVPMat;
	private boolean visible;
	private int time;
	
	public static final Rectangle[] Numbers = {
		new Rectangle (3,49-42,30,49-4), 		// 0
		new Rectangle (42,49-41,64,49-6), 		// 1
		new Rectangle (75,49-40,103,49-5), 		// 2
		new Rectangle (116,49-40,140,49-5), 	// 3
		new Rectangle (156,49-40,184,49-5), 	// 4
		new Rectangle (197,49-40,222,49-5), 	// 5
		new Rectangle (238,49-40,264,49-5), 	// 6
		new Rectangle (277,49-40,304,49-5), 	// 7
		new Rectangle (316,49-40,340,49-4), 	// 8
		new Rectangle (356,49-40,382,49-5), 	// 9
	};
	
	int currentAnimation = 0;
	Animation[] animList;
	Point refFrame;
	
	public Animation getCurrentAnimation(){
		return animList[currentAnimation];
	}
	
	// Modified to take in only a rotation animation list
	public Numbers(Activity activity,float height,float width){
				
		this.animList = new Animation[0];
		mMVPMat = new float[16];
		animList[0] = new Animation();
		modelMat = new float[16];
		scaleMat = new float[16];
		mHeight = height;
		mWidth = width;
		
		this.currentAnimation = 0;
		this.setRefFrame((int)(Numbers[2].topRight.x - Numbers[2].bottomLeft.x), 
				(int)(Numbers[2].topRight.y - Numbers[2].bottomLeft.y));
		setResource(R.drawable.bomb);
		mNumImg = new BitmapImg(activity, this.getResourceId());
		populateAnimList(Numbers);
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

		mPosition.x = p.x;
		mPosition.y = p.y;
	}
	
	public Point getPosition() {
		return mPosition;
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
	
	public Point getPixelDimensions(float viewportWidth, float viewportHeight, float viewWidth, float viewHeight) {
		Point glCoords = getScaleDimensions();

		float normalizedW = glCoords.x / viewWidth;
		float normalizedH = glCoords.y / viewHeight;
		
		return new Point(normalizedW * viewportWidth, normalizedH * viewportHeight);
	}
	
	public void drawNumber(float[] modelViewMatrix, float ViewPortX, float ViewPortY, 
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
		
		mNumImg.setTexturePortion(this.getCurrentAnimation().getCurrentFrame().bottomLeft, this.getCurrentAnimation().getCurrentFrame().topRight);
		mNumImg.draw(mMVPMat);
	}
}
