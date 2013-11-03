package org.ubc.tartarus;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ubc.tartarus.ParticleSystem.Type;

import android.content.Context;

public class GameRenderer extends CustomRenderer {

	public static final int MATRIX_SIZE = 4;
	
	public volatile float mAngle;
	
	private ParticleSystem mParticleSystem;

	private float stagnantColourList[][] = {
			{1.0f, 1.0f, 1.0f, 1.0f},
			{0.8f, 0.8f, 0.9f, 1.0f},
			{0.8f, 0.7f, 0.9f, 1.0f},
			{0.9f, 0.9f, 0.7f, 1.0f},
			{0.8f, 0.8f, 0.7f, 1.0f},
			{1.0f, 1.0f, 0.0f, 1.0f},
			{0.8f, 0.7f, 0.2f, 1.0f},
		};
	
	private Player mPlayer;
	private WorldMap mWorldMap;
	
	public GameRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		super.onDrawFrame(arg0);
		
		mWorldMap.drawViewport(getModelViewMatrix(), 2*getAspectRatio(), 2);
		mPlayer.onUpdate();
		mPlayer.drawPlayer(getModelViewMatrix());
		
		mParticleSystem.updateParticleSystem(getFingerX(), getFingerY(), 0, getAspectRatio());
		mParticleSystem.drawParticles(getModelViewMatrix());
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);

		byte[][] tileMap = {
				{0, 1, 2, 3, 4, 5, 6},
				{7, 8, 9, 10, 11, 12, 13},
				{14, 15, 16, 17, 18, 19, 20},
				{21, 22, 23, 24, 25, 26, 27},
				{28, 29, 30, 31, 32, 33, 34},
				{35, 36, 37, 38, 39, 40, 41},
		}; 
		
		mWorldMap = new WorldMap(getContext(), R.drawable.tileset3, 1, 25, 16, 16, 80, 48, 16, 16);
		mWorldMap.loadTileMap(tileMap, 7, 6);
		mPlayer = new Player(getContext(), R.drawable.tmp_minotaur, 0, 0, 0.5f, 0.5f, 0.001f);
		mParticleSystem = new ParticleSystem(getContext(), 100, R.drawable.particle, 10, Type.STAGNANT, stagnantColourList);
	}
		
	@Override
	public void onDownTouch(float x, float y, float width, float height) {
		super.onDownTouch(x, y, width, height);
		
		if (mParticleSystem != null) {
			mParticleSystem.beginSpawning();
		}
		
		mPlayer.setGoal(new Point(getFingerX(), getFingerY()));
	}

	@Override
	public void onReleaseTouch() {
		super.onReleaseTouch();
		
		if (mParticleSystem != null) {
			mParticleSystem.endSpawning();	
		}
	}

	@Override
	public void onMoveTouch(float x, float y, float width, float height) { 
		super.onMoveTouch(x, y, width, height);
		mPlayer.setGoal(new Point(getFingerX(), getFingerY()));
	}
}
