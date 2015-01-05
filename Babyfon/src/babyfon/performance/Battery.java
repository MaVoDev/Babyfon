package babyfon.performance;

import babyfon.Message;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class Battery {

	private static final String TAG = Battery.class.getCanonicalName();

	private MainActivity mMainActivity;

	public Battery(MainActivity mMainActivity) {
		this.mMainActivity = mMainActivity;

		register();
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				// Akkuladestand hat sich geändert
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

				Log.i(TAG, "Try to send battery level (" + level + "%)...");
				new Message(mMainActivity).send(mMainActivity.getString(R.string.MESSAGE_BATTERY) + ";" + level + "%");
			}

			if (Intent.ACTION_BATTERY_LOW.equals(action)) {
				// TODO Warnung senden, dass der Akku fast leer ist.
			}
		}
	};

	public void unregister() {
		mMainActivity.unregisterReceiver(this.mBatInfoReceiver);
		Log.i(TAG, "Battery receiver unregistered.");
	}

	public void register() {

		Log.i(TAG, "Try to register battery receiver...");

		// Änderung des Akkulevels
		mMainActivity.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		// Schwacher Akkustand
		mMainActivity.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

		// Akku wechselt vom schwachen in den normalen Levelbereich
		mMainActivity.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_OKAY));

		Log.i(TAG, "Battery receiver is registered.");
	}
}
