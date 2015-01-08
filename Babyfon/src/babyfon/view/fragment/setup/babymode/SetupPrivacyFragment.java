package babyfon.view.fragment.setup.babymode;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class SetupPrivacyFragment extends Fragment {

	// Define ui elements
	private Button btnForwardSetupPrivacy;
	private CheckBox chkboxPrivacyCall;
	private CheckBox chkboxPrivacySMS;

	// Fragments
	private SetupCompleteBabyModeFragment mCompleteSetupFragment;

	private SharedPrefs mSharedPrefs;

	// Constructor
	public SetupPrivacyFragment(Context mContext) {
		mCompleteSetupFragment = new SetupCompleteBabyModeFragment(mContext);
		mSharedPrefs = new SharedPrefs(mContext);
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Initialize Buttons
		btnForwardSetupPrivacy = (Button) view.findViewById(R.id.btn_forwardSetupPrivacy);

		// Initialize Checkboxes
		chkboxPrivacyCall = (CheckBox) view.findViewById(R.id.chkbox_privacyCall);
		chkboxPrivacySMS = (CheckBox) view.findViewById(R.id.chkbox_privacySMS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_setup_privacy, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		btnForwardSetupPrivacy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (chkboxPrivacyCall.isChecked()) {
					mSharedPrefs.setPrivacyCall(true);
				} else {
					mSharedPrefs.setPrivacyCall(false);
				}

				if (chkboxPrivacySMS.isChecked()) {
					mSharedPrefs.setPrivacySMS(true);
				} else {
					mSharedPrefs.setPrivacySMS(false);
				}

				fragmentManager.beginTransaction().replace(R.id.frame_container, mCompleteSetupFragment, null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}
}
