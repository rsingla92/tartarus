package org.ubc.tartarus;

import java.io.IOException;

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

	GLSurfaceView surfaceView;
	MediaPlayer player;
	String songName = "gameover.mp3";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ApplicationData app = (ApplicationData) getApplication();
		surfaceView = new GameOverView(this);
		
		setContentView(surfaceView);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
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
		getMenuInflater().inflate(R.menu.game_over, menu);
		return true;
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
