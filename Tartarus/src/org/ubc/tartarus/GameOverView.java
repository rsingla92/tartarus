package org.ubc.tartarus;

import org.ubc.tartarus.graphics.CustomRenderer;
import org.ubc.tartarus.graphics.GameOverRenderer;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GameOverView extends GLSurfaceView implements 
						GestureDetector.OnGestureListener {
	CustomRenderer mRenderer;
	private GestureDetectorCompat mGestureDetector; 
	
	public GameOverView(Activity activity) {
		super(activity);
		setEGLContextClientVersion(2);
		mRenderer = new GameOverRenderer(activity);
		setRenderer(mRenderer);
		mGestureDetector = new GestureDetectorCompat(activity, this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		this.mGestureDetector.onTouchEvent(e);
		
	    switch (e.getAction()) {
	        case MotionEvent.ACTION_UP: 
	        	mRenderer.onReleaseTouch();
	        break;
	    }

	    return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		
		mRenderer.onDownTouch(x, y, getWidth(), getHeight());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float x = e1.getX();
	    float y = e1.getY();
		float x2 = e2.getX();
	    float y2 = e2.getY();
	    Log.i("GameView", "In onFling");
		mRenderer.onSwipe(x, y, x2, y2, getWidth(), getHeight(), velocityX, velocityY);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		float x = e2.getX();
		float y = e2.getY();
        mRenderer.onMoveTouch(x, y, getWidth(), getHeight());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
