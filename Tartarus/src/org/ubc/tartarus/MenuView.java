package org.ubc.tartarus;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MenuView extends GLSurfaceView {
	CustomRenderer mRenderer;

	public MenuView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mRenderer = new MenuRenderer(context);
		setRenderer(mRenderer);
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
