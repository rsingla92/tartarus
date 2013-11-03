package org.ubc.tartarus;

import java.util.Random;

import android.content.Context;
import android.opengl.Matrix;

public class Particle {
	public static final int NUM_COLS = 4;
	
	private float mDecay; 
	private float mScaleFactor;
	private float mColour[]; 
	private float mX, mY, mZ;
	private float mXSpeed, mYSpeed, mZSpeed;
	private float mWidth, mHeight;
	private boolean mDead; 
	private Random mRandomGenerator; 
	private static BitmapImg particleImg;
	private static boolean loadedParticleImg = false;
	private float[] mMVPMat;
	private float[] modelMat;
	private float[] scaleMat;
	
	// Spinner parameters:
	private boolean mSpinner; 
	private float mRadSpeed;
	private float mAngleSpeed;
	private float mOriginX, mOriginY;
	private float mCurrentRad, mCurrentAngle;
	
	public Particle(final Context context, int resId,
			float xPos, float yPos, float zPos, float r, float g, float b, float a,
			float width, float height, boolean dead) {
		this(context, resId, xPos, yPos, zPos, r, g, b, a, width, height, dead, -1);
	}
	
	public Particle(final Context context, int resId,
			float xPos, float yPos, float zPos, float r, float g, float b, float a,
			float width, float height, boolean dead, float decay) {
		mRandomGenerator = new Random();
		mColour = new float[NUM_COLS];
		mMVPMat = new float[16];
		modelMat = new float[16];
		scaleMat = new float[16];
		
		if (decay < 0) {
			mDecay = (mRandomGenerator.nextFloat() * 0.009f) + 0.001f; 
		} else {
			mDecay = decay;
		}
		
		mColour[0] = r;
		mColour[1] = g;
		mColour[2] = b;
		mColour[3] = a;
		mX = xPos;
		mY = yPos;
		mZ = zPos;
		mWidth = width;
		mHeight = height;
		mXSpeed = 0.005f - (mRandomGenerator.nextFloat()*100.0f)/100000.0f;
		mYSpeed = (mRandomGenerator.nextFloat()*100.0f)/100000.0f - 0.01f;
		mZSpeed = 0.005f - (mRandomGenerator.nextFloat()*100.0f)/100000.0f;
		mDead = dead;
		mScaleFactor = 1.0f;
		mSpinner = false;
		mRadSpeed = 0;
		mAngleSpeed = 0;
		mOriginX = mOriginY = mCurrentRad = mCurrentAngle = 0;
	}
	
	// Allows for sharing of image between particles (saves a lot of memory)
	public static void loadParticleImg(final Context context, int resId) {
		particleImg = new BitmapImg(context, resId);
		loadedParticleImg = true;
	}
	
	public static boolean getParticleImgLoaded() {
		return loadedParticleImg;
	}
	
	public static void setParticleImgLoaded(boolean loaded) {
		loadedParticleImg = loaded;
	}
	
	void makeSpinner(float radiusSpeed, float angleSpeed, float originX, float originY) {
		makeSpinner(radiusSpeed, angleSpeed, originX, originY, 0, 0);
	}
	
	void makeSpinner(float radiusSpeed, float angleSpeed, float originX, float originY, 
			float initialAngle, float initialRad) {
		mSpinner = true;
		mRadSpeed = radiusSpeed;
		mAngleSpeed = angleSpeed;
		mOriginX = originX;
		mOriginY = originY;
		mCurrentRad = initialRad;
		mCurrentAngle = initialAngle;
	}
	
	public void setDecay(float decay) {
		if (decay >= 0) mDecay = decay;
	}
	
	void setParticleSpeed(float xSpeed, float ySpeed, float zSpeed) {
		mXSpeed = xSpeed;
		mYSpeed = ySpeed;
		mZSpeed = zSpeed;
	}
	
	void setParticlePosition(float x, float y, float z) {
		mX = x;
		mY = y;
		mZ = z;
	}
	
	void setParticleSize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}
	
	void reGenerateParticle(float xPos, float yPos, float zPos, float r, float g, float b, float a,
			float width, float height) {
		mColour[0] = r;
		mColour[1] = g;
		mColour[2] = b;
		mColour[3] = a;
		mX = xPos;
		mY = yPos;
		mZ = zPos;
		mWidth = width;
		mHeight = height;
		mXSpeed = 0.005f - (mRandomGenerator.nextFloat()*100.0f)/10000.0f;
		mYSpeed = 0.005f - (mRandomGenerator.nextFloat()*100.0f)/10000.0f;
		mZSpeed = 0.005f - (mRandomGenerator.nextFloat()*100.0f)/10000.0f;
		mDead = false;
		mScaleFactor = 1.0f;
		mDecay = (mRandomGenerator.nextFloat() * 0.009f) + 0.001f; 
		
		if (mSpinner) {
			mOriginX = xPos;
			mOriginY = yPos;
			mCurrentRad = 0;
			mCurrentAngle = 0;
		}
	}
	
	void setDead(boolean dead) {
		mDead = dead;
	}
	
	void updateParticle(float aspect) {
		if (!mDead) {
			mScaleFactor -= mDecay;
			
			if (mSpinner) {
				mCurrentRad += mRadSpeed;
				mCurrentAngle += mAngleSpeed; 
				
				mX = (float) (mCurrentRad * Math.cos(mCurrentAngle)) + mOriginX;
				mY = (float) (mCurrentRad * Math.sin(mCurrentAngle)) + mOriginY;
			} else {
				mX += mXSpeed;
				mY += mYSpeed;
				mZ += mZSpeed;
			}
			
			if (mScaleFactor <= 0 || mX < -aspect - mWidth || mY < -1.0f - mHeight || mX > aspect + mWidth || mY > 1.0f + mHeight ) {
				mScaleFactor = 0;
				mDead = true; 
			}
		}
	}
	
	void drawParticle(float[] modelViewMatrix) {
		if (mDead == false) {
			Matrix.setIdentityM(modelMat, 0);
			Matrix.setIdentityM(scaleMat, 0);
			Matrix.translateM(modelMat, 0, mX, mY, mZ);

			Matrix.scaleM(scaleMat, 0, mWidth*mScaleFactor, mHeight*mScaleFactor, 1);
			Matrix.multiplyMM(modelMat, 0, modelMat.clone(), 0, scaleMat, 0);
			Matrix.multiplyMM(mMVPMat, 0, modelViewMatrix, 0, modelMat, 0);
			particleImg.setColour(mColour[0], mColour[1], mColour[2], mColour[3]);
			particleImg.draw(mMVPMat);
		}
	}
	
	boolean isAlive() {
		return !mDead;
	}
}

