package babyfon.connectivity.wifi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.util.Log;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;

public class UDPBroadcastSender {

	private Context mContext;
	private SharedPrefs mSharedPrefs;
	
	private static final String TAG = UDPBroadcastSender.class.getCanonicalName();

	public UDPBroadcastSender(Context mContext) {
		this.mContext = mContext;
		mSharedPrefs = new SharedPrefs(mContext);
	}

	public void sendUDPMessage(String ipRange) {
		String localIP;
		try {
			localIP = new WifiHandler(mContext).getLocalIPv4Address();
		} catch (SocketException e1) {
			localIP = null;
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			localIP = null;
			e1.printStackTrace();
		}

		if (localIP != null) {
			try {
				byte[] message = mContext.getString(R.string.BABYFON_MSG_CONNECTION_SEARCH).getBytes();
				Log.d(TAG, "Send broadcast message...");
				for (int i = 1; i < 255; i++) {
					InetAddress address = InetAddress.getByName(ipRange + i);
					if (!localIP.equals(ipRange + i)) {
						// filter own address
						DatagramPacket packet = new DatagramPacket(message, message.length, address,
								mSharedPrefs.getUDPPort());
						DatagramSocket dsocket = new DatagramSocket();
						dsocket.send(packet);
						dsocket.close();
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "Error");
			}
		} else {
			Log.e(TAG, "The local ip is null!");
		}
	}
}
