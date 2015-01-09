package babyfon.connectivity;

public interface ConnectionInterface {

	public void searchDevices();

	public void connectToDeviceFromList(int position);

	// public ListAdapter getListAdapter();

	public interface OnSearchStatusChangedListener {

		void onSearchStatusChanged(boolean isSearching);
	}

	public void setOnSearchStatusChangedListener(OnSearchStatusChangedListener l);

}
