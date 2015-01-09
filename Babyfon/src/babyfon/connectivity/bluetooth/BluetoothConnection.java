package babyfon.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import babyfon.connectivity.ConnectionInterface;
import babyfon.init.R;

public class BluetoothConnection implements ConnectionInterface {

	private static final String TAG = BluetoothConnection.class.getCanonicalName();

	// private static int REQUEST_ENABLE_BT = 1;

	private Context mContext;
	private boolean mBtDiscovering = false;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothListAdapter mArrayAdapter;

	OnSearchStatusChangedListener mOnSearchStatusChangedListener;

	// Receiver
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				Log.i(TAG, "ADD DEVICE: " + device.getName());

				mArrayAdapter.add(device);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Toast.makeText(context, "DISCOVERY FINISHED", Toast.LENGTH_LONG).show();
				mBtDiscovering = false;

				mOnSearchStatusChangedListener.onSearchStatusChanged(false);
			}
		}
	};

	// Konstruktor
	public BluetoothConnection(Context context, BluetoothListAdapter adapter) {
		this.mContext = context;

		initBluetooth();

		// mArrayAdapter = new BluetoothListAdapter(context,
		// R.layout.bluetooth_row_element);
		this.mArrayAdapter = adapter;

	}

	private void initBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// WENN ELTERN-MODE REICHT ES, DASS BT-ENABLED IST

		// WENN BABY-MODE MUSS DEVICE SICHTBAR SEIN

		// Register the BroadcastReceiver
		IntentFilter bluetoothFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter bluetoothScanFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		mContext.registerReceiver(mReceiver, bluetoothFoundFilter);
		mContext.registerReceiver(mReceiver, bluetoothScanFinishedFilter);
	}

	// public BroadcastReceiver getReceiver() {
	// return mReceiver;
	// }

	private boolean initBluetooth_ALT() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		boolean btEnabled = false;

		if (mBluetoothAdapter != null) {
			String status;

			if (mBluetoothAdapter.isEnabled()) {
				// Enabled. Work with bluetooth.
				String mydeviceaddress = mBluetoothAdapter.getAddress();
				String mydevicename = mBluetoothAdapter.getName();
				status = mydevicename + " : " + mydeviceaddress;
				btEnabled = true;

				// If BT is enabled, it can be that the device is not
				// discoverable, so we check for
				if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
					enableDiscoverability();

			} else {

				enableDiscoverability();

				// Disabled. Do something else.
				status = "Bluetooth is not enabled.";

			}

			Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
		}

		// Register the BroadcastReceiver
		IntentFilter bluetoothFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter bluetoothScanFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		mContext.registerReceiver(mReceiver, bluetoothFoundFilter); // Don't
																	// forget to
																	// unregister
																	// during
																	// onDestroy
		mContext.registerReceiver(mReceiver, bluetoothScanFinishedFilter); // Don't
		// forget to
		// unregister
		// during
		// onDestroy

		return btEnabled;
	}

	private void enableDiscoverability() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		// startActivity(discoverableIntent);

		// TODO: AUSLAGERN ODER ACTIVITY REINHOLEN!
		// mContext.startActivityForResult(discoverableIntent,
		// REQUEST_ENABLE_BT);

	}

	@Override
	public void searchDevices() {
		if (mBtDiscovering) {
			// Wenn wir gerade nach Geräten Suchen nichts tun...

		} else {
			// ...ansonsten Liste leeren und neue Suche starten

			mBtDiscovering = true;
			mArrayAdapter.clear();

			Log.i(TAG, "STARTING BLUETOOTH DISCOVERY...");
			boolean searching = mBluetoothAdapter.startDiscovery();
			
			mOnSearchStatusChangedListener.onSearchStatusChanged(searching);
			
			
		}
	}

	@Override
	public void connectToDeviceFromList(int position) {
		BluetoothDevice device = mArrayAdapter.getItem(position);
		Log.i(TAG, "Try to connect to device: " + device.getName());
		new BluetoothClientThread(device, mBluetoothAdapter).start();
	}

	@Override
	public void setOnSearchStatusChangedListener(OnSearchStatusChangedListener l) {
		this.mOnSearchStatusChangedListener = l;
	}

	// @Override
	// public ListAdapter getListAdapter() {
	// return mArrayAdapter;
	// }

}
