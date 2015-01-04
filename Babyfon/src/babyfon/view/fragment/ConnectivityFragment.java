package babyfon.view.fragment;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.WiFiHandler;
import babyfon.connectivity.wifi.WiFiHandler;
import babyfon.init.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ConnectivityFragment extends Fragment {

	// Define UI elements
	private RadioGroup rgConnectivity;
	private RadioButton rbBluetooth;
	private RadioButton rbWifi;
	private RadioButton rbWifiDirect;
	
	private BluetoothHandler mBluetoothHandler;
	private WiFiHandler mWifiHandler;

	private ConnectionFragment mConnectionFragment;

	private boolean isBluetoothAvailable = false;
	private boolean isWifiAvailable = false;
	private boolean isWifiDirectAvailable = false;

	private int connectivityType;
	private int deviceMode;

	// Constructor
	public ConnectivityFragment(Context mContext) {
		mConnectionFragment = new ConnectionFragment(mContext);
		mBluetoothHandler = new BluetoothHandler(mContext);
		mWifiHandler = new WiFiHandler(mContext);
	}

	public void getAvailability() {
		if (mBluetoothHandler.getBluetoothState() != -1) {
			isBluetoothAvailable = true;
		}

		if (mWifiHandler.getWifiState() != -1) {
			isWifiAvailable = true;
			isWifiDirectAvailable = true;
		}
	}

	/**
	 * Liest die ausgewählte Verbindungsart aus.
	 * 
	 * @param checkId
	 *            ausgewählte Verbindungsart
	 */
	private void getConnectivityType(int checkedId) {
		switch (checkedId) {
		case R.id.radioConnectivityBluetooth:
			// Bluetooth ausgewählt
			connectivityType = 1;
			break;
		case R.id.radioConnectivityWifi:
			// Wi-Fi ausgewählt
			connectivityType = 2;
			break;
		case R.id.radioConnectivityWifiDirect:
			// Wi-Fi Direct ausgewählt
			connectivityType = 3;
			break;
		}
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		
		// Initialize RadioGroups
		rgConnectivity = (RadioGroup) view.findViewById(R.id.radioConnectivity);
		
		// Initialize RadioButtons
		rbBluetooth = (RadioButton) view.findViewById(R.id.radioConnectivityBluetooth);
		rbWifi = (RadioButton) view.findViewById(R.id.radioConnectivityWifi);
		rbWifiDirect = (RadioButton) view.findViewById(R.id.radioConnectivityWifiDirect);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connectivity, container, false);

		final FragmentManager fragmentManager = getFragmentManager();
		
		initUiElements(view);
		getAvailability();

		Bundle bundle = this.getArguments();
		if (bundle != null) {
			deviceMode = bundle.getInt("deviceMode", -1);
		}
		
		getConnectivityType(rgConnectivity.getCheckedRadioButtonId());

		// Set Bluetooth availability
		rbBluetooth.setEnabled(isBluetoothAvailable);

		// Set Wi-Fi availability
		rbWifi.setEnabled(isWifiAvailable);

		// Set Wi-Fi Direct availability
		rbWifiDirect.setEnabled(isWifiDirectAvailable);

		// Check the next available connectivity
		if (!isBluetoothAvailable) {
			if (!isWifiAvailable) {
				if (!isWifiDirectAvailable) {
					rgConnectivity.clearCheck();
				}
			} else {
				rbWifi.setChecked(true);
			}
		}

		rgConnectivity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId repräsentiert die ausgewählte Option.
				getConnectivityType(checkedId);
			}
		});

		Button button = (Button) view.findViewById(R.id.btn_forwardConnectivity);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt("deviceMode", deviceMode);
				bundle.putInt("connectivityType", connectivityType);
				mConnectionFragment.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.frame_container, mConnectionFragment, null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}
}

// TODO Den Weiter Button erst aktivieren, wenn eine Option gewählt wurde