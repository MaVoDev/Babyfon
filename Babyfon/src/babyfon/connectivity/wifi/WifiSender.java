package babyfon.connectivity.wifi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Zust�ndig f�r das Senden eines Srings �ber Wi-Fi.
 */
public class WifiSender {

	public int port;

	public WifiSender(int port) {
		this.port = port;
	}

	public void sendMessage(String target, String msg) {
		SocketAddress sAddress = new InetSocketAddress(target, port);
		Socket socket = new Socket();
		PrintWriter outSingle = null;
		try {
			socket.connect(sAddress);
			if (socket.isBound()) {
				outSingle = new PrintWriter(socket.getOutputStream(), true);
			}
		} catch (IOException e) {
			// TODO: Fehler auf dem Ger�t anzeigen, wenn Senden fehlschl�gt
			System.out.println("sendMessage failed!!!");
			System.out.println("Error: " + e.getMessage());
		}
		if (outSingle != null) {
			outSingle.println(msg);
		}
	}
}
