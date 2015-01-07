package babyfon.view.fragment;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.connectivity.wifi.WifiHandler;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class ConnectionBabyFragment extends Fragment {

	// Define UI elements
	Button btnForwardConnectionBaby;
	private CheckBox chkBoxBluetooth;
	private CheckBox chkBoxWifi;
	private CheckBox chkBoxWifiDirect;

	private BluetoothHandler mBluetoothHandler;
	private WifiHandler mWifiHandler;

	private CompleteSetupFragment mCompleteSetupFragment;

	private Context mContext;

	private boolean isBluetoothAvailable = false;
	private boolean isWifiAvailable = false;

	// Constructor
	public ConnectionBabyFragment(Context mContext) {
		mCompleteSetupFragment = new CompleteSetupFragment(mContext);
		mBluetoothHandler = new BluetoothHandler(mContext);
		mWifiHandler = new WifiHandler(mContext);

		this.mContext = mContext;

		getAvailability();
	}

	public void getAvailability() {
		if (mBluetoothHandler.getBluetoothState() != -1) {
			isBluetoothAvailable = true;
		}

		if (mWifiHandler.getWifiState() != -1) {
			isWifiAvailable = true;
		}
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Initialize Buttons
		btnForwardConnectionBaby = (Button) view.findViewById(R.id.btn_forwardConnectionBaby);

		// Initialize Checkboxes
		chkBoxBluetooth = (CheckBox) view.findViewById(R.id.chkBoxConnectionBluetooth);
		chkBoxBluetooth.setEnabled(true);
		chkBoxWifi = (CheckBox) view.findViewById(R.id.chkBoxConnectionWifi);
		chkBoxWifi.setEnabled(true);
		chkBoxWifiDirect = (CheckBox) view.findViewById(R.id.chkBoxConnectionWifiDirect);
		chkBoxWifiDirect.setEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connection_baby, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		if (!isBluetoothAvailable) {
			chkBoxBluetooth.setEnabled(false);
		}

		if (!isWifiAvailable) {
			chkBoxWifi.setEnabled(false);
			chkBoxWifiDirect.setEnabled(false);
		}

		btnForwardConnectionBaby.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (chkBoxBluetooth.isChecked() || chkBoxWifi.isChecked() || chkBoxWifiDirect.isChecked()) {
					// TODO gewählte Verbindungen freischalten/starten
					fragmentManager.beginTransaction().replace(R.id.frame_container, mCompleteSetupFragment, null)
							.addToBackStack(null).commit();
				} else {
					Toast toast = Toast.makeText(mContext, "Wählen Sie eine Verbindung.", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

		return view;
	}
}

// TODO Den Weiter Button erst aktivieren, wenn eine Option gewählt wurde
