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
	
	public enum eDIR {
		LEFT, RIGHT, UP, DOWN
	}
	
	private eDIR mDirection;
	
	public Player(final Context context, final int resId, float x, float y, float width, float height, float speed) {
		mPlayerImg = new BitmapImg(context, resId);
		mPosition = new Point(x, y);
		modelMat = new float[16];
		scaleMat = new float[16];
		mMVPMat = new float[16];
		mSpeed = speed;
		mDirection = eDIR.LEFT;
		mGoal = new Point(-1, -1);
		mWidth = width;
		mHeight = height;
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
	
	public void setGoal(Point goal) {
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
		
		Log.i("Player", "Goal X: " + mGoal.x + ", Goal Y: " + mGoal.y);
	}
	
	public void onUpdate() {
		if (mGoal.x != -1 && mGoal.y != -1) {
			float dx = mGoal.x - mPosition.x;
			float dy = mGoal.y - mPosition.y;

			if (dx > 0) {
				movePlayer(eDIR.RIGHT);
			} else if (dx < 0) {
				movePlayer(eDIR.LEFT);
			} else if (dy > 0) {
				movePlayer(eDIR.UP);
			} else if (dy < 0) {
				movePlayer(eDIR.DOWN);
			} else {
				// Reached position
				mGoal.x = -1;
				mGoal.y = -1;
			}
		}
	}
}
