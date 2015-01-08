package babyfon.view.fragment.setup.babymode;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SetupConnectionBabyFragment extends Fragment {

	// Define UI elements
	private Button btnForwardConnectionBaby;
	private CheckBox chkBoxBluetooth;
	private CheckBox chkBoxWifi;
	private CheckBox chkBoxWifiDirect;

	private BluetoothHandler mBluetoothHandler;
	private WifiHandler mWifiHandler;

	private SetupPrivacyFragment mSetupPrivacyFragment;

	private Context mContext;

	private boolean isBluetoothAvailable = false;
	private boolean isWifiAvailable = false;

	// Constructor
	public SetupConnectionBabyFragment(Context mContext) {
		mSetupPrivacyFragment = new SetupPrivacyFragment(mContext);
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

		// Initialize TextViews
		Typeface myTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/mtsc.ttf");
		TextView myTextView = (TextView) view.findViewById(R.id.tv_titleConnectionBaby);
		myTextView.setTypeface(myTypeface);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_connection_babymode, container, false);

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
					fragmentManager.beginTransaction().replace(R.id.frame_container, mSetupPrivacyFragment, null)
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