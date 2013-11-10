package org.ubc.tartarus;

import java.util.Random;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class ParticleSystem {
	
	private Particle[] particles;
	private int[] deadIndices;
	private int deadIndexCount;
	private boolean mSpawning; 
	private int mMaxParticles;
	private Context mContext; 
	private int mParticleResId;
	private Random mRandGenerator;
	private int mSpawnDelay;
	private long mLastTime;
	private float startX, startY, endX, endY, diffX, diffY; 
	private boolean xMotionGreater, yMotionGreater;
	
	public static final float DEFAULT_BURST_MIN_ANGLE = 0.0f;
	public static final float DEFAULT_BURST_MAX_ANGLE = 360.0f;
	
	private float mBurstMinAngle, mBurstMaxAngle; 
	
	public enum Type {
		STANDARD, BURST, SPIRAL, STAGNANT, MOTION
	}
	
	private Type mType; 
	
	public static final int NUM_COLS_PER_ENTRY = 4;
	
	public static final float[][] DEFAULT_COLOUR_LIST = {
		{1.0f, 0.5f, 0.0f, 1.0f},
		{0.71765f, 0.654902f, 0.317647f, 1.0f},
		{0.309804f, 0.184314f, 0.184314f, 1.0f},
		{1.0f, 1.0f, 0.25f, 1.0f},
		{0.72f, 0.45f, 0.2f, 1.0f},
		{0.71f, 0.65f, 0.26f, 1.0f},
		{0.85f, 0.85f, 0.1f, 1.0f},
		{0.9255f, 0.28627f, 0.027f, 1.0f},
		{0.9529f, 0.032916f, 0.032916f, 1.0f},
		{0.73725f, 0.07843f, 0.0784f, 1.0f},
		{0.93725f, 0.91765f, 0.42745f, 1.0f},
		{0.3451f, 0.34117f, 0.3098f, 1.0f},
		{0.5804f, 0.5804f, 0.56078f, 1.0f},
		{0.5804f, 0.5804f, 0.56078f, 1.0f},
		{1.0f, 0.4235f, 0.0f, 1.0f},
		{1.0f, 0.4235f, 0.0f, 1.0f},
		{1.0f, 0.5882f, 0.0f, 1.0f},
		{1.0f, 0.23529f, 0.0f, 1.0f},
		{0.36078f, 0.35294f, 0.32549f, 1.0f},
	};
	
	public float[][] mColourList;
	
	public ParticleSystem(Context context, int maxParticles, int particleResId, int spawnDelay) {
		this(context, maxParticles, particleResId, spawnDelay, Type.STANDARD, DEFAULT_COLOUR_LIST);
	}
	
	public ParticleSystem(Context context, int maxParticles, int particleResId, 
			int spawnDelay, Type type, float[][] colourList) {
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
		deadIndexCount = maxParticles;
		startX = startY = endX = endY = diffX = diffY = 0;
		xMotionGreater = yMotionGreater = false;
		
		setColourList(colourList);
		
		particles = new Particle[maxParticles];
		deadIndices = new int[maxParticles];
		
		for (int i = 0; i < deadIndices.length; i++) {
			deadIndices[i] = -1;
		}
		
		if (!Particle.getParticleImgLoaded()) {
			// Load particle image
			Particle.loadParticleImg(mContext, particleResId);
		}
		
		// Create all the particles up-front. They will be reused.
		// They are all initially dead.
		for (int i = 0; i < maxParticles; i++) {
			particles[i] = CreateParticle(0.0f, 0.0f, 0.0f);
			deadIndices[i] = i;
		}
	}
	
	public void makeBurstSystem() {
		makeBurstSystem(DEFAULT_BURST_MIN_ANGLE, DEFAULT_BURST_MAX_ANGLE);
	}
	
	public void makeBurstSystem(float minAngle, float maxAngle) {
		for (int i = 0; i < particles.length; i++) {
			particles[i].setDead(true);
		}
		
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
		tmpParticle.setDead(true);
		
		return tmpParticle;
	}
	
	public void grabParticle(float x, float y, float z) {
		grabParticle(x, y, z, false, 0, 0, 0);
	}
	
	public void grabParticle(float x, float y, float z, boolean setSpeed, float xSpeed, float ySpeed, float zSpeed) {
		if (deadIndexCount > 0) {
			float[] randCol = mColourList[mRandGenerator.nextInt(mColourList.length)];
			
			Particle tmpParticle = particles[deadIndices[deadIndexCount-1]];
			deadIndices[deadIndexCount-1] = -1;
			deadIndexCount--;
			
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
				float radiusSpeed = 0.002f + mRandGenerator.nextFloat() / 1000.0f;
				float angleSpeed = 0.05f + mRandGenerator.nextFloat() / 100.0f;
				
				tmpParticle.setDecay(mRandGenerator.nextFloat() * 0.0009f + 0.0001f);
				tmpParticle.makeSpinner(radiusSpeed, angleSpeed, x, y);
			} else {
				tmpParticle.setSpinner(false);
			}
		}
	}
	
	public void makeSpiralSystem() {
		mType = Type.SPIRAL;
	}
	
	public void makeNormalSystem() {
		mType = Type.STANDARD;
	}
	
	public void beginSpawning() {
		mSpawning = true;
	}
	
	public void endSpawning() {
		mSpawning = false;
	}
	
	public void setMotion(float x1, float y1, float x2, float y2, float motionRate) {
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
		diffX = endX - startX;
		diffY = endY - startY; 
		
		if (diffX > 0) xMotionGreater = true;
		else xMotionGreater = false;
		
		if (diffY > 0) yMotionGreater = true;
		else yMotionGreater = false;
		
		// Normalize difference vector
		float magnitude = (float) Math.sqrt(diffX*diffX + diffY*diffY);
		diffX = (diffX / magnitude) * motionRate;
		diffY = (diffY / magnitude) * motionRate;
		mSpawning = true;
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
	
		if (mSpawning == true && deadIndexCount > 0) {
			
			long currentTime = SystemClock.uptimeMillis();
			
			if (mType == Type.BURST) {
				generateBurst(x, y, z);
			} else if (currentTime - mLastTime > mSpawnDelay) {
				// Do not create a new particle each time -- grab and reuse a particle 
				// from the dead list. 
				if (mType == Type.MOTION) {
					grabParticle(startX, startY, 0); 
					startX += diffX;
					startY += diffY; 
					
					if (((xMotionGreater && startX >= endX) || (!xMotionGreater && startX <= endX)) &&
							((yMotionGreater && startY >= endY) || (!yMotionGreater && startY <= endY))) {
						mSpawning = false;
						startX = startY = endX = endY = diffX = diffY = 0;
					}
				} else {
					grabParticle(x, y, z);
				}
				mLastTime = currentTime;
			} 
		}
		
		for (int i = 0; i < mMaxParticles; i++) {
			if (!particles[i].isAlive()) continue;
			
			particles[i].updateParticle(aspect);
			
			if (!particles[i].isAlive()) {
				// Particle now dead -- add to dead index list.
				deadIndices[deadIndexCount] = i;
				deadIndexCount++;
			}
		}
	}
	
	public void drawParticles(float[] perspectiveViewMat) {
		for (int i = 0; i < particles.length; i++) {
			particles[i].drawParticle(perspectiveViewMat);
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
