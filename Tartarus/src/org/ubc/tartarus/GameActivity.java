package org.ubc.tartarus;

import org.ubc.tartarus.particle.Particle;
import org.ubc.tartarus.character.Character;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GameActivity extends Activity {

	GLSurfaceView surfaceView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Character.CharacterType charType = (Character.CharacterType) super.getIntent().getExtras().getSerializable(Character.TYPE_INTENT);
		surfaceView = new GameView(this, charType);
		setContentView(surfaceView);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// When we resume, we have to reload the particle bitmap.
		Particle.setParticleImgLoaded(false);
	}
}
