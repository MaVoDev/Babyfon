package babyfon.view.fragment;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.WiFiHandler;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
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

	private Context mContext;

	boolean isBluetoothAvailable = false;
	boolean isWiFiAvailable = false;
	boolean isWiFiDirectAvailable = false;

	int connectivityType;

	public ConnectivityFragment(Context mContext) {
		this.mContext = mContext;
		System.out.println("Modus: " + new SharedPrefs(mContext).getDeviceMode());
	}

	public void getAvailability() {
		BluetoothHandler mBluetoothHandler = new BluetoothHandler(mContext);
		WiFiHandler mWifiHandler = new WiFiHandler(mContext);

		if (mBluetoothHandler.getBluetoothState() != -1) {
			isBluetoothAvailable = true;
		}

		if (mWifiHandler.getWifiState() != -1) {
			isWiFiAvailable = true;
			isWiFiDirectAvailable = true;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connectivity, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		getAvailability();

		RadioGroup rgConnectivity = (RadioGroup) view.findViewById(R.id.radioConnectivity);
		getConnectivityType(rgConnectivity.getCheckedRadioButtonId());

		RadioButton rbBluetooth = (RadioButton) view.findViewById(R.id.radioConnectivityBluetooth);
		RadioButton rbWiFi = (RadioButton) view.findViewById(R.id.radioConnectivityWifi);
		RadioButton rbWiFiDirect = (RadioButton) view.findViewById(R.id.radioConnectivityWifiDirect);

		// Set Bluetooth availability
		rbBluetooth.setEnabled(isBluetoothAvailable);

		// Set Wi-Fi availability
		rbWiFi.setEnabled(isWiFiAvailable);

		// Set Wi-Fi Direct availability
		rbWiFiDirect.setEnabled(isWiFiDirectAvailable);

		// Check the next available connectivity
		if (!isBluetoothAvailable) {
			if (!isWiFiAvailable) {
				if (!isWiFiDirectAvailable) {
					rgConnectivity.clearCheck();
				}
			} else {
				rbWiFi.setChecked(true);
			}
		}

		rgConnectivity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId repräsentiert die ausgewählte Option.
				getConnectivityType(checkedId);
			}
		});

		Button button = (Button) view.findViewById(R.id.button_forward_connectivity);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt("connectivityType", connectivityType);
				ConnectionFragment connectionFragment = new ConnectionFragment(mContext);
				connectionFragment.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionFragment, null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}
}

// TODO Den Weiter Button erst aktivieren, wenn eine Option gewählt wurde