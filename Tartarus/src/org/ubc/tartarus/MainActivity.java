package org.ubc.tartarus;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
	
	GLSurfaceView surfaceView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		surfaceView = new GameView(this);
		
		setContentView(surfaceView);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
	}
}
