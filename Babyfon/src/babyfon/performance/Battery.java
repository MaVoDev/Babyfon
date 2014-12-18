package babyfon.performance;

import babyfon.Message;
import babyfon.view.activity.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery {

	MainActivity context;

	public Battery(MainActivity context) {
		this.context = context;

		// �nderung des Akkulevels
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		// Schwacher Akkustand
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

		// Akku wechselt vom schwachen in den normalen Levelbereich
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_OKAY));
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				// Akkuladestand hat sich ge�ndert
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				new Message(context).send("BATTERY;" + level + "%");
			}

			if (Intent.ACTION_BATTERY_LOW.equals(action)) {
				// TODO Warnung senden, dass der Akku fast leer ist.
			}
		}
	};

	public void unregister() {
		context.unregisterReceiver(this.mBatInfoReceiver);
	}
}
