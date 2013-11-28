package org.ubc.tartarus;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.ubc.tartarus.communication.SocketComm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SocketActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket);

		// Set up a timer task.  We will use the timer to check the
		// input queue every 500 ms
		
		ApplicationData app = (ApplicationData) getApplication();
		
		app.socketComm = new SocketComm(app, this);
		// TODO: When user presses connect
		// app.socketComm.openSocket();
		
		
		// Schedule the read task, using the socketComm in the Application so that it exist
		// throughout all activities.
		SocketComm.TCPReadTimerTask tcp_task = app.socketComm.new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.socket, menu);
		return true;
	}
	
	public void connectButton(View view) {
		ApplicationData app = (ApplicationData) getApplication(); 
		
		if (app.sock != null && app.sock.isConnected() && !app.sock.isClosed()) {
			Toast.makeText(this, "Already connected!", Toast.LENGTH_LONG).show();
			
			//Send user back to the main menu.
			Intent mainMenuIntent = new Intent(this, MainActivity.class); 
			startActivity(mainMenuIntent);
			return;
		}
		
		app.socketComm.setIp(getConnectToIP()); 
		app.socketComm.setPort(getConnectToPort());
		app.socketComm.openSocket();
	}
	
	// Construct an IP address from the four boxes
	public String getConnectToIP() {
		String addr = "";
		EditText text_ip;
		text_ip = (EditText) findViewById(R.id.ip1);
		addr += text_ip.getText().toString();
		text_ip = (EditText) findViewById(R.id.ip2);
		addr += "." + text_ip.getText().toString();
		text_ip = (EditText) findViewById(R.id.ip3);
		addr += "." + text_ip.getText().toString();
		text_ip = (EditText) findViewById(R.id.ip4);
		addr += "." + text_ip.getText().toString();
		return addr;
	}

	// Gets the Port from the appropriate field.
	public Integer getConnectToPort() {
		Integer port;
		EditText text_port;

		text_port = (EditText) findViewById(R.id.port);
		port = Integer.parseInt(text_port.getText().toString());

		return port;
	}
}
