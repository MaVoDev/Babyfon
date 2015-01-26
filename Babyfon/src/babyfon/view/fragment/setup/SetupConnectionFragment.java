package babyfon.view.fragment.setup;

import java.net.SocketException;
import java.net.UnknownHostException;

import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.phone.CallStateListener;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.OverviewFragment;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SetupConnectionFragment extends Fragment {

	// Define UI elements
	private Button btnBackward;
	private Button btnForward;
	private RadioGroup radioGrpConnectivity;
	private RadioButton radioBluetooth;
	private RadioButton radioWifi;
	private RadioButton radioCall;
	private TextView subtitle;
	private TextView title;
	private TextView infoText;

	private boolean isBluetoothAvailable;
	private boolean isWifiAvailable;

	private int connectivityType;

	private BluetoothHandler mBluetoothHandler;
	private WifiHandler mWifiHandler;

	private SetupForwardingFragment nextFragmentBaby;
	private SetupCompleteBabyModeFragment nextFragmentBabyCall;
	private SetupSearchDevicesFragment nextFragmentParents;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public SetupConnectionFragment(Context mContext) {
		nextFragmentBaby = new SetupForwardingFragment(mContext);
		nextFragmentBabyCall = new SetupCompleteBabyModeFragment(mContext);
		nextFragmentParents = new SetupSearchDevicesFragment(mContext);
		mBluetoothHandler = new BluetoothHandler();
		mWifiHandler = new WifiHandler(mContext);

		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;

		getAvailability();
	}

	public void getAvailability() {
		if (mBluetoothHandler.getBluetoothState() != -1) {
			isBluetoothAvailable = true;
		} else {
			isBluetoothAvailable = false;
		}

		if (mWifiHandler.getWifiState() != -1) {
			isWifiAvailable = true;
		} else {
			isWifiAvailable = false;
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
		case R.id.radio_wifi:
			// Wi-Fi ausgewählt
			connectivityType = 2;
			break;
		case R.id.radio_bluetooth:
			// Bluetooth ausgewählt
			connectivityType = 1;
			break;
		case R.id.radio_call:
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

		// Update radio buttons
		if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
			radioBluetooth.setChecked(true);
			radioWifi.setChecked(false);
			if (mSharedPrefs.getDeviceModeTemp() == 0) {
				radioCall.setChecked(false);
			}
		}

		if (mSharedPrefs.getConnectivityTypeTemp() == 2) {
			radioBluetooth.setChecked(false);
			radioWifi.setChecked(true);
			if (mSharedPrefs.getDeviceModeTemp() == 0) {
				radioCall.setChecked(false);
			}
		}

		if (mSharedPrefs.getConnectivityTypeTemp() == 3) {
			if (mSharedPrefs.getDeviceModeTemp() == 0) {
				radioBluetooth.setChecked(false);
				radioWifi.setChecked(false);
				radioCall.setChecked(true);
			} else {
				radioBluetooth.setChecked(false);
				radioWifi.setChecked(true);
			}
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
		btnForward = (Button) view.findViewById(R.id.btn_forward_setup_connection);
		btnForward.setTypeface(mTypeface_i);
		btnBackward = (Button) view.findViewById(R.id.btn_backward_setup_connection);
		btnBackward.setTypeface(mTypeface_i);

		// Initialize RadioGroups
		radioGrpConnectivity = (RadioGroup) view.findViewById(R.id.radio_group_connection);

		// Initialize RadioButtons
		radioBluetooth = (RadioButton) view.findViewById(R.id.radio_bluetooth);
		radioBluetooth.setTypeface(mTypeface_i);
		radioWifi = (RadioButton) view.findViewById(R.id.radio_wifi);
		radioWifi.setTypeface(mTypeface_i);
		radioCall = (RadioButton) view.findViewById(R.id.radio_call);
		radioCall.setTypeface(mTypeface_i);
		if (mSharedPrefs.getDeviceModeTemp() == 0) {
			radioCall.setVisibility(View.VISIBLE);
		} else {
			radioCall.setVisibility(View.INVISIBLE);
		}

		// Initialize TextViews
		subtitle = (TextView) view.findViewById(R.id.subtitle_setup_connection);
		if (mSharedPrefs.getDeviceModeTemp() == 0) {
			subtitle.setText(R.string.subtitle_baby_mode);
		} else {
			subtitle.setText(R.string.subtitle_parents_mode);
		}
		subtitle.setTypeface(mTypeface_i);
		title = (TextView) view.findViewById(R.id.text_title_connection);
		title.setTypeface(mTypeface_bi);
		infoText = (TextView) view.findViewById(R.id.text_connection);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_connection, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);
		getAvailability();

		getConnectivityType(radioGrpConnectivity.getCheckedRadioButtonId());

		// Set Bluetooth availability
		radioBluetooth.setEnabled(isBluetoothAvailable);

		// Set Wi-Fi availability
		radioWifi.setEnabled(isWifiAvailable);

		// Check the next available connectivity
		if (!isWifiAvailable) {
			if (!isBluetoothAvailable) {
				radioGrpConnectivity.clearCheck();
				radioCall.setChecked(true);
			} else {
				radioGrpConnectivity.clearCheck();
				radioBluetooth.setChecked(true);
			}
		} else {
			radioGrpConnectivity.clearCheck();
			radioWifi.setChecked(true);
		}

		radioGrpConnectivity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId repräsentiert die ausgewählte Option.
				getConnectivityType(checkedId);
			}
		});

		btnBackward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
				ft.replace(R.id.frame_container, new SetupDeviceModeFragment(mContext), null).addToBackStack(null)
						.commit();
			}
		});

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setConnectivityTypeTemp(connectivityType);

				if (connectivityType == 2) {
					try {
						mSharedPrefs.setHostAdress(new WifiHandler(mContext).getLocalIPv4Address());
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
		
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

				if (mSharedPrefs.getDeviceModeTemp() == 0) {
					if (connectivityType == 3) {
						ft.replace(R.id.frame_container, nextFragmentBabyCall, null).addToBackStack(null).commit();
					} else {
						ft.replace(R.id.frame_container, nextFragmentBaby, null).addToBackStack(null).commit();
					}

				} else {
					ft.replace(R.id.frame_container, nextFragmentParents, null).addToBackStack(null).commit();
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
														.replace(R.id.frame_container, new OverviewFragment(mContext),
																null).addToBackStack(null).commit();
											} else if (mSharedPrefs.getDeviceMode() == 1) {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container,
																new BabyMonitorFragment(mContext), null)
														.addToBackStack(null).commit();
											} else {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container,
																new SetupStartFragment(mContext), null)
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
