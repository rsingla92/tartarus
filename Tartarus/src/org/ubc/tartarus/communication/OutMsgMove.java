package org.ubc.tartarus.communication;

import java.nio.ByteBuffer;

import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgMove extends OutgoingMessage {

	private short storeX, storeY;
	private boolean newMsg; 
	
	public OutMsgMove(Activity context) {
		super(context);
		newMsg = false;
		storeX = storeY = 0;
	}

	public void sendMessage() throws MessageTypeMismatchException {
		if (!newMsg) return; // No need to send if there is no new message ready.
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putShort(storeX);
		buffer.putShort(storeY);
		byte buf[] = buffer.array();
		Log.i("TestSocket", "Sent X: " + storeX + ", Sent Y: + " + storeY);
		sendMessage(OutMessageType.MSG_MOVE, buf);
		newMsg = false; 
	}
	
	public void setMessage(short x, short y) {
		if (Math.abs(storeX - x) >= 4 || Math.abs(storeY - y) >= 4) {
			newMsg = true;
			storeX = x;
			storeY = y;
		}
	}
}
