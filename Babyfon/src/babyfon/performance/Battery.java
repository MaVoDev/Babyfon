package babyfon.performance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery {

	Context context;

	public Battery(Context context) {
		this.context = context;
		
		// Änderung des Akkulevels
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
			
			if(Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				// Akkuladestand hat sich geändert
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				//TODO Akkulevel senden.
			}
			
			if(Intent.ACTION_BATTERY_LOW.equals(action)) {
				//TODO Warnung senden, dass der Akku fast leer ist.
			}
		}
	};
}
