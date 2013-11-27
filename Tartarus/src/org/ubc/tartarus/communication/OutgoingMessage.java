package org.ubc.tartarus.communication;

import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.exceptions.MessageTypeMismatchException;

import android.app.Activity;
import android.util.Log;

public class OutgoingMessage {

	public enum OutMessageType {
		
		// Add any other messages here... 
		MSG_JOIN((byte) 0, 0),
		MSG_READY((byte) 1, 0),
		MSG_MOVE((byte) 2, 4),
		MSG_SELECT_CHAR((byte) 3, 1),
		MSG_DISCONNECT((byte) 4, 0),
		MSG_GEM_ACK((byte) 5, 0),
		MSG_GEM_PICKED((byte) 6, 4);
		
		
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
		
		if (dat == null) {
			data = new byte[3];
			data[0] = (byte) 0; 
			data[1] = (byte) 1;
			data[2] = msgType.getId();
		} else {
			data = new byte[dat.length + 3];
			int msgLen = dat.length + 1;
			data[0] = (byte) ((msgLen >> 8) & 0x00FF); // MSB of length
			data[1] = (byte) (msgLen & 0x00FF); // LSB of lenth
			data[2] = msgType.getId();
		}
		
		if (dat != null) {
			if (dat.length != msgType.getDataLen()) {
				throw new MessageTypeMismatchException("OutgoingMessage");
			}
			
			System.arraycopy(dat, 0, data, 3, dat.length);
		}
		
		if (app.socketComm != null) {
			app.socketComm.sendMessage(data);
		} else {
			Log.i("TestSocket", "Socket Comm is null!");
		}
	}
}
