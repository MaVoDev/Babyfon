package babyfon.connectivity;

import android.widget.ArrayAdapter;

public interface ConnectionInterface {

	//
	// Methoden
	//

	public void searchDevices();

	public void connectToDeviceFromList(int position);

	public void startServer();

	public <T> void startClient(T listAdapter);

	/** Will cancel an in-progress connection, and close the socket */
	public void closeConnection();

	/**
	 * Sends data over the established connection.
	 * 
	 * @param data
	 *            The data as byte-Array
	 * @param type
	 *            0 = String; 1 = Audio-Stream
	 */
	public void sendData(byte[] data, byte type);

	//
	// Listener
	//

	public interface OnSearchStatusChangedListener {

		void onSearchStatusChanged(boolean isSearching);
	}

	public interface OnReceiveDataListener {
		void onReceiveDataListener(byte[] bData, byte type, int bytesRead);
	}

	public interface OnConnectionLostListener {
		void onConnectionLostListener(String errorMsg);
	}

	public interface OnConnnectedListener {

		void onConnectedListener(String deviceName);
	}

	public void setOnSearchStatusChangedListener(OnSearchStatusChangedListener l);

	public void setOnReceiveDataListener(OnReceiveDataListener l);

	public void setOnConnectionLostListener(OnConnectionLostListener l);

	public void setOnConnnectedListener(OnConnnectedListener l);

	// TODO: Evtl noch einen OnConnectionFailed Listener einbauen

}
