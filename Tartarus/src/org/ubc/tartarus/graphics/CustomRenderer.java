package org.ubc.tartarus.graphics;

import java.net.Socket;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.character.Character.CharacterType;
import org.ubc.tartarus.communication.SocketComm;
import org.ubc.tartarus.utils.Point;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class CustomRenderer implements Renderer {
	public static final int MATRIX_SIZE = 4;
	
	public static final int VIEW_HEIGHT = 2;
	
	// Matrices used by all renderers
	private float[] mProjectionMatrix;
	private float[] mViewMatrix;
	private float[] mModelViewMatrix;
	private float fingerX, fingerY;
	
	private Activity mActivity;
	private float mAspectRatio;
	protected SocketComm socketComm;
	
	public CustomRenderer(Activity activity) {
		super();
		
		mActivity = activity;
		ApplicationData dat = (ApplicationData) activity.getApplication(); 
		socketComm = dat.socketComm;
	}
	
	protected float getFingerX() {
		return fingerX;
	}
	
	protected float getFingerY() {
		return fingerY;
	}
	
	protected float[] getModelViewMatrix() {
		return mModelViewMatrix;
	}
	
	protected float[] getViewMatrix() {
		return mViewMatrix;
	}
	
	protected float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}
	
	protected Activity getActivity() {
		return mActivity;
	}
	
	protected float getAspectRatio() {
		return mAspectRatio;
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	    
		// Set up the viewing and projection matrices
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);	
		Matrix.multiplyMM(mModelViewMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		float aspectRatio = (float) width / height;
		mAspectRatio = aspectRatio;
		
		//Matrix.frustumM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 3, 7);
		Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f,  3, 7);

	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Load shaders for all BitmapImg objects.
		BitmapImg.loadBitmapShaders();
		mProjectionMatrix = new float[MATRIX_SIZE*MATRIX_SIZE];
		mViewMatrix = new float[MATRIX_SIZE*MATRIX_SIZE];
		mModelViewMatrix = new float[MATRIX_SIZE*MATRIX_SIZE]; 
		
		// Enable alpha blending and depth test.
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDepthMask(false);
		
	//	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public static int loadShader(int type, String shaderCode){

	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	} 
	   
	// Found online at:
	// http://stackoverflow.com/questions/10985487/android-opengl-es-2-0-screen-coordinates-to-world-coordinates
  public Point getGLCoords( float x, float y, float screenW, float screenH)
   { 
       // Auxiliary matrix and vectors
       // to deal with ogl.
       float[] invertedMatrix, transformMatrix,
           normalizedInPoint, outPoint;
       invertedMatrix = new float[16];
       transformMatrix = new float[16];
       normalizedInPoint = new float[4];
       outPoint = new float[4];

       int oglTouchY = (int) (screenH - y);

       /* Transform the screen point to clip
       space in ogl (-1,1) */       
       normalizedInPoint[0] =
    	        (float) (x * 2.0f / screenW - 1.0);
       normalizedInPoint[1] =
        (float) ((oglTouchY) * 2.0f / screenH - 1.0);
       normalizedInPoint[2] = -1.0f;
       normalizedInPoint[3] = 1.0f;

       /* Obtain the transform matrix and
       then the inverse. */
       
       Matrix.multiplyMM(
           transformMatrix, 0,
           mProjectionMatrix, 0,
           mViewMatrix, 0);
       
       Matrix.invertM(invertedMatrix, 0,
           transformMatrix, 0);       
       
       /* Apply the inverse to the point
       in clip space */
       Matrix.multiplyMV(
           outPoint, 0,
           invertedMatrix, 0,
           normalizedInPoint, 0);

       if (outPoint[3] == 0.0)
       {
           Log.e("World coords", "ERROR!");
       }

       Point glPoint = new Point(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3]); 
       return glPoint;
   }
   
   public void addFingerCoords( float x, float y, float screenW, float screenH) {
	   Point p = getGLCoords(x, y, screenW, screenH); 
	   fingerX = p.x;
	   fingerY = p.y;
   }
    
   public Point worldToOpenGLCoords(float x, float y, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
		float viewWidth = VIEW_HEIGHT * getAspectRatio();
	    float convertedX = (-(x - viewportX)/ viewportWidth) * viewWidth + viewWidth/2;
		float convertedY = (-(y - viewportY) / viewportHeight) * VIEW_HEIGHT + VIEW_HEIGHT/2; 
		
		Point p = new Point(convertedX, convertedY);

		return p;
   }
   
   public Point openGLToWorldCoords(float x, float y,  float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
		float viewWidth = VIEW_HEIGHT * getAspectRatio();
	    float convertedX = (-x/ viewWidth) * viewportWidth + viewportWidth/2;
		float convertedY = (-y/ VIEW_HEIGHT) * viewportHeight + viewportHeight/2; 
		
		convertedX += viewportX;
		convertedY += viewportY;
		
		Point p = new Point(convertedX, convertedY);

		return p;
   }
   
	public void onDownTouch(float x, float y, float width, float height) {
		addFingerCoords(x, y, width, height);
	}

	public void onReleaseTouch() { }

	public void onMoveTouch(float x, float y, float width, float height) {
		addFingerCoords(x, y, width, height);
	}
	
	public void onSwipe(float x1, float y1, float x2, float y2, float width, float height, float vx, float vy) {
		addFingerCoords(x2, y2, width, height);
	}

	public void onSingleTap(float x, float y, float width, float height) {
		addFingerCoords(x, y, width, height);		
	}

	public void onDoubleTap(float x, float y, float width, float height) {
		addFingerCoords(x,y,width,height);
	}
	
	public static Point getConvertWorld(float x, float y, float viewportW, float viewportH, 
		float viewWidth, float viewHeight, float viewportX, float viewportY) {
		float adjustedX = -x + (viewWidth)/2.0f; 
		float adjustedY = -y + (viewHeight)/2.0f; 
		
		int convertedX = (int)(adjustedX*(viewportW/viewWidth) + viewportX); 
		int convertedY = (int)(adjustedY*(viewportH/viewHeight) + viewportY);  
		
		return new Point(convertedX, convertedY);
	}
}
