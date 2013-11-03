package org.ubc.tartarus;

import android.content.Context;

public class Map {

	private BitmapImg mTileset;
	private byte[][] mTilemap;
	private int mWorldHeight, mWorldWidth;
	private int mTileWidth, mTileHeight;
	private int mTilePadding;
	private int mNumTilesPerRow;
	private int mViewportWidth, mViewportHeight;
	private int mViewportTilesWidth, mViewportTilesHeight;
	
	public Map() {
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
	}
	
	public Map(final Context context, final int tileSetResId, int tilePadding, int numTilesPerRow, 
			int tileWidth, int tileHeight, int viewportWidth, int viewportHeight) {
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
	}
	
	public void loadTileMap(byte[][] tileMap, int worldWidth, int worldHeight) {
		mWorldWidth = worldWidth;
		mWorldHeight = worldHeight;
		
		mTilemap = new byte[worldWidth][worldHeight];
		
		for (int i = 0; i < worldWidth; i++) {
			for (int j = 0; j < worldHeight; j++) {
				mTilemap[i][j] = tileMap[i][j];
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
	public void setViewport(int viewportWidth, int viewportHeight) {
		mViewportWidth = viewportWidth;
		mViewportHeight = viewportHeight;	
		mViewportTilesWidth = mViewportWidth / mTileWidth;
		mViewportTilesHeight = mViewportHeight / mTileHeight;
	}
	
	private void pluckTile(byte tileId) {
		int row = tileId / mNumTilesPerRow;
		int col = tileId % mNumTilesPerRow;
		int xPadding = mTilePadding * (row + 1);
		int yPadding = mTilePadding * (col + 1); 
		Point bottomLeft = new Point(col * mTileWidth + xPadding, row * mTileHeight + yPadding + mTileHeight);
		Point topRight = new Point(bottomLeft.x + mTileWidth, bottomLeft.y - mTileHeight);
		bottomLeft.y = mTileset.getHeight() - bottomLeft.y;
		topRight.y = mTileset.getHeight() - topRight.y;
		
		mTileset.setTexturePortion(bottomLeft, topRight);
	}
}
