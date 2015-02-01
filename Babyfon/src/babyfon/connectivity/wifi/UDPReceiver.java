package babyfon.connectivity.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.content.Context;
import android.util.Log;
import babyfon.audio.AudioDetection;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;

public class UDPReceiver {

	private Context mContext;
	private DatagramSocket udpServerSocket;
	private SharedPrefs mSharedPrefs;

	private boolean isRunning = false;

	private static final String TAG = UDPReceiver.class.getCanonicalName();

	public UDPReceiver(Context mContext) {
		this.mContext = mContext;
		this.mSharedPrefs = new SharedPrefs(mContext);
	}

	private class UDPReceiverThread extends Thread {
		public void run() {
			isRunning = true;
			try {

				udpServerSocket = new DatagramSocket(mSharedPrefs.getUDPPort());
				byte[] buffer;

				if (mSharedPrefs.getRemoteAddress() != null) {
					buffer = new byte[2048];
				} else {
					buffer = new byte[mContext.getString(R.string.BABYFON_MSG_CONNECTION_SEARCH).length()];
				}

				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

				while (isRunning) {
					udpServerSocket.receive(receivePacket);
					String incomingUDPMessage = new String(buffer, 0, buffer.length);
					String targetIP = receivePacket.getAddress() + "";
					// Cut the "/" from the InetAddress value
					targetIP = targetIP.substring(1);

					if (incomingUDPMessage.equals(mContext.getString(R.string.BABYFON_MSG_CONNECTION_SEARCH))) {
						if (mSharedPrefs.getRemoteAddress() == null) {
							new TCPSender(mContext).sendMessage(targetIP,
									mContext.getString(R.string.BABYFON_MSG_CONNECTION_FOUND) + ";"
											+ new WifiHandler(mContext).getLocalIPv4Address() + ";"
											+ android.os.Build.MODEL);
						}
					} else {
						if (mSharedPrefs.getRemoteAddress() != null) {
							if (mSharedPrefs.getDeviceMode() == 0) {
								MainActivity.mAudioPlayer.playData(buffer);
							} else {
								((BabyMonitorFragment) ((MainActivity) mContext).getFragmentById("BabyMonitorFragment"))
										.updateVolume(AudioDetection.calculateVolume(buffer, 0));
								
								if (mSharedPrefs.isHearActivated()) {
									MainActivity.mAudioPlayer.playData(buffer);
								}
							}
						}
					}
				}
			} catch (IOException e) {

			}
		}
	}

	public boolean start() {
		try {
			UDPReceiverThread udpReceiverThread = new UDPReceiverThread();
			udpReceiverThread.start();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean stop() {
		isRunning = false;
		try {
			udpServerSocket.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
