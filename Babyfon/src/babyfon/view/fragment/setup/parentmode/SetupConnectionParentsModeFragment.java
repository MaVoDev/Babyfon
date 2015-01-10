package babyfon.view.fragment.setup.parentmode;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.overview.OverviewBabyFragment;
import babyfon.view.fragment.overview.OverviewParentsFragment;
import babyfon.view.fragment.setup.SetupDeviceModeFragment;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SetupConnectionParentsModeFragment extends Fragment {

	// Define UI elements
	private Button btnBackward;
	private Button btnForward;
	private RadioGroup rgConnectivity;
	private RadioButton rbBluetooth;
	private RadioButton rbWifi;
	private RadioButton rbWifiDirect;
	private TextView title;

	private BluetoothHandler mBluetoothHandler;
	private WifiHandler mWifiHandler;

	private SetupSearchDevicesFragment nextFragment;

	private boolean isBluetoothAvailable = false;
	
	private boolean isWifiAvailable = false;
	private boolean isWifiDirectAvailable = false;

	private int connectivityType;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public SetupConnectionParentsModeFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
		nextFragment = new SetupSearchDevicesFragment(mContext);
		mBluetoothHandler = new BluetoothHandler(mContext);
		mWifiHandler = new WifiHandler(mContext);

		this.mContext = mContext;
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
		btnBackward = (Button) view.findViewById(R.id.btn_backwardSetupConnectionParentsMode);
		btnBackward.setTypeface(mTypeface_i);
		btnForward = (Button) view.findViewById(R.id.btn_forwardSetupConnectionParentsMode);
		btnForward.setTypeface(mTypeface_i);

		// Initialize RadioGroups
		rgConnectivity = (RadioGroup) view.findViewById(R.id.radioConnectivity);

		// Initialize RadioButtons
		rbBluetooth = (RadioButton) view.findViewById(R.id.radioConnectivityBluetooth);
		rbWifi = (RadioButton) view.findViewById(R.id.radioConnectivityWifi);
		rbWifiDirect = (RadioButton) view.findViewById(R.id.radioConnectivityWifiDirect);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_connection_parentsmode);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_connection_parents_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);
		getAvailability();

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

		btnBackward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mFragmentManager.beginTransaction()
						.replace(R.id.frame_container, new SetupDeviceModeFragment(mContext), null)
						.addToBackStack(null).commit();
			}
		});

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setConnectivityTypeTemp(connectivityType);
				mFragmentManager.beginTransaction().replace(R.id.frame_container, nextFragment, null)
						.addToBackStack(null).commit();
			}
		});
		
		onBackPressed(view, mFragmentManager);

		return view;
	}
	
	public void onBackPressed(View view, final FragmentManager mFragmentManager) {
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() != KeyEvent.ACTION_DOWN)
					return true;

				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					new AlertDialog.Builder(getActivity())
							.setTitle(mContext.getString(R.string.dialog_title_cancel_setup))
							.setMessage(mContext.getString(R.string.dialog_message_cancel_setup))
							.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
							.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											if (mSharedPrefs.getDeviceMode() == 0) {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container,
																new OverviewBabyFragment(mContext), null)
														.addToBackStack(null).commit();
											} else {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container,
																new OverviewParentsFragment(mContext), null)
														.addToBackStack(null).commit();
											}
										}
									}).create().show();
					break;
				}
				return true;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnBackward != null && btnForward != null) {
			updateUI();
		}
	}
}
