package org.ubc.tartarus;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameView extends GLSurfaceView {

	private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX, mPreviousY;
	GameRenderer mRenderer;

	public GameView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mPreviousX = mPreviousY = 0;
		mRenderer = new GameRenderer(context);
		setRenderer(mRenderer);
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    float x = e.getX();
	    float y = e.getY();

	    switch (e.getAction()) {
	        case MotionEvent.ACTION_MOVE:

	            float dx = x - mPreviousX;
	            float dy = y - mPreviousY;

	            // reverse direction of rotation above the mid-line
	            if (y > getHeight() / 2) {
	              dx = dx * -1 ;
	            }

	            // reverse direction of rotation to left of the mid-line
	            if (x < getWidth() / 2) {
	              dy = dy * -1 ;
	            }

	            mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
	            mRenderer.onMoveTouch(x, y, getWidth(), getHeight());
	        break;
	        case MotionEvent.ACTION_DOWN:
	        	mRenderer.onDownTouch(x, y, getWidth(), getHeight());
	        break;
	        case MotionEvent.ACTION_UP: 
	        	mRenderer.onReleaseTouch();
	        break;
	    }

	    mPreviousX = x;
	    mPreviousY = y;
	    return true;
	}

}
