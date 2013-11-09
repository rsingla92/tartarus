package org.ubc.tartarus;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class SocketComm extends AsyncTask<Void, Void, Socket> {

	@Override
	protected Socket doInBackground(Void... voids) {
		Socket sock = null;
		String ip = "169.254.138.151";
		Integer port = Integer.valueOf(50002);
		
		try {
			sock = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sock;
	}
	
	protected void onPostExecute(Socket s) {
		//MainActivity myApp = (MainActivity) MainActivity.this
	//			.getApplication();
		//myApp.sock = s;
	}
}
