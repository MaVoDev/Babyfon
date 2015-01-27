package babyfon;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.init.R;
import babyfon.performance.ConnectivityStateCheck;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.AbsenceFragment;
import babyfon.view.fragment.setup.SetupCompleteParentsModeFragment;
import babyfon.view.fragment.setup.SetupSearchDevicesFragment;

public class Message {

	private ConnectivityStateCheck mConnectivityStateCheck;
	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	protected static final String TAG = Message.class.getCanonicalName();

	public Message(Context mContext) {
		mConnectivityStateCheck = new ConnectivityStateCheck(mContext);
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void send(String str) {

		if (mSharedPrefs.getConnectivityType() == 1 || mSharedPrefs.getConnectivityTypeTemp() == 1) {
			if (MainActivity.mBoundService != null)
				MainActivity.mBoundService.getConnection().sendMessage(str);
		}
		if (mSharedPrefs.getConnectivityType() == 2 || mSharedPrefs.getConnectivityTypeTemp() == 2) {
			new TCPSender(mContext).sendMessage(mSharedPrefs.getRemoteAddress(), str);
		}
	}

	public void handleIncomingMessage(String str) {
		final String[] strArray = str.split(";");

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_BATTERY))) {
			// Batterie
			mSharedPrefs.setBatteryLevel(Integer.parseInt(strArray[1]));
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SMS))) {
			// SMS
			final String numberName = strArray[1];
			final String message = strArray[2];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AbsenceFragment.setNewMessage(1, numberName, message);
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SMS_INFO))) {
			// SMS
			final String numberName = strArray[1];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AbsenceFragment.setNewMessage(1, numberName, "");
					;
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_CONNECTION_FOUND))) {
			final String ip = strArray[1];
			final String name = strArray[2];

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SetupSearchDevicesFragment fragment = (SetupSearchDevicesFragment) ((MainActivity) mContext)
							.getFragmentById("SetupSearchDevicesFragment");
					fragment.setNewDevice(ip, name);
				}
			});
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_REQ))) {
			final String password = strArray[1];
			final String remoteAddress = strArray[2];
			final String remoteName = strArray[3];

			mSharedPrefs.setRemoteAddress(remoteAddress);

			if (mSharedPrefs.getPassword().equals(password)) {
				mSharedPrefs.setRemoteName(remoteName);
				mSharedPrefs.setRemoteOnlineState(true);

				send(mContext.getString(R.string.BABYFON_MSG_AUTH_CONFIRMED) + ";" + mSharedPrefs.getPassword());

				mModuleHandler.stopUDPReceiver();
				mModuleHandler.registerBattery();

				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.registerSMS();
				}
				mModuleHandler.startRemoteCheck();
			} else {
				send(mContext.getString(R.string.BABYFON_MSG_AUTH_DENIED));
				mSharedPrefs.setRemoteAddress(null);
			}
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_CONFIRMED))) {
			mSharedPrefs.setPassword(strArray[1]);

			mModuleHandler.startRemoteCheck();

			FragmentManager mFragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
			mFragmentManager.beginTransaction().replace(R.id.frame_container, new SetupCompleteParentsModeFragment(mContext), null)
					.addToBackStack(null).commit();
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_DENIED))) {

			mSharedPrefs.setRemoteAddress(null);
			mSharedPrefs.setRemoteName(null);
			mSharedPrefs.setRemoteOnlineState(false);

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

			mModuleHandler.stopRemoteCheck();
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED))) {
			mModuleHandler.stopRemoteCheck();

			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.unregisterBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.unregisterSMS();
				}
				if (mSharedPrefs.getConnectivityType() == 2) {
					mModuleHandler.startUDPReceiver();
				}
			}
			mSharedPrefs.setRemoteOnlineState(false);
			mSharedPrefs.setRemoteAddress(null);
			mSharedPrefs.setRemoteName(null);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_REJOIN))) {

			if (mSharedPrefs.getDeviceMode() == 0) {
				if (!mSharedPrefs.getRemoteAddress().equals(strArray[1]) || !mSharedPrefs.getPassword().equals(strArray[2])) {
					send((mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED)));
				} else {
					mModuleHandler.registerBattery();
					if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
						mModuleHandler.registerSMS();
					}
					mSharedPrefs.setRemoteOnlineState(true);
					mModuleHandler.startRemoteCheck();
				}
			}
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_CONNECTION_HELLO))) {
			// Hello
			if (mSharedPrefs.getRemoteAddress() != null) {
				if (!mSharedPrefs.getRemoteAddress().equals(strArray[1]) || !mSharedPrefs.getPassword().equals(strArray[2])) {
					new TCPSender(mContext).sendMessage(strArray[1], mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));
				} else {
					mSharedPrefs.setRemoteOnlineState(true);
				}
			} else {
				new TCPSender(mContext).sendMessage(strArray[1], mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));
			}
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_PWCHANGED))) {
			// password changed
			mSharedPrefs.setPassword(strArray[1]);
		}
	}
}
