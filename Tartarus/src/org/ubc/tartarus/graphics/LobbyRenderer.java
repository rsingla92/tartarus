package org.ubc.tartarus.graphics;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.GameActivity;
import org.ubc.tartarus.Player;
import org.ubc.tartarus.R;
import org.ubc.tartarus.particle.Particle;
import org.ubc.tartarus.particle.ParticleSystem;
import org.ubc.tartarus.utils.Point;
import org.ubc.tartarus.character.Character;
import org.ubc.tartarus.character.Gem;
import org.ubc.tartarus.character.Gem.GemType;
import org.ubc.tartarus.communication.IncomingMessage;
import org.ubc.tartarus.communication.IncomingMessageParser;
import org.ubc.tartarus.communication.OutMsgGemAck;
import org.ubc.tartarus.communication.OutMsgReady;
import org.ubc.tartarus.communication.OutMsgSelectChar;
import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

public class LobbyRenderer extends CustomRenderer {
	
	public static final int MATRIX_SIZE = 4;
	public static final float CURSOR_ACCELERATION = 0.01f;
	public static final int MAX_PLAYERS = 4;
	
	public static final int TOKEN_WIDTH = 100;
	public static final int TOKEN_HEIGHT = 100;
	
	// Character box information
	public static final Point NEKU_CENTRE = new Point(130.5f, 156f); 
	public static final Point MAGUS_CENTRE = new Point(284.0f, 156.0f);
	public static final Point MONSTER_CENTRE = new Point(437.5f, 156f);
	public static final Point SERDIC_CENTRE = new Point(597.0f, 157.0f);
	public static final Point ROOSTER_CENTRE = new Point(130.0f, 313f);
	public static final Point STRIDER_CENTRE = new Point(283.5f, 313.0f);
	public static final Point BEAT_CENTRE = new Point(438.5f, 312.5f);
	public static final Point LOCK_CENTRE = new Point(596.0f, 313.0f);
	
	// Used to properly draw tokens.
	public static final Point[] centreList = {
		MAGUS_CENTRE, LOCK_CENTRE, MONSTER_CENTRE, ROOSTER_CENTRE, SERDIC_CENTRE, 
		STRIDER_CENTRE, NEKU_CENTRE, BEAT_CENTRE
	};
	
	private float readyX;
	private float readyY;
	private float readyWidth, readyHeight;
	
	private float backX;
	private float backY;
	private float backWidth, backHeight;
	
	public volatile float mAngle;
	
	private BitmapImg lobbyBackground, titleImg, readyImg, backImg;
	private BitmapImg tokenImg;

	private ParticleSystem mParticleSystem;
	
	private Particle mCursor;
	private float cursorVelocityX, cursorVelocityY;
	private boolean cursorXDirection, cursorYDirection;
	private boolean hitReady = false;
	private boolean pickedChar = false, confirmedChar = false;
	private float readyCountdown = 1.0f;
	private Character.CharacterType charType = Character.CharacterType.NUM_TYPES;
	private int playerId;
	private int[] chosenChars;
	
	private ArrayList<Gem> gemList;
	
