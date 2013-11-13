package org.ubc.tartarus;

import java.util.Timer;

import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.communication.SocketComm.TCPReadTimerTask;
import org.ubc.tartarus.particle.Particle;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class LobbyActivity extends Activity {
	GLSurfaceView surfaceView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationData app = (ApplicationData) getApplication();
		surfaceView = new LobbyView(this);
		
		setContentView(surfaceView);

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
	}
}
