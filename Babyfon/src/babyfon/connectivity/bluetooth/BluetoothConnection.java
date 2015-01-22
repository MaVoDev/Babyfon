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

public class BluetoothConnection implements ConnectionInterface {

	private static final String TAG = BluetoothConnection.class.getCanonicalName();

	// private static int REQUEST_ENABLE_BT = 1;

	private Context mContext;
	private boolean mBtDiscovering = false;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothListAdapter mArrayAdapter;

	private BluetoothConnectionThread mBluetoothConnectionThread;

	private OnSearchStatusChangedListener mOnSearchStatusChangedListener;
	private OnReceiveDataListener mOnReceiveMsgListener;
	private OnConnectionLostListener mOnConnectionLostListener;
	private OnConnnectedListener mOnConnnectedListener;

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

	public BluetoothConnection(Context context) {
		this.mContext = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void startServer() {
		enableDiscoverability();

		mBluetoothConnectionThread = new BluetoothServerThread(mBluetoothAdapter, this);
		mBluetoothConnectionThread.start();
	}

	@Override
	public <T> void startClient(T listAdapter) {

		enableBluetooth();

		this.mArrayAdapter = (BluetoothListAdapter) listAdapter;

		// Register the BroadcastReceiver
		IntentFilter bluetoothFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter bluetoothScanFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		mContext.registerReceiver(mReceiver, bluetoothFoundFilter);
		mContext.registerReceiver(mReceiver, bluetoothScanFinishedFilter);
	}

	private void enableBluetooth() {
		if (mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mContext.startActivity(enableBtIntent);
		}
	}

	private void enableDiscoverability() {
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			mContext.startActivity(discoverableIntent);
		}
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
		mBluetoothConnectionThread = new BluetoothClientThread(device, mBluetoothAdapter, this);
		mBluetoothConnectionThread.start();
	}

	@Override
	public void setOnSearchStatusChangedListener(OnSearchStatusChangedListener l) {
		this.mOnSearchStatusChangedListener = l;
	}

	@Override
	public void setOnReceiveDataListener(OnReceiveDataListener l) {
		this.mOnReceiveMsgListener = l;
	}

	@Override
	public void setOnConnectionLostListener(OnConnectionLostListener l) {
		this.mOnConnectionLostListener = l;
	}

	@Override
	public void setOnConnnectedListener(OnConnnectedListener l) {
		this.mOnConnnectedListener = l;
	}

	@Override
	public void closeConnection() {
		// TODO Disconnect Zeug
	}

	@Override
	public void sendData(byte[] data, byte type) {

		// if (mBluetoothConnectionThread != null)
		// mBluetoothConnectionThread.sendData(data);

		// if (mmOutStream != null) {

		if (mBluetoothConnectionThread != null) {

			// TODO: SO ANPASSEN DASS ES AUF HUAWEI NICHT BLOCKIERT

			// Array der art Array[TYPE, DatenByte_1, DatenByte_2, ..., DatenByte_N) erzeugen...
//			byte[] sendDataPackage = new byte[data.length + 1];
//			System.arraycopy(data, 0, sendDataPackage, 1, data.length);
//			sendDataPackage[0] = type;

//			mBluetoothConnectionThread.sendData(sendDataPackage);

			 mBluetoothConnectionThread.sendData(data);

		} else {
			Log.i(TAG, "No Message sent. Outstream is null!");
		}

	}

	public OnConnectionLostListener getOnConnectionLostListener() {
		return mOnConnectionLostListener;
	}

	public OnConnnectedListener getOnConnnectedListener() {
		return mOnConnnectedListener;
	}

	public OnReceiveDataListener getOnReceiveMsgListener() {
		return mOnReceiveMsgListener;
	}
}