	public LobbyRenderer(Activity activity, int playerId) {
		super(activity);
		
		this.playerId = playerId;
		chosenChars = new int[MAX_PLAYERS]; 
		gemList = new ArrayList<Gem>();
		
		for (int i = 0; i < MAX_PLAYERS; i++) {
			// Each index corresponds to an ID - 1. 
			chosenChars[i] = -1;
		}
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		super.onDrawFrame(arg0);
		
		float[] copyMat = new float[16];
		Matrix.setIdentityM(copyMat, 0);
		Matrix.scaleM(copyMat, 0, 2 * getAspectRatio(), 2, 2);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		lobbyBackground.draw(copyMat);
		
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
		
		// Transformations for ready image. 
		readyX = -(getAspectRatio()/2.0f);
		readyY = -0.7f;
		
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.setIdentityM(copyMat, 0);
		scaleX = (2 * getAspectRatio()) / 4.0f; 
		scaleY = scaleX * ( ((float) readyImg.getHeight()) / readyImg.getWidth());
		readyWidth = scaleX;
		readyHeight = scaleY;
		Matrix.translateM(copyMat, 0, readyX, readyY, 0);
		Matrix.scaleM(scaleMat, 0, scaleX, scaleY, 1);
		Matrix.multiplyMM(copyMat, 0, copyMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		readyImg.draw(copyMat);
		
		// Transformations for back image
		backX = (getAspectRatio()/2.0f);
		backY = -0.7f;
		Matrix.setIdentityM(scaleMat, 0);
		Matrix.setIdentityM(copyMat, 0);
		scaleX = (2 * getAspectRatio()) / 4.0f; 
		scaleY = scaleX * ( ((float) backImg.getHeight()) / backImg.getWidth());
		backWidth = scaleX;
		backHeight = scaleY;
		Matrix.translateM(copyMat, 0, backX, backY, 0);
		Matrix.scaleM(scaleMat, 0, scaleX, scaleY, 1);
		Matrix.multiplyMM(copyMat, 0, copyMat.clone(), 0, scaleMat, 0);
		Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
		backImg.draw(copyMat);
		
		mCursor.setParticlePosition(mCursor.getParticleXPos() + cursorVelocityX, mCursor.getParticleYPos() + cursorVelocityY, 0);
		mCursor.drawParticle(getModelViewMatrix());
		
		if (hitReady) {
			readyCountdown -= 0.005f; 
			if (readyCountdown <= 0) {
				hitReady = false;
				readyCountdown = 1.0f;
				mParticleSystem.endSpawning();
				// Transition to game activity...
				Intent intent = new Intent(getActivity(), GameActivity.class);
				intent.putExtra(Character.TYPE_INTENT, charType);
				
				ApplicationData app = (ApplicationData) getActivity().getApplication();
				app.gemList = gemList;
				
				getActivity().startActivity(intent);
			}
		}
		
		if (pickedChar) {
			readyCountdown -= 0.03f; 
			if (readyCountdown <= 0) {
				pickedChar = false;
				readyCountdown = 1.0f;
				mParticleSystem.endSpawning();
				mParticleSystem.makeNormalSystem();
			}
		}	
		
		// Draw the tokens
		for (int i = 0; i < MAX_PLAYERS; i++) {
			if (chosenChars[i] != -1 && chosenChars[i] < centreList.length && chosenChars[i] >= 0) {
				// Get the centre in pixel coordinates, then translate to openGL coordinates.
				Matrix.setIdentityM(scaleMat, 0);
				scaleY = (TOKEN_HEIGHT / (float) lobbyBackground.getHeight()) * 2.0f;
				scaleX = scaleY;	
				float transX = (-centreList[chosenChars[i]].x / lobbyBackground.getWidth()) * 2 * getAspectRatio() + 
						(2*getAspectRatio())/2.0f;
				float transY = (-centreList[chosenChars[i]].y / lobbyBackground.getHeight()) * 2.0f + 1.0f;
				Matrix.setIdentityM(copyMat, 0);
				Matrix.translateM(copyMat, 0, transX, transY, 0);
				Matrix.scaleM(scaleMat, 0, scaleX, scaleY, 1);
				Matrix.multiplyMM(copyMat, 0, copyMat.clone(), 0, scaleMat, 0);
				Matrix.multiplyMM(copyMat, 0, getModelViewMatrix(), 0, copyMat.clone(), 0);
				
				float[] col = Player.getColorFromId(i + 1);
				tokenImg.setColour(col[0], col[1], col[2], col[3]);
				tokenImg.draw(copyMat);
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
		if (msg.getID() == IncomingMessageParser.InMessageType.MSG_START.getId()) {
			// Transition to the game
			mParticleSystem.makeSpiralSystem();
			mParticleSystem.beginSpawning();

			hitReady = true;
		} else if (msg.getID() == IncomingMessageParser.InMessageType.MSG_CHAR_CHOSEN.getId()) {
			// Character chosen handling -- make the character un-selectable.
			Log.i("Msg", "Received Char Chosen Msg!");
			byte[] dat = msg.getData(); 
			
			try {
				byte player = dat[0];
				byte charId = dat[1];
				
				Log.i("Msg", "Data received: " + dat[0] + ", " + dat[1]);
				
				if (player == playerId) {
					charType = Character.CharacterType.values()[charId];
					chosenChars[player - 1] = charType.ordinal();
					confirmedChar = true; 
					Log.i("Msg", "Character confirmed");
				} else {
					// Another player has chosen a character.
					chosenChars[player - 1] = charId;
					Log.i("Msg", "Character " + player + " has chosen character.");
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e("LobbyRenderer", "Character chosen message not received correctly.");
			}
		} else if (msg.getID() == IncomingMessageParser.InMessageType.MSG_GEM.getId()) {
			Log.i("Msg", "Received Gem Msg!");
			byte[] dat = msg.getData(); 
			
			try {
				Log.i("LobbyRenderer", "Gem Type: " + dat[0] + ", Number of Gems: " + dat[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e("LobbyRenderer", "Message mismatch with Gem Msg");
			}
			
			generateGems(dat);
			
			try {
				new OutMsgGemAck(getActivity()).sendMessage();
			} catch (MessageTypeMismatchException e) {
				Log.e("LobbyRenderer", "Sending gem acknowledgment error.");
			}
		} else if (msg.getID() == IncomingMessageParser.InMessageType.PLAYER_POS_MSG.getId()) {
			Log.i("LobbyRenderer", "Received Player Pos Message!");
			ByteBuffer bb = ByteBuffer.wrap(msg.getData());
			
			short x = (short) (bb.getShort() & 0xFFFF);
			short y = (short) (bb.getShort() & 0xFFFF);
			
			ApplicationData dat = (ApplicationData) getActivity().getApplication();
			dat.startPos = new Point(x, y); 
			Log.i("LobbyRenderer", "Created new point at: (" + x + ", " + y + ")");
		}
	}
	
	private void generateGems(byte buf[]) 
	{
		ByteBuffer bb = ByteBuffer.wrap(buf);
		
		try {
			int count = 0;
			
			while (count < buf.length) 
			{
				int currentPlayerId = bb.get(); 
				int numGems = bb.get(); 
				count += 2;
				Log.i("LobbyRenderer", "Player: " + currentPlayerId + ", Number of Gems: " + numGems);
				
				for (int i = 0; i < numGems; i++) 
				{
					int x = bb.getShort() & 0x00FF;
					int y = bb.getShort() & 0x00FF;
					
					x *= GameRenderer.TILE_WIDTH;
					y *= GameRenderer.TILE_HEIGHT;
					
					Log.i("LobbyRenderer", "Gem " + i + ": (" + x + ", " + y + ")");
					count += 4; 
					
					gemList.add(new Gem(getActivity(), GemType.values()[currentPlayerId - 1], x, y));
				}
			}
		} catch (IndexOutOfBoundsException e) {
			Log.e("LobbyRenderer", "Error parsing gem message.");
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);
		
		lobbyBackground = new BitmapImg(getActivity(), R.drawable.img_tartarus_lobby);
		titleImg = new BitmapImg(getActivity(), R.drawable.tart_title);
		readyImg = new BitmapImg(getActivity(), R.drawable.ready); 
		backImg = new BitmapImg(getActivity(), R.drawable.back); 
		tokenImg = new BitmapImg(getActivity(), R.drawable.token);
		
		mCursor = new Particle(getActivity(), R.drawable.particle, 0, 0, 0,
				0.85098f, 0.0f, 0.0f, 1.0f, 0.2f, 0.2f, false, 0);
		mCursor.setParticleSpeed(0, 0, 0);
		
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
		
		// check chars
		
		Character.CharacterType type = Character.CharacterType.NEKU; 
		
		// neku w,h = 44.5,54
		// neku centre = 130.5, 156		
		if (testBoundingBox(fx,fy, 130.5f,156f,44.5f,54f,width, height)){
			type = Character.CharacterType.NEKU;
			Log.i("CHAR", "im neku");
			pickedChar = true;
		}
		
		// magus w,h = 44,54
		// magus centre = 284, 156
		else if (testBoundingBox(fx,fy, 284f,156f,44f,54f, width, height)){
			type = Character.CharacterType.MAGUS;
			Log.i("CHAR", "im magus");
			pickedChar = true;
		}
		
		// monster w,h = 48.5,54
		// monster centre = 437.5,156
		else if (testBoundingBox(fx,fy, 437.5f,156f,48.5f,54f,width, height)){
			type = Character.CharacterType.MONSTER;
			Log.i("CHAR", "im a monster");
			pickedChar = true;
		}
		
		// serdic w,h = 42,55
		// serdic centre = 597,157
		else if (testBoundingBox(fx,fy, 597f,157f,42f,55f,width, height)){
			type = Character.CharacterType.SERDIC;
			Log.i("CHAR", "im Ser Dic");
			pickedChar = true;
		}
		
		// rooster w,h = 43,54
		// rooster centre = 130,313
		else if (testBoundingBox(fx,fy, 130f,313f,43f,54f,width, height)){
			type = Character.CharacterType.ROOSTER;
			Log.i("CHAR", "im a rooster");
			pickedChar = true;
		}
		
		// strider w,h = 42.5,54
		// strider centre = 283.5,313
		else if (testBoundingBox(fx,fy, 283.5f,313f,42.5f,54f,width, height)){
			type = Character.CharacterType.STRIDER;
			Log.i("CHAR", "im strider");
			pickedChar = true;
		}
		
		// beat w,h = 48.5,54.5
		// beat centre =438.5,312.5 
		else if (testBoundingBox(fx,fy, 438.5f,312.5f,48.5f,54.5f,width, height)){
			type = Character.CharacterType.BEAT;
			Log.i("CHAR", "im beat");
			pickedChar = true;
		}
		
		// lock w,h = 47,54
		// lock centre = 596,313
		else if (testBoundingBox(fx,fy, 596f,313f,47f,54f,width, height)){
			type = Character.CharacterType.LOCK;
			Log.i("CHAR", "im lock");
			pickedChar = true;
		}
		
		if (pickedChar) {
			// Send out a message that the character has been picked.
			if (socketComm == null || socketComm.getSock() == null) {
				confirmedChar = true;
				playerId = 1;
				charType = type;
				chosenChars[playerId - 1] = type.ordinal();
			}
			
			pickedChar = false;
			
			OutMsgSelectChar selectCharMsg = new OutMsgSelectChar(getActivity());
			try {
				selectCharMsg.sendMessage((byte) type.ordinal());
			} catch (MessageTypeMismatchException e) {
				Log.e("LobbyRenderer", "Could not send Select Character Message.");
			}
		}
		
		if (confirmedChar && charType != Character.CharacterType.NUM_TYPES){
			if (fx >= readyX - readyWidth && fx <= readyX + readyWidth && 
					fy >= readyY - readyHeight && fy <= readyY + readyHeight) {
				// Touched join game
				// Send out a message that the character has been picked.
				if (socketComm == null || socketComm.getSock() == null) {
					Log.i("LobbyRenderer", "Running in single-player mode, allowing you to join.");
					mParticleSystem.makeSpiralSystem();
					hitReady = true;
				}
				else 
				{
					OutMsgReady readyMsg = new OutMsgReady(getActivity());
					
					try {
						readyMsg.sendMessage();
						
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getActivity(), "Starting game!", Toast.LENGTH_SHORT).show();
							}
						}); 
					} catch (MessageTypeMismatchException e) {
						Log.e("LobbyRenderer", "Could not send Ready Message.");
					}
				}
				// Now we wait for a response from the DE2.
				//mParticleSystem.makeSpiralSystem();
				//hitReady = true;
			} else if (fx >= backX - backWidth && fx <= backX + backWidth && 
					fy >= backY - backHeight && fy <= backY + backHeight) {
				// Touched join game
				mParticleSystem.makeSpiralSystem();
				//hitReady = true;
			}
			else {	
				mParticleSystem.makeNormalSystem();
				hitReady = false;
			}
		
		}
		
		if (mParticleSystem != null) {
			mParticleSystem.beginSpawning();
		} 
	}

	private boolean testBoundingBox (float fx, float fy, float x1, float y1, float width, float height, float widthScreen, float heightScreen){
		Point centre = getGLCoords(x1, y1, 722, 519);
		
		// need to change the widthScreen to pass in pixel with and height
		float convertedW = (width/ 722)* (2*getAspectRatio());
		float convertedH = (height / 519)*2;
		
		if (fx >= centre.x - convertedW && fx <= centre.x + convertedW && 
				fy >= centre.y - convertedH && fy <= centre.y + convertedH) {
			// Touched character 
			mParticleSystem.makeBurstSystem();
			return true;
		}
		return false;
	}
	@Override
	public void onReleaseTouch() {
		super.onReleaseTouch();
		
		if (mParticleSystem != null && !hitReady && !pickedChar) {
			mParticleSystem.endSpawning();	
		}
	}

	@Override
	public void onMoveTouch(float x, float y, float width, float height) { 
		super.onMoveTouch(x, y, width, height);
		mCursor.setParticlePosition(getFingerX(), getFingerY(), 0);
	}
	
	
}
