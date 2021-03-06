package babyfon.connectivity.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.util.Log;
import babyfon.connectivity.ConnectionInterface.OnConnectedListener;

public class BluetoothServerThread extends BluetoothConnectionThread {
	private static final String TAG = BluetoothServerThread.class.getCanonicalName();
	private final BluetoothServerSocket mmServerSocket;

	public int type = SERVER;

	public BluetoothServerThread(BluetoothAdapter mBluetoothAdapter, BluetoothConnection bluetoothConnection) {

		this.mBTConnection = bluetoothConnection;

		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothServerSocket tmp = null;

		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Babyfon BT",
					UUID.fromString("5644D080-6B9F-11E4-9803-0800200C9A66"));
		} catch (IOException e) {
		}
		mmServerSocket = tmp;
	}

	@Override
	public void run() {
		mSocket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				Log.i(TAG, "Server waiting for connections...");
				mSocket = mmServerSocket.accept();
			} catch (IOException e) {
				Log.e(TAG, "Waiting for Clients aborted...");
				Log.e(TAG, "Exception: " + e.getMessage());
				break;
			}
			// If a connection was accepted
			if (mSocket != null) {
				// Do work to manage the connection (in a separate thread)
				// manageConnectedSocket(socket);

				try {
					isRunning = true;

					mmInStream = mSocket.getInputStream();
					mmOutStream = mSocket.getOutputStream();

					startListening();

				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "Error during Connection: " + e.getMessage());
				}

				try {
					mmServerSocket.close();

					// TODO: REMOVE THIS OR CONNECTION IS CLOSED IMMEDIATELY
					// AFTER IT WAS ESTABLISHED!!!!!!!!!!
					// socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.i(TAG, "Client connected: [Name: " + mSocket.getRemoteDevice().getName() + "; MAC: "
						+ mSocket.getRemoteDevice().getAddress() + "]");

				OnConnectedListener listener = mBTConnection.getOnConnnectedListener();
				if (listener != null)
					listener.onConnectedListener(mSocket.getRemoteDevice().getName());

				// STOP WHILE SCHLEIFE
				break;
			}
		}
	}

	@Override
	public void kill() {

		if (mmServerSocket != null) {
			try {
				mmServerSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		super.kill();
	}
}