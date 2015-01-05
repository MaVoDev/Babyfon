package babyfon.connectivity.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.util.Log;
import babyfon.Message;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;

/**
 * Zust�ndig f�r den Empfang eines Strings �ber Wi-Fi.
 */
public class TCPReceiver {

	private static final String TAG = TCPReceiver.class.getCanonicalName();
	private MainActivity mMainActivity;
	private ServerSocket mServerSocket;

	private boolean isRunning = false;

	private int tcpPort; // TCP Port �ber den kommuniziert wird.

	public TCPReceiver(MainActivity activity) {
		this.mMainActivity = activity;
		this.tcpPort = new SharedPrefs(activity).getTCPPort();

		Receive receive = new Receive();
		receive.start();
	}

	private class Receive extends Thread {

		public void run() {
			Log.i(TAG, "WifiReceiver started...");

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
						new Message(mMainActivity).handleIncomingMessage(in.readLine());
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}