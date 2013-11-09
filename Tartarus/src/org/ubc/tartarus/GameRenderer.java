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
	private CharMagus magus;
	
	public GameRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		super.onDrawFrame(arg0);
		
		mWorldMap.drawViewport(getModelViewMatrix(), VIEW_HEIGHT*getAspectRatio(), VIEW_HEIGHT);
		mPlayer.onUpdate(VIEW_HEIGHT*getAspectRatio(), VIEW_HEIGHT);
		mPlayer.drawPlayer(getModelViewMatrix());
		
		mParticleSystem.updateParticleSystem(getFingerX(), getFingerY(), 0, getAspectRatio());
		mParticleSystem.drawParticles(getModelViewMatrix());
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);
		
		magus = new CharMagus();
	
		//mWorldMap = new WorldMap(getContext(), R.drawable.tileset3, 1, 25, 16, 16, 240, 128, 0, 0);
		MapParser.TileMap map = MapParser.readMapFromFile(getContext(), R.raw.tartarus_map1);
		
		mWorldMap = new WorldMap(getContext(), R.drawable.tileset1, 1, 36, 16, 16, 240, 128, 0, 0);
		mWorldMap.loadTileMap(map.tiles, map.worldWidth, map.worldHeight);
		mPlayer = new Player(getContext(), R.drawable.sprite_magus, 0, 0, 0.3f, 0.3f, 0.02f, mWorldMap, magus);
		mParticleSystem = new ParticleSystem(getContext(), 100, R.drawable.particle, 1, Type.MOTION, stagnantColourList);
	}
		
	@Override
	public void onDownTouch(float x, float y, float width, float height) {
		super.onDownTouch(x, y, width, height);
	}

	@Override
	public void onReleaseTouch() {
		super.onReleaseTouch();
	}

	@Override
	public void onSwipe(float x1, float y1, float x2, float y2, float width, float height, float vx, float vy) { 
		super.onSwipe(x1, y1, x2, y2, width, height, vx, vy);
		Point beginCoords = getGLCoords(x1, y1, width, height);
		if (mPlayer != null) {
			mPlayer.setGoal(new Point(getFingerX(), getFingerY()), beginCoords, -getAspectRatio(), getAspectRatio(), 1, -1, mParticleSystem);
		}
	}
	
	@Override
	public void onSingleTap(float x, float y, float width, float height) {
		super.onSingleTap(x, y, width, height);
		if (mPlayer != null) {
			mPlayer.setGoalPoint(new Point(getFingerX(), getFingerY()), mParticleSystem);
		}
	}
}
