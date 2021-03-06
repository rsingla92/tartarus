package org.ubc.tartarus;

import java.io.IOException;
import java.util.Timer;

import org.ubc.tartarus.character.Bomb;
import org.ubc.tartarus.communication.OutMsgDisconnect;
import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.particle.Particle;

import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;

public class MainActivity extends Activity {
	
	GLSurfaceView surfaceView;
	MediaPlayer player;
	String songName = "title2.mp3";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		surfaceView = new MenuView(this);
		
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
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
		Bomb.setBombImgLoaded(false);
		player.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		player.start();
	}
}
