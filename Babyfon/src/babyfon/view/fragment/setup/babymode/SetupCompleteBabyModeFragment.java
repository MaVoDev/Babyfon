package babyfon.view.fragment.setup.babymode;

import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.init.R;
import babyfon.performance.Sound;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.overview.OverviewBabyFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

	private SharedPrefs mSharedPrefs;
	private Sound mSound;

	private Context mContext;

	private static final String TAG = TCPReceiver.class.getCanonicalName();

	// Constructor
	public SetupCompleteBabyModeFragment(Context mContext) {
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
		btnForward = (Button) view.findViewById(R.id.btn_setup_complete);
		btnForward.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.text_titleSetupComplete);
		title.setTypeface(mTypeface_bi);
		tvPassword = (TextView) view.findViewById(R.id.tv_password);
		
		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(TAG, "Start baby mode...");

		// Start UDP receiver
		if (MainActivity.mUDPReceiver == null) {
			Log.i(TAG, "Try to start UDP receiver...");
			MainActivity.mUDPReceiver = new UDPReceiver(mContext);
			MainActivity.mUDPReceiver.start();
		}

		View view = inflater.inflate(R.layout.setup_complete_babymode, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		tvPassword.setText(getRandomPassword() + "");

		mSound.mute();

		// OnClickListener for the Button btnCompleteSetup
		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new OverviewBabyFragment(mContext), null).addToBackStack(null)
						.commit();
			}
		});

		return view;
	}

	public int getRandomPassword() {
		// Generating a password between 1000 and 10000
		int password = (int) Math.floor(Math.random() * 9000 + 1000);

		// Saving the password in SharedPreferences
		mSharedPrefs.setPassword(password);

		return password;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnForward != null) {
			updateUI();
		}
	}
}
