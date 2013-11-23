package org.ubc.tartarus.communication;

import org.ubc.tartarus.communication.OutgoingMessage.OutMessageType;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgDisconnect extends OutgoingMessage {

	public OutMsgDisconnect(Activity context) {
		super(context);
	}

	public void sendMessage() throws MessageTypeMismatchException {
		Log.i("TestSocket", "Sent Disconnect");
		sendMessage(OutMessageType.MSG_DISCONNECT, null);
	}
}
