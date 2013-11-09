package org.ubc.tartarus;

import java.net.Socket;

import android.app.Application;

public class ApplicationData extends Application {
	public Socket sock = null;
	public SocketComm socketComm = null;
}
