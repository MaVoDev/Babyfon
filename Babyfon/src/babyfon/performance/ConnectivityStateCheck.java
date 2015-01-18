package babyfon.performance;

import babyfon.settings.SharedPrefs;
import android.content.Context;

public class ConnectivityStateCheck {

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
}