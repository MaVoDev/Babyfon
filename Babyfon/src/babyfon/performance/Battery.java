package babyfon.performance;

import babyfon.Message;
import babyfon.init.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class Battery {

	private static final String TAG = Battery.class.getCanonicalName();

	private Context mContext;

	public Battery(Context mContext) {
		this.mContext = mContext;

		register();
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				// Akkuladestand hat sich geändert
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

				Log.i(TAG, "New battery level: " + level + "%");
				new Message(mContext).send(mContext.getString(R.string.BABYFON_MSG_BATTERY) + ";" + level);
			}

			if (Intent.ACTION_BATTERY_LOW.equals(action)) {
				// TODO Warnung senden, dass der Akku fast leer ist.
			}
		}
	};

	public boolean unregister() {
		try {
			mContext.unregisterReceiver(this.mBatInfoReceiver);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean register() {
		new Message(mContext).send(mContext.getString(R.string.BABYFON_MSG_BATTERY) + ";" + getBatteryLevel());
		try {
			// Änderung des Akkulevels
			mContext.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

			// Schwacher Akkustand
			mContext.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

			// Akku wechselt vom schwachen in den normalen Levelbereich
			mContext.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_OKAY));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public int getBatteryLevel() {
		Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return (int) 50.0f;
		}

		return (int) (((float) level / (float) scale) * 100.0f);
	}
}
