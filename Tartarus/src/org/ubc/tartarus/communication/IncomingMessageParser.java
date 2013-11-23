package org.ubc.tartarus.communication;

public class IncomingMessageParser {

	public enum InMessageType {
		
		// Add any other messages here... 
		MSG_JOIN_RESPONSE((byte) 0, 1),
		MSG_START((byte) 1, 1),
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
	
	static IncomingMessage getMessageFromID(byte id) {
		if (id == InMessageType.MSG_JOIN_RESPONSE.id) {
			return new InMsgJoinResponse();
		} else if (id == InMessageType.MSG_START.id) {
			return new InMsgStart();
		} else if (id == InMessageType.MSG_CHAR_CHOSEN.id) {
			return new InMsgCharChosen();
		}
		
		return null;
	}
}
