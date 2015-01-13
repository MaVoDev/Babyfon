package babyfon.view.fragment.setup.babymode;

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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SetupConnectionBabyModeFragment extends Fragment {

	// Define UI elements
	private Button btnBackward;
	private Button btnForward;
	private CheckBox chkBoxBluetooth;
	private CheckBox chkBoxWifi;
	private CheckBox chkBoxWifiDirect;
	private TextView title;
	private TextView infoText;

	private BluetoothHandler mBluetoothHandler;
	private WifiHandler mWifiHandler;

	private SetupForwardingFragment nextFragment;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	private boolean isBluetoothAvailable = false;
	private boolean isWifiAvailable = false;

	// Constructor
	public SetupConnectionBabyModeFragment(Context mContext) {
		nextFragment = new SetupForwardingFragment(mContext);
		mBluetoothHandler = new BluetoothHandler();
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
		chkBoxBluetooth.setTypeface(mTypeface_i);
		chkBoxWifi = (CheckBox) view.findViewById(R.id.chkBoxConnectionWifi);
		chkBoxWifi.setEnabled(true);
		chkBoxWifi.setTypeface(mTypeface_i);
		chkBoxWifiDirect = (CheckBox) view.findViewById(R.id.chkBoxConnectionWifiDirect);
		chkBoxWifiDirect.setEnabled(true);
		chkBoxWifiDirect.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.text_titleConnectionBaby);
		title.setTypeface(mTypeface_bi);
		infoText = (TextView) view.findViewById(R.id.text_connection);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_connection_baby_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);

		if (!isBluetoothAvailable) {
			chkBoxBluetooth.setEnabled(false);
		}

		if (!isWifiAvailable) {
			chkBoxWifi.setEnabled(false);
			chkBoxWifiDirect.setEnabled(false);
		}

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
				if (chkBoxBluetooth.isChecked() || chkBoxWifi.isChecked() || chkBoxWifiDirect.isChecked()) {
					mSharedPrefs.setBluetoothSharedStateTemp(chkBoxBluetooth.isChecked());
					mSharedPrefs.setWiFiSharedStateTemp(chkBoxWifi.isChecked());
					mSharedPrefs.setWiFiDirectSharedStateTemp(chkBoxWifiDirect.isChecked());
					
					mFragmentManager.beginTransaction().replace(R.id.frame_container, nextFragment, null)
							.addToBackStack(null).commit();
				} else {
					Toast toast = Toast.makeText(mContext, "W�hle mindestens eine Verbindung aus.", Toast.LENGTH_SHORT);
					toast.show();
				}
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