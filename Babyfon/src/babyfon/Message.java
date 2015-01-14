package babyfon;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.setup.parentmode.SetupCompleteParentsModeFragment;
import babyfon.view.fragment.setup.parentmode.SetupSearchDevicesFragment;

public class Message {

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	protected static final String TAG = Message.class.getCanonicalName();

	public Message(Context mContext) {
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void send(String str) {
		new TCPSender(mContext).sendMessage(mSharedPrefs.getRemoteAddress(), str);
	}

	public void handleIncomingMessage(String str) {
		final String[] strArray = str.split(";");

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_BATTERY))) {
			// Batterie
			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Fragment fragment = ((MainActivity) mContext).getFragmentById("BabymonitorFragment");
					((BabyMonitorFragment) fragment).setBatteryLevel(strArray[1]);
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_CONNECTION_FOUND))) {
			final String ip = strArray[1];
			final String name = strArray[2];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SetupSearchDevicesFragment.setNewDevice(ip, name);
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_AUTH_REQ))) {
			final String password = strArray[1];
			final String remoteAddress = strArray[2];

			mSharedPrefs.setRemoteAdress(remoteAddress);

			if (mSharedPrefs.getPassword().equals(password)) {

				int numberOfConnections = mSharedPrefs.getNumberOfConnections() + 1;
				mSharedPrefs.setNumberOfConnections(numberOfConnections);
				if (mSharedPrefs.getNumberOfAllowedConnections() < numberOfConnections) {

				} else {
					mModuleHandler.stopUDPReceiver();
				}
				mModuleHandler.registerBattery();
				send(mContext.getString(R.string.MESSAGE_AUTH_CONFIRMED));
			} else {
				send(mContext.getString(R.string.MESSAGE_AUTH_DENIED));
			}
		}

		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_AUTH_CONFIRMED))) {
			FragmentManager mFragmentManager = ((Activity) mContext).getFragmentManager();
			mFragmentManager.beginTransaction()
					.replace(R.id.frame_container, new SetupCompleteParentsModeFragment(mContext), null)
					.addToBackStack(null).commit();
		}

		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_AUTH_DENIED))) {
			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(mContext, "Falsches Passwort!", Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.MESSAGE_SYSTEM_EXIT))) {
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.startUDPReceiver();
				mModuleHandler.unregisterBattery();

				mSharedPrefs.setRemoteAdress(null);
				mSharedPrefs.setRemoteName(null);
			} else {
				mSharedPrefs.setRemoteAdress(null);
				mSharedPrefs.setRemoteName(null);
			}

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(mContext, "Das Remotegerät wurde beendet.", Toast.LENGTH_SHORT);
					toast.show();
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
