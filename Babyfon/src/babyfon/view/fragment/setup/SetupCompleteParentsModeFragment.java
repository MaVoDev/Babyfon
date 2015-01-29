package babyfon.view.fragment.setup;

import babyfon.audio.AudioDetection;
import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.ConnectionInterface.OnReceiveDataListener;
import babyfon.init.R;
import babyfon.performance.Sound;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.BabyMonitorFragment;
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

public class SetupCompleteParentsModeFragment extends Fragment {

	// Define UI elements
	private Button btnForward;
	private TextView subtitle;
	private TextView title;
	private TextView info;

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;
	private Sound mSound;

	private Context mContext;

	private static final String TAG = SetupCompleteParentsModeFragment.class.getCanonicalName();

	// Constructor
	public SetupCompleteParentsModeFragment(Context mContext) {
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
		btnForward = (Button) view.findViewById(R.id.btn_forward_complete_parents_mode);
		btnForward.setTypeface(mTypeface_i);

		// Initialize TextViews
		subtitle = (TextView) view.findViewById(R.id.subtitle_setup_complete_parents);
		subtitle.setTypeface(mTypeface_i);
		title = (TextView) view.findViewById(R.id.title_setup_complete_parents_mode);
		title.setTypeface(mTypeface_bi);
		info = (TextView) view.findViewById(R.id.text_connection_complete);
		info.setText("Du bist nun mit '" + mSharedPrefs.getRemoteName() + "' verbunden.");
		info.setTypeface(mTypeface_i);

		updateUI();
	}

	public void handleModules() {
		mModuleHandler.unregisterBattery();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(TAG, "Starting parents mode...");

		View view = inflater.inflate(R.layout.setup_complete_parents_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		if (mSharedPrefs.getDeviceMode() != 1) {
			// Turn the sound on if the baby mode or no mode was previously
			// active.
			mSound.soundOn();
		}

		// Store values in the shared preferences
		mSharedPrefs.setCounter(0);
		mSharedPrefs.setActiveStateBabyMode(false);
		mSharedPrefs.setDeviceMode(mSharedPrefs.getDeviceModeTemp());
		Log.d(TAG, "Device mode: " + mSharedPrefs.getDeviceMode());
		mSharedPrefs.setConnectivityType(mSharedPrefs.getConnectivityTypeTemp());
		Log.d(TAG, "Connectivity type: " + mSharedPrefs.getConnectivityType());

		if (mSharedPrefs.getConnectivityType() == 2) {
			mModuleHandler.startUDPReceiver();
			mSharedPrefs.setRemoteAddress(mSharedPrefs.getRemoteAddressTemp());
			Log.d(TAG, "Remote address: " + mSharedPrefs.getRemoteAddress());
		}

		initUiElements(view);
		handleModules();

		// OnClickListener for the Button btnCompleteSetup
		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
				ft.replace(R.id.frame_container, new BabyMonitorFragment(mContext), null).addToBackStack(null).commit();
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
