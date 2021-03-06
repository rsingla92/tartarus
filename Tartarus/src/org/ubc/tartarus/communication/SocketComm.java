package org.ubc.tartarus.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TimerTask;
import org.ubc.tartarus.ApplicationData;
import org.ubc.tartarus.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SocketComm {

	private ApplicationData mAppData;
	private Handler socketMsgHandler;
	private LinkedList<IncomingMessage> msgList; 
	private String ip;
	private Integer port;
	private Activity socketActivity;
	
	public SocketComm(ApplicationData appData, Activity activity) {
		mAppData = appData;
		msgList = new LinkedList<IncomingMessage>();
		socketActivity = activity;
		
		socketMsgHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				IncomingMessage incoming = (IncomingMessage) msg.obj;
				Log.i("Msg", "Received msg with ID " + incoming.msgID + ", adding to list.");
				msgList.addFirst(incoming);
			}
		};
	}
	
	public void setIp(String newIP) {
		ip = newIP;
	}
	
	public void setPort(Integer newPort) {
		port = newPort;
	}
	
	public Socket getSock() {
		return mAppData.sock;
	}
	
	public IncomingMessage getNextMessage() throws NoSuchElementException {
	   return msgList.removeFirst();
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
	//	byte buf[]; 
		
	 //   buf = new byte[data.length + 1];
//		buf[0] = (byte) data.length; 
	//	System.arraycopy(data, 0, buf, 1, data.length);
		
		// Now send through the output stream of the socket
		
		OutputStream out = null;
		try {
			if (mAppData.sock == null) {
				Log.i("TestSocket", "Sock is null!");
			} else {
				out = mAppData.sock.getOutputStream();
			}
			
			try {
				out.write(data, 0, data.length);
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
			
			Log.i("TestSocket", "Trying to connect!");
			try {
				sock = new Socket(ip, port);
			} catch (UnknownHostException e) {
				Log.e("TestSocket", "Unknown Host.");
			} catch (IOException e) {
				Log.e("TestSocket", "IOException when connecting to socket.");
			}
			
			if (sock == null) {
				Log.i("TestSocket", "Could not connect... socket null!");
			} else {
				Log.i("TestSocket", "Connected!");
			}
			return sock;
		}
		
		protected void onPostExecute(Socket s) {
			mAppData.sock = s;
			
			if (mAppData.sock != null && mAppData.sock.isConnected()) {
				Intent mainMenuIntent = new Intent(socketActivity, MainActivity.class); 
				socketActivity.startActivity(mainMenuIntent);
			} else {
				Toast.makeText(socketActivity, "Could not connect. Check your IP address and port.", Toast.LENGTH_LONG).show();
			}
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
					int msgLen = 0;
					
					if (bytes_avail > 0) {
						
						// If so, read them in and create a sring
						byte buf[] = new byte[bytes_avail];
						int cur_pos = 0;
						
						Log.i("Msg", "Available bytes: " + bytes_avail);
						
						in.read(buf);
						
						while (cur_pos < bytes_avail) {
							// Read the length first.
							int msgLenMSB = buf[cur_pos++] & 0x00FF;
							int msgLenLSB = buf[cur_pos++] & 0x00FF;
							
							msgLen = ((msgLenMSB << 8) | msgLenLSB) & 0xFFFF;
							
							Log.i("Msg", "Got message of length " + msgLen);
							
							if (msgLen >= 1) {
								IncomingMessage msg = IncomingMessageParser.getMessageFromID(buf[cur_pos++]);
									
								Log.i("Msg", "Message ID: " + buf[1]);
								if (msg == null) {
									Log.e("SocketComm", "Invalid message received.");
									break;
								}
								
								if (msgLen - 1 > 0) {								
									msg.populateData(buf, cur_pos, msgLen - 1);
									cur_pos += msgLen - 1; 
								}
									
								Message out_msg = new Message(); 
								out_msg.obj = msg;
								Log.i("Msg", "Sending msg from socketComm.");
								socketMsgHandler.sendMessage(out_msg);
							} 
							else
							{
								Log.e("SocketComm", "Incoming message did not include an ID.");			
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
