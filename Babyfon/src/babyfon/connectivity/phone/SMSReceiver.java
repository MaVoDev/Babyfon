package babyfon.connectivity.phone;

import babyfon.Generator;
import babyfon.Message;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
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
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					String message = currentMessage.getDisplayMessageBody();

					Log.i(TAG, "Phone number: " + phoneNumber);
					Log.i(TAG, "Message: " + message);
					
					String currentDate = new Generator().getCurrentDate();
					String currentTime = new Generator().getCurrentTime();

					new Message(mContext).sms(phoneNumber, message, currentDate, currentTime);
				}
			}
		} catch (Exception e) {

		}
	}
}
