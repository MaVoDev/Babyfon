package babyfon.connectivity.phone;

import babyfon.settings.SharedPrefs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

	private Context mContext;

	private static final String TAG = CallReceiver.class.getCanonicalName();

	public CallReceiver() {

	}

	public CallReceiver(Context mContext) {
		this.mContext = mContext;
	}

	public void onReceive(Context context, Intent intent) {

		try {
			// TELEPHONY MANAGER class object to register one listner
			TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			// Create Listner
			MyPhoneStateListener mMyPhoneStateListener = new MyPhoneStateListener();

			// Register listener for LISTEN_CALL_STATE
			mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		} catch (Exception e) {
			Log.e(TAG, "Error: Phone receiver!");
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String phoneNumber) {
			if (state == 1) {
				Log.d(TAG, "New incoming call.");
				Log.i(TAG, "Number: " + phoneNumber);
				new babyfon.Message(mContext).call(phoneNumber);
			}
		}
	}
}
