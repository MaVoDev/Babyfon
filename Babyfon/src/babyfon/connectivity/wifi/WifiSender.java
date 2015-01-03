package babyfon.connectivity.wifi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import babyfon.SharedPrefs;
import babyfon.view.activity.MainActivity;

/**
 * Zust�ndig f�r das Senden eines Srings �ber Wi-Fi.
 */
public class WifiSender {

	private int tcpPort; // TCP Port �ber den kommuniziert wird.

	public WifiSender(MainActivity activity) {
		this.tcpPort = new SharedPrefs(activity).getTCPPort();
	}

	public void sendMessage(final String target, final String msg) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Try to send message...");

				SocketAddress sAddress = new InetSocketAddress(target, tcpPort);
				Socket socket = new Socket();
				PrintWriter outSingle = null;
				try {
					socket.connect(sAddress);
					if (socket.isBound()) {
						outSingle = new PrintWriter(socket.getOutputStream(), true);
					}
				} catch (IOException e) {
					// TODO: Fehler auf dem Ger�t anzeigen, wenn Senden
					// fehlschl�gt
					System.out.println("Send Message failed!");
				}

				if (outSingle != null) {
					outSingle.println(msg);
					System.out.println("Send message successfully!");
				}
			}
		}).start();

	}
}
