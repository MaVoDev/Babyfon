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
import babyfon.view.fragment.AbsenceFragment;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.overview.OverviewBabyFragment;
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
		if (mSharedPrefs.getConnectivityType() == 2 || mSharedPrefs.getConnectivityTypeTemp() == 2) {
			new TCPSender(mContext).sendMessage(mSharedPrefs.getRemoteAddress(), str);
		}
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

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SMS))) {
			// SMS
			final String numberName = strArray[1];
			final String message = strArray[2];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AbsenceFragment.setNewNessage(numberName, message);
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SMS_INFO))) {
			// SMS
			final String numberName = strArray[1];
			
			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AbsenceFragment.setNewNessage(numberName, "");;
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_CONNECTION_FOUND))) {
			final String ip = strArray[1];
			final String name = strArray[2];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SetupSearchDevicesFragment.setNewDevice(ip, name);
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_REQ))) {
			final String password = strArray[1];
			final String remoteAddress = strArray[2];
			final String remoteName = strArray[3];

			mSharedPrefs.setRemoteAdress(remoteAddress);

			if (mSharedPrefs.getPassword().equals(password)) {
				mSharedPrefs.setRemoteName(remoteName);
				mSharedPrefs.setRemoteOnlineState(true);
				int numberOfConnections = mSharedPrefs.getNumberOfConnections() + 1;

				send(mContext.getString(R.string.BABYFON_MSG_AUTH_CONFIRMED));

				mSharedPrefs.setNumberOfConnections(numberOfConnections);

				mModuleHandler.stopUDPReceiver();
				mModuleHandler.registerBattery();

				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.registerSMS();
				}
			} else {
				send(mContext.getString(R.string.BABYFON_MSG_AUTH_DENIED));
			}
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_CONFIRMED))) {
			FragmentManager mFragmentManager = ((Activity) mContext).getFragmentManager();
			mFragmentManager.beginTransaction()
					.replace(R.id.frame_container, new SetupCompleteParentsModeFragment(mContext), null)
					.addToBackStack(null).commit();
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_DENIED))) {
			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(mContext, "Falsches Passwort!", Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_AWAY))) {
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.unregisterBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.unregisterSMS();
				}
			}
			mSharedPrefs.setRemoteOnlineState(false);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED))) {
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.unregisterBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.unregisterSMS();
				}
			}
			mSharedPrefs.setRemoteOnlineState(false);
			mSharedPrefs.setRemoteAdress(null);
			mSharedPrefs.setRemoteName(null);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_REJOIN))) {
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.registerBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.registerSMS();
				}
			}
			mSharedPrefs.setRemoteOnlineState(true);
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
