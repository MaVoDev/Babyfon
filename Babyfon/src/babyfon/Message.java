package babyfon;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import babyfon.connectivity.phone.PhoneBookHandler;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.init.R;
import babyfon.performance.ConnectivityStateCheck;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.Output;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.setup.SetupCompleteParentsModeFragment;
import babyfon.view.fragment.setup.SetupSearchDevicesFragment;

public class Message {

	private ConnectivityStateCheck mConnectivityStateCheck;
	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;
	private PhoneBookHandler mPhoneBookHandler;

	private Context mContext;

	protected static final String TAG = Message.class.getCanonicalName();

	public Message(Context mContext) {
		mConnectivityStateCheck = new ConnectivityStateCheck(mContext);
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);
		mPhoneBookHandler = new PhoneBookHandler(mContext);

		this.mContext = mContext;
	}

	public void call(String phoneNumber, String date, String time) {
		String contactName = mPhoneBookHandler.getContactName(phoneNumber);

		if (mSharedPrefs.getForwardingCallInfo()) {
			send(mContext.getString(R.string.BABYFON_MSG_CALL_INFO) + ";" + contactName + ";" + phoneNumber + ";"
					+ date + ";" + time);
		}

	}

	public void sms(String phoneNumber, String message, String date, String time) {
		String contactName = mPhoneBookHandler.getContactName(phoneNumber);

		if (mSharedPrefs.getForwardingSMS()) {
			send(mContext.getString(R.string.BABYFON_MSG_SMS) + ";" + contactName + ";" + message + ";" + date + ";"
					+ time);
		}

		if (mSharedPrefs.getForwardingSMSInfo()) {
			send(mContext.getString(R.string.BABYFON_MSG_SMS_INFO) + ";" + contactName + ";" + date + ";" + time);
		}
	}

	/**
	 * Send methode
	 * 
	 * @param str
	 */
	public void send(String str) {

		send(mSharedPrefs.getRemoteAddress(), str);
	}

	private void send(String target, String msg) {
		Log.e(TAG, "Sending: " + msg);

		// BT
		if (mSharedPrefs.getConnectivityType() == 1) {
			if (MainActivity.mBoundService != null) {
				MainActivity.mBoundService.getConnection().sendMessage(msg + ";");
			}
		}
		// WIFI
		else if (mSharedPrefs.getConnectivityType() == 2) {
			new TCPSender(mContext).sendMessage(target, msg);
		}
	}

	public void handleIncomingMessage(String str) {

		Log.e(TAG, "Incoming: " + str);

		final String[] strArray = str.split(";");

		// AUF SEITE DES ELTERN-MESSAGES
		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_BATTERY))) {
			// Batterie
			mSharedPrefs.setBatteryLevel(Integer.parseInt(strArray[1]));
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_CALL_INFO))) {
			// Call
			final String contactName = strArray[1];
			final String phoneNumber = strArray[2];
			final String date = strArray[3];
			final String time = strArray[4];

			mSharedPrefs.addCallSMS(0 + ";" + contactName + ";" + phoneNumber + ";" + date + ";" + time);
			mSharedPrefs.setCallSMSCounter(mSharedPrefs.getCallSMSCounter() + 1);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SMS))) {
			// SMS
			final String contactName = strArray[1];
			final String message = strArray[2];
			final String date = strArray[3];
			final String time = strArray[4];

			mSharedPrefs.addCallSMS(1 + ";" + contactName + ";" + message + ";" + date + ";" + time);
			mSharedPrefs.setCallSMSCounter(mSharedPrefs.getCallSMSCounter() + 1);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SMS_INFO))) {
			// SMS
			final String contactName = strArray[1];
			final String date = strArray[2];
			final String time = strArray[3];

			mSharedPrefs.addCallSMS(1 + ";" + contactName + ";" + "" + ";" + date + ";" + time);
			mSharedPrefs.setCallSMSCounter(mSharedPrefs.getCallSMSCounter() + 1);
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

		// AUF SEITE DES BABY-GERÄTS
		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_REQ))) {
			final String password = strArray[1];
			final String remoteAddress = strArray[2];
			final String remoteName = strArray[3];

			// TODO: warum wird das auch bei falschem PW gesetzt?!?!
			mSharedPrefs.setRemoteAddress(remoteAddress);

			if (mSharedPrefs.getPassword().equals(password)) {
				mSharedPrefs.setRemoteName(remoteName);
				mSharedPrefs.setRemoteOnlineState(true);

				send(mContext.getString(R.string.BABYFON_MSG_AUTH_CONFIRMED) + ";" + mSharedPrefs.getPassword());

				mModuleHandler.registerBattery();

				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.registerSMS();
				}

				// BLUETOOTH
				if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
					if (MainActivity.mBoundService != null) {

						// Recording wird erst im OverviewFragment gestartet
						// Wenn Confirmed recording starten
						// MainActivity.mBoundService.startRecording();

						mSharedPrefs.setNoiseActivated(true);

					}
				} // WIFI
				else if (mSharedPrefs.getConnectivityType() == 2) {
					mModuleHandler.stopTCPReceiver();
					mModuleHandler.stopUDPReceiver();
					mModuleHandler.startUDPReceiver();
					mModuleHandler.startTCPReceiver();
					mModuleHandler.startRemoteCheck();
				}

			} else {

				send(mContext.getString(R.string.BABYFON_MSG_AUTH_DENIED));

				if (mSharedPrefs.getConnectivityTypeTemp() == 1) {

					MainActivity.mBoundService.getConnection().stopConnection();
					MainActivity.mBoundService.initBtConnection(); // TODO: Test
					MainActivity.mBoundService.startServer();
				}

				mSharedPrefs.setRemoteAddress(null);
			}
		}

		// AUF SEITE DES ELTERN-GERÄTS
		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_CONFIRMED))) {
			mSharedPrefs.setPassword(strArray[1]);

			// BT
			if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
				mSharedPrefs.setRemoteOnlineState(true);
				MainActivity.mBoundService.getConnection().registerDisconnectHandler();
			}
			// WIFI
			else if (mSharedPrefs.getConnectivityTypeTemp() == 2) {
				mModuleHandler.startRemoteCheck();
			}

			FragmentManager mFragmentManager = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();
			mFragmentManager.beginTransaction()
					.replace(R.id.frame_container, new SetupCompleteParentsModeFragment(mContext), null)
					.addToBackStack(null).commit();
		}

		// AUF SEITE DES ELTERN-GERÄTS
		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_AUTH_DENIED))) {

			mSharedPrefs.setRemoteAddress(null);
			mSharedPrefs.setRemoteName(null);
			mSharedPrefs.setRemoteOnlineState(false);

			if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
				// Bluetooth neu initialisieren
				((SetupSearchDevicesFragment) ((MainActivity) MainActivity.getContext())
						.getFragmentById("SetupSearchDevicesFragment")).initViewBluetooth();
			}

			new Output().toast(mContext.getString(R.string.wrong_password), 0);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_AWAY))) {
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.unregisterBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.unregisterSMS();
				}
			} else {
				mSharedPrefs.setBatteryLevel(-1);
			}
			mSharedPrefs.setRemoteOnlineState(false);

			mModuleHandler.stopRemoteCheck();
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED))) {
			mModuleHandler.stopRemoteCheck();
			new Output().toast(mContext.getString(R.string.disconnected), 1);
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.unregisterBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.unregisterSMS();
				}
				if (mSharedPrefs.getConnectivityType() == 1) {
					mModuleHandler.stopBT();
				} else if (mSharedPrefs.getConnectivityType() == 2) {
					mModuleHandler.startUDPReceiver();
					mModuleHandler.startTCPReceiver();
				}
			}
			mSharedPrefs.setRemoteOnlineState(false);
			mSharedPrefs.setRemoteAddress(null);
			mSharedPrefs.setRemoteName(null);
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_REJOIN))) {

			if (mSharedPrefs.getDeviceMode() == 0) {
				if (!mSharedPrefs.getRemoteAddress().equals(strArray[1])
						|| !mSharedPrefs.getPassword().equals(strArray[2])) {
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
				if (!mSharedPrefs.getRemoteAddress().equals(strArray[1])
						|| !mSharedPrefs.getPassword().equals(strArray[2])) {
					send(strArray[1], mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));
					// new TCPSender(mContext).sendMessage(strArray[1],
					// mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));
				} else {
					mSharedPrefs.setRemoteOnlineState(true);
					if (mSharedPrefs.getConnectivityType() == 2) {
						mModuleHandler.startRemoteCheck();
					}
				}
			} else {
				send(strArray[1], mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));
			}
		}

		if (strArray[0].equals(mContext.getString(R.string.BABYFON_MSG_SYSTEM_PWCHANGED))) {
			// password changed
			mSharedPrefs.setPassword(strArray[1]);
		}
	}
}
