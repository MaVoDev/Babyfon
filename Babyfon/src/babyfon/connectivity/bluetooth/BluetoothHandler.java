package babyfon.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothHandler {

	Context context;

	public BluetoothHandler(Context context) {
		this.context = context;
	}

	/**
	 * Überprüft, ob Bluetooth unterstützt wird und ob Bluetooth ein- oder
	 * ausgeschaltet ist.
	 * 
	 * @return Bluetooth Status
	 */
	public int bluetoothState() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			// Bluetooth wird nicht unterstützt.
			return -1;
		} else {
			// Bluetooth wird unterstützt.
			if (!bluetoothAdapter.isEnabled()) {
				// Bluetooth ist inaktiv.
				return 0;
			}
			// Bluetooth ist aktiv.
			return 1;
		}
	}
}
