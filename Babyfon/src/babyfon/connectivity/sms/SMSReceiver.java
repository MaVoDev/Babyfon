package babyfon.connectivity.sms;

import babyfon.Message;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	protected static final String TAG = SMSReceiver.class.getCanonicalName();

	public SMSReceiver() {

	}

	public SMSReceiver(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	final SmsManager mSmsManager = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(TAG, "Receiving new SMS.");

		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String number = currentMessage.getDisplayOriginatingAddress();
					String message = currentMessage.getDisplayMessageBody();

					Log.i(TAG, "Number: " + number);
					Log.i(TAG, "Message: " + message);

					if (mSharedPrefs.getForwardingSMS()) {
						new Message(mContext).send(mContext.getString(R.string.BABYFON_MSG_SMS) + ";" + number + ";"
								+ message);
					}

					if (mSharedPrefs.getForwardingSMSInfo()) {
						new Message(mContext).send(mContext.getString(R.string.BABYFON_MSG_SMS_INFO) + ";" + number);
					}

				}
			}
		} catch (Exception e) {

		}
	}
}
