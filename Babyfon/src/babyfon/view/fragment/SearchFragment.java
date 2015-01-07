package babyfon.view.fragment;

import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.connectivity.bluetooth.BluetoothListAdapter;
import babyfon.connectivity.wifi.UDPBroadcastSender;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.performance.Sound;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SearchFragment extends Fragment {

	// Define UI elements
	private ListView listViewDevices;
	private Button btnCompleteSetup;
	private Button btnSearchDevices;
	private TextView titleConnectivity;

	private int connectivityType;
	private int deviceMode;

	private SharedPrefs mSharedPrefs;
	private Sound mSound;

	private Context mContext;

	private ConnectionInterface mConnection;

	// Constructor
	public SearchFragment(Context mContext) {

		setArguments(new Bundle());

		this.mSharedPrefs = new SharedPrefs(mContext);
		this.mSound = new Sound(mContext);
		this.mContext = mContext;
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {

		// Initialize ListView
		listViewDevices = (ListView) view.findViewById(R.id.listView_devices);

		// Initialize Buttons
		btnCompleteSetup = (Button) view.findViewById(R.id.btn_completeSetup);
		btnSearchDevices = (Button) view.findViewById(R.id.btn_searchDevices);

		// Initialize TextViews
		titleConnectivity = (TextView) view.findViewById(R.id.titleConnectivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_serach, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		connectivityType = mSharedPrefs.getConnectivityType();
		switch (connectivityType) {
		case 1:
			initViewBluetooth();
			break;
		case 2:
			initViewBWifi();
			break;
		case 3:
			initViewBWifiDirect();
			break;
		}

		// Setup Search-Button
		btnSearchDevices.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mConnection.searchDevices();
			}
		});

		// Setup List-View
		listViewDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mConnection.connectToDeviceFromList(position);
			}
		});

		// OnClickListener for the Button btnCompleteSetup
		btnCompleteSetup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Store values in SharedPreferences
				mSharedPrefs.setDeviceMode(deviceMode);
				mSharedPrefs.setConnectivityType(connectivityType);

				startParentMode();
				fragmentManager.beginTransaction().replace(R.id.frame_container, new OverviewFragment(mContext), null)
						.addToBackStack(null).commit();
			}
		});
		return view;
	}

	public void startBabyMode() {
		System.out.println("Start baby mode...");

		mSound.mute();
	}

	public void startParentMode() {
		System.out.println("Start parent mode...");
		mSound.soundOn();

	}

	public void initViewBluetooth() {
		titleConnectivity.setText(getString(R.string.connect_bluetooth));

		BluetoothListAdapter deviceListAdapter = new BluetoothListAdapter(mContext, R.layout.bluetooth_row_element);
		mConnection = new BluetoothConnection(mContext, deviceListAdapter);

		// Setup ListView Adapter
		// listViewDevices.setAdapter(mConnection.getListAdapter()); // TODO
		// gucken ob es funzt
		listViewDevices.setAdapter(deviceListAdapter);
	}

	public void initViewBWifi() {
		titleConnectivity.setText(getString(R.string.connect_wifi));
		new UDPBroadcastSender(mContext).sendUDPMessage(new WifiHandler(mContext).getNetworkAddressClassC());
	}

	public void initViewBWifiDirect() {
		titleConnectivity.setText(getString(R.string.connect_wifip2p));
	}
}
