package babyfon.view.fragment.setup.babymode;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
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
	private Button btnBackward;
	private Button btnForward;
	private CheckBox chkBoxBluetooth;
	private CheckBox chkBoxWifi;
	private CheckBox chkBoxWifiDirect;
	private TextView title;

	private BluetoothHandler mBluetoothHandler;
	private WifiHandler mWifiHandler;

	private SetupPrivacyFragment mSetupPrivacyFragment;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	private boolean isBluetoothAvailable = false;
	private boolean isWifiAvailable = false;

	// Constructor
	public SetupConnectionBabyFragment(Context mContext) {
		mSetupPrivacyFragment = new SetupPrivacyFragment(mContext);
		mBluetoothHandler = new BluetoothHandler(mContext);
		mWifiHandler = new WifiHandler(mContext);

		mSharedPrefs = new SharedPrefs(mContext);

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

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnBackward.setBackgroundResource(R.drawable.btn_selector_male);
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnBackward.setBackgroundResource(R.drawable.btn_selector_female);
			btnForward.setBackgroundResource(R.drawable.btn_selector_female);
		}
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface_bi = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");
		Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");

		// Initialize Buttons
		btnForward = (Button) view.findViewById(R.id.btn_forwardSetupConnectionBabyMode);
		btnForward.setTypeface(mTypeface_i);
		btnBackward = (Button) view.findViewById(R.id.btn_backwardSetupConnectionBabyMode);
		btnBackward.setTypeface(mTypeface_i);

		// Initialize Checkboxes
		chkBoxBluetooth = (CheckBox) view.findViewById(R.id.chkBoxConnectionBluetooth);
		chkBoxBluetooth.setEnabled(true);
		chkBoxWifi = (CheckBox) view.findViewById(R.id.chkBoxConnectionWifi);
		chkBoxWifi.setEnabled(true);
		chkBoxWifiDirect = (CheckBox) view.findViewById(R.id.chkBoxConnectionWifiDirect);
		chkBoxWifiDirect.setEnabled(true);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.text_titleConnectionBaby);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_connection_babymode, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		if (!isBluetoothAvailable) {
			chkBoxBluetooth.setEnabled(false);
		}

		if (!isWifiAvailable) {
			chkBoxWifi.setEnabled(false);
			chkBoxWifiDirect.setEnabled(false);
		}

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (chkBoxBluetooth.isChecked() || chkBoxWifi.isChecked() || chkBoxWifiDirect.isChecked()) {
					// TODO gewählte Verbindungen freischalten/starten
					fragmentManager.beginTransaction().replace(R.id.frame_container, mSetupPrivacyFragment, null)
							.addToBackStack(null).commit();
				} else {
					Toast toast = Toast.makeText(mContext, "Wähle mindestens eine Verbindung aus.", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnBackward != null && btnForward != null) {
			updateUI();
		}
	}
}