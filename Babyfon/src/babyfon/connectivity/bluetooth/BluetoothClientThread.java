package babyfon.connectivity.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import babyfon.connectivity.ConnectionInterface.OnReceiveMsgListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothClientThread extends Thread {
	private static final String TAG = BluetoothClientThread.class.getCanonicalName();
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothConnection mBTConnection;

	private InputStream mmInStream;
	private OutputStream mmOutStream;
	private PrintWriter mPrintWriter;
	private BufferedReader mBufferedReader;
	protected boolean isRunning = false;

	public BluetoothClientThread(BluetoothDevice device, BluetoothAdapter bTAdapter, BluetoothConnection btConnection) {

		this.mBluetoothAdapter = bTAdapter;
		this.mBTConnection = btConnection;

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

	@Override
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
				// ...und User fragen, ob auf dem Gerät die Babyfon-App läuft
				Log.e(TAG, "Connection error: " + connectException.getMessage());
				mmSocket.close();
			} catch (IOException closeException) {
				Log.e(TAG, "Error on Closing Socket: " + closeException.getMessage());
			}

			if (mBTConnection.getOnConnectionLostListener() != null)
				mBTConnection.getOnConnectionLostListener().onConnectionLostListener(connectException.getMessage());

			return;
		}

		// Do work to manage the connection (in a separate thread)
		Log.i(TAG, "CONNECTED SUCCESSFULLY TO SERVER!!!!!!!! [Name: " + mmSocket.getRemoteDevice().getName() + "; MAC: "
				+ mmSocket.getRemoteDevice().getAddress() + "]");

		try {
			isRunning = true;

			mmInStream = mmSocket.getInputStream();
			mmOutStream = mmSocket.getOutputStream();
			mPrintWriter = new PrintWriter(mmOutStream);
			mBufferedReader = new BufferedReader(new InputStreamReader(mmInStream));

			// Receiver Thread
			new Thread(new Runnable() {

				@Override
				public void run() {

					String msg = null;
					Log.i(TAG, "START listening for messages...");
					// while (isRunning ) {
					try {
						OnReceiveMsgListener listener = mBTConnection.getOnReceiveMsgListener();

						// Leite die empfangenen Nachrichten an den OnReceiveMsgListener weiter
						while (isRunning && (msg = mBufferedReader.readLine()) != null) {
							if (listener != null)
								listener.onReceiveMsgListener(msg);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// }
					Log.i(TAG, "STOP listening for messages...");
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// new BluetoothReceiver(mmSocket, mBTConnection.getOnReceiveMsgListener());

	}

	public void sendMessage(String msg) {
		mPrintWriter.println(msg);
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}