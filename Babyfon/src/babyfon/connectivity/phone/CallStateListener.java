package babyfon.connectivity.phone;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import babyfon.Generator;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.OverviewFragment;

public class CallStateListener extends PhoneStateListener {

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	public CallStateListener(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
		this.mContext = mContext;
	}

	public void onCallStateChanged(int state, String phoneNumber) {

		if (state == TelephonyManager.CALL_STATE_IDLE) {
			if (mSharedPrefs.getConnectivityType() == 3) {
				((OverviewFragment) ((MainActivity) mContext).getFragmentById("OverviewFragment")).setCounter();
			}
		}

		if (state == TelephonyManager.CALL_STATE_RINGING) {

			String currentDate = new Generator().getCurrentDate();
			String currentTime = new Generator().getCurrentTime();

			new babyfon.Message(mContext).call(phoneNumber, currentDate, currentTime);
		}
	}
}