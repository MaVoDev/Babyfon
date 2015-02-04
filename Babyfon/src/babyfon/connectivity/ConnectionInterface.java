package babyfon.connectivity;

public interface ConnectionInterface {

	//
	// Methoden
	//

	public void connectToAdress(String adress);

	public void startServer();

	/** Will cancel an in-progress connection, and close the socket */
	public void stopConnection();

	/**
	 * Sends data over the established connection.
	 * 
	 * @param data
	 *            The data as byte-Array
	 * @param type
	 *            0 = String; 1 = Audio-Stream
	 */
	public void sendData(byte[] data, byte type);

	public void sendMessage(String msg);

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

	public interface OnConnectedListener {

		void onConnectedListener(String deviceName);
	}

	public void setOnSearchStatusChangedListener(OnSearchStatusChangedListener l);

	public void setOnReceiveDataListener(OnReceiveDataListener l);

	public void setOnConnectionLostListener(OnConnectionLostListener l);

	public void setOnConnectedListener(OnConnectedListener l);

	public void registerDisconnectHandler();

	public boolean isConnected();

	// TODO: Evtl noch einen OnConnectionFailed Listener einbauen

}
