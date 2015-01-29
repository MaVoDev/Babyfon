package babyfon.connectivity.wifi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.util.Log;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;

/**
 * Zuständig für das Senden eines Srings über Wi-Fi.
 */
public class TCPSender {

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	private static final String TAG = TCPSender.class.getCanonicalName();

	public TCPSender(Context mContext) {
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);
	}

	public void sendMessage(final String target, final String msg) {
		if (target != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
//					Log.i(TAG, "Try to send message: " + msg);

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
						Log.i(TAG, "Send message successfully!");
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
