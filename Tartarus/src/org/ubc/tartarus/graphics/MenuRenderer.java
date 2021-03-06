package org.ubc.tartarus.graphics;

import java.util.NoSuchElementException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.LobbyActivity;
import org.ubc.tartarus.R;
import org.ubc.tartarus.communication.IncomingMessage;
import org.ubc.tartarus.communication.IncomingMessageParser;
import org.ubc.tartarus.communication.OutMsgJoin;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.particle.Particle;
import org.ubc.tartarus.particle.ParticleSystem;
import org.ubc.tartarus.utils.Point;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

public class MenuRenderer extends CustomRenderer {

	public static final String PLAYER_ID_INTENT = "PlayerID";
	
	public static final int MATRIX_SIZE = 4;
	public static final float CURSOR_ACCELERATION = 0.01f;
	
	private float joinX;
	private float joinY;
	private float joinWidth, joinHeight;
	
	public volatile float mAngle;
	
	private BitmapImg menuBackground, titleImg, joinImg;

	private ParticleSystem mParticleSystem;
	
	private Particle mCursor;
	private float cursorVelocityX, cursorVelocityY;
	private boolean cursorXDirection, cursorYDirection;
	private boolean hitJoin = false;
	private float joinCountdown = 1.0f;
	private OutMsgJoin joinMsg = null; 
	private int playerID = 0;
	
	public MenuRenderer(Activity activity) {
		super(activity);
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
		
		// Transformations for title image. 
		float[] scaleMat = new float[16];
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.setIdentityM(copyMat, 0);
		float scaleX = (2 * getAspectRatio()) / 4.0f; 
		float scaleY = scaleX * ( ((float) titleImg.getHeight()) / titleImg.getWidth());
		Matrix.translateM(copyMat, 0, 0, 0.75f, 0);
		Matrix.scaleM(scaleMat, 0, scaleX, scaleY, 1);
		Matrix.multiplyMM(copyMat, 0, copyMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		titleImg.draw(copyMat);
		
		// Transformations for join image. 
		joinX = (getAspectRatio()/2.0f);
		joinY = 0.4f;
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.setIdentityM(copyMat, 0);
		scaleX = (2 * getAspectRatio()) / 4.0f; 
		scaleY = scaleX * ( ((float) joinImg.getHeight()) / joinImg.getWidth());
		joinWidth = scaleX;
		joinHeight = scaleY;
		Matrix.translateM(copyMat, 0, joinX, joinY, 0);
		Matrix.scaleM(scaleMat, 0, scaleX, scaleY, 1);
		Matrix.multiplyMM(copyMat, 0, copyMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		joinImg.draw(copyMat);
		
		mCursor.setParticlePosition(mCursor.getParticleXPos() + cursorVelocityX, mCursor.getParticleYPos() + cursorVelocityY, 0);
		mCursor.drawParticle(getModelViewMatrix());
		
		if (hitJoin) {
			joinCountdown -= 0.005f; 
			if (joinCountdown <= 0) {
				hitJoin = false;
				joinCountdown = 1.0f;
				mParticleSystem.makeNormalSystem();
				mParticleSystem.endSpawning();

				// Transition to game activity...
				Intent intent = new Intent(getActivity(), LobbyActivity.class);
				intent.putExtra(PLAYER_ID_INTENT, playerID);
				getActivity().startActivity(intent);
			}
		}
		
		mParticleSystem.updateParticleSystem(getFingerX(), getFingerY(), 0, getAspectRatio());
		mParticleSystem.drawParticles(getModelViewMatrix());
		
		try {
			while (true) {
				if (socketComm == null) {
					Log.i("Msg", "SocketComm is NULL!!");
					break;
				}
				
				IncomingMessage msg = socketComm.getNextMessage();
				parseMsg(msg);
			}
		} catch(NoSuchElementException e) {
			// Intentionally empty
		}
	}

	void parseMsg(IncomingMessage msg) {
		if (msg.getID() == IncomingMessageParser.InMessageType.MSG_JOIN_RESPONSE.getId()) 
		{
			int ret = IncomingMessage.handleJoinResponse(msg);
			
			if (ret == -1) {
				Log.e("MenuRenderer", "Failure to parse join response.");
			} else if (ret == 0) {
				// Send a message indicating that the player cannot join yet.
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), "The Lobby is full. Please wait until the next game is available.",
								Toast.LENGTH_LONG).show();
					}
				});
			} else {
				// Successfully joined the game. 
				// TODO: Set the player's ID-- this is in ret. 
				Log.i("Msg", "Got a join response!");
				playerID = ret;
				
				ApplicationData dat = (ApplicationData) getActivity().getApplication();
				dat.playerId = playerID;
				
				mParticleSystem.makeSpiralSystem();
				mParticleSystem.beginSpawning();
				hitJoin = true;
			}
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);
		
		joinMsg = new OutMsgJoin(getActivity());
		menuBackground = new BitmapImg(getActivity(), R.drawable.img_tartarus_menu);
		titleImg = new BitmapImg(getActivity(), R.drawable.tart_title);
		joinImg = new BitmapImg(getActivity(), R.drawable.join_game);
		
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
		
		float fx = getFingerX();
		float fy = getFingerY();
		
		if (fx >= joinX - joinWidth && fx <= joinX + joinWidth && 
				fy >= joinY - joinHeight && fy <= joinY + joinHeight) {
			// Touched join game
			
			if (socketComm == null || socketComm.getSock() == null) {
				// Not connected -- allow the player to join as a single player.
				Log.i("MenuRenderer", "Not connected to a socket. Joining in single-player mode.");
				mParticleSystem.makeSpiralSystem();
				hitJoin = true;
			}
			else 
			{
				try {
					joinMsg.sendMessage();
				} catch (MessageTypeMismatchException e) {
					Log.i("MenuRenderer", "Could not send a join!");
				}
			}
		} else {
			mParticleSystem.makeNormalSystem();
			hitJoin = false;
		}
		
		if (mParticleSystem != null) {
			mParticleSystem.beginSpawning();
		}
	}

	@Override
	public void onReleaseTouch() {
		super.onReleaseTouch();
		
		if (mParticleSystem != null && !hitJoin) {
			mParticleSystem.endSpawning();	
		}
	}

	@Override
	public void onMoveTouch(float x, float y, float width, float height) { 
		super.onMoveTouch(x, y, width, height);
		mCursor.setParticlePosition(getFingerX(), getFingerY(), 0);
	}
}