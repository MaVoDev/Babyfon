package babyfon.connectivity.wifi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.os.StrictMode;
import android.util.Log;

public class StreamSender {

//	public static final int MODE_NORMAL = 0;
//	public static final int MODE_SEND_ONLY = 1;
//	public static final int MODE_RECEIVE_ONLY = 2;
//	private static final int MODE_LAST = 2;
//
//	// private InetAddress mLocalAddress;
//	private final int mLocalPort;
//
//	private InetAddress mRemoteAddress;
//	private int mRemotePort = -1;
//	private int mMode = MODE_NORMAL;
//
//	private int mSocket = -1;

	private Context mContext;

	public StreamSender(Context mContext) {
		this.mContext = mContext;
//		start();
	}

	public void start() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			audio.setMode(AudioManager.MODE_IN_COMMUNICATION);
			AudioGroup audioGroup = new AudioGroup();
			audioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);
			AudioStream audioStream = new AudioStream(InetAddress.getByAddress(getLocalIPAddress()));
			audioStream.setCodec(AudioCodec.PCMU);
			audioStream.setMode(RtpStream.MODE_SEND_ONLY);
			audioStream.associate(
					InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 178, (byte) 81 }), 22222);
			audioStream.join(audioGroup);

		} catch (Exception e) {

		}
	}

	public static byte[] getLocalIPAddress() {
		byte ip[] = null;
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ip = inetAddress.getAddress();
					}
				}
			}
		} catch (SocketException ex) {
			Log.i("SocketException ", ex.toString());
		}
		return ip;

	}
}
