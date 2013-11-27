package org.ubc.tartarus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.ubc.tartarus.particle.Particle;

/*import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.view.Menu;*/

import org.ubc.tartarus.character.Bomb;
import org.ubc.tartarus.character.Character;
import org.ubc.tartarus.character.Gem;
import org.ubc.tartarus.communication.OutMsgDisconnect;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.graphics.GameOverRenderer;
import org.ubc.tartarus.particle.Particle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;

public class GameOverActivity extends Activity {

	public static final String RANKS_INTENT = "RANKS_INTENT";
	GLSurfaceView surfaceView;
	MediaPlayer player;
	String songName = "gameover.mp3";	
	private ArrayList<Integer> playerInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		playerInfo = (ArrayList<Integer>) super.getIntent().getExtras().getSerializable(RANKS_INTENT);
		ApplicationData app = (ApplicationData) getApplication();
		surfaceView = new GameOverView(this, playerInfo);
		
		Log.i("GameOverActivity", "Player Info Size: " + playerInfo.size());
		
		setContentView(surfaceView);
		Particle.setParticleImgLoaded(false);
		
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
			player.prepare();
			player.start();
			} catch (IOException e) {
				e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) { 
	       // Send a disconnect message
			ApplicationData dat = (ApplicationData) getApplication();
			
	    	Particle.setParticleImgLoaded(false);
	    	
	    	Intent intent = new Intent(this, MainActivity.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
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
	protected void onResume() {
		super.onResume();
		player.start();
	}

}
