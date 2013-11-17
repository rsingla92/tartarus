package org.ubc.tartarus.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.TimerTask;

import org.ubc.tartarus.ApplicationData;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class SocketComm {

	private ApplicationData mAppData;
	
	public SocketComm(ApplicationData appData) {
		mAppData = appData;
	}
	
	public void openSocket() {

		// Make sure the socket is not already opened 		
		if (mAppData.sock != null && mAppData.sock.isConnected() && !mAppData.sock.isClosed()) {
			return;
		}
		
		// open the socket.  SocketConnect is a new subclass
	    // (defined below).  This creates an instance of the subclass
		// and executes the code in it.
		
		Log.i("TestSocket", "Creating socket connect to run...\n");
		new SocketConnect().execute((Void) null);
	}
	
	public void closeSocket() {
		Socket s = mAppData.sock;
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Send a raw message in bytes.
	public void sendMessage(byte data[]) {
		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		byte buf[]; 
		
	    buf = new byte[data.length + 1];
		buf[0] = (byte) data.length; 
		System.arraycopy(data, 0, buf, 1, data.length);
		
		// Now send through the output stream of the socket
		
		OutputStream out = null;
		try {
			if (mAppData.sock == null) {
				Log.i("TestSocket", "Sock is null!");
			} else {
				out = mAppData.sock.getOutputStream();
			}
			
			try {
				out.write(buf, 0, data.length + 1);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				// Do nothing..
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

		@Override
		protected Socket doInBackground(Void... voids) {
			Socket sock = null;
			String ip = "192.168.1.132";
			
			Log.i("TestSocket", "Trying to connect!");
			try {
				sock = new Socket(ip, 50002);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (sock == null) {
				Log.i("TestSocket", "Could not connect... socket null!");
			}
			
			Log.i("TestSocket", "Connected!");
			return sock;
		}
		
		protected void onPostExecute(Socket s) {
			mAppData.sock = s;
		}
	}
	
	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			if (mAppData.sock != null && mAppData.sock.isConnected()
					&& !mAppData.sock.isClosed()) {
				
				try {
					InputStream in = mAppData.sock.getInputStream();

					// See if any bytes are available from the Middleman
					
					int bytes_avail = in.available();
					if (bytes_avail > 0) {
						
						// If so, read them in and create a sring
						
						byte buf[] = new byte[bytes_avail];
						in.read(buf);

						// buf contains data
						if (buf[1] == OutgoingMessage.OutMessageType.MSG_MOVE.getId()) {
							ByteBuffer buffer = ByteBuffer.wrap(buf, 2, buf.length - 2);
							short x = buffer.getShort();
							short y = buffer.getShort();
							Log.i("TestSocket", "Received X: " + x + ", Received Y: + " + y);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
