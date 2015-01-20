package babyfon.connectivity.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothServerThread extends Thread {
	private static final String TAG = BluetoothServerThread.class.getCanonicalName();
	private final BluetoothServerSocket mmServerSocket;
	private BluetoothSocket mSocket;

	public BluetoothServerThread(BluetoothAdapter mBluetoothAdapter) {

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

	public void run() {
		mSocket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				mSocket = mmServerSocket.accept();
			} catch (IOException e) {
				break;
			}
			// If a connection was accepted
			if (mSocket != null) {
				// Do work to manage the connection (in a separate thread)
				// manageConnectedSocket(socket);

				Log.i(TAG, "SOCKET CONNECTED!!!!!!!! [Name: " + mSocket.getRemoteDevice().getName() + "; MAC: "
						+ mSocket.getRemoteDevice().getAddress() + "]");

				// Start sending Audio to client
//				mNoise = new AudioRecording(mSocket);
//				mNoise.startRecording();

				try {
					mmServerSocket.close();

					// TODO: REMOVE THIS OR CONNECTION IS CLOSED IMMEDIATELY AFTER IT WAS ESTABLISHED!!!!!!!!!!
					// socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}
		}

		Log.i(TAG, "Server stopped!");
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();

			if (mSocket != null)
				mSocket.close();

			// Stop Recording
//			if (mNoise != null)
//				mNoise.stopRecording();

		} catch (IOException e) {
		}
	}
}