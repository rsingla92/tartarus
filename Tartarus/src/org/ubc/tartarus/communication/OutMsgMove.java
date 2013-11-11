package org.ubc.tartarus.communication;

import java.nio.ByteBuffer;

import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgMove extends OutgoingMessage {

	public OutMsgMove(Activity context) {
		super(context);
	}

	public void sendMessage(short x, short y) throws MessageTypeMismatchException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putShort(x);
		buffer.putShort(y);
		byte buf[] = buffer.array();
		Log.i("TestSocket", "Sent X: " + x + ", Sent Y: + " + y);
		sendMessage(OutMessageType.MSG_MOVE, buf);
	}
}
