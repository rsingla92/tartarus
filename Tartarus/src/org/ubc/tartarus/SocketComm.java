package org.ubc.tartarus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

public class SocketComm {

	private Activity mActivity;
	
	public SocketComm(Activity activity) {
		mActivity = activity;
	}
	
	public void openSocket(View view) {
		ApplicationData app = (ApplicationData) mActivity.getApplication();
		
		// Make sure the socket is not already opened 		
		if (app.sock != null && app.sock.isConnected() && !app.sock.isClosed()) {
			return;
		}
		
		// open the socket.  SocketConnect is a new subclass
	    // (defined below).  This creates an instance of the subclass
		// and executes the code in it.
		
		new SocketConnect().execute((Void) null);
	}
	
	public void closeSocket(View view) {
		ApplicationData app = (ApplicationData) mActivity.getApplication();
		Socket s = app.sock;
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Send a raw message in bytes.
	public void sendMessage(View view, byte data[]) {
		ApplicationData app = (ApplicationData) mActivity.getApplication();
		
		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		
		byte buf[] = new byte[data.length + 1];
		buf[0] = (byte) data.length; 
		System.arraycopy(data, 0, buf, 1, data.length);

		// Now send through the output stream of the socket
		
		OutputStream out;
		try {
			out = app.sock.getOutputStream();
			try {
				out.write(buf, 0, data.length + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

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
			ApplicationData app = (ApplicationData) mActivity.getApplication();
			app.sock = s;
		}
	}
	
	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			ApplicationData app = (ApplicationData) mActivity.getApplication();
			if (app.sock != null && app.sock.isConnected()
					&& !app.sock.isClosed()) {
				
				try {
					InputStream in = app.sock.getInputStream();

					// See if any bytes are available from the Middleman
					
					int bytes_avail = in.available();
					if (bytes_avail > 0) {
						
						// If so, read them in and create a sring
						
						byte buf[] = new byte[bytes_avail];
						in.read(buf);

						// buf contains data
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
