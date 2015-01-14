package babyfon.view.fragment.setup.babymode;

import babyfon.init.R;
import babyfon.performance.Sound;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.overview.OverviewBabyFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SetupCompleteBabyModeFragment extends Fragment {

	// Define UI elements
	private Button btnForward;
	private TextView tvPassword;
	private TextView title;
	private TextView infoText;

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;
	private Sound mSound;

	private Context mContext;

	private static final String TAG = SetupCompleteBabyModeFragment.class.getCanonicalName();

	// Constructor
	public SetupCompleteBabyModeFragment(Context mContext) {
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);
		mSound = new Sound(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
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

		// Initialize Button
		btnForward = (Button) view.findViewById(R.id.btn_forward_complete_baby_mode);
		btnForward.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_setup_complete_baby_mode);
		title.setTypeface(mTypeface_bi);
		tvPassword = (TextView) view.findViewById(R.id.tv_password);
		tvPassword.setTypeface(mTypeface_i);
		infoText = (TextView) view.findViewById(R.id.text_complete_baby);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	public void handleModules() {
		if (mSharedPrefs.getWiFiSharedStateTemp()) {
			mModuleHandler.startTCPReceiver();
			mModuleHandler.startUDPReceiver();
		} else {
			mModuleHandler.stopTCPReceiver();
			mModuleHandler.stopUDPReceiver();
		}

		mSharedPrefs.setRemoteAdress(null);
		mSharedPrefs.setRemoteName(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(TAG, "Starting baby mode...");

		View view = inflater.inflate(R.layout.setup_complete_baby_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);
		handleModules();

		String password = getRandomPassword();
		tvPassword.setText(password);

		// Store values in the shared preferences
		mSharedPrefs.setDeviceMode(mSharedPrefs.getDeviceModeTemp());
		Log.d(TAG, "Device mode: " + mSharedPrefs.getDeviceMode());
		mSharedPrefs.setBluetoothSharedState(mSharedPrefs.getBluetoothSharedStateTemp());
		Log.d(TAG, "Bluetooth state: " + mSharedPrefs.getBluetoothSharedState());
		mSharedPrefs.setWiFiSharedState(mSharedPrefs.getWiFiSharedStateTemp());
		Log.d(TAG, "Wi-Fi state: " + mSharedPrefs.getWiFiSharedState());
		mSharedPrefs.setWiFiDirectSharedState(mSharedPrefs.getWiFiDirectSharedStateTemp());
		Log.d(TAG, "Wi-Fi Direct state: " + mSharedPrefs.getWiFiDirectSharedState());
		mSharedPrefs.setForwardingCallInfo(mSharedPrefs.getForwardingCallInfoTemp());
		Log.d(TAG, "Forwarding Call info: " + mSharedPrefs.getForwardingCallInfo());
		mSharedPrefs.setForwardingSMS(mSharedPrefs.getForwardingSMSTemp());
		Log.d(TAG, "Forwarding SMS: " + mSharedPrefs.getForwardingSMS());
		mSharedPrefs.setForwardingSMSInfo(mSharedPrefs.getForwardingSMSInfoTemp());
		Log.d(TAG, "Forwarding SMS info: " + mSharedPrefs.getForwardingSMSInfo());
		mSharedPrefs.setPassword(password);
		Log.d(TAG, "Password: " + mSharedPrefs.getPassword());
		mSharedPrefs.setNumberOfAllowedConnections(mSharedPrefs.getNumberOfAllowedConnectionsTemp());
		Log.d(TAG, "Number of allowed connections: " + mSharedPrefs.getNumberOfAllowedConnections());
		mSharedPrefs.setNumberOfConnections(0);
		Log.d(TAG, "Number of connections: " + mSharedPrefs.getNumberOfConnections());

		mSound.mute();

		// OnClickListener for the Button btnCompleteSetup
		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mFragmentManager.beginTransaction()
						.replace(R.id.frame_container, new OverviewBabyFragment(mContext), null).addToBackStack(null)
						.commit();
			}
		});

		return view;
	}

	public String getRandomPassword() {
		// Generating a password between 1000 and 10000
		int password = (int) Math.floor(Math.random() * 9000 + 1000);

		return password + "";
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnForward != null) {
			updateUI();
		}
	}
}
