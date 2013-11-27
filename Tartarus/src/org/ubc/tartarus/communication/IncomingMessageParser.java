package org.ubc.tartarus.communication;

import android.util.Log;

public class IncomingMessageParser {

	public enum InMessageType {
		
		// Add any other messages here... 
		MSG_JOIN_RESPONSE((byte) 0, 1),
		MSG_START((byte) 1, 1),
		MSG_CHAR_CHOSEN((byte) 2, 2),
		MSG_GEM((byte) 3, -1),
		MSG_UPDATE_GEM((byte) 4, 9),
		MSG_GAME_OVER((byte)5, 12),
		PLAYER_POS_MSG((byte)6, 4);
		
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
			return new IncomingMessage(InMessageType.MSG_JOIN_RESPONSE.getDataLen(), InMessageType.MSG_JOIN_RESPONSE.getId());
		} else if (id == InMessageType.MSG_START.id) {
			return new IncomingMessage(InMessageType.MSG_START.getDataLen(), InMessageType.MSG_START.getId());
		} else if (id == InMessageType.MSG_CHAR_CHOSEN.id) {
			return new IncomingMessage(InMessageType.MSG_CHAR_CHOSEN.getDataLen(), InMessageType.MSG_CHAR_CHOSEN.getId());
		} else if (id == InMessageType.MSG_GEM.id) {
			Log.i("Msg", "Got the Gem message!");
			return new IncomingMessage(InMessageType.MSG_GEM.getDataLen(), InMessageType.MSG_GEM.getId());
		} else if (id == InMessageType.MSG_UPDATE_GEM.id) {
			return new IncomingMessage(InMessageType.MSG_UPDATE_GEM.getDataLen(), InMessageType.MSG_UPDATE_GEM.getId());
		} else if (id == InMessageType.MSG_GAME_OVER.id) {
			return new IncomingMessage(InMessageType.MSG_GAME_OVER.getDataLen(), InMessageType.MSG_GAME_OVER.getId());
		} else if (id == InMessageType.PLAYER_POS_MSG.id) {
			return new IncomingMessage(InMessageType.PLAYER_POS_MSG.getDataLen(), InMessageType.PLAYER_POS_MSG.getId());
		}
		
		Log.i("Msg", "Invalid message received.");
		return null;
	}
}
