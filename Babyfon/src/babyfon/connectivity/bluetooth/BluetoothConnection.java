package babyfon.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import babyfon.Message;
import babyfon.connectivity.ConnectionInterface;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;

public class BluetoothConnection implements ConnectionInterface {

	private static final String TAG = BluetoothConnection.class.getCanonicalName();

	// private static int REQUEST_ENABLE_BT = 1;

	private BluetoothAdapter mBluetoothAdapter;

	private BluetoothConnectionThread mBluetoothConnectionThread;

	private SharedPrefs mSharedPrefs;

	private OnSearchStatusChangedListener mOnSearchStatusChangedListener;
	private OnReceiveDataListener mOnReceiveMsgListener;
	private OnConnectionLostListener mOnConnectionLostListener;
	private OnConnectedListener mOnConnnectedListener;

	// Receiver
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				// disconnected, do what you want to notify user here, toast, or dialog, etc.
				Log.e(TAG, "Bluetooth disconnected!");

				mSharedPrefs.setRemoteOnlineState(false);

				new Message(MainActivity.getContext()).send(MainActivity.getContext().getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));

				MainActivity.getContext().unregisterReceiver(mReceiver);
			}
		}
	};

	public BluetoothConnection() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mSharedPrefs = new SharedPrefs(MainActivity.getContext());
	}

	@Override
	public void startServer() {
		// enableDiscoverability();

		mBluetoothConnectionThread = new BluetoothServerThread(mBluetoothAdapter, this);
		mBluetoothConnectionThread.start();
	}

	// private void enableBluetooth() {
	// if (mBluetoothAdapter.isEnabled()) {
	// Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	// mContext.startActivity(enableBtIntent);
	// }
	// }
	//
	// private void enableDiscoverability() {
	// if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	// Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	// discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
	// mContext.startActivity(discoverableIntent);
	// }
	// }

	@Override
	public void connectToAdress(String address) {
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mBluetoothConnectionThread = new BluetoothClientThread(device, mBluetoothAdapter, this);
		mBluetoothConnectionThread.start();
	}

	@Override
	public void stopConnection() {
		// TODO Disconnect Zeug
		if (mBluetoothConnectionThread != null)
			mBluetoothConnectionThread.stopBT();
		mBluetoothConnectionThread = null;
	}

	@Override
	public void sendMessage(String msg) {
		if (mBluetoothConnectionThread != null) {
			mBluetoothConnectionThread.sendData(msg.getBytes());
		} else {
			Log.i(TAG, "No Message sent. Outstream is null!");
		}
	}

	@Override
	public void sendData(byte[] data, byte type) {

		// if (mBluetoothConnectionThread != null)
		// mBluetoothConnectionThread.sendData(data);

		// if (mmOutStream != null) {

		if (mBluetoothConnectionThread != null) {

			// TODO: SO ANPASSEN DASS ES AUF HUAWEI NICHT BLOCKIERT

			// Array der art Array[TYPE, DatenByte_1, DatenByte_2, ..., DatenByte_N) erzeugen...
			// byte[] sendDataPackage = new byte[data.length + 1];
			// System.arraycopy(data, 0, sendDataPackage, 1, data.length);
			// sendDataPackage[0] = type;

			// mBluetoothConnectionThread.sendData(sendDataPackage);

			mBluetoothConnectionThread.sendData(data);

		} else {
			Log.i(TAG, "No Message sent. Outstream is null!");
		}

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
	public void setOnConnectedListener(OnConnectedListener l) {
		this.mOnConnnectedListener = l;
	}

	public OnConnectionLostListener getOnConnectionLostListener() {
		return mOnConnectionLostListener;
	}

	public OnConnectedListener getOnConnnectedListener() {
		return mOnConnnectedListener;
	}

	public OnReceiveDataListener getOnReceiveDataListener() {
		return mOnReceiveMsgListener;
	}

	@Override
	public void registerDisconnectHandler() {

		// Register the BroadcastReceiver
		IntentFilter bluetoothDisconnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

		MainActivity.getContext().registerReceiver(mReceiver, bluetoothDisconnectedFilter);
	}

}
