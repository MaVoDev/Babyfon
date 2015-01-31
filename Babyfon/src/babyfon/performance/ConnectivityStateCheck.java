package babyfon.performance;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import babyfon.Message;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import android.content.Context;

public class ConnectivityStateCheck {

	private InetAddress remoteAddress;

	// Timer
	private Timer timerRemoteCheck;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	public ConnectivityStateCheck(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public int getConnectivityType() {
		return mSharedPrefs.getConnectivityType();
	}

	public String getConnectivityState() {
		switch (getConnectivityType()) {
		case 1: // bluetooth
			return getBluetoothState();
		case 2: // wi-fi
			return getWiFiState();
		case 3: // wi-fi direct
			return getWiFiDirectState();
		default:
			return null;
		}
	}

	public String getBluetoothState() {
		return null;
	}

	public String getWiFiState() {
		return null;
	}

	public String getWiFiDirectState() {
		return null;
	}

	public void showErrorDialog() {

	}

	public boolean startConnectivityStateThread() {
		try {
			if (timerRemoteCheck == null) {
				timerRemoteCheck = new Timer();

				try {
					remoteAddress = InetAddress.getByName(mSharedPrefs.getRemoteAddress());
				} catch (UnknownHostException e1) {

				}

				timerRemoteCheck.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						((MainActivity) mContext).runOnUiThread(new Runnable() {
							public void run() {
								if (mSharedPrefs.getConnectivityType() == 2) {
									// wi-fi
									if (mSharedPrefs.getRemoteAddress() != null) {
										// remote address is available
										try {
											if (remoteAddress.isReachable(4000)) {
												new Message(mContext).send(mContext
														.getString(R.string.BABYFON_MSG_CONNECTION_HELLO)
														+ ";"
														+ mSharedPrefs.getHostAddress()
														+ ";"
														+ mSharedPrefs.getPassword());
											} else {
												mSharedPrefs.setRemoteOnlineState(false);
											}
										} catch (IOException e) {

										}
									}
								}
							}
						});
					}
				}, 5000, 5000);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean stopConnectivityStateThread() {
		try {
			if (timerRemoteCheck != null) {
				timerRemoteCheck.cancel();
			}
			timerRemoteCheck = null;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}