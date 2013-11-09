package org.ubc.tartarus;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class Player {

	private BitmapImg mPlayerImg; 
	private Point mPosition;
	private float mWidth, mHeight;
	private float mSpeed; 
	private Point mGoal;
	private float[] modelMat;
	private float[] scaleMat;
	private float[] mMVPMat;
	
	private WorldMap mWorldMap;
	
	public enum eDIR {
		LEFT, RIGHT, UP, DOWN
	}
	
	private eDIR mDirection;
	
	public Player(final Context context, final int resId, float x, float y, float width, float height, float speed, WorldMap worldMap) {
		mPlayerImg = new BitmapImg(context, resId);
		mPosition = new Point(x, y);
		modelMat = new float[16];
		scaleMat = new float[16];
		mMVPMat = new float[16];
		mSpeed = speed;
		mDirection = eDIR.LEFT;
		mGoal = new Point(-5, -5);
		mWidth = width;
		mHeight = height;
		mWorldMap = worldMap;
	}
	
	public void drawPlayer(float[] modelViewMatrix) {
		Matrix.setIdentityM(modelMat, 0);
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.translateM(modelMat, 0, mPosition.x, mPosition.y, 0);

		Matrix.scaleM(scaleMat, 0, mWidth, mHeight, 1);
		Matrix.multiplyMM(modelMat, 0, modelMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(mMVPMat, 0, modelViewMatrix, 0, modelMat, 0);
		mPlayerImg.draw(mMVPMat);
	}
	
	public void movePlayer(eDIR direction) {
		
		switch (direction) {
			case LEFT:
				mPosition.x -= mSpeed;
				break;
			case RIGHT:
				mPosition.x += mSpeed;
				break;
			case UP:
				mPosition.y += mSpeed;
				break;
			case DOWN:
				mPosition.y -= mSpeed;
				break;
		}
	}
	
	public void setGoal(Point endPoint, Point beginPoint, float left, float right, float top, float bottom) {
		// Determine the goal point based on the scroll line (distance of the goal from the player is the same
		// as the distance of the scroll line, clamped to the size of the viewport). 
		Point goal = new Point(endPoint.x - beginPoint.x + mPosition.x, endPoint.y - beginPoint.y + mPosition.y); 
		
		Log.i("Player", "Goal X: " + goal.x + ", Goal Y: " + goal.y);
		if (goal.x > right) goal.x = right;
		else if (goal.x < left) goal.x = left;
		
		if (goal.y > top) goal.y = top;
		else if (goal.y < bottom) goal.y = bottom;
	
		Log.i("Player", "Goal X (after clamp): " + goal.x + ", Goal Y (after clamp): " + goal.y);
		
		// Use implicit line equations of y - x + x_1 - y_1 and y + x - y_1 - x_1 to 
		// determine which side of these lines the goal is on. 
		boolean firstSign = goal.y - goal.x + mPosition.x - mPosition.y >= 0; 
		boolean secondSign = goal.y + goal.x - mPosition.y - mPosition.x >= 0;
		
		if (!firstSign && secondSign) {
			// In the first region, set x to goal's x. 
			mDirection = eDIR.RIGHT;
			mGoal.x = goal.x;
			mGoal.y = mPosition.y;
		} else if (firstSign && secondSign) {
			// In the upper region
			mDirection = eDIR.UP;
			mGoal.y = goal.y; 
			mGoal.x = mPosition.x;
		} else if (firstSign && !secondSign) {
			// In the left region
			mDirection = eDIR.LEFT;
			mGoal.x = goal.x;
			mGoal.y = mPosition.y;
		} else {
			mDirection = eDIR.DOWN;
			mGoal.y = goal.y;
			mGoal.x = mPosition.x;
		}
	}
	
	public void onUpdate(float viewWidth, float viewHeight) {
		if (mGoal.x != -5 && mGoal.y != -5) {
			float dx = mGoal.x - mPosition.x;
			float dy = mGoal.y - mPosition.y;

			float viewportXUnitsPerGL = mWorldMap.getViewportWidth() / viewWidth;
			float viewportYUnitsPerGL = mWorldMap.getViewportHeight() / viewHeight;

			if (dx > 0) {
				if (mDirection == eDIR.LEFT){
					//Overshot the goal
					mGoal.x = -5;
					mGoal.y = -5;
				} else {
					if (mWorldMap.atViewportXBoundary() == -1 || mPosition.x <  viewWidth * 0.25f) {
						movePlayer(eDIR.RIGHT);
					} else {
						// Move the viewport
						mWorldMap.shiftViewport(-mSpeed*viewportXUnitsPerGL, 0);
						mGoal.x -= mSpeed;
					}
				}
			} else if (dx < 0) {
				if (mDirection == eDIR.RIGHT){
					//Overshot the goal
					mGoal.x = -5;
					mGoal.y = -5;
				} else {
					if (mWorldMap.atViewportXBoundary() == 1 || mPosition.x > -viewWidth * 0.25f) {
						movePlayer(eDIR.LEFT);
					} else {
						// Move the viewport -- must shift the goal to account for this
						mWorldMap.shiftViewport(mSpeed*viewportXUnitsPerGL, 0);
						mGoal.x += mSpeed;
					}
				}
			} else if (dy > 0) {
				if (mDirection == eDIR.DOWN){
					//Overshot the goal
					mGoal.x = -5;
					mGoal.y = -5;
				} else {
					if (mWorldMap.atViewportYBoundary() == -1 || mPosition.y < viewHeight * 0.1f) {
						movePlayer(eDIR.UP);
					} else {
						// Move the viewport
						mWorldMap.shiftViewport(0, -mSpeed*viewportYUnitsPerGL);
						mGoal.y -= mSpeed;
					}
				}
			} else if (dy < 0) {
				if (mDirection == eDIR.UP){
					//Overshot the goal
					mGoal.x = -5;
					mGoal.y = -5;
				} else {
					if (mWorldMap.atViewportYBoundary() == 1 || mPosition.y > -viewHeight * 0.1f) {
						movePlayer(eDIR.DOWN);
					} else {
						// Move the viewport
						mWorldMap.shiftViewport(0, mSpeed*viewportYUnitsPerGL);
						mGoal.y += mSpeed;
					}
				}
			} else {
				// Reached position
				mGoal.x = -5;
				mGoal.y = -5;
			}
		}
	}
}
