package org.ubc.tartarus.graphics;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ubc.tartarus.GameOverActivity;
import org.ubc.tartarus.R;
import org.ubc.tartarus.communication.IncomingMessage;
import org.ubc.tartarus.communication.IncomingMessageParser;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.particle.Particle;
import org.ubc.tartarus.particle.ParticleSystem;
import org.ubc.tartarus.utils.Point;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

public class GameOverRenderer extends CustomRenderer {

	public static final String PLAYER_ID_INTENT = "PlayerID";
	
	public static final int MATRIX_SIZE = 4;
	public static final float CURSOR_ACCELERATION = 0.01f;

	public static final int MAX_PLAYERS = 4;
	
	public volatile float mAngle;
	
	private BitmapImg menuBackground;
	private BitmapImg playerImgs[];
	
	private ParticleSystem mParticleSystem;
	
	private Particle mCursor;
	private float cursorVelocityX, cursorVelocityY;
	private boolean cursorXDirection, cursorYDirection;
	private int playerID = 0;
	private ArrayList<Integer> playerInfo;
	private float[] positions;

	public GameOverRenderer(Activity activity, ArrayList<Integer> playerInfo) {
		super(activity);
		this.playerInfo = playerInfo;
		Log.i("GameOverRenderer", "Player Info Size: " + playerInfo.size());
		positions = new float[4];
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		super.onDrawFrame(arg0);
		
		float[] copyMat = new float[16];
		Matrix.setIdentityM(copyMat, 0);
		Matrix.scaleM(copyMat, 0, 2 * getAspectRatio(), 2, 2);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		menuBackground.draw(copyMat);
		
		if (cursorXDirection && cursorVelocityX > 0) {
			// Moving to the right, slowly decrease velocity.
			cursorVelocityX -= CURSOR_ACCELERATION;
			if (cursorVelocityX <= 0) cursorVelocityX = 0;
			if (mCursor.getParticleXPos() + cursorVelocityX >= getAspectRatio()) {
				cursorXDirection = false;
				cursorVelocityX *= -1; 
			}
		} else if (!cursorXDirection && cursorVelocityX < 0) {
			// Moving to the left, slowly increase velocity
			cursorVelocityX += CURSOR_ACCELERATION;
			if (cursorVelocityX >= 0) cursorVelocityX = 0;
			if (mCursor.getParticleXPos() + cursorVelocityX <= -getAspectRatio()) {
				cursorXDirection = true;
				cursorVelocityX *= -1; 
			}
		}
		
		if (cursorYDirection && cursorVelocityY > 0) {
			cursorVelocityY -= CURSOR_ACCELERATION;
			if (cursorVelocityY <= 0) cursorVelocityY = 0;
			if (mCursor.getParticleYPos() + cursorVelocityY >= 1) {
				cursorYDirection = false;
				cursorVelocityY *= -1; 
			}			
		} else if (!cursorYDirection && cursorVelocityY < 0) {
			cursorVelocityY += CURSOR_ACCELERATION;
			if (cursorVelocityY >= 0) cursorVelocityY = 0;
			if (mCursor.getParticleYPos() + cursorVelocityY <= -1) {
				cursorYDirection = true;
				cursorVelocityY *= -1; 
			}			
		}
		
		if (playerInfo != null){
			Log.i("Game Over", "draw player images");
			
		}
		
		// centre: 288 158 
		// 150 by 50 scale width = 0.41551 height = 0.23585
		// big dim = 722 424
		// diff = 0.05
		// %x = 0.101108 *getAspectRatio()*2
		// %y = 0.127358 
		
		float scaleX = 0.41551f;
		float scaleY = 0.23585f;
		float diff = 0.05f + scaleY / 2; 
		float startX = 0.101108f * getAspectRatio() * 2; 
		float startY = 0.127358f;
		
		float[] scaleMat = new float[16];
		
		for (int i = 0; i < playerInfo.size() / 2; i++)
		{
			int playerID = playerInfo.get(2*i); 
			int score = playerInfo.get(2*i+1); 
			
			Matrix.setIdentityM(scaleMat, 0);
			Matrix.setIdentityM(copyMat, 0);
			Matrix.translateM(copyMat, 0, startX, startY - diff * i, 0);
			Matrix.scaleM(scaleMat, 0, scaleX, scaleY, 1);
			Matrix.multiplyMM(copyMat, 0, copyMat.clone(), 0, scaleMat, 0);
			Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
			
			Log.i("GameOverRenderer", "PlayerID: " + playerID);
			playerImgs[playerID-1].draw(copyMat);
		}
		
		mCursor.setParticlePosition(mCursor.getParticleXPos() + cursorVelocityX, mCursor.getParticleYPos() + cursorVelocityY, 0);
		mCursor.drawParticle(getModelViewMatrix());

		mParticleSystem.updateParticleSystem(getFingerX(), getFingerY(), 0, getAspectRatio());
		mParticleSystem.drawParticles(getModelViewMatrix());
		
	}
	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);
		
		menuBackground = new BitmapImg(getActivity(), R.drawable.img_tartarus_gameover);
		playerImgs = new BitmapImg[MAX_PLAYERS];
		playerImgs[0] = new BitmapImg(getActivity(), R.drawable.player1);
		playerImgs[1] = new BitmapImg(getActivity(), R.drawable.player2);
		playerImgs[2] = new BitmapImg(getActivity(), R.drawable.player3);
		playerImgs[3] = new BitmapImg(getActivity(), R.drawable.player4);
				
		mCursor = new Particle(getActivity(), R.drawable.particle, 0, 0, 0,
				0.85098f, 0.0f, 0.0f, 1.0f, 0.2f, 0.2f, false, 0);
		mCursor.setParticleSpeed(0, 0, 0);
		
		Log.i("Particle", "On surface created! Making new particle system...");
		mParticleSystem = new ParticleSystem(getActivity(), 500, R.drawable.particle, 5);
	}
	
	@Override
	public void onSwipe(float x1, float y1, float x2, float y2, float width, float height, float vx, float vy) {
		super.onSwipe(x1, y1, x2, y2, width, height, vx, vy);
		Point glSpeed = getGLCoords(vx, vy, width, height);
		cursorVelocityX = glSpeed.x / 100.0f;
		cursorVelocityY = glSpeed.y / 100.0f; // Scale factor to slow down speed
		mCursor.setParticleSpeed(cursorVelocityX, cursorVelocityY, 0);
		cursorXDirection = (cursorVelocityX > 0); 
		cursorYDirection = (cursorVelocityY > 0);
	}
	
	@Override
	public void onDownTouch(float x, float y, float width, float height) {
		super.onDownTouch(x, y, width, height);
		
		mCursor.setParticlePosition(getFingerX(), getFingerY(), 0);
		cursorVelocityX = cursorVelocityY = 0; 
		
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
	public void onMoveTouch(float x, float y, float width, float height) { 
		super.onMoveTouch(x, y, width, height);
		mCursor.setParticlePosition(getFingerX(), getFingerY(), 0);
	}
}