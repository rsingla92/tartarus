package org.ubc.tartarus;

import java.util.Timer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
	
	GLSurfaceView surfaceView; 
	SocketComm socketComm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		surfaceView = new GameView(this);
		
		setContentView(surfaceView);
		socketComm = new SocketComm(this);
		
		// Schedule the read task
		SocketComm.TCPReadTimerTask tcp_task = socketComm.new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
	}
	
}
