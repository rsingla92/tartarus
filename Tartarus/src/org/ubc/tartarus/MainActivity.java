package org.ubc.tartarus;

import java.io.IOException;
import java.util.Timer;

import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.particle.Particle;

import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;

public class MainActivity extends Activity {
	
	GLSurfaceView surfaceView;
	MediaPlayer player;
	String songName = "title2.mp3";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationData app = (ApplicationData) getApplication();
		
		app.socketComm = new SocketComm(app);
		app.socketComm.openSocket();
		
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
		
		// Schedule the read task, using the socketComm in the Application so that it exist
		// throughout all activities.
		SocketComm.TCPReadTimerTask tcp_task = app.socketComm.new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
		player.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		player.start();
	}
}
