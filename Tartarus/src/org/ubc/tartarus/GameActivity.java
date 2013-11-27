package org.ubc.tartarus;

import java.io.IOException;
import java.util.ArrayList;

import org.ubc.tartarus.character.Bomb;
import org.ubc.tartarus.character.Character;
import org.ubc.tartarus.character.Gem;
import org.ubc.tartarus.communication.OutMsgDisconnect;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;
import org.ubc.tartarus.graphics.GameRenderer;
import org.ubc.tartarus.particle.Particle;

import android.app.Activity;
import android.content.Intent;
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
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.action_settings:
        	Log.i("Menu", "Action Settings!");
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) { 
	       // Send a disconnect message
			ApplicationData dat = (ApplicationData) getApplication();
			
			if (dat.socketComm != null) {
				try {
					new OutMsgDisconnect(this).sendMessage();
				} catch (MessageTypeMismatchException e) {
					Log.e("LobbyActivity", "Message type mismatch sending disconnect message.");
				}
			} else {
				Log.i("LobbyActivity", "Socket comm is null. Not sending a disconnect message.");
			}
			
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
