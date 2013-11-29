package org.ubc.tartarus.communication;

import java.nio.ByteBuffer;

import org.ubc.tartarus.communication.OutgoingMessage.OutMessageType;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgBombPlanted extends OutgoingMessage {
	private short x, y;
	private boolean newMsg;
	
	public OutMsgBombPlanted(Activity activity) {
		super(activity);
		this.newMsg = false;
	}
	
	public void sendMessage() throws MessageTypeMismatchException {
		if (!newMsg) return; // No need to send if there is no new message ready.
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putShort(x);
		buffer.putShort(y);
		byte buf[] = buffer.array();
		Log.i("TestSocket", "Sent row: " + x + ", Sent col: + " + y);
		sendMessage(OutMessageType.MSG_BOMB_PLANTED, buf);
		newMsg = false; 
	}
	
	public void setMessage(short x, short y) {
			this.x = x;
			this.y = y;
			this.newMsg = true;
		}
}
