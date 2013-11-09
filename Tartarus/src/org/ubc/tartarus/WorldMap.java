package org.ubc.tartarus;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class WorldMap {

	private BitmapImg mTileset;
	private int[][] mTilemap;
	private int mWorldHeight, mWorldWidth;
	private int mTileWidth, mTileHeight;
	private int mTilePadding;
	private int mNumTilesPerRow;
	private int mViewportWidth, mViewportHeight;
	private int mViewportTilesWidth, mViewportTilesHeight;
	private int mViewportX, mViewportY;
	
	public WorldMap() {
		mTileset = null;
		mTilemap = null;
		mWorldHeight = mWorldWidth = 0;
		mTilePadding = 0;
		mNumTilesPerRow = 0;
		mTileWidth = 0;
		mTileHeight = 0;
		mViewportWidth = 0;
		mViewportHeight = 0;
		mViewportTilesWidth = 0;
		mViewportTilesHeight = 0;
		mViewportX = mViewportY = 0;
	}
	
	public WorldMap(final Context context, final int tileSetResId, int tilePadding, int numTilesPerRow, 
			int tileWidth, int tileHeight, int viewportWidth, int viewportHeight, int viewportX, int viewportY) {
		mTileset = new BitmapImg(context, tileSetResId);
		mTilemap = null;
		mWorldHeight = mWorldWidth = 0;
		mTilePadding = tilePadding;
		mNumTilesPerRow = numTilesPerRow;
		mTileWidth = tileWidth;
		mTileHeight = tileHeight;
		mViewportWidth = viewportWidth;
		mViewportHeight = viewportHeight;
		mViewportTilesWidth = mViewportWidth / mTileWidth;
		mViewportTilesHeight = mViewportHeight / mTileHeight;
		mViewportX = viewportX;
		mViewportY = viewportY;
	}
	
	public void loadTileMap(int[][] tileMap, int worldWidth, int worldHeight) {
		mWorldWidth = worldWidth;
		mWorldHeight = worldHeight;
		
		mTilemap = new int[worldHeight][worldWidth];
		
		for (int i = 0; i < worldWidth; i++) {
			for (int j = 0; j < worldHeight; j++) {
				mTilemap[j][i] = tileMap[j][i];
			}
		}
	}
	
	public void loadTileset(final Context context, final int tileSetResId, int tilePadding, int tileWidth, int tileHeight) {
		mTileset = new BitmapImg(context, tileSetResId);
		mTilePadding = tilePadding;
		mTileWidth = tileWidth;
		mTileHeight = tileHeight;
	}
	
	// Sets the viewport width and height (in pixels) 
	// Note that this is set in pixels (just like the tile width/height)
	// It will be mapped to the opengl coordinate system.
	// The x and y coordinates are relative to the world (in pixels)
	public void setViewport(int viewportWidth, int viewportHeight, int viewportX, int viewportY) {
		mViewportWidth = viewportWidth;
		mViewportHeight = viewportHeight;	
		mViewportTilesWidth = mViewportWidth / mTileWidth;
		mViewportTilesHeight = mViewportHeight / mTileHeight;
		mViewportX = viewportX;
	}
	
	public void setViewportPosition(int viewportX, int viewportY) {
		mViewportX = viewportX;
		mViewportY = viewportY;
	}
	
	public float getViewportX() {
		return mViewportX;
	}
	
	public float getViewportY() {
		return mViewportY;
	}
	
	public void shiftViewport(float shiftX, float shiftY) {
		mViewportX += shiftX;
		mViewportY += shiftY; 

		if (mViewportX <= 0) mViewportX = 0;
		if (mViewportY <= 0) mViewportY = 0;
		if (mViewportX >= mWorldWidth*mTileWidth - mViewportWidth) mViewportX = mWorldWidth*mTileWidth - mViewportWidth;
		if (mViewportY >= mWorldHeight*mTileHeight - mViewportHeight) mViewportY = mWorldHeight*mTileHeight - mViewportHeight;
	}
	
	public int atViewportXBoundary() {
		if (mViewportX <= 0) return -1;
		if (mViewportX + mViewportWidth >= mWorldWidth * mTileWidth) return 1;
		
		return 0;
	}

	public int getViewportWidth() {
		return mViewportWidth;
	}
	
	public int getViewportHeight() {
		return mViewportHeight;
	}
	
	public int atViewportYBoundary() {
		if (mViewportY <= 0) return -1;
		if (mViewportY + mViewportHeight >= mWorldHeight * mTileHeight) return 1;
		
		return 0;
	}
	
	public void drawViewport(float[] modelViewMatrix, float viewWidth, float viewHeight) {
		int col0 = mViewportX / mTileWidth;
		int row0 = mViewportY / mTileHeight;
		int x0 = col0 * mTileWidth - mViewportX; 
		int y0 = row0 * mTileHeight - mViewportY;
		int x1 = x0 + mViewportWidth;
		int y1 = y0 + mViewportHeight;
		
		if (x1 > mWorldWidth * mTileWidth) x1 = mWorldWidth * mTileWidth;
		if (y1 > mWorldHeight * mTileHeight) y1 = mWorldHeight * mTileHeight;
		
		float tileWidthInView, tileHeightInView;
		float[] modelMat = new float[16];
		float[] scaleMat = new float[16];
		
		
		tileWidthInView = ((float) mTileWidth / mViewportWidth) * viewWidth;
		tileHeightInView = ((float) mTileHeight / mViewportHeight) * viewHeight;
		
		for (int i = x0; i <= x1; i += mTileWidth) {
			for (int j = y0; j <= y1; j += mTileHeight) {
				int col = (i + mViewportX) / mTileWidth;
				int row = (j + mViewportY) / mTileHeight;
				float translateX, translateY; 

				if (row >= mWorldWidth) continue;
				if (col >= mWorldHeight) break;
				
				pluckTile(mTilemap[row][col]);
				translateX = (viewWidth / 2) - (((float)i) / mViewportWidth) * viewWidth;
				translateY = (viewHeight / 2) - (((float)j) / mViewportHeight) * viewHeight;
				
				translateX -= tileWidthInView / 2.0f; 
				translateY -= tileHeightInView / 2.0f;
				
				Matrix.setIdentityM(modelMat, 0);
				Matrix.setIdentityM(scaleMat, 0);
				Matrix.translateM(modelMat, 0, translateX, translateY, 1);

				Matrix.scaleM(scaleMat, 0, tileWidthInView, tileHeightInView, 1);
				Matrix.multiplyMM(modelMat, 0, modelMat.clone(), 0, scaleMat, 0);
				Matrix.multiplyMM(modelMat, 0, modelViewMatrix, 0, modelMat.clone(), 0);

		     	mTileset.draw(modelMat);
			}
		}
	}
	
	private void pluckTile(int tileId) {
		int row = tileId / mNumTilesPerRow;
		int col = tileId % mNumTilesPerRow;
		int xPadding = mTilePadding * (col + 1);
		int yPadding = mTilePadding * (row + 1); 
		Point bottomLeft = new Point(col * mTileWidth + xPadding, row * mTileHeight + yPadding + mTileHeight);
		Point topRight = new Point(bottomLeft.x + mTileWidth, bottomLeft.y - mTileHeight);
		bottomLeft.y = mTileset.getHeight() - bottomLeft.y;
		topRight.y = mTileset.getHeight() - topRight.y;
		
		mTileset.setTexturePortion(bottomLeft, topRight);
	}
}
