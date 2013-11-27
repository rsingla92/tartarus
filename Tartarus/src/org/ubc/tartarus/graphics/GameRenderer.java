package org.ubc.tartarus.graphics;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Vector;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.Player;
import org.ubc.tartarus.R;

import org.ubc.tartarus.R.drawable;
import org.ubc.tartarus.R.raw;
import org.ubc.tartarus.character.Bomb;

import org.ubc.tartarus.character.Character.CharacterType;
import org.ubc.tartarus.character.Gem;
import org.ubc.tartarus.character.Gem.GemType;
import org.ubc.tartarus.communication.IncomingMessage;
import org.ubc.tartarus.communication.IncomingMessageParser;
import org.ubc.tartarus.communication.OutMsgGemPicked;
import org.ubc.tartarus.communication.OutMsgMove;
import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.map.MapParser;
import org.ubc.tartarus.map.WorldMap;
import org.ubc.tartarus.particle.ParticleSystem;
import org.ubc.tartarus.particle.ParticleSystem.Type;
import org.ubc.tartarus.utils.Point;

import android.app.Activity;
import android.util.Log;

public class GameRenderer extends CustomRenderer {

	public static final int MATRIX_SIZE = 4;
	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;
	public static final int VIEWPORT_WIDTH = 240;
	public static final int VIEWPORT_HEIGHT = 128;
	public static final String GEM_INTENT = "GemIntent";
	
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
	private CharacterType charType;
	private ArrayList<Gem> GemArray;
	private Vector<Bomb> BombVector;
	private int playerID;
	private OutMsgGemPicked gemMsg;
	
