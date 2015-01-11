package babyfon.view.fragment.setup.parentmode;

import java.util.ArrayList;

import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.connectivity.bluetooth.BluetoothListAdapter;
import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPBroadcastSender;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SetupSearchDevicesFragment extends Fragment {

	// Define ui elements
	private Button btnBackward;
	private Button btnForward;
	private ListView listViewDevices;
	private TextView title;

	private static ArrayList<BabyfonDevice> device;

	private SetupCompleteParentsModeFragment nextFragment;

	private int connectivityType;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	private ConnectionInterface mConnection;

	private static final String TAG = TCPReceiver.class.getCanonicalName();

	// Constructor
	public SetupSearchDevicesFragment(Context mContext) {
		nextFragment = new SetupCompleteParentsModeFragment(mContext);

		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
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

		// Initialize ListView
		listViewDevices = (ListView) view.findViewById(R.id.listView_devices);

		// Initialize Buttons
		btnBackward = (Button) view.findViewById(R.id.btn_backward_search);
		btnBackward.setTypeface(mTypeface_i);
		btnForward = (Button) view.findViewById(R.id.btn_forward_search);
		btnForward.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_search);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_search, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		device = new ArrayList<BabyfonDevice>();

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

		// Setup List-View
		listViewDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mConnection.connectToDeviceFromList(position);
			}
		});

		btnBackward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mFragmentManager.beginTransaction()
						.replace(R.id.frame_container, new SetupConnectionParentsModeFragment(mContext), null)
						.addToBackStack(null).commit();
			}
		});

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
												if (MainActivity.mTCPReceiver != null) {
													// Stop TCP Receiver
													Log.i(TAG, "Try to stop TCP receiver...");
													MainActivity.mTCPReceiver.stop();
												} else {
													Log.e(TAG, "TCP receiver is not running.");
												}
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
		if (device == null) {
			device = new ArrayList<BabyfonDevice>();
		}

		device.add(new BabyfonDevice(ip, name));
		Log.i(TAG, "Device found: " + ip + " | " + name);
		Log.i(TAG, "Number of devices: " + device.size());
	}

	static class BabyfonDevice {
		private String ip;
		private String name;

		public BabyfonDevice(String ip, String name) {
			this.ip = ip;
			this.name = name;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (device == null) {
			device = new ArrayList<BabyfonDevice>();
		}

		if (btnForward != null) {
			updateUI();
		}
	}
}