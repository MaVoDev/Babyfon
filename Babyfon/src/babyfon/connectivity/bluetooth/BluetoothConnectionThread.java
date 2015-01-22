package babyfon.connectivity.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import babyfon.audio.AudioRecording;
import babyfon.connectivity.ConnectionInterface.OnReceiveDataListener;

public abstract class BluetoothConnectionThread extends Thread {

	private static final String TAG = BluetoothConnectionThread.class.getCanonicalName();
	protected BluetoothSocket mSocket = null;
	protected BluetoothAdapter mBluetoothAdapter;
	protected BluetoothConnection mBTConnection;

	protected InputStream mmInStream;
	protected OutputStream mmOutStream;
	protected boolean isRunning = false;

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
					OnReceiveDataListener listener = mBTConnection.getOnReceiveMsgListener();

					// Leite die empfangenen Nachrichten an den
					// OnReceiveMsgListener weiter

					// STRINGS EMPFANGEN
					// while (isRunning
					// && (msg = mBufferedReader.readLine()) != null) {
					// Log.i(TAG, "Message received: " + msg);
					// if (listener != null)
					// listener.onReceiveMsgListener(msg);
					// }

					// byte[] bData = new byte[1024];
					byte[] bData = new byte[AudioRecording.BufferElements2Rec];

					int bytesRead = 0;

					while (isRunning && (bytesRead = mmInStream.read(bData)) != -1) {
						// while (isRunning && (bytesRead = mInputStream.read(bData)) != -1) {
						// while (isRunning && (bytesRead = mInputStream.read(bData, 0, bData.length)) != -1) {

						if (listener != null)
							// handleMsg(bData, listener);
							handleMsg(bData, bytesRead, listener);

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// }
				Log.i(TAG, "STOP listening for messages...");
			}
		}).start();

	}

	// private void handleMsg(byte[] bData, OnReceiveDataListener listener) {
	//
	// Log.i(TAG, "Sende: data.length: " + data.length);
	//
	// byte[] sendData = new byte[bData.length - 1];
	//
	// System.arraycopy(bData, 1, sendData, 0, sendData.length);
	//
	// listener.onReceiveDataListener(bData, bData[0]);
	//
	// }

	private void handleMsg(byte[] bData, int bytesRead, OnReceiveDataListener listener) {

		// TODO: METHODE ANPASSEN, DASS AUF DEM HUAWEI HIER NICHT BLOCKIERT

		Log.i(TAG, "Empfangen: bytesRead: " + bytesRead + "; bData.length: " + bData.length);

		// byte[] sendData = new byte[bData.length - 1];
		// System.arraycopy(bData, 1, sendData, 0, sendData.length);

		// byte[] receivedData = new byte[bData.length - 1];

		// byte[] receivedData = new byte[bytesRead - 1];
		// System.arraycopy(bData, 1, receivedData, 0, receivedData.length);
		// Log.i(TAG, "Verarbeitetes Array: " + receivedData.length);
		// Log.i(TAG, "TYPE: " + bData[0]);

		// Gucke ob String oder Stream ankommt

		byte type = 0; // 0 = String

		// 250 ist geschätzter Wert, String wird vermutlich immer kleiner sein bzw. wir sorgen dafür, dass sie es sind
		if (bytesRead >= 250)
			type = 1; // 1 = Audio

		listener.onReceiveDataListener(bData, type, bytesRead);
		// listener.onReceiveDataListener(receivedData, bData[0]);
		// listener.onReceiveDataListener(receivedData, type);

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
			Log.i(TAG, "No Message sent. Outstream is null!");
		}
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mSocket.close();
		} catch (IOException e) {
		}
	}

}
