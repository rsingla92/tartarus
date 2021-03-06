package org.ubc.tartarus.communication;

import java.nio.ByteBuffer;

import org.ubc.tartarus.communication.OutgoingMessage.OutMessageType;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgGemPicked extends OutgoingMessage {
	private short row, column;
	private boolean newMsg;
	
	public OutMsgGemPicked(Activity context) {
		super(context);
		newMsg = false;
	}
	
	public void sendMessage() throws MessageTypeMismatchException {
		if (!newMsg) return; // No need to send if there is no new message ready.
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putShort(column);
		buffer.putShort(row);
		byte buf[] = buffer.array();
		Log.i("TestSocket", "Sent row: " + row + ", Sent col: + " + column);
		sendMessage(OutMessageType.MSG_GEM_PICKED, buf);
		newMsg = false; 
	}
	
	public void setMessage(short row, short col) {
			this.row = row;
			this.column = col;
			this.newMsg = true;
		}
	}
