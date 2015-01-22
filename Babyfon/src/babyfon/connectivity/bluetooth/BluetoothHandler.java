package babyfon.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;

public class BluetoothHandler {
	private BluetoothAdapter mBluetoothAdapter;

	public BluetoothHandler() {
	}

	/**
	 * Überprüft, ob Bluetooth unterstützt wird und ob Bluetooth ein- oder ausgeschaltet ist.
	 * 
	 * @return int Bluetooth Status: -1 = not available, 0 = not enabled, 1 = enabled
	 */
	public int getBluetoothState() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Bluetooth wird nicht unterstützt.
			return -1;
		} else {
			// Bluetooth wird unterstützt.
			if (!mBluetoothAdapter.isEnabled()) {
				// Bluetooth ist inaktiv.
				return 0;
			}
			// Bluetooth ist aktiv.
			return 1;
		}
	}

	public void startBluetooth() {
		mBluetoothAdapter.startDiscovery();
	}
}