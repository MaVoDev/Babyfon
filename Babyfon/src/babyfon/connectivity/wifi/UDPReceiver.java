package babyfon.connectivity.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;

public class UDPReceiver {

	private MainActivity mMainActivity;
	private DatagramSocket udpServerSocket;
	private SharedPrefs mSharedPrefs;

	private boolean isRunning = false;

	private static final String TAG = TCPReceiver.class.getCanonicalName();

	public UDPReceiver(MainActivity mMainActivity) {
		this.mMainActivity = mMainActivity;
		this.mSharedPrefs = new SharedPrefs(mMainActivity);
	}

	public void startUDPReceiver() {
		UDPReceiverThread udpReceiverThread = new UDPReceiverThread();
		udpReceiverThread.start();
	}

	private class UDPReceiverThread extends Thread {
		public void run() {

			isRunning = true;

			try {
				Log.i(TAG, "UDP receiver is running.");

				udpServerSocket = new DatagramSocket(mSharedPrefs.getUDPPort());
				byte[] buffer = new byte[mMainActivity.getString(R.string.MESSAGE_CONNECTION_REQUEST).length()];

				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

				while (isRunning) {
					udpServerSocket.receive(receivePacket);
					String incomingUDPMessage = new String(buffer, 0, buffer.length);
					System.out.println("Incoming UDP Message: " + incomingUDPMessage);
					String targetIP = receivePacket.getAddress() + "";

					// Cut the "/" from the InetAddress value
					targetIP = targetIP.substring(1);

					if (incomingUDPMessage.equals(mMainActivity.getString(R.string.MESSAGE_CONNECTION_REQUEST))) {
						new TCPSender(mMainActivity).sendMessage(targetIP, mMainActivity.getString(R.string.MESSAGE_CONNECTION_CONFIRM)
								+ ";" + new WifiHandler(mMainActivity).getLocalIPv4Address() + ";" + android.os.Build.MODEL);
					}
				}
			} catch (IOException e) {

			}
		}
	}

	public void stop() {
		isRunning = false;
		try {
			udpServerSocket.close();
			Log.i(TAG, "UDP receiver closed.");
		} catch (Exception e) {
			Log.e(TAG, "Can't close UDP receiver.");
		}
	}
}
