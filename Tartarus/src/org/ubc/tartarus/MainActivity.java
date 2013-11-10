package org.ubc.tartarus;

import java.util.Timer;

import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.particle.Particle;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
	
	GLSurfaceView surfaceView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationData app = (ApplicationData) getApplication();
		surfaceView = new MenuView(this);
		
		setContentView(surfaceView);
		app.socketComm = new SocketComm(this);
		
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
	}
	
}
