package babyfon.connectivity.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import android.util.Log;
import babyfon.Message;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;

/**
 * Zuständig für den Empfang eines Strings über Wi-Fi.
 */
public class TCPReceiver {

	private MainActivity mMainActivity;
	private ServerSocket mServerSocket;

	private boolean isRunning = false;

	private int tcpPort; // TCP Port über den kommuniziert wird.

	private static final String TAG = TCPReceiver.class.getCanonicalName();

	public TCPReceiver(MainActivity activity) {
		this.mMainActivity = activity;
		this.tcpPort = new SharedPrefs(activity).getTCPPort();

		TCPReceiverThread tcpReceiverThread = new TCPReceiverThread();
		tcpReceiverThread.start();
	}

	private class TCPReceiverThread extends Thread {

		public void run() {
			Log.i(TAG, "TCP receiver is running.");

			String incomingTCPmessage;
			isRunning = true;

			while (isRunning) {
				try {
					mServerSocket = new ServerSocket(tcpPort);

					// Wartet auf eine eingehende Anfrage und blockiert solange
					// die Verbindung besteht.
					Socket socket = mServerSocket.accept();

					final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					if (mServerSocket.isBound()) {
						// Eingehende Nachricht lesen und weiterleiten.
						incomingTCPmessage = in.readLine();
						Log.i(TAG, "Incoming TCP message: " + incomingTCPmessage);
						new Message(mMainActivity).handleIncomingMessage(incomingTCPmessage);
					}

					mServerSocket.close();
				} catch (SocketTimeoutException e) {

				} catch (IOException e) {

				}
			}
		}
	}

	public void stop() {
		isRunning = false;
		try {
			mServerSocket.close();
			Log.i(TAG, "TCP receiver closed.");
		} catch (IOException e) {
			Log.e(TAG, "Can't close TCP receiver.");
		}
	}
}
