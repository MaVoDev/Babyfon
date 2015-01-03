package babyfon.performance;

import babyfon.Message;
import babyfon.view.activity.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class Battery {

	private static final String TAG = Battery.class.getCanonicalName();
	MainActivity context;

	public Battery(MainActivity context) {
		this.context = context;

		register();
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				// Akkuladestand hat sich geändert
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

				Log.i(TAG, "Send battery message. Level: " + level);

				new Message(context).send("BATTERY;" + level + "%");
			}

			if (Intent.ACTION_BATTERY_LOW.equals(action)) {
				// TODO Warnung senden, dass der Akku fast leer ist.
			}
		}
	};

	public void unregister() {
		Log.i(TAG, "Unregistering Battery-Receiver...");
		context.unregisterReceiver(this.mBatInfoReceiver);
	}

	public void register() {

		Log.i(TAG, "Registering Battery-Receiver...");

		// Änderung des Akkulevels
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		// Schwacher Akkustand
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_LOW));

		// Akku wechselt vom schwachen in den normalen Levelbereich
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_OKAY));

	}
}
