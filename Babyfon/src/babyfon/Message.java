package babyfon;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.Toast;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.setup.parentmode.SetupSearchDevicesFragment;

public class Message {

	protected static final String TAG = Message.class.getCanonicalName();
	private Context mContext;

	public Message(Context mContext) {
		this.mContext = mContext;
	}

	public void send(String str) {
		new TCPSender(mContext).sendMessage("192.168.178.92", str);
	}

	public void handleIncomingMessage(String str) {
		final String[] strArray = str.split(";");

		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_BATTERY))) {
			// Batterie
			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Fragment fragment = ((MainActivity) mContext).getFragmentById("BabymonitorFragment");
					((BabyMonitorFragment) fragment).setBatteryLevel(strArray[1]);
				}
			});
		}
		
		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_CONNECTION_CONFIRM))) {
			final String ip = strArray[1];
			final String name = strArray[2];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SetupSearchDevicesFragment.setNewDevice(ip, name);
				}
			});
		}
	}
	
	public void getError(String errMsg) {
		String errorMessage;
		if (errMsg.equals(mContext.getString(R.string.WIFI_STATE_ERROR))) {
			// Wi-Fi ist inaktiv
			errorMessage = mContext.getString(R.string.ERRMSG_WIFI_STATE_ERROR);
		} else if (errMsg.equals(mContext.getString(R.string.WIFI_CONNECTION_ERROR))) {
			// Wi-Fi ist mit keinem Netzwerk verbunden
			errorMessage = mContext.getString(R.string.ERRMSG_WIFI_CONNECTION_ERROR);
		} else {
			// Unbekannter Fehler
			errorMessage = mContext.getString(R.string.ERRMSG_UNKNOWN_ERROR);
		}
		Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
	}
}
