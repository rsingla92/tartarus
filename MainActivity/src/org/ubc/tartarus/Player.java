package org.ubc.tartarus;

import android.content.Context;

public class Player {

	private BitmapImg mPlayerImg; 
	private Point mPosition;
	private int speed; 
	
	public Player(final Context context, final int resId, int x, int y) {
		mPlayerImg = new BitmapImg(context, resId);
		mPosition.x = x;
		mPosition.y = y;
	}
	
	public Player(final Context context, final int resId, Point pos) {
		mPlayerImg = new BitmapImg(context, resId);
		mPosition.x = pos.x;
		mPosition.y = pos.y; 
	}
	
	public void drawPlayer() {
		
	}
	
}
