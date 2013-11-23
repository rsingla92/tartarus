package org.ubc.tartarus.communication;

import android.util.Log;

public class IncomingMessage {

	protected int msgLength; 
	protected byte msgID;
	protected byte[] data;
	
	public IncomingMessage() { 
		msgLength = msgID = 0;
	}
	
	protected IncomingMessage(int len, byte ID) {
		msgLength = len;
		msgID = ID;
	}
	
	public int getLength() {
		return msgLength;
	}
	
	public byte getID() {
		return msgID;
	}
	
	public void populateData(byte[] src, int src_offset, int data_len) {
		data = new byte[data_len];
		System.arraycopy(src, src_offset, data, 0, data_len);
	}
	
	public boolean handleMsg() {
		if (data == null || data.length != msgLength) {
			Log.e("Msg", "Message length incorrect.");
			return false;
		}
		
		return true;
	}
}