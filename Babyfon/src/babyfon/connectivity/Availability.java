package babyfon.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;

public class Availability {

	Context context;

	public Availability(Context context) {
		this.context = context;
	}

	/**
	 * �berpr�ft, ob Bluetooth unterst�tzt wird und ob Bluetooth ein- oder
	 * ausgeschaltet ist.
	 * 
	 * @return Bluetooth Status
	 */
	public int bluetoothState() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			// Bluetooth wird nicht unterst�tzt.
			return -1;
		} else {
			// Bluetooth wird unterst�tzt.
			if (!bluetoothAdapter.isEnabled()) {
				// Bluetooth ist inaktiv.
				return 0;
			}
			// Bluetooth ist aktiv.
			return 1;
		}
	}

	/**
	 * �berpr�ft, ob Wi-Fi unterst�tzt wird und ob das Wi-Fi ein- oder
	 * ausgeschaltet ist.
	 * 
	 * @return Wi-Fi Status
	 */
	public int wifiState() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager == null) {
			// Wi-Fi wird nicht unterst�tzt.
			return -1;
		} else {
			// Wi-Fi wird unterst�tzt.
			if (!wifiManager.isWifiEnabled()) {
				// Wi-Fi ist inaktiv.
				return 0;
			}
			// Wi-Fi ist aktiv.
			return 1;
		}
	}
}
