package org.ubc.tartarus.communication;

import org.ubc.tartarus.communication.OutgoingMessage.OutMessageType;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutMsgGemAck extends OutgoingMessage {

	public OutMsgGemAck(Activity activity) {
		super(activity);
	}
	
	public void sendMessage() throws MessageTypeMismatchException {
		Log.i("TestSocket", "Sent Gem Ack");
		sendMessage(OutMessageType.MSG_GEM_ACK, null);
	}
}
