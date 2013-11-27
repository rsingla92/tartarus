package org.ubc.tartarus;

import java.net.Socket;
import java.util.ArrayList;

import org.ubc.tartarus.character.Gem;
import org.ubc.tartarus.communication.SocketComm;

import android.app.Application;

public class ApplicationData extends Application {
	public Socket sock = null;
	public SocketComm socketComm = null;
	public ArrayList<Gem> gemList = null;
	public int playerId = -1;
}
