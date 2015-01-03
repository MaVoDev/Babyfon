package babyfon.connectivity.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.util.Log;
import babyfon.Message;
import babyfon.SharedPrefs;
import babyfon.view.activity.MainActivity;

/**
 * Zuständig für den Empfang eines Strings über Wi-Fi.
 */
public class WifiReceiver {

	private static final String TAG = WifiReceiver.class.getCanonicalName();
	private MainActivity mMainActivity;
	private ServerSocket mServerSocket;

	private boolean isRunning = false;

	private int tcpPort; // TCP Port über den kommuniziert wird.

	public WifiReceiver(MainActivity activity) {
		this.mMainActivity = activity;
		this.tcpPort = new SharedPrefs(activity).getTCPPort();

		Receive receive = new Receive();
		receive.start();
	}

	private class Receive extends Thread {

		public void run() {
			Log.i(TAG, "New WifiReceiver started!");
			Log.i(TAG, "Thread ID: " + this.getId());

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
