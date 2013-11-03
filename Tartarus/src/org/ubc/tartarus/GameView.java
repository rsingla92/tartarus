package org.ubc.tartarus;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameView extends GLSurfaceView {
	CustomRenderer mRenderer;

	public GameView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mRenderer = new GameRenderer(context);
		setRenderer(mRenderer);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    float x = e.getX();
	    float y = e.getY();

	    switch (e.getAction()) {
	        case MotionEvent.ACTION_MOVE:
	            mRenderer.onMoveTouch(x, y, getWidth(), getHeight());
	        break;
	        case MotionEvent.ACTION_DOWN:
	        	mRenderer.onDownTouch(x, y, getWidth(), getHeight());
	        break;
	        case MotionEvent.ACTION_UP: 
	        	mRenderer.onReleaseTouch();
	        break;
	    }

	    return true;
	}

}
