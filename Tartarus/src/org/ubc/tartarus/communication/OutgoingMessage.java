package org.ubc.tartarus.communication;

import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutgoingMessage {

	public enum OutMessageType {
		
		// Add any other messages here... 
		MSG_MOVE ((byte) 2, 4);
		
		
		private byte id;
		private int dataLen;
		
		private OutMessageType(byte id, int dataLen) {
			this.id = id;
			this.dataLen = dataLen;
		}
		
		public byte getId() {
			return id;
		}
		
		public int getDataLen() {
			return dataLen;
		}
	}
	
	private Activity mActivity;
	
	public OutgoingMessage(Activity activity) {
		mActivity = activity;
	}
	
	public void sendMessage(OutMessageType msgType, byte[] dat) throws MessageTypeMismatchException {
		ApplicationData app = (ApplicationData) mActivity.getApplication();
		
		byte[] data = null;
		if (dat.length != msgType.getDataLen()) {
			throw new MessageTypeMismatchException("OutgoingMessage");
		}
		
	    data = new byte[dat.length + 2]; 
		data[0] = (byte) (dat.length + 1); // The type and data
		data[1] = msgType.getId();
		System.arraycopy(dat, 0, data, 2, dat.length);
		
		if (app.socketComm != null) {
			app.socketComm.sendMessage(data);
		} else {
			Log.i("TestSocket", "Socket Comm is null!");
		}
	}
}
