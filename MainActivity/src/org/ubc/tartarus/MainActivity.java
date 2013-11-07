package org.ubc.tartarus;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
	
	GameView surfaceView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		surfaceView = new GameView(this);
		
		setContentView(surfaceView);
	}
}