	public GameRenderer(Activity activity, CharacterType charType) {
		super(activity);
		this.charType = charType;

		ApplicationData app = (ApplicationData) getActivity().getApplication();
		this.GemArray = app.gemList;
		this.playerID = app.playerId;
		this.gemMsg = new OutMsgGemPicked(activity);
		Log.i("GameRenderer", "Size of gem array: " + GemArray.size());
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		super.onDrawFrame(arg0);

		mWorldMap.drawViewport(getModelViewMatrix(), VIEW_HEIGHT*getAspectRatio(), VIEW_HEIGHT);
		mPlayer.onUpdate(VIEW_HEIGHT*getAspectRatio(), VIEW_HEIGHT);
		
		//Log.i("LobbyRenderer", "Gem Array Size: " + GemArray.size());
		
		for (int i = 0; i < GemArray.size(); i ++ ){
			GemArray.get(i).getCurrentAnimation().animate();
			GemArray.get(i).drawGems(getModelViewMatrix(), mWorldMap.getViewportX(), mWorldMap.getViewportY(), 
					mWorldMap.getViewportWidth(), mWorldMap.getViewportHeight(), VIEW_HEIGHT*getAspectRatio(), (float)VIEW_HEIGHT);	
			
			if (mPlayer.isCollision(GemArray.get(i).getPosition().x, GemArray.get(i).getPosition().y, 
				GemArray.get(i).getScaleDimensions().x , GemArray.get(i).getScaleDimensions().y, 
				mWorldMap.getViewportX(), mWorldMap.getViewportY(), mWorldMap.getViewportWidth(), 
				mWorldMap.getViewportHeight(), VIEW_HEIGHT*getAspectRatio(), (float) VIEW_HEIGHT)) {
				
				if (playerID-1 == GemArray.get(i).getGemType().ordinal()) {

					gemMsg.setMessage((short)(GemArray.get(i).getPosition().x/16), (short)(GemArray.get(i).getPosition().y/16));
					
					try {
						gemMsg.sendMessage();
					} catch (MessageTypeMismatchException e) {
						Log.e("Gem", "Message Type Mismatch!");
					}
					
					mPlayer.addPoints(10);
					GemArray.remove(i);
					
				}
			}
		}
		
		if (BombVector.size() > 0 && !mPlayer.isCollision(BombVector.lastElement().getPosition().x, BombVector.lastElement().getPosition().y, 
				BombVector.lastElement().getScaleDimensions().x , BombVector.lastElement().getScaleDimensions().y, 
				mWorldMap.getViewportX(), mWorldMap.getViewportY(), mWorldMap.getViewportWidth(), 
				mWorldMap.getViewportHeight(), VIEW_HEIGHT*getAspectRatio(), (float) VIEW_HEIGHT)){

			BombVector.lastElement().activateBomb();
		}
		
		for (int i = 0; i < BombVector.size(); i ++ ){
			Point pixelDimensions = BombVector.elementAt(i).getPixelDimensions(mWorldMap.getViewportWidth(), mWorldMap.getViewportHeight(),
					VIEW_HEIGHT*getAspectRatio(), VIEW_HEIGHT);
			Log.i("BOMB", " "+ pixelDimensions.x + " " + pixelDimensions.y);
			if (mPlayer.isCollision(BombVector.elementAt(i).getPosition().x, BombVector.elementAt(i).getPosition().y, 
					pixelDimensions.x , pixelDimensions.y, 
					mWorldMap.getViewportX(), mWorldMap.getViewportY(), mWorldMap.getViewportWidth(), 
					mWorldMap.getViewportHeight(), VIEW_HEIGHT*getAspectRatio(), (float) VIEW_HEIGHT) && BombVector.elementAt(i).isBombActivated())
				
				BombVector.elementAt(i).explodeBomb();
			
			if (BombVector.elementAt(i).isExploding()){
				BombVector.elementAt(i).getCurrentAnimation().animate();
			}
			BombVector.elementAt(i).drawBomb(getModelViewMatrix(), mWorldMap.getViewportX(), mWorldMap.getViewportY(), 
					mWorldMap.getViewportWidth(), mWorldMap.getViewportHeight(), VIEW_HEIGHT*getAspectRatio(), (float)VIEW_HEIGHT);
			if (BombVector.elementAt(i).getCurrentAnimation().getFrameNumber() >= 9)
				BombVector.remove(i);
		}
		
		mPlayer.drawPlayer(getModelViewMatrix());
		
		//Particles should be used for special events.
	//	mParticleSystem.updateParticleSystem(getFingerX(), getFingerY(), 0, getAspectRatio());
	//	mParticleSystem.drawParticles(getModelViewMatrix());
		
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
		if (msg.getID() == IncomingMessageParser.InMessageType.MSG_UPDATE_GEM.getId())
		{
			// Received an update gem message.
			Log.i("GameRenderer", "Received an update gem message!");
		} else if (msg.getID() == IncomingMessageParser.InMessageType.MSG_GAME_OVER.getId()){
			// Received an update gem message.
			Log.i("GameRenderer", "Received a Game Over message!");
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		super.onSurfaceCreated(arg0, arg1);
		
		//mWorldMap = new WorldMap(getContext(), R.drawable.tileset3, 1, 25, 16, 16, 240, 128, 0, 0);
		MapParser.TileMap map = MapParser.readMapFromFile(getActivity(), R.raw.tartarus_map1);
		
		mWorldMap = new WorldMap(getActivity(), R.drawable.tileset1, 1, 36, TILE_WIDTH, TILE_HEIGHT, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0, 0);
		mWorldMap.loadTileMap(map.tiles, map.worldWidth, map.worldHeight);

		mPlayer = new Player(getActivity(), 0, 0, 0.3f, 0.3f, 0.02f, mWorldMap, charType);
		mParticleSystem = new ParticleSystem(getActivity(), 100, R.drawable.particle, 1, Type.MOTION, stagnantColourList);
		
		if (!Bomb.getBombImgLoaded()){
			Bomb.loadBombImg(getActivity(), R.drawable.bomb);
			Bomb.setBombImgLoaded(true);
		}
		
		for (int i = 0; i < GemArray.size(); i++) {
			GemArray.get(i).loadGemImage();
		}
		
		BombVector = new Vector<Bomb>();
	}

	@Override
	public void onDownTouch(float x, float y, float width, float height) {
		super.onDownTouch(x, y, width, height);
	}

	@Override
	public void onReleaseTouch() {
		super.onReleaseTouch();
		if (mPlayer != null) {
			mPlayer.setReachableGoal(true);
		}
	}

	@Override
	public void onSwipe(float x1, float y1, float x2, float y2, float width, float height, float vx, float vy) { 
		super.onSwipe(x1, y1, x2, y2, width, height, vx, vy);
	}
	
	@Override
	public void onMoveTouch(float x, float y, float width, float height) {
		super.onMoveTouch(x, y, width, height);
		
		if (mPlayer != null) {
			mPlayer.setGoalPoint(new Point(getFingerX(), getFingerY()), mParticleSystem);
			Log.i("TestSocket", "Finger X: " + getFingerX() + ", Finger Y: " + getFingerY());
			mPlayer.setReachableGoal(false);
		}		
	}
	
	@Override
	public void onSingleTap(float x, float y, float width, float height) {
		super.onSingleTap(x, y, width, height);
		if (mPlayer != null) {
			mPlayer.setGoalPoint(new Point(getFingerX(), getFingerY()), mParticleSystem);
		}
	}
	
	@Override
	public void onDoubleTap (float x, float y, float width, float height) {
		super.onDoubleTap(x, y, width, height);
		
		if (mPlayer != null) { // TODO: need to also check if player has bombs
			Point converted = openGLToWorldCoords(mPlayer.getPosition().x, mPlayer.getPosition().y, 
					mWorldMap.getViewportX(), mWorldMap.getViewportY(), mWorldMap.getViewportWidth(), 
					mWorldMap.getViewportHeight());
			Point finger = openGLToWorldCoords(getFingerX(), getFingerY(), 
					mWorldMap.getViewportX(), mWorldMap.getViewportY(), mWorldMap.getViewportWidth(), 
					mWorldMap.getViewportHeight());
			
			//Log.i("TAP", "player wh " + mPlayer.getCurrentFrameWidth()/2.0f + " " + mPlayer.getCurrentFrameHeight()/2.0f);
			Log.i("TAP", "player wh " + mPlayer.getPixelDimensions().x/4 + " " +mPlayer.getPixelDimensions().y/4);
			//Log.i("TAP", "finger " + finger.x + " " + finger.y);
			Log.i("TAP", "player pos " + converted.x + " " +converted.y);
			if (finger.x < converted.x + mPlayer.getPixelDimensions().x/4 && finger.x > converted.x - mPlayer.getPixelDimensions().x/4 && 
					finger.y < converted.y + mPlayer.getPixelDimensions().y/4 && finger.y > converted.y - mPlayer.getPixelDimensions().y/4 ){
			
				Bomb b = new Bomb(getActivity(), 0.25f, 0.25f);
				BombVector.add(b);
				BombVector.lastElement().setPosition(new Point(converted.x, converted.y));
				
			}

		}
	}
}
