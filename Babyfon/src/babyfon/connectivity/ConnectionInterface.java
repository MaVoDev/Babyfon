package babyfon.connectivity;

public interface ConnectionInterface {

	//
	// Methoden
	//

	public void searchDevices();

	public void connectToDeviceFromList(int position);

	public void closeConnection();

	public void sendMessage(String message);

	public void sendData(Object data);

	//
	// Listener
	//

	public interface OnSearchStatusChangedListener {

		void onSearchStatusChanged(boolean isSearching);
	}

	public interface OnReceiveMsgListener {
		void onReceiveMsgListener(String msg);
	}

	public interface OnConnectionLostListener {
		void onConnectionLostListener(String errorMsg);
	}

	public void setOnSearchStatusChangedListener(OnSearchStatusChangedListener l);

	public void setOnReceiveMsgListener(OnReceiveMsgListener l);

	public void setOnConnectionLostListener(OnConnectionLostListener l);
}
