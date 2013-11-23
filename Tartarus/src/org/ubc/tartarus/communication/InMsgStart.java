package org.ubc.tartarus.communication;

import android.util.Log;

public class InMsgStart extends IncomingMessage {
	public InMsgStart() {
		super(IncomingMessageParser.InMessageType.MSG_START.getDataLen(), 
				IncomingMessageParser.InMessageType.MSG_START.getId()); 
	}
	
	@Override
	public boolean handleMsg() {
		if (super.handleMsg()) {
			// 
			Log.i("Msg", "Received a Start message!");
			return true;
		} else {
			return false;
		}
	}
}
