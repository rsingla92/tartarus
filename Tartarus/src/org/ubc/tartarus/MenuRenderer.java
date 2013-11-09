package org.ubc.tartarus;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.Matrix;

public class MenuRenderer extends CustomRenderer {

	public static final int MATRIX_SIZE = 4;
	
	public volatile float mAngle;
	
	private BitmapImg menuBackground;

	private ParticleSystem mParticleSystem;
	
	private Particle mCursor;
	
	public MenuRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		super.onDrawFrame(arg0);
		
		float[] copyMat = new float[16];
		Matrix.setIdentityM(copyMat, 0);
		Matrix.scaleM(copyMat, 0, 2 * getAspectRatio(), 2, 2);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		menuBackground.draw(copyMat);
		
		mCursor.setParticlePosition(getFingerX(), getFingerY(), 0);
		mCursor.drawParticle(getModelViewMatrix());
		mParticleSystem.updateParticleSystem(getFingerX(), getFingerY(), 0, getAspectRatio());
		mParticleSystem.drawParticles(getModelViewMatrix());
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);
		
		menuBackground = new BitmapImg(getContext(), R.drawable.img_tartarus_menu);
		
		mCursor = new Particle(getContext(), R.drawable.particle, 0, 0, 0,
				0.85098f, 0.0f, 0.0f, 1.0f, 0.2f, 0.2f, false, 0);
		mCursor.setParticleSpeed(0, 0, 0);
		
		mParticleSystem = new ParticleSystem(getContext(), 50, R.drawable.particle, 10);
	}
		
	@Override
	public void onDownTouch(float x, float y, float width, float height) {
		super.onDownTouch(x, y, width, height);
		
		if (mParticleSystem != null) {
			mParticleSystem.beginSpawning();
		}
	}

	@Override
	public void onReleaseTouch() {
		super.onReleaseTouch();
		
		if (mParticleSystem != null) {
			mParticleSystem.endSpawning();	
		}
	}

	@Override
	public void onMoveTouch(float x, float y, float x2, float y2, float width, float height) { 
		super.onMoveTouch(x, y, x2, y2, width, height);
	}
}