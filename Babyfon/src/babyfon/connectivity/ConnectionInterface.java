package babyfon.connectivity;

import android.widget.ListAdapter;

public interface ConnectionInterface {

	public void searchDevices();

	public void connectToDeviceFromList(int position);

	public ListAdapter getListAdapter();

}
