package babyfon.connectivity.sms;

import babyfon.Message;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	private MainActivity mMainActivity;
	
	protected static final String TAG = SMSReceiver.class.getCanonicalName();

	public SMSReceiver() {

	}
	
	public SMSReceiver(MainActivity activity) {
		this.mMainActivity = activity;
	}

	final SmsManager mSmsManager = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Receiving new SMS.");

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
								
					//TODO Diese Zeile will er nicht ausführen, zumindest wird nichts gesendet.
					new Message(mMainActivity).send(mMainActivity.getString(R.string.MESSAGE_SMS) + ";"
							+ number + ";" + message);
				}
			}
		} catch (Exception e) {

		}
	}
}
