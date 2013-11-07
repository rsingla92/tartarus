package org.ubc.tartarus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class BitmapImg {

	static final int BYTES_PER_FLOAT = 4;
	static final int BYTES_PER_SHORT = 2;
	static final int COORDS_PER_VERTEX = 3;
	
	private ShortBuffer drawOrderBuf; 
	private FloatBuffer vertexBuffer; 
	private FloatBuffer mTexCoordinates;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	 
	/** This will be used to pass in the texture. */
	private int mTexUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTexCoordHandle;
	 
	/** Size of the texture coordinate data in elements. */
	//private final int mTexNumCoords = 2;
	 
	/** This is a handle to our texture data. */
	private int mTexDataHandle;
	
	static float vertices[] = { -0.5f,  0.5f, 0.0f, // Top-left corner of square
								 0.5f,  0.5f, 0.0f, // Top-right corner
								-0.5f, -0.5f, 0.0f, // Bottom-left corner of square
								 0.5f, -0.5f, 0.0f }; // Bottom-right corner of square 
	
	private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; 
	
    private float[] mTexCoordinateData =
		{
			1.0f, 0.0f, //bottom-right
	       	0.0f, 0.0f, //Bottom-left
        	1.0f, 1.0f,  // top-right
        	0.0f, 1.0f, //top-left
		};
	
	private float colour[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	
	private static final String vertexShaderCode =
		    "attribute vec4 vPosition;" +
		    "uniform mat4 uMVPMatrix;" +
		    "attribute vec2 aTextureCoordinates;" +
		    "varying vec2 vTextureCoordinates;" +
		    "void main() {" +
		    "  gl_Position = uMVPMatrix * vPosition;" +
		    "  vTextureCoordinates = aTextureCoordinates;" +
		    "}";

	private static final String fragmentShaderCode =
		    "precision mediump float;" +
		    "uniform sampler2D uTex;" +
		    "uniform vec4 vColor;" +
		    "varying vec2 vTextureCoordinates;" +
		    "void main() {" +
		    "  gl_FragColor = vColor * texture2D(uTex, vTextureCoordinates);" +
		    "}";
	
	private final int vertexStride = COORDS_PER_VERTEX * 4;
//	private final int vertexCount = vertices.length / COORDS_PER_VERTEX;
	private static int mProgram;
	private static boolean shadersInitialized = false;
	
	public BitmapImg(final Context context, final int resId, float r, float g, float b, float a) {
		this(context, resId);
		
		colour[0] = r;
		colour[1] = g;
		colour[2] = b;
		colour[3] = a;
	}
	
	public BitmapImg(final Context context, final int resId) {
		ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// Now set the byte buffer for the draw list.
		ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()); 
		drawOrderBuf = dlb.asShortBuffer();
		drawOrderBuf.put(drawOrder);
		drawOrderBuf.position(0);
		
		ByteBuffer tlb = ByteBuffer.allocateDirect(mTexCoordinateData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());
		mTexCoordinates = tlb.asFloatBuffer();
		mTexCoordinates.put(mTexCoordinateData);
		mTexCoordinates.position(0);
		
		// Load shaders
/*		int vertexShader = OrpheusRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	    int fragmentShader = OrpheusRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

	    mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
	    */
	    loadImage(context, resId);
	}
	
	public void setTextureCoordinates(Point bottomLeft, Point bottomRight, Point topRight, Point topLeft) {
	    mTexCoordinateData[0] = bottomRight.x; 
	    mTexCoordinateData[1] = bottomRight.y;
	    mTexCoordinateData[2] = bottomLeft.x;
	    mTexCoordinateData[3] = bottomLeft.y;
	    mTexCoordinateData[4] = topRight.x;
	    mTexCoordinateData[5] = topRight.y;
	    mTexCoordinateData[6] = topLeft.x;
	    mTexCoordinateData[7] = topLeft.y;
	}
	
	public void setTexturePortion(Point topLeft, Point bottomRight) {
		
	}
	
	public static void loadBitmapShaders() {
		// Load shaders
		int vertexShader = GameRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	    int fragmentShader = GameRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

	    mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
	    
	    shadersInitialized = true;
	}
	
	public void loadImage(final Context context, final int resId)
	{
	    final int[] textureHandle = new int[1];
	 
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
	 
	        if (bitmap == null) {
	        	Log.i("BitmapImg", "Could not decode resource!");
	        } 
	        
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    mTexDataHandle = textureHandle[0];
	}
	
	public void draw(float[] modelViewMatrix) {
	    // Add program to OpenGL ES environment
		if (!shadersInitialized) return;
		
	    GLES20.glUseProgram(mProgram);

	    // get handle to vertex shader's vPosition member
	    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
	    mTexUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTex");
	    mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinates");
	    
	    // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	 
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexDataHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(mTexUniformHandle, 0);
	    
	    // Enable a handle to the triangle vertices
	    GLES20.glEnableVertexAttribArray(mPositionHandle);

	    // Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
	                                 GLES20.GL_FLOAT, false,
	                                 vertexStride, vertexBuffer);

	    GLES20.glEnableVertexAttribArray(mTexCoordHandle);
	    
	    GLES20.glVertexAttribPointer(mTexCoordHandle, 2, 
	    							 GLES20.GL_FLOAT, false, 
	    							 0, mTexCoordinates);
	    
	    // get handle to fragment shader's vColor member
	    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

	    // Set color for drawing the triangle
	    GLES20.glUniform4fv(mColorHandle, 1, colour, 0);

	    // get handle to shape's transformation matrix
	    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	    
	    // Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, modelViewMatrix, 0);

	    // Draw the triangle
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

	    // Disable vertex array
	    GLES20.glDisableVertexAttribArray(mPositionHandle);
	    GLES20.glDisableVertexAttribArray(mTexCoordHandle);
	}
}
