package babyfon.connectivity.bluetooth;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothClientThread extends Thread {
	private static final String TAG = BluetoothClientThread.class.getCanonicalName();
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private BluetoothAdapter mBluetoothAdapter;

	public BluetoothClientThread(BluetoothDevice device, BluetoothAdapter bTAdapter) {

		this.mBluetoothAdapter = bTAdapter;

		// Use a temporary object that is later assigned to mmSocket, because
		// mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("5644D080-6B9F-11E4-9803-0800200C9A66"));
		} catch (IOException e) {
		}
		mmSocket = tmp;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		mBluetoothAdapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block until it
			// succeeds or throws an exception
			mmSocket.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {

				// TODO: Meldung im UI ausgeben, dass Verbinden fehlgeschlagen
				// ist
				// ...und User fragen, ob auf dem Ger�t die Babyfon-App l�uft
				Log.e(TAG, "Connection error: " + connectException.getMessage());
				mmSocket.close();
			} catch (IOException closeException) {
			}
			return;
		}

		// Do work to manage the connection (in a separate thread)
		Log.i(TAG, "CONNECTED SUCCESSFULLY TO SERVER!!!!!!!! [Name: " + mmSocket.getRemoteDevice().getName() + "; MAC: "
				+ mmSocket.getRemoteDevice().getAddress() + "]");

		// mAudioPlayer = new AudioPlayer(mmSocket, mMainActivity);
		// mAudioPlayer.startPlaying();
		//
		// Toast.makeText(
		// mMainActivity,
		// "CONNECTED SUCCESSFULLY TO SERVER!!!!!!!! [Name: " +
		// mmSocket.getRemoteDevice().getName() + "; MAC: "
		// + mmSocket.getRemoteDevice().getAddress(), Toast.LENGTH_LONG).show();

	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}

		// if(mAudioPlayer != null)
		// mAudioPlayer.stopPlaying();
	}
}