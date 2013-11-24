package org.ubc.tartarus.character;
import org.ubc.tartarus.character.Gem.AnimTypes;
import org.ubc.tartarus.character.Gem.GemType;
import org.ubc.tartarus.graphics.BitmapImg;
import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.utils.Rectangle;

import org.ubc.tartarus.R;

import android.app.Activity;
import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class Bomb {
	public enum BombAnimTypes{ EXPLODE, NUM_ANIM };
	private static BitmapImg mBombImg;
	private boolean activated = false;
	private boolean exploding = false;
	private Point mPosition;
	private static int resId;
	private float mWidth, mHeight;
	private static boolean loadedBombImg = false;
	private float[] scaleMat;
	private float[] modelMat;
	private float[] mMVPMat;
	
	public static final Rectangle[] bombAnimation = {
		new Rectangle(2, 950-67, 67, 950-3),		// bomb image
		new Rectangle(70, 950-160, 193, 950-42),	// explosion start
		new Rectangle(350, 950-164, 480, 950-39),
		new Rectangle(33, 950-438, 230, 950-266),
		new Rectangle(312, 950-432, 517, 950-267),
		new Rectangle(577, 950-430,786, 950-272),
		new Rectangle(19, 950-684, 240, 950-491),
		new Rectangle(289, 950-705, 544, 950-473 ),
		new Rectangle(552, 950-692,809, 950-483),
		new Rectangle(297, 950-917,538, 950-750),
	};
	
	int currentAnimation = 0;
	Animation[] animList;
	Point refFrame;
	
	public Animation getCurrentAnimation(){
		return animList[currentAnimation];
	}
	
	public static void loadBombImg(final Context context, int resId) {
		mBombImg = new BitmapImg(context, resId);
		loadedBombImg = true;
	}
	
	public static boolean getBombImgLoaded() {
		return loadedBombImg;
	}
	
	public static void setBombImgLoaded(boolean loaded) {
		loadedBombImg = loaded;
	}
	
	public void activateBomb(){
		activated = true;
	}
	
	public boolean isBombActivated(){
		return activated;
	}
	
	public void explodeBomb(){
		exploding = true;
	}
	
	public boolean isExploding(){
		return exploding;
	}
	
	// Modified to take in only a rotation animation list
	public Bomb(Activity activity,float height,float width){
				
		this.animList = new Animation[AnimTypes.NUM_ANIM.ordinal()];
		mMVPMat = new float[16];
		animList[0] = new Animation();
		modelMat = new float[16];
		scaleMat = new float[16];
		mHeight = height;
		mWidth = width;
		
		this.currentAnimation = 0;
		this.setRefFrame((int)(bombAnimation[2].topRight.x - bombAnimation[2].bottomLeft.x), 
				(int)(bombAnimation[2].topRight.y - bombAnimation[2].bottomLeft.y));
		setResource(R.drawable.bomb);
		this.mPosition = new Point (32,50);
		populateAnimList(bombAnimation);
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
	
	public Point getPosition(){
		return mPosition;
	}
	
	public Point getDimensions(){
		Point bottomLeft = this.getCurrentAnimation().getCurrentFrame().bottomLeft;
		Point topRight = this.getCurrentAnimation().getCurrentFrame().topRight;
			 
		float width = topRight.x - bottomLeft.x;
		float height = topRight.y - bottomLeft.y;
		return new Point(width, height);
	}
	
	public void drawBomb(float[] modelViewMatrix, float ViewPortX, float ViewPortY, 
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
		
		mBombImg.setTexturePortion(this.getCurrentAnimation().getCurrentFrame().bottomLeft, this.getCurrentAnimation().getCurrentFrame().topRight);
		mBombImg.draw(mMVPMat);
	}
}
