package babyfon.connectivity.wifi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;

public class UDPBroadcastSender {

	private Context mContext;
	private SharedPrefs mSharedPrefs;

	public UDPBroadcastSender(Context mContext) {
		this.mContext = mContext;
		mSharedPrefs = new SharedPrefs(mContext);
	}

	public void sendUDPMessage(String ipRange) {
		String localIP = null; // TODO lokale IP übergeben

		try {
			byte[] message = mContext.getString(R.string.MESSAGE_CONNECTION_REQUEST).getBytes();

			for (int i = 1; i < 255; i++) {
				InetAddress address = InetAddress.getByName(ipRange + i);
				if (!localIP.equals(address)) {
					// filter own address
					DatagramPacket packet = new DatagramPacket(message, message.length, address,
							mSharedPrefs.getUDPPort());
					DatagramSocket dsocket = new DatagramSocket();
					dsocket.send(packet);
					dsocket.close();
				}
			}

		} catch (Exception e) {

		}
	}
}
