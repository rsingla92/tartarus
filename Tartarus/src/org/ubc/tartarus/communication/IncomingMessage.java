package org.ubc.tartarus.communication;

public class IncomingMessage {

	public enum InMessageType {
			
		// Add any other messages here... 
		MSG_JOIN_RESPONSE((byte) 0, 1),
		MSG_START((byte) 1, 0),
		MSG_CHAR_CHOSEN((byte) 2, 2);
		
		private byte id;
		private int dataLen;
		
		private InMessageType(byte id, int dataLen) {
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
	
	public 
}
