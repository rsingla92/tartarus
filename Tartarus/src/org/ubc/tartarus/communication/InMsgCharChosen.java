package org.ubc.tartarus.communication;

import android.util.Log;

public class InMsgCharChosen extends IncomingMessage {
	public InMsgCharChosen() {
		super(IncomingMessageParser.InMessageType.MSG_CHAR_CHOSEN.getDataLen(), 
				IncomingMessageParser.InMessageType.MSG_CHAR_CHOSEN.getId()); 
	}
	
	@Override
	public boolean handleMsg() {
		if (super.handleMsg()) {
			// 
			Log.i("Msg", "Received a Char Chosen message!");
			return true;
		} else {
			return false;
		}
	}
}
