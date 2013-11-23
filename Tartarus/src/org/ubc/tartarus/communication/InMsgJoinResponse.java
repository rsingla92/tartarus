package org.ubc.tartarus.communication;

import android.util.Log;

public class InMsgJoinResponse extends IncomingMessage {

	public InMsgJoinResponse() {
		super(IncomingMessageParser.InMessageType.MSG_JOIN_RESPONSE.getDataLen(), 
				IncomingMessageParser.InMessageType.MSG_JOIN_RESPONSE.getId()); 
	}
	
	@Override
	public boolean handleMsg() {
		if (super.handleMsg()) {
			// 
			Log.i("Msg", "Received a Join message!");
			return true;
		} else {
			return false;
		}
	}
}
