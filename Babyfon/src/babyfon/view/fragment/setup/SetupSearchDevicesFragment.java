package babyfon.view.fragment.setup;

import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import babyfon.adapter.DeviceListAdapter;
import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.ConnectionInterface.OnConnnectedListener;
import babyfon.connectivity.bluetooth.BluetoothHandler;
import babyfon.connectivity.wifi.TCPSender;
import babyfon.connectivity.wifi.UDPSender;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.model.DeviceListItemModel;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.OverviewFragment;

public class SetupSearchDevicesFragment extends Fragment {

	// Define ui elements
	private Button btnBackward;
	private Button btnRefresh;
	private static ListView listViewDevices;
	private TextView subtitle;
	private TextView title;

	// private ArrayList<DeviceListItemModel> devices;
	private DeviceListAdapter mDevicesAdapter;

	private int connectivityType;

	private ModuleHandler mModuleHandler;
	private ConnectionInterface mConnection;
	private BluetoothHandler mBtHandler;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	private static final String TAG = SetupSearchDevicesFragment.class.getCanonicalName();

	// Constructor
	public SetupSearchDevicesFragment(Context mContext) {
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnRefresh.setBackgroundResource(R.drawable.btn_selector_male);
			btnBackward.setBackgroundResource(R.drawable.btn_selector_male);
			listViewDevices.setBackgroundResource(R.drawable.listview_male);
		} else {
			btnRefresh.setBackgroundResource(R.drawable.btn_selector_female);
			btnBackward.setBackgroundResource(R.drawable.btn_selector_female);
			listViewDevices.setBackgroundResource(R.drawable.listview_female);
		}
	}

	public void initList() {

		mDevicesAdapter = new DeviceListAdapter(mContext, R.layout.listview_devices);
		// mDevicesAdapter = new DeviceListAdapter(mContext, R.layout.bluetooth_row_element);

		// Assign adapter to ListView
		listViewDevices.setAdapter(mDevicesAdapter);

		// ListView Item Click Listener
		listViewDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String deviceName = mDevicesAdapter.getItem(position).getDeviceName();
				String deviceIP = mDevicesAdapter.getItem(position).getIP();

				Log.d(TAG, "Selected item: " + deviceName + " (" + deviceIP + ")");

				// Bluetooth erstmal ausgelagert
				if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
					mSharedPrefs.setRemoteAddressTemp(deviceIP);
					mSharedPrefs.setRemoteName(deviceName);
					openConnectDialog(deviceName, deviceIP);
				}
				if (mSharedPrefs.getConnectivityTypeTemp() == 2) {
					mSharedPrefs.setRemoteAddressTemp(deviceIP);
					mSharedPrefs.setRemoteName(deviceName);
					openAuthDialog(deviceName, deviceIP);
				}
			}
		});
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

		// Initialize ListView
		listViewDevices = (ListView) view.findViewById(R.id.listView_devices);

		// Initialize Buttons
		btnRefresh = (Button) view.findViewById(R.id.btn_refresh_list);
		btnRefresh.setTypeface(mTypeface_i);
		btnBackward = (Button) view.findViewById(R.id.btn_backwardSetupSearch);
		btnBackward.setTypeface(mTypeface_i);

		// Initialize TextViews
		subtitle = (TextView) view.findViewById(R.id.subtitle_setup_search);
		subtitle.setTypeface(mTypeface_i);
		title = (TextView) view.findViewById(R.id.title_search);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_search, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);

		initList();

		mModuleHandler.unregisterBattery();

		connectivityType = mSharedPrefs.getConnectivityTypeTemp();
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

		btnBackward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
				ft.replace(R.id.frame_container, new SetupConnectionFragment(mContext), null).addToBackStack(null).commit();
			}
		});

		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshDeviceList();
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
					new AlertDialog.Builder(getActivity()).setTitle(mContext.getString(R.string.dialog_title_cancel_setup))
							.setMessage(mContext.getString(R.string.dialog_message_cancel_setup))
							.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
							.setPositiveButton(mContext.getString(R.string.dialog_button_yes), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									if (mSharedPrefs.getConnectivityType() != 2) {
										mModuleHandler.stopTCPReceiver();
									}
									if (mSharedPrefs.getDeviceMode() == 0) {
										mFragmentManager.beginTransaction()
												.replace(R.id.frame_container, new OverviewFragment(mContext), null).addToBackStack(null)
												.commit();
									} else if (mSharedPrefs.getDeviceMode() == 1) {
										mFragmentManager.beginTransaction()
												.replace(R.id.frame_container, new BabyMonitorFragment(mContext), null)
												.addToBackStack(null).commit();
									} else {
										mFragmentManager.beginTransaction()
												.replace(R.id.frame_container, new SetupStartFragment(mContext), null).addToBackStack(null)
												.commit();
									}
								}
							}).create().show();
					break;
				}
				return true;
			}
		});
	}

	public void refreshDeviceList() {
		mDevicesAdapter.clear();

		if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
			mBtHandler.searchDevices();
		} else if (mSharedPrefs.getConnectivityTypeTemp() == 2) {
			// initList();
			initViewBWifi();
		}
	}

	public void initViewBluetooth() {
		mModuleHandler.stopTCPReceiver();

		mBtHandler = new BluetoothHandler(mContext);
		mBtHandler.enableBluetooth();
		mBtHandler.prepareForSearch(mDevicesAdapter);

		if (MainActivity.mBoundService != null) {
			mConnection = MainActivity.mBoundService.getConnection();

			mConnection.setOnConnnectedListener(new OnConnnectedListener() {
				@Override
				public void onConnectedListener(String deviceName) {
					// Verbunden also auf die Abschlussseite wechseln
					getFragmentManager().beginTransaction()
							.replace(R.id.frame_container, new SetupCompleteParentsModeFragment(mContext), null).addToBackStack(null)
							.commit();
				}
			});
		}

	}

	public void initViewBWifi() {
		mModuleHandler.startTCPReceiver();
		new UDPSender(mContext).sendUDPMessage(new WifiHandler(mContext).getNetworkAddressClassC());
	}

	public void initViewBWifiDirect() {

	}

	public void setNewDevice(String ip, String name) {

		mDevicesAdapter.add(new DeviceListItemModel(name, ip));

		Log.i(TAG, "Device found: " + ip + " | " + name);
		Log.i(TAG, "Number of devices: " + mDevicesAdapter.getCount());
	}

	public void openAuthDialog(final String deviceName, String deviceIP) {
		((MainActivity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle("Mit \"" + deviceName + "\" verbinden");
				alert.setMessage("Passwort:");

				// Set an EditText view to get user input
				final EditText input = new EditText(mContext);
				input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				input.setFilters(new InputFilter[] {
						// Maximum 4 characters
						new InputFilter.LengthFilter(4),
						// Digits only.
						DigitsKeyListener.getInstance(), });

				// Digits only & use numeric soft-keyboard.
				input.setKeyListener(DigitsKeyListener.getInstance());
				alert.setView(input);

				alert.setPositiveButton("Senden", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String password = input.getText().toString();
						String localIP = null;
						try {
							localIP = new WifiHandler(mContext).getLocalIPv4Address();
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						new TCPSender(mContext).sendMessage(mSharedPrefs.getRemoteAddressTemp(),
								mContext.getString(R.string.BABYFON_MSG_AUTH_REQ) + ";" + password + ";" + localIP + ";"
										+ android.os.Build.MODEL);
					}
				});

				alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				alert.show();
			}
		});
	}

	public void openConnectDialog(final String deviceName, final String deviceIP) {
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

		alert.setTitle("Verbinden");
		alert.setMessage("Mit " + deviceName + " verbinden?");

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				// mConnection.connectToAdress(deviceIP);
				MainActivity.mBoundService.connectTo(deviceIP);

			}
		});

		alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mDevicesAdapter != null)
			mDevicesAdapter.clear();

		if (btnRefresh != null && listViewDevices != null) {
			updateUI();
		}
	}
}
