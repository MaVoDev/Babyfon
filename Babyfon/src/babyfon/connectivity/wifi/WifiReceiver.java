package babyfon.connectivity.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Zuständig für den Empfang eines Strings über Wi-Fi.
 */
public class WifiReceiver {

	private ServerSocket serverSocket;

	private int port; // Port über den kommuniziert wird.

	public WifiReceiver(int port) {
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
						System.out.println(in.readLine());
					}
					serverSocket.close();
				} catch (SocketTimeoutException e) {

				} catch (IOException e) {

				}
			}
		}
	}
}
