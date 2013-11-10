package org.ubc.tartarus;

import org.ubc.tartarus.particle.Particle;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GameActivity extends Activity {

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
