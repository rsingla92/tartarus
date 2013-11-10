package org.ubc.tartarus;

import org.ubc.tartarus.graphics.CustomRenderer;
import org.ubc.tartarus.graphics.GameRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector;

public class GameView extends GLSurfaceView implements 
								GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
	private CustomRenderer mRenderer;
	private GestureDetectorCompat mGestureDetector; 
	 
	public GameView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mRenderer = new GameRenderer(context);
		setRenderer(mRenderer);
		mGestureDetector = new GestureDetectorCompat(context, this);
		mGestureDetector.setOnDoubleTapListener(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		this.mGestureDetector.onTouchEvent(e);
		
	    switch (e.getAction()) {
	    /*    case MotionEvent.ACTION_MOVE:
	            mRenderer.onMoveTouch(x, y, getWidth(), getHeight());
	        break;
	        case MotionEvent.ACTION_DOWN:
	        	mRenderer.onDownTouch(x, y, getWidth(), getHeight());
	        break;*/
		    case MotionEvent.ACTION_MOVE:
	            mRenderer.onMoveTouch(e.getX(), e.getY(), getWidth(), getHeight());
	        break;
	        case MotionEvent.ACTION_UP: 
	        	mRenderer.onReleaseTouch();
	        break;
	    }

	//	return super.onTouchEvent(e);
	    return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.i("GameView", "In onDoubleTap");
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		mRenderer.onSingleTap(e.getX(), e.getY(), getWidth(), getHeight());
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		//Log.i("GameView", "In onDown");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float x = e1.getX();
	    float y = e1.getY();
		float x2 = e2.getX();
	    float y2 = e2.getY();
	  //  Log.i("GameView", "In onFling");
		//mRenderer.onSwipe(x, y, x2, y2, getWidth(), getHeight(), velocityX, velocityY);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e, MotionEvent e2, float vx,
			float vy) {
		//mRenderer.onMoveTouch(e2.getX(), e2.getY(), getWidth(), getHeight());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
