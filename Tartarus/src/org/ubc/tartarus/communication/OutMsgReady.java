package org.ubc.tartarus.communication;

import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgReady extends OutgoingMessage {

	public OutMsgReady(Activity activity) {
		super(activity);
	}

	public void sendMessage() throws MessageTypeMismatchException {
		Log.i("TestSocket", "Sent Ready");
		sendMessage(OutMessageType.MSG_READY, null);
	}
}
