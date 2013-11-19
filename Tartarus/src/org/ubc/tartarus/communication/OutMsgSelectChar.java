package org.ubc.tartarus.communication;

import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;

public class OutMsgSelectChar extends OutgoingMessage {

	public OutMsgSelectChar(Activity activity) {
		super(activity);
	}

	public void sendMessage(byte charType) throws MessageTypeMismatchException {
		byte buf[] = new byte[1]; 
		buf[0] = charType; 
		sendMessage(OutMessageType.MSG_SELECT_CHAR, buf);
	}
}
