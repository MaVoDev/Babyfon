package babyfon.connectivity.wifi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.util.Log;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;

/**
 * Zuständig für das Senden eines Srings über Wi-Fi.
 */
public class TCPSender {

	private int tcpPort; // TCP Port über den kommuniziert wird.

	private static final String TAG = TCPReceiver.class.getCanonicalName();

	public TCPSender(Context mContext) {
		this.tcpPort = new SharedPrefs(mContext).getTCPPort();
	}

	public void sendMessage(final String target, final String msg) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "Try to send message...");

				SocketAddress sAddress = new InetSocketAddress(target, tcpPort);
				Socket socket = new Socket();
				PrintWriter outSingle = null;
				try {
					socket.connect(sAddress);
					if (socket.isBound()) {
						outSingle = new PrintWriter(socket.getOutputStream(), true);
					}
				} catch (IOException e) {
					// TODO: Fehler auf dem Gerät anzeigen, wenn Senden
					// fehlschlägt
					Log.e(TAG, "Send message failed!");
				}

				if (outSingle != null) {
					outSingle.println(msg);
					Log.i(TAG, "Send message successfully!");
				}
			}
		}).start();
	}
}
