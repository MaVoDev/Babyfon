package babyfon.connectivity.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.util.Log;
import babyfon.Message;
import babyfon.settings.SharedPrefs;

/**
 * Zuständig für den Empfang eines Strings über Wi-Fi.
 */
public class TCPReceiver {

	private Context mContext;
	private ServerSocket mServerSocket;

	private boolean isRunning = false;

	private int tcpPort; // TCP Port über den kommuniziert wird.

	private static final String TAG = TCPReceiver.class.getCanonicalName();

	public TCPReceiver(Context mContext) {
		this.mContext = mContext;
		this.tcpPort = new SharedPrefs(mContext).getTCPPort();
	}

	private class TCPReceiverThread extends Thread {

		public void run() {

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
						new Message(mContext).handleIncomingMessage(incomingTCPmessage);
					}

					mServerSocket.close();
				} catch (SocketTimeoutException e) {

				} catch (IOException e) {

				}
			}
		}
	}

	public boolean start() {
		try {
			TCPReceiverThread tcpReceiverThread = new TCPReceiverThread();
			tcpReceiverThread.start();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean stop() {
		isRunning = false;
		try {
			mServerSocket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
