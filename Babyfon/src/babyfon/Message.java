package babyfon;

import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import babyfon.connectivity.wifi.WifiSender;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabymonitorFragment;

public class Message {

	protected static final String TAG = Message.class.getCanonicalName();
	MainActivity mainActivity;

	public Message(MainActivity activity) {
		this.mainActivity = activity;
	}

	public void getError(String errMsg) {
		String errorMessage;
		if (errMsg.equals(mainActivity.getString(R.string.WIFI_STATE_ERROR))) {
			// Wi-Fi ist inaktiv
			errorMessage = mainActivity
					.getString(R.string.ERRMSG_WIFI_STATE_ERROR);
		} else if (errMsg.equals(mainActivity
				.getString(R.string.WIFI_CONNECTION_ERROR))) {
			// Wi-Fi ist mit keinem Netzwerk verbunden
			errorMessage = mainActivity
					.getString(R.string.ERRMSG_WIFI_CONNECTION_ERROR);
		} else {
			// Unbekannter Fehler
			errorMessage = mainActivity
					.getString(R.string.ERRMSG_UNKNOWN_ERROR);
		}
		Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
	}

	public void send(String str) {
		new WifiSender(12789).sendMessage("192.168.178.205", str);
	}

	public void splitString(String str) {
		final String[] strArray = str.split(";");

		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Fragment fragment = mainActivity
						.getFragmentById("BabymonitorFragment");
				((BabymonitorFragment) fragment).setBatteryLevel(strArray[1]);
			}
		});

	}
}
