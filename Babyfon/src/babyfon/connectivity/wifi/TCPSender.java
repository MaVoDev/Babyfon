package babyfon.connectivity.wifi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.util.Log;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.Output;
import babyfon.view.activity.MainActivity;

/**
 * Zust�ndig f�r das Senden eines Srings �ber Wi-Fi.
 */
public class TCPSender {

	private SharedPrefs mSharedPrefs;

	private static final String TAG = TCPSender.class.getCanonicalName();

	public TCPSender(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
	}

	public void sendMessage(final String target, final String msg) {
		if (new WifiHandler(MainActivity.getContext()).getWifiState() == 0) {
			new Output().simpleDialog(MainActivity.getContext().getString(R.string.dialog_title_connection_error),
					MainActivity.getContext().getString(R.string.dialog_message_wifi_disabled), MainActivity
							.getContext().getString(R.string.dialog_button_ok));
		} else if (!new WifiHandler(MainActivity.getContext()).isWifiConnected()) {
			new Output().simpleDialog(MainActivity.getContext().getString(R.string.dialog_title_connection_error),
					MainActivity.getContext().getString(R.string.dialog_message_wifi_not_connected), MainActivity
							.getContext().getString(R.string.dialog_button_ok));
		} else {
			if (target != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						SocketAddress sAddress = new InetSocketAddress(target, mSharedPrefs.getTCPPort());
						Socket socket = new Socket();
						PrintWriter outSingle = null;
						try {
							socket.connect(sAddress);
							if (socket.isBound()) {
								outSingle = new PrintWriter(socket.getOutputStream(), true);
							}
						} catch (IOException e) {
							mSharedPrefs.setRemoteOnlineState(false);
							Log.e(TAG, "Send message failed!");
						}

						if (outSingle != null) {
							outSingle.println(msg);
							Log.i(TAG, "Send: " + msg);
							mSharedPrefs.setRemoteOnlineState(true);
						}
					}
				}).start();
			} else {
				mSharedPrefs.setRemoteOnlineState(false);
				Log.e(TAG, "The remote host is null or not reachable!");
			}
		}
	}
}
