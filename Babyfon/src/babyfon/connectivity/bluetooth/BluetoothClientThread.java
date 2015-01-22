package babyfon.connectivity.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothClientThread extends BluetoothConnectionThread {
	private static final String TAG = BluetoothClientThread.class.getCanonicalName();

	public BluetoothClientThread(BluetoothDevice device, BluetoothAdapter bTAdapter, BluetoothConnection btConnection) {

		this.mBluetoothAdapter = bTAdapter;
		this.mBTConnection = btConnection;

		// Use a temporary object that is later assigned to mmSocket, because
		// mmSocket is final
		BluetoothSocket tmp = null;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("5644D080-6B9F-11E4-9803-0800200C9A66"));
		} catch (IOException e) {
		}
		mSocket = tmp;
	}

	@Override
	public void run() {
		// Cancel discovery because it will slow down the connection
		mBluetoothAdapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block until it
			// succeeds or throws an exception
			mSocket.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				// TODO: Meldung im UI ausgeben, dass Verbinden fehlgeschlagen
				// ist
				// ...und User fragen, ob auf dem Gerät die Babyfon-App läuft
				Log.e(TAG, "Connection error: " + connectException.getMessage());
				mSocket.close();
			} catch (IOException closeException) {
				Log.e(TAG, "Error on Closing Socket: " + closeException.getMessage());
			}

			if (mBTConnection.getOnConnectionLostListener() != null)
				mBTConnection.getOnConnectionLostListener().onConnectionLostListener(connectException.getMessage());

			return;
		}

		// Do work to manage the connection (in a separate thread)
		Log.i(TAG, "CONNECTED SUCCESSFULLY TO SERVER!!!!!!!! [Name: " + mSocket.getRemoteDevice().getName() + "; MAC: "
				+ mSocket.getRemoteDevice().getAddress() + "]");

		try {
			isRunning = true;

			mmInStream = mSocket.getInputStream();
			mmOutStream = mSocket.getOutputStream();

			// mInputStream = new BufferedInputStream(mSocket.getInputStream());
			// mOutputStream = new BufferedOutputStream(mSocket.getOutputStream());

			startListening();

			mBTConnection.getOnConnnectedListener().onConnectedListener(mSocket.getRemoteDevice().getName());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Error during Connection: " + e.getMessage());
		}

		// new BluetoothReceiver(mmSocket,
		// mBTConnection.getOnReceiveMsgListener());

	}

}