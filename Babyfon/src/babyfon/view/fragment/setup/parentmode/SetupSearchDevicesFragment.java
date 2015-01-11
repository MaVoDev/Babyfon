package babyfon.view.fragment.setup.parentmode;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import babyfon.Message;
import babyfon.adapter.DeviceListAdapter;
import babyfon.adapter.NavigationDrawerListAdapter;
import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.connectivity.bluetooth.BluetoothListAdapter;
import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPBroadcastSender;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.model.DeviceListItemModel;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.overview.OverviewBabyFragment;
import babyfon.view.fragment.overview.OverviewParentsFragment;
import babyfon.view.fragment.setup.SetupStartFragment;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetupSearchDevicesFragment extends Fragment {

	// Define ui elements
	private Button btnRefresh;
	private static ListView listViewDevices;
	private TextView title;

	private static boolean isConnected = false;

	private static ArrayList<DeviceListItemModel> devices;

	private SetupCompleteParentsModeFragment nextFragment;

	private int connectivityType;

	private ModuleHandler mModuleHandler;
	private static SharedPrefs mSharedPrefs;

	private static Context mContext;

	private static ConnectionInterface mConnection;

	private static final String TAG = SetupSearchDevicesFragment.class.getCanonicalName();

	// Constructor
	public SetupSearchDevicesFragment(Context mContext) {
		nextFragment = new SetupCompleteParentsModeFragment(mContext);

		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnRefresh.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnRefresh.setBackgroundResource(R.drawable.btn_selector_female);
		}
	}

	public static void updateList() {
		isConnected = false;
		DeviceListAdapter adapter = new DeviceListAdapter(mContext.getApplicationContext(), devices);

		// Assign adapter to ListView
		listViewDevices.setAdapter(adapter);

		// ListView Item Click Listener
		listViewDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String deviceName = devices.get(position).getDeviceName();
				String deviceIP = devices.get(position).getIP();

				Log.d(TAG, "Selected item: " + deviceName + " (" + deviceIP + ")");

				mSharedPrefs.setRemoteAdress(deviceIP);
				mSharedPrefs.setRemoteName(deviceName);

				openAuthDialog(deviceName, deviceIP);
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

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_search);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_search, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		devices = new ArrayList<DeviceListItemModel>();

		initUiElements(view);

		if (mSharedPrefs.getConnectivityTypeTemp() == 2) {
			new ModuleHandler(mContext).startTCPReceiver();
		} else {
			new ModuleHandler(mContext).stopTCPReceiver();
		}

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
					new AlertDialog.Builder(getActivity())
							.setTitle(mContext.getString(R.string.dialog_title_cancel_setup))
							.setMessage(mContext.getString(R.string.dialog_message_cancel_setup))
							.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
							.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											if (mSharedPrefs.getDeviceMode() != -1) {
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
											} else {
												mModuleHandler.stopTCPReceiver();
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

	public void refreshDeviceList() {
		if (mSharedPrefs.getConnectivityType() == 2) {
			devices.clear();
			initViewBWifi();
		}
	}

	public void initViewBluetooth() {
		BluetoothListAdapter deviceListAdapter = new BluetoothListAdapter(mContext, R.layout.bluetooth_row_element);
		mConnection = new BluetoothConnection(mContext, deviceListAdapter);

		listViewDevices.setAdapter(deviceListAdapter);
	}

	public void initViewBWifi() {
		new UDPBroadcastSender(mContext).sendUDPMessage(new WifiHandler(mContext).getNetworkAddressClassC());
	}

	public void initViewBWifiDirect() {

	}

	public static void setNewDevice(String ip, String name) {

		if (devices == null) {
			devices = new ArrayList<DeviceListItemModel>();
		}

		devices.add(new DeviceListItemModel(name, ip));

		Log.i(TAG, "Device found: " + ip + " | " + name);
		Log.i(TAG, "Number of devices: " + devices.size());

		updateList();
	}

	public static void openAuthDialog(final String deviceName, String deviceIP) {
		((MainActivity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

				alert.setTitle("Mit \"" + deviceName + "\" verbinden");
				alert.setMessage("Passwort:");

				// Set an EditText view to get user input
				final EditText input = new EditText(mContext);
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
						new Message(mContext).send(mContext.getString(R.string.MESSAGE_AUTH_REQ) + ";" + password + ";"
								+ localIP);
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

	@Override
	public void onResume() {
		super.onResume();

		if (devices == null) {
			devices = new ArrayList<DeviceListItemModel>();
		}

		if (btnRefresh != null) {
			updateUI();
		}
	}
}