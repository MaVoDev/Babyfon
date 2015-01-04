package babyfon.view.fragment;

import babyfon.init.R;
import babyfon.performance.Sound;
import babyfon.settings.SharedPrefs;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConnectionFragment extends Fragment {

	// Define UI elements
	private Button btnCompleteSetup;
	private ImageView iconConnectivity;
	private TextView titleConnectivity;

	private int connectivityType;
	private int deviceMode;

	private SharedPrefs mSharedPrefs;
	private Sound mSound;

	public ConnectionFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
		mSound = new Sound(mContext);
	}

	private void initUiElements(View view) {

		// Initialize Buttons
		btnCompleteSetup = (Button) view.findViewById(R.id.btn_complete_setup);

		// Initialize ImageViews
		iconConnectivity = (ImageView) view.findViewById(R.id.iconConnectivity);

		// Initialize TextViews
		titleConnectivity = (TextView) view.findViewById(R.id.titleConnectivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connection, container, false);

		initUiElements(view);

		Bundle bundle = this.getArguments();
		if (bundle != null) {
			connectivityType = bundle.getInt("connectivityType", -1);
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
			deviceMode = bundle.getInt("deviceMode", -1);
		}

		// OnClickListener for the Button btnCompleteSetup
		btnCompleteSetup = (Button) view.findViewById(R.id.btn_complete_setup);
		btnCompleteSetup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (deviceMode == 0) {
					// Start the baby mode for that device
					startBabyMode();
				} else {
					// Start the parent mode for that device
					startParentMode();
				}

				// Store values in SharedPreferences
				mSharedPrefs.setDeviceMode(deviceMode);
				mSharedPrefs.setConnectivityType(connectivityType);
			}
		});
		return view;
	}

	public void startBabyMode() {
		System.out.println("Start baby mode...");
		mSound.mute();
	}

	public void startParentMode() {
		System.out.println("Start parent mode...");
		mSound.soundOn();
	}

	public void initViewBluetooth() {
		iconConnectivity.setImageResource(R.drawable.ic_bluetooth);
		titleConnectivity.setText(getString(R.string.bluetooth));
	}

	public void initViewBWifi() {
		iconConnectivity.setImageResource(R.drawable.ic_wifi);
		titleConnectivity.setText(getString(R.string.wifi));
	}

	public void initViewBWifiDirect() {
		iconConnectivity.setImageResource(R.drawable.ic_wifidirect);
		titleConnectivity.setText(getString(R.string.wifip2p));
	}
}
