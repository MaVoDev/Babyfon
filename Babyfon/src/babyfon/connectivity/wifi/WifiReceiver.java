package babyfon.connectivity.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import babyfon.Message;
import babyfon.view.activity.MainActivity;

/**
 * Zust�ndig f�r den Empfang eines Strings �ber Wi-Fi.
 */
public class WifiReceiver {

	private MainActivity mainActivity;
	private ServerSocket serverSocket;

	private int port; // Port �ber den kommuniziert wird.

	public WifiReceiver(MainActivity activity, int port) {
		this.mainActivity = activity;
		this.port = port;
		
		Receive receive = new Receive();
		receive.start();
	}

	private class Receive extends Thread {

		private boolean isRunning = true;

		public void run() {
			while (isRunning) {
				try {
					serverSocket = new ServerSocket(port);

					// Wartet auf eine eingehende Anfrage und blockiert solange
					// die Verbindung besteht.
					Socket socket = serverSocket.accept();

					final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					if (serverSocket.isBound()) {
						// Eingehende Nachricht lesen und weiterleiten.
						new Message(mainActivity).splitString(in.readLine());
					}
					serverSocket.close();
				} catch (SocketTimeoutException e) {

				} catch (IOException e) {

				}
			}
		}
	}
}
