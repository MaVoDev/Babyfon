package babyfon.connectivity.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import babyfon.audio.AudioRecorder;

public abstract class BluetoothConnectionThread extends Thread {

	private static final String TAG = BluetoothConnectionThread.class.getCanonicalName();
	protected BluetoothSocket mSocket = null;
	protected BluetoothAdapter mBluetoothAdapter;
	protected BluetoothConnection mBTConnection;

	protected InputStream mmInStream;
	protected OutputStream mmOutStream;
	protected boolean isRunning = false;

	public static final int CLIENT = 0;
	public static final int SERVER = 1;
	public int type = -1;

	// protected BufferedOutputStream mOutputStream;
	// protected BufferedInputStream mInputStream;

	protected void startListening() {

		// Receiver Thread
		new Thread(new Runnable() {

			@Override
			public void run() {

				String msg = null;
				Log.i(TAG, "START listening for messages...");
				// while (isRunning ) {
				try {

					// STRINGS EMPFANGEN
					// while (isRunning
					// && (msg = mBufferedReader.readLine()) != null) {
					// Log.i(TAG, "Message received: " + msg);
					// if (listener != null)
					// listener.onReceiveMsgListener(msg);
					// }

					// byte[] bData = new byte[1024];
					byte[] bData = new byte[AudioRecorder.BufferElements2Rec];

					int bytesRead = 0;

					while (isRunning && (bytesRead = mmInStream.read(bData)) != -1) {
						// while (isRunning && (bytesRead = mInputStream.read(bData)) != -1) {
						// while (isRunning && (bytesRead = mInputStream.read(bData, 0, bData.length)) != -1) {

						// if (listener != null)
						// handleMsg(bData, listener);
						handleMsg(bData, bytesRead);

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// }
				Log.e(TAG, BluetoothConnectionThread.class.getSimpleName() + ": STOP listening for messages...");
				mBTConnection.onDisconnect();
			}
		}).start();

	}

	private void handleMsg(byte[] bData, int bytesRead) {

		Log.i(TAG, "Empfangen: bytesRead: " + bytesRead + "; bData.length: " + bData.length);

		// Gucke ob String oder Stream ankommt
		byte type = 0; // 0 = String

		// 100 ist geschätzter Wert, String wird vermutlich immer kleiner sein bzw. wir sorgen dafür, dass sie es sind
		if (bytesRead >= 100)
			type = 1; // 1 = Audio

		// Leite die empfangenen Nachrichten an den OnReceiveMsgListener weiter
		if (mBTConnection.getOnReceiveDataListener() != null)
			mBTConnection.getOnReceiveDataListener().onReceiveDataListener(bData, type, bytesRead);
	}

	public void sendData(byte[] data) {
		if (mmOutStream != null) {
			// if (mOutputStream != null) {
			try {
				Log.i(TAG, "Sende: data.length: " + data.length);

				mmOutStream.write(data);
				// mOutputStream.write(data);
				// mOutputStream.write(data, 0, data.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "No Message sent. Outstream is null!");
		}
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void kill() {

		Log.e(TAG, "Stopping Bluetooth connection...");

		isRunning = false;

		mBTConnection.onDisconnect();

		try {
			if (mSocket != null)
				mSocket.close();

			mSocket = null;
			// mmInStream = null;
			// mmOutStream = null;

			if (mBTConnection.getOnConnectionLostListener() != null)
				mBTConnection.getOnConnectionLostListener().onConnectionLostListener("Bluetooth Connection closed");

		} catch (IOException e) {
		}
	}

	public boolean isConnected() {
		if (mSocket == null) {
			return false;
		} else {
			return mSocket.isConnected();
		}
	}
}
