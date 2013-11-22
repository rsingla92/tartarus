package org.ubc.tartarus.communication;

import android.util.Log;

public class IncomingMessage {

	protected int msgLength; 
	protected int msgID;
	
	public IncomingMessage() { 
		msgLength = msgID = 0;
	}
	
	public IncomingMessage(int len, int ID) {
		msgLength = len;
		msgID = ID;
	}
	
	public int getLength() {
		return msgLength;
	}
	
	public int getID() {
		return msgID;
	}
	
	public int handleMsg(byte[] buf) {
		if (buf.length != msgLength) {
			Log.e("IncomingMessage", "Message length incorrect.");
			return 0;
		}
		
		return 1;
	}
}