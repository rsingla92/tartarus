package org.ubc.tartarus.communication;

import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgJoin extends OutgoingMessage {

	public OutMsgJoin(Activity context) {
		super(context);
	}

	public void sendMessage() throws MessageTypeMismatchException {
		Log.i("TestSocket", "Sent Join");
		sendMessage(OutMessageType.MSG_JOIN, null);
	}
}
