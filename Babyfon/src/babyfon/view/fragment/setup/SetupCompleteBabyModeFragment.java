package babyfon.view.fragment.setup;

import babyfon.Generator;
import babyfon.audio.AudioRecorder;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.init.R;
import babyfon.performance.Sound;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.OverviewFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
	private TextView subtitle;
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
		subtitle = (TextView) view.findViewById(R.id.subtitle_setup_complete_baby);
		subtitle.setTypeface(mTypeface_i);
		title = (TextView) view.findViewById(R.id.title_setup_complete_baby_mode);
		title.setTypeface(mTypeface_bi);
		tvPassword = (TextView) view.findViewById(R.id.tv_password);
		tvPassword.setTypeface(mTypeface_i);
		infoText = (TextView) view.findViewById(R.id.text_complete_baby);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	public void handleModules() {
		
		if(mSharedPrefs.getForwardingSMSInfoTemp() || mSharedPrefs.getForwardingSMSTemp()) {
			mModuleHandler.registerSMS();
		}
		
		if(mSharedPrefs.getForwardingCallInfoTemp()) {
			mModuleHandler.registerCall();
		}

		if (mSharedPrefs.getConnectivityTypeTemp() == 1) {
			MainActivity.mConnection = new BluetoothConnection(mContext);
			MainActivity.mConnection.startServer();

			MainActivity.mAudioRecorder = new AudioRecorder(MainActivity.mConnection);
			MainActivity.mAudioRecorder.startRecording();

		} else if (mSharedPrefs.getConnectivityTypeTemp() == 2) {
			mModuleHandler.startTCPReceiver();
			mModuleHandler.startUDPReceiver();
		} else {
			mModuleHandler.stopTCPReceiver();
			mModuleHandler.stopUDPReceiver();
		}

		mSharedPrefs.setRemoteAddress(null);
		mSharedPrefs.setRemoteName(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(TAG, "Starting baby mode...");

		View view = inflater.inflate(R.layout.setup_complete_baby_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);
		handleModules();

		String password = new Generator().getRandomPassword();
		tvPassword.setText(password);

		// Store values in the shared preferences
		mSharedPrefs.setActiveStateBabyMode(true);
		mSharedPrefs.setDeviceMode(mSharedPrefs.getDeviceModeTemp());
		Log.d(TAG, "Device mode: " + mSharedPrefs.getDeviceMode());
		mSharedPrefs.setConnectivityType(mSharedPrefs.getConnectivityTypeTemp());
		Log.d(TAG, "Connectivity type: " + mSharedPrefs.getConnectivityType());
		mSharedPrefs.setForwardingCallInfo(mSharedPrefs.getForwardingCallInfoTemp());
		Log.d(TAG, "Forwarding Call info: " + mSharedPrefs.getForwardingCallInfo());
		mSharedPrefs.setForwardingSMS(mSharedPrefs.getForwardingSMSTemp());
		Log.d(TAG, "Forwarding SMS: " + mSharedPrefs.getForwardingSMS());
		mSharedPrefs.setForwardingSMSInfo(mSharedPrefs.getForwardingSMSInfoTemp());
		Log.d(TAG, "Forwarding SMS info: " + mSharedPrefs.getForwardingSMSInfo());
		mSharedPrefs.setPassword(password);
		Log.d(TAG, "Password: " + mSharedPrefs.getPassword());

		mSound.mute();

		// OnClickListener for the Button btnCompleteSetup
		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
				ft.replace(R.id.frame_container, new OverviewFragment(mContext), null).addToBackStack(null).commit();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnForward != null) {
			updateUI();
		}
	}
}
