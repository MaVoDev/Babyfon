package babyfon.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import babyfon.adapter.DeviceListAdapter;
import babyfon.model.DeviceListItemModel;

public class BluetoothHandler {
	private static final String TAG = BluetoothHandler.class.getCanonicalName();
	private BluetoothAdapter mBluetoothAdapter;
	private Context mContext;
	private DeviceListAdapter mDeviceListAdapter;

	// Receiver
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				Log.i(TAG, "ADD DEVICE: " + device.getName());

				mDeviceListAdapter.add(new DeviceListItemModel(device.getName(), device.getAddress()));

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// Toast.makeText(context, "DISCOVERY FINISHED", Toast.LENGTH_LONG).show();
				mBtDiscovering = false;
			}
		}
	};

	public BluetoothHandler(Context context) {
		this.mContext = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

	/**
	 * Gibt zurück, ob Bluetooth supported wird.
	 * 
	 * @return true, wenn Bluetooth supported wird.<br>
	 *         false, wenn Bluetooth nicht supported wird.
	 */
	public boolean isBluetoothSupported() {
		return (mBluetoothAdapter != null);
	}

	/**
	 * Falls es nicht schon an ist: Bluetooth einschalten.
	 * 
	 * @return <b>true</b>, wenn BT schon an ist; <b>false</b>, wenn BT bisher aus war
	 */
	public boolean enableBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (!adapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mContext.startActivity(enableBtIntent);

			return false;
		} else
			return true;
	}

	/**
	 * Falls es nicht schon an ist: Bluetooth einschalten und Gerät sichtbar für andere Geräte machen.
	 * 
	 * @return <b>true</b>, wenn BT-discoverability schon an ist; <b>false</b>, wenn BT-discoverability bisher aus war
	 */
	public boolean enableBluetoothDiscoverability() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			mContext.startActivity(discoverableIntent);

			return false;
		} else
			return true;
	}

	public void prepareForSearch(DeviceListAdapter mDevicesAdapter) {
		this.mDeviceListAdapter = mDevicesAdapter;

		// Register the BroadcastReceiver
		IntentFilter bluetoothFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter bluetoothScanFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		mContext.registerReceiver(mReceiver, bluetoothFoundFilter);
		mContext.registerReceiver(mReceiver, bluetoothScanFinishedFilter);
	}

	private boolean mBtDiscovering = false;

	public void searchDevices() {

		Log.i(TAG, "Devices: " + mDeviceListAdapter.getCount());

		if (mBtDiscovering) {
			// Wenn wir gerade nach Geräten Suchen nichts tun...

		} else {
			// ...ansonsten Liste leeren und neue Suche starten

			mDeviceListAdapter.clear();

			Log.i(TAG, "STARTING BLUETOOTH DISCOVERY...");
			mBtDiscovering = mBluetoothAdapter.startDiscovery();
		}
	}

	public void stopSearch() {
		mBtDiscovering = false;
		mBluetoothAdapter.cancelDiscovery();
	}

	public void unregisterReceiver() {
		mContext.unregisterReceiver(mReceiver);
	}

	public String getOwnAddress() {
		return mBluetoothAdapter.getAddress();
	}

}