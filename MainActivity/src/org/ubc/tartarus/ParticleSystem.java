package org.ubc.tartarus;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class ParticleSystem {
	
	private Vector<Particle> mParticles;
	private Vector<Particle> mDeadParticles;
	private boolean mSpawning; 
	private int mMaxParticles;
	private Context mContext; 
	private int mParticleResId;
	private Random mRandGenerator;
	private int mSpawnDelay;
	private long mLastTime;
	
	public static final float DEFAULT_BURST_MIN_ANGLE = 0.0f;
	public static final float DEFAULT_BURST_MAX_ANGLE = 360.0f;
	
	private float mBurstMinAngle, mBurstMaxAngle; 
	
	public enum Type {
		STANDARD, BURST, SPIRAL, STAGNANT
	}
	
	private Type mType; 
	
	public static final int NUM_COLS_PER_ENTRY = 4;
	
	public static final float[][] DEFAULT_COLOUR_LIST = {
		{0.7f, 0.4f, 1.0f, 1.0f},
		{0.7f, 0.7f, 1.0f, 1.0f},
		{1.0f, 0.4f, 0.4f, 1.0f},
		{0.8f, 0.8f, 0.8f, 1.0f},
		{0.5f, 1.0f, 0.5f, 1.0f},
		{0.3f, 1.0f, 0.7f, 1.0f}
	};
	
	public float[][] mColourList;
	
	public ParticleSystem(Context context, int maxParticles, int particleResId, int spawnDelay) {
		this(context, maxParticles, particleResId, spawnDelay, Type.STANDARD, DEFAULT_COLOUR_LIST);
	}
	
	public ParticleSystem(Context context, int maxParticles, int particleResId, 
			int spawnDelay, Type type, float[][] colourList) {
		mParticles = new Vector<Particle>(maxParticles);
		mDeadParticles = new Vector<Particle>(maxParticles);
		mRandGenerator = new Random();
		mSpawning = false;
		mMaxParticles = maxParticles;
		mContext = context;
		mParticleResId = particleResId;
		mSpawnDelay = spawnDelay;
		mLastTime = 0;
		mType = type;
		mBurstMinAngle = DEFAULT_BURST_MIN_ANGLE;
		mBurstMaxAngle = DEFAULT_BURST_MAX_ANGLE;
			
		setColourList(colourList);
		
		// Create all the particles upfront. They will be reused.
		// They are all initially dead.
		for (int i = 0; i < maxParticles; i++) {
			mDeadParticles.add(CreateParticle(0.0f, 0.0f, 0.0f));
		}
	}
	
	public void makeBurstSystem() {
		makeBurstSystem(DEFAULT_BURST_MIN_ANGLE, DEFAULT_BURST_MAX_ANGLE);
	}
	
	public void makeBurstSystem(float minAngle, float maxAngle) {
		for (int i = 0; i < mParticles.size(); i++) {
			mDeadParticles.add(mParticles.get(i));
		}
		
		mParticles.clear();
		mType = Type.BURST;
		
		// Make sure the minimum < maximum. 
		mBurstMinAngle = Math.min(minAngle, maxAngle);
		mBurstMaxAngle = Math.max(minAngle, maxAngle);
	}
	
	public void makeStagnantSystem() {
		mType = Type.STAGNANT;
	}
	
	public Particle CreateParticle(float x, float y, float z) {
		float[] randCol = mColourList[mRandGenerator.nextInt(mColourList.length)];
		
		float randRadius = (mRandGenerator.nextFloat() * 0.2f) + 0.1f;
		
		Particle tmpParticle = new Particle(mContext, mParticleResId, x, y,
				z, randCol[0], randCol[1], randCol[2], randCol[3], randRadius, randRadius, true);
		
		return tmpParticle;
	}
	
	public void grabParticle(float x, float y, float z) {
		grabParticle(x, y, z, false, 0, 0, 0);
	}
	
	public void grabParticle(float x, float y, float z, boolean setSpeed, float xSpeed, float ySpeed, float zSpeed) {
		if (mDeadParticles.size() > 0) {
			float[] randCol = mColourList[mRandGenerator.nextInt(mColourList.length)];
			
			Particle tmpParticle = mDeadParticles.get(0);
			
			tmpParticle.reGenerateParticle(x, y, z, 
					randCol[0], randCol[1], randCol[2], randCol[3], 0.1f, 0.1f);
			
			if (setSpeed) {
				tmpParticle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
			} else if (mType == Type.STAGNANT) {
				xSpeed = 0.0005f - (mRandGenerator.nextFloat()*100.0f)/100000.0f;
				ySpeed = 0.0005f - (mRandGenerator.nextFloat()*100.0f)/100000.0f;
				zSpeed = 0.0005f - (mRandGenerator.nextFloat()*100.0f)/100000.0f;
				
				tmpParticle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
				float size = 0.1f + mRandGenerator.nextFloat() / 10.0f;
				tmpParticle.setParticleSize(size, size);
			}
			
			if (mType == Type.SPIRAL) {
				float radiusSpeed = 0.001f + mRandGenerator.nextFloat() / 1000.0f;
				float angleSpeed = 0.01f + mRandGenerator.nextFloat() / 100.0f;
				
				tmpParticle.setDecay(mRandGenerator.nextFloat() * 0.0009f + 0.0001f);
				tmpParticle.makeSpinner(radiusSpeed, angleSpeed, x, y);
			}
			
			mParticles.add(tmpParticle);
			mDeadParticles.remove(0);
		}
	}
	
	public void beginSpawning() {
		mSpawning = true;
	}
	
	public void endSpawning() {
		mSpawning = false;
	}
	
	public void setColourList(float[][] colourList) {
		if (colourList == null || colourList.equals(DEFAULT_COLOUR_LIST)) {
			mColourList = DEFAULT_COLOUR_LIST;
		} else {
			mColourList = new float[colourList.length][NUM_COLS_PER_ENTRY]; 
			
			for (int i = 0; i < colourList.length; i++) {
				try {
					System.arraycopy(colourList[i], 0, mColourList[i], 0, NUM_COLS_PER_ENTRY);
				} catch(Exception e) {
					Log.e("ParticleSystem", "Malformed colour list. Reverting to default colour list.");
					mColourList = DEFAULT_COLOUR_LIST;
					break;
				}
			}
		}
	}
	
	public void updateParticleSystem(float x, float y, float z, float aspect) {
		
		if (mSpawning == true && mParticles.size() < mMaxParticles) {
			
			long currentTime = SystemClock.uptimeMillis();
			
			if (mType == Type.BURST) {
				generateBurst(x, y, z);
			} else if (currentTime - mLastTime > mSpawnDelay) {
				// Do not create a new particle each time -- grab and reuse a particle 
				// from the dead list. 
				grabParticle(x, y, z);
				mLastTime = currentTime;
			} 
		}
		
		Iterator<Particle> it = mParticles.iterator(); 
		
		while (it.hasNext()) {
			Particle tmpParticle = it.next();
			tmpParticle.updateParticle(aspect);
			
			if (!tmpParticle.isAlive()) {
				// Particle is now dead-- add to the dead list for recycling.
				mDeadParticles.add(tmpParticle);
				it.remove();
			}
		}
	}
	
	public void drawParticles(float[] perspectiveViewMat) {
		for (int i = 0; i < mParticles.size(); i++) {
			mParticles.get(i).drawParticle(perspectiveViewMat);
		}
	}
	
	private void generateBurst(float x, float y, float z) {
		float degreesPerParticle =  (mBurstMaxAngle - mBurstMinAngle) / mMaxParticles;
		
		for (float startDegree = mBurstMinAngle; startDegree <= mBurstMaxAngle; startDegree += degreesPerParticle) {
			float diagSpeed = 0.005f - (mRandGenerator.nextFloat()*100.0f)/10000.0f; 
			
			grabParticle(x, y, z, true, diagSpeed * ((float) Math.cos(startDegree)), 
					diagSpeed * ((float) Math.sin(startDegree)), diagSpeed);
		}
	}
}
