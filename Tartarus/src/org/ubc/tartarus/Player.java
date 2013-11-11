package org.ubc.tartarus;

import org.ubc.tartarus.character.Character;
import org.ubc.tartarus.character.Character.AnimTypes;
import org.ubc.tartarus.graphics.BitmapImg;
import org.ubc.tartarus.map.WorldMap;
import org.ubc.tartarus.particle.ParticleSystem;
import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.communication.OutMsgMove;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
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
	private boolean reachableGoal = false;
	
	private WorldMap mWorldMap;
	
	public enum eDIR {
		LEFT, RIGHT, UP, DOWN
	}
	
	private eDIR mDirection;
	private Character character;	
	private OutMsgMove moveMsg; 
	
	public Player(Activity activity, final int resId, float x, float y, float width, float height, float speed, WorldMap worldMap, Character c) {
		mPlayerImg = new BitmapImg(activity, resId);
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
		character = c;
		moveMsg = new OutMsgMove(activity);
	}
	
	public void setReachableGoal(boolean enable) {
		reachableGoal = enable;
	}
	
	public Point getScaleDimensions() {
		Point bottomLeft = character.getCurrentAnimation().getCurrentFrame().bottomLeft;
		Point topRight = character.getCurrentAnimation().getCurrentFrame().topRight;
		Point refFrame = character.getRefFrame();
		 
		float width = (refFrame.x/refFrame.y)*mHeight;
		float scaleWidth = width * ((topRight.x - bottomLeft.x)/refFrame.x);
		float scaleHeight = mHeight * ((topRight.y - bottomLeft.y)/refFrame.y);
		
		Point scaleDimensions = new Point(scaleWidth, scaleHeight);
		return scaleDimensions;
	}
	
	public void drawPlayer(float[] modelViewMatrix) {
		Point bottomLeft = character.getCurrentAnimation().getCurrentFrame().bottomLeft;
		Point topRight = character.getCurrentAnimation().getCurrentFrame().topRight;
		Point refFrame = character.getRefFrame();
		 
		float width = (refFrame.x/refFrame.y)*mHeight;
		float scaleWidth = width * ((topRight.x - bottomLeft.x)/refFrame.x);
		float scaleHeight = mHeight * ((topRight.y - bottomLeft.y)/refFrame.y);
		Matrix.setIdentityM(modelMat, 0);
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.translateM(modelMat, 0, mPosition.x, mPosition.y, 0);

		int flipVal = (character.getCurrentAnimation().getFlip()? 1 : -1);
		Matrix.scaleM(scaleMat, 0, flipVal*scaleWidth, scaleHeight, 1);
		Matrix.multiplyMM(modelMat, 0, modelMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(mMVPMat, 0, modelViewMatrix, 0, modelMat, 0);
		
		mPlayerImg.setTexturePortion(character.getCurrentAnimation().getCurrentFrame().bottomLeft, character.getCurrentAnimation().getCurrentFrame().topRight);
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
	
	public void setGoalPoint(Point goal, ParticleSystem motionParticles) {
		// Use implicit line equations of y - x + x_1 - y_1 and y + x - y_1 - x_1 to 
		// determine which side of these lines the goal is on. 
		boolean firstSign = goal.y - goal.x + mPosition.x - mPosition.y >= 0; 
		boolean secondSign = goal.y + goal.x - mPosition.y - mPosition.x >= 0;
		float addX = 0, addY = 0;
		
		if (!firstSign && secondSign) {
			if (mDirection != eDIR.RIGHT){
				character.getCurrentAnimation().reset();
				character.setCurrentAnimation(AnimTypes.WALK_RIGHT);
			}
			
			// In the first region, set x to goal's x. 
			addX = getScaleDimensions().x / 2.0f; 
			mDirection = eDIR.RIGHT;
			mGoal.x = goal.x;
			mGoal.y = mPosition.y;
		} else if (firstSign && secondSign) {
			if (mDirection != eDIR.UP){
				character.getCurrentAnimation().reset();
				character.setCurrentAnimation(AnimTypes.WALK_UP);
			}
			// In the upper region
			addY = getScaleDimensions().y / 2.0f;
			mDirection = eDIR.UP;
			mGoal.y = goal.y; 
			mGoal.x = mPosition.x;
		} else if (firstSign && !secondSign) {
			if (mDirection != eDIR.LEFT){
				character.getCurrentAnimation().reset();
				character.setCurrentAnimation(AnimTypes.WALK_LEFT);
			}
			// In the left region
			addX = -getScaleDimensions().x / 2.0f;
			mDirection = eDIR.LEFT;
			mGoal.x = goal.x;
			mGoal.y = mPosition.y;
		} else {
			if (mDirection != eDIR.DOWN){
				character.getCurrentAnimation().reset();
				character.setCurrentAnimation(AnimTypes.WALK_DOWN);
			}
			addY = -getScaleDimensions().y / 2.0f;
			mDirection = eDIR.DOWN;
			mGoal.y = goal.y;
			mGoal.x = mPosition.x;
		}
		
		motionParticles.setMotion(mPosition.x + addX, mPosition.y + addY, mGoal.x, mGoal.y, 0.1f);
	}
	
	public void setGoal(Point endPoint, Point beginPoint, float left, float right, float top, float bottom, ParticleSystem motionParticles) {
		// Determine the goal point based on the scroll line (distance of the goal from the player is the same
		// as the distance of the scroll line, clamped to the size of the viewport). 
		Point goal = new Point(endPoint.x - beginPoint.x + mPosition.x, endPoint.y - beginPoint.y + mPosition.y); 
		
		if (goal.x > right) goal.x = right;
		else if (goal.x < left) goal.x = left;
		
		if (goal.y > top) goal.y = top;
		else if (goal.y < bottom) goal.y = bottom;
	
		setGoalPoint(goal, motionParticles);
	}
	
	public Point getPosition() {
		return new Point(mPosition.x, mPosition.y);
	}
	
	public void onUpdate(float viewWidth, float viewHeight) {
		if (mGoal.x != -5 && mGoal.y != -5) {
			character.getCurrentAnimation().animate();

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
						
						if (reachableGoal) {
							mGoal.x -= mSpeed;
						}
					}
					
					try {
						moveMsg.sendMessage((short)(mSpeed*viewportXUnitsPerGL), (short) 0);
					} catch (MessageTypeMismatchException e) {
						Log.e("Player", "Could not send a move message.");
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
						if (reachableGoal) {
							mGoal.x += mSpeed;
						}
					}
					
					try {
						moveMsg.sendMessage((short)(-mSpeed*viewportXUnitsPerGL), (short) 0);
					} catch (MessageTypeMismatchException e) {
						Log.e("Player", "Could not send a move message.");
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
						if (reachableGoal) {
							mGoal.y -= mSpeed;
						}
					}
					
					try {
						moveMsg.sendMessage((short)0, (short)(mSpeed*viewportYUnitsPerGL));
					} catch (MessageTypeMismatchException e) {
						Log.e("Player", "Could not send a move message.");
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
						if (reachableGoal) {
							mGoal.y += mSpeed;
						}
					}
					
					try {
						moveMsg.sendMessage((short)0, (short)(-mSpeed*viewportYUnitsPerGL));
					} catch (MessageTypeMismatchException e) {
						Log.e("Player", "Could not send a move message.");
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
