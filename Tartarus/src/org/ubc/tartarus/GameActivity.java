package org.ubc.tartarus;

import java.io.IOException;

import org.ubc.tartarus.character.Character;
import org.ubc.tartarus.communication.OutMsgDisconnect;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.particle.Particle;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends Activity {

	GLSurfaceView surfaceView; 
	MediaPlayer player;
	String songName = "vkmp3.mp3";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Character.CharacterType charType = (Character.CharacterType) super.getIntent().getExtras().getSerializable(Character.TYPE_INTENT);
		surfaceView = new GameView(this, charType);
		setContentView(surfaceView);
		
		//Play music here!
		AssetFileDescriptor afd;
		try {
			// Read the music file from the asset folder
			afd = getAssets().openFd(songName);
			// Creation of new media player;
			player = new MediaPlayer();
			// Set the player music source.
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			// Set the looping and play the music.
			player.setLooping(true);
			player.prepare();
			player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override 
	protected void onDestroy() {
		ApplicationData dat = (ApplicationData) getApplication();
		
		if (dat.socketComm != null) {
			try {
				new OutMsgDisconnect(this).sendMessage();
			} catch (MessageTypeMismatchException e) {
				Log.e("GameActivity", "Message type mismatch sending disconnect message.");
			}
		} else {
			Log.i("GameActivity", "Socket comm is null. Not sending a disconnect message.");
		}
		
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
		player.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		player.start();
	}
}
