package org.ubc.tartarus.communication;

import android.util.Log;

public class IncomingMessage {

	protected int msgLength; 
	protected byte msgID;
	protected byte[] data;
	
	public IncomingMessage() { 
		msgLength = msgID = 0;
	}
	
	public IncomingMessage(int len, byte ID) {
		msgLength = len;
		msgID = ID;
	}
	
	public int getLength() {
		return msgLength;
	}
	
	public byte getID() {
		return msgID;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void populateData(byte[] src, int src_offset, int data_len) {
		data = new byte[data_len];
		System.arraycopy(src, src_offset, data, 0, data_len);
	}
	
	public static int handleJoinResponse(IncomingMessage msg) {
		if (msg.getLength() != IncomingMessageParser.InMessageType.MSG_JOIN_RESPONSE.getDataLen()) return -1;
		
		byte[] dat = msg.getData(); 
		int response;
		
		// The Join response has one byte of data: the response (the player number from 1 to 4, or 0 on failure).
		try {
			response = dat[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
		
		return response;
	}
	
	public static boolean handleCharChosen() {
		
		return true;
	}
	
	public static boolean handleStart() {
		
		return true;
	}
}