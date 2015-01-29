package babyfon.connectivity.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.util.Log;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;

public class UDPSender {

	private Context mContext;
	private SharedPrefs mSharedPrefs;

	private static final String TAG = UDPSender.class.getCanonicalName();

	public UDPSender(Context mContext) {
		this.mContext = mContext;
		mSharedPrefs = new SharedPrefs(mContext);
	}

	public void sendUDPMessage(String ipRange) {
		// send Broadcast
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

	public void sendUDPMessage(byte[] bData) {
		InetAddress mInetAddress;
		try {
			mInetAddress = InetAddress.getByName(mSharedPrefs.getRemoteAddress());
			DatagramPacket packet = new DatagramPacket(bData, bData.length, mInetAddress, mSharedPrefs.getUDPPort());
			DatagramSocket dsocket = new DatagramSocket();
			dsocket.send(packet);
			dsocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
