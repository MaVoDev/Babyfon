package babyfon;

import android.app.Fragment;
import android.widget.Toast;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabymonitorFragment;
import babyfon.view.fragment.setup.SetupSearchDevicesFragment;

public class Message {

	protected static final String TAG = Message.class.getCanonicalName();
	MainActivity mMainActivity;

	public Message(MainActivity activity) {
		this.mMainActivity = activity;
	}

	public void send(String str) {
		new TCPSender(mMainActivity).sendMessage("192.168.178.92", str);
	}

	public void handleIncomingMessage(String str) {
		final String[] strArray = str.split(";");

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
		
		if (strArray[0].equals(mMainActivity.getString(R.string.MESSAGE_CONNECTION_CONFIRM))) {
			final String ip = strArray[1];
			final String name = strArray[2];

			mMainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Fragment fragment = mMainActivity.getFragmentById("SetupSearchDevicesFragment");
					((SetupSearchDevicesFragment) fragment).setNewDevice(ip, name);
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
