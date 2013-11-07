package org.ubc.tartarus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;

public class MapParser {

	public static class TileMap {
		public int worldWidth, worldHeight;
		public int[][] tiles;
		
		public TileMap(int width, int height, int[][] tiles) {
			worldWidth = width;
			worldHeight = height;
			
			this.tiles = new int[height][width]; 
			
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					this.tiles[i][j] = tiles[i][j];
				}
			}
		}
		
		public TileMap() {
			worldWidth = worldHeight = 0;
			tiles = null;
		}
	}
	
	//TODO: Replace Scanner -- this is very slow in Android!
	public static TileMap readMapFromFile(final Context context, final int resId) {
		InputStreamReader inStream = new InputStreamReader(context.getResources().openRawResource(resId));
		Scanner scan = new Scanner(new BufferedReader(inStream));
		
		int worldWidth = 0, worldHeight = 0;
		int[][] tiles = null;
		int curRow = 0, curCol = 0; 
		
		try {
			worldWidth = Integer.parseInt(scan.next());
			worldHeight = Integer.parseInt(scan.next());
			
			//Log.i("MapParser", "World Width: " + worldWidth + ", World Height: " + worldHeight);
			tiles = new int[worldHeight][worldWidth];
			
			while (scan.hasNext()) {
				tiles[curRow][curCol] = Integer.parseInt(scan.next()); 
				
				scan.next(); //For now: Discarding the 'solid' int. 
				
				//Log.i("MapParser", "Received: " + tiles[curRow][curCol]);
				
				if (++curCol >= worldWidth) {
					curCol = 0;
					curRow++;
				}
			}
		} catch (Exception e) {
			Log.e("MapParser", "Cannot parse map: " + e.getMessage());
			return null;
		}
		
		scan.close();
		return new TileMap(worldWidth, worldHeight, tiles);
	}
}
