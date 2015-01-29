package babyfon.connectivity.phone;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import babyfon.Generator;

public class CallStateListener extends PhoneStateListener {
	
	private Context mContext;
	
	public CallStateListener(Context mContext) {
		this.mContext = mContext;
	}
	
	public void onCallStateChanged(int state, String phoneNumber) {

		if (state == TelephonyManager.CALL_STATE_RINGING) {

			String currentDate = new Generator().getCurrentDate();
			String currentTime = new Generator().getCurrentTime();

			new babyfon.Message(mContext).call(phoneNumber, currentDate, currentTime);
		}
	}
}