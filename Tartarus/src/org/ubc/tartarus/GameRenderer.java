package org.ubc.tartarus;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class GameRenderer implements Renderer {

	public static final int MATRIX_SIZE = 4;
	
	public volatile float mAngle;
	
	private BitmapImg menuBackground;
	private float[] mProjectionMatrix;
	private float[] mViewMatrix;
	private float[] mModelViewMatrix;
	
	private Context mContext;
		
	private ParticleSystem mParticleSystem;
	
	private float fingerX, fingerY; 
	
	private float mAspectRatio;
	
	//Test:
	private Particle p;
	
	public GameRenderer(Context context) {
		super();
		
		mContext = context;
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	    
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		
		Matrix.multiplyMM(mModelViewMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		
		// Scale and rotate the background 
		float[] copyMat = new float[16];
		Matrix.setIdentityM(copyMat, 0);
		Matrix.scaleM(copyMat, 0, 2 * mAspectRatio, 2, 2);
		Matrix.multiplyMM(copyMat, 0, mModelViewMatrix, 0, copyMat.clone(), 0);
		menuBackground.draw(copyMat);
		
		p.setParticlePosition(fingerX, fingerY, 0);
		p.drawParticle(mModelViewMatrix);
		mParticleSystem.updateParticleSystem(fingerX, fingerY, 0, mAspectRatio);
		mParticleSystem.drawParticles(mModelViewMatrix);
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
		menuBackground = new BitmapImg(mContext, R.drawable.img_tartarus_menu);
		mProjectionMatrix = new float[MATRIX_SIZE*MATRIX_SIZE];
		mViewMatrix = new float[MATRIX_SIZE*MATRIX_SIZE];
		mModelViewMatrix = new float[MATRIX_SIZE*MATRIX_SIZE]; 
		fingerX = fingerY = 0.0f;
		
		p = new Particle(mContext, R.drawable.particle, 0, 0, 0,
				0.6f, 0.6f, 1.0f, 1.0f, 0.2f, 0.2f, false, 0);
		p.setParticleSpeed(0, 0, 0);
		
		float stagnantColourList[][] = {
			{1.0f, 1.0f, 1.0f, 1.0f},
			{0.8f, 0.8f, 0.9f, 1.0f},
			{0.8f, 0.7f, 0.9f, 1.0f},
			{0.9f, 0.9f, 0.7f, 1.0f},
			{0.8f, 0.8f, 0.7f, 1.0f},
			{1.0f, 1.0f, 0.0f, 1.0f},
			{0.8f, 0.7f, 0.2f, 1.0f},
		};
		
	//	mParticleSystem = new ParticleSystem(mContext, 100, R.drawable.particle, 10, Type.STAGNANT, stagnantColourList);
		
		mParticleSystem = new ParticleSystem(mContext, 100, R.drawable.particle, 10);
		
		// Enable alpha blending and depth test.
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDepthMask(false);
		
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
	public void addFingerCoords( float x, float y, float screenW, float screenH)
	   { 
	       // Auxiliary matrix and vectors
	       // to deal with ogl.
	       float[] invertedMatrix, transformMatrix,
	           normalizedInPoint, outPoint;
	       invertedMatrix = new float[16];
	       transformMatrix = new float[16];
	       normalizedInPoint = new float[4];
	       outPoint = new float[4];

	       // Invert y coordinate, as android uses
	       // top-left, and ogl bottom-left.
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
	           // Avoid /0 error.
	           Log.e("World coords", "ERROR!");
	       }

	       fingerX = outPoint[0] / outPoint[3];
	       fingerY = outPoint[1] / outPoint[3];
	   }
	       
	/*private void addFingerCoords(float x, float y, float width, float height) {
		if (width == 0 || height == 0) return;
		
		float glX = ((-2.0f / width) * x) + 1.0f;
		float glY = ((-2.0f / height) * y) + 1.0f;
		fingerX = glX;
		fingerY = glY;
	}
	*/
	public void onDownTouch(float x, float y, float width, float height) {
		if (mParticleSystem != null) {
			mParticleSystem.beginSpawning();
		}
		
		addFingerCoords(x, y, width, height);
		Log.i("Renderer", "Finger X: " + String.valueOf(fingerX) + ", Finger Y: " + String.valueOf(fingerY));
	}

	public void onReleaseTouch() {
		if (mParticleSystem != null) {
			mParticleSystem.endSpawning();	
		}
	}

	public void onMoveTouch(float x, float y, float width, float height) {
		addFingerCoords(x, y, width, height);
	}

}