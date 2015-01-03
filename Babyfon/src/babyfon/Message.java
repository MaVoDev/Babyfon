package babyfon;

import android.app.Fragment;
import android.widget.Toast;
import babyfon.connectivity.wifi.WifiSender;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabymonitorFragment;
import babyfon.view.fragment.ConnectionFragment;

public class Message {

	protected static final String TAG = Message.class.getCanonicalName();
	MainActivity mMainActivity;

	public Message(MainActivity activity) {
		this.mMainActivity = activity;
	}

	public void send(String str) {
		new WifiSender(mMainActivity).sendMessage("192.168.178.92", str);
	}

	public void handleIncomingMessage(String str) {
		final String[] strArray = str.split(";");

		System.out.println("Split: " + strArray[0]);

		if (strArray[0].equals(mMainActivity.getString(R.string.MESSAGE_BATTERY))) {
			// Batterie
			mMainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Fragment fragment = mMainActivity.getFragmentById("BabymonitorFragment");
					((BabymonitorFragment) fragment).setBatteryLevel(strArray[1]);
				}
			});
		}

		if (strArray[0].equals(mMainActivity.getString(R.string.MESSAGE_SMS_DETAILS))) {
			// SMS
			System.out.println("From: " + strArray[1] + "  Message: " + strArray[2]);
		}

		if (strArray[0].equals(mMainActivity.getString(R.string.MESSAGE_CONFIRM_CONNECTION))) {
			// Verbindung
			mMainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Fragment fragment = mMainActivity.getFragmentById("ConnectionFragment");
					((ConnectionFragment) fragment).addIP(strArray[1]);
				}
			});
		}
	}
	
	public void getError(String errMsg) {
		String errorMessage;
		if (errMsg.equals(mMainActivity.getString(R.string.WIFI_STATE_ERROR))) {
			// Wi-Fi ist inaktiv
			errorMessage = mMainActivity.getString(R.string.ERRMSG_WIFI_STATE_ERROR);
		} else if (errMsg.equals(mMainActivity.getString(R.string.WIFI_CONNECTION_ERROR))) {
			// Wi-Fi ist mit keinem Netzwerk verbunden
			errorMessage = mMainActivity.getString(R.string.ERRMSG_WIFI_CONNECTION_ERROR);
		} else {
			// Unbekannter Fehler
			errorMessage = mMainActivity.getString(R.string.ERRMSG_UNKNOWN_ERROR);
		}
		Toast.makeText(mMainActivity, errorMessage, Toast.LENGTH_SHORT).show();
	}
}
