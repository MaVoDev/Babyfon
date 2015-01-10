package babyfon.view.fragment.setup.babymode;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SetupPrivacyFragment extends Fragment {

	// Define ui elements
	private Button btnBackward;
	private Button btnForward;
	private CheckBox chkboxPrivacyCall;
	private CheckBox chkboxPrivacySMS;
	private TextView title;

	// Fragments
	private SetupCompleteBabyModeFragment mCompleteSetupFragment;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public SetupPrivacyFragment(Context mContext) {
		mCompleteSetupFragment = new SetupCompleteBabyModeFragment(mContext);
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

		// Initialize Buttons
		btnBackward = (Button) view.findViewById(R.id.btn_backwardSetupPrivacy);
		btnBackward.setTypeface(mTypeface_i);
		btnForward = (Button) view.findViewById(R.id.btn_forwardSetupPrivacy);
		btnForward.setTypeface(mTypeface_i);

		// Initialize Checkboxes
		chkboxPrivacyCall = (CheckBox) view.findViewById(R.id.chkbox_privacyCall);
		chkboxPrivacySMS = (CheckBox) view.findViewById(R.id.chkbox_privacySMS);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.text_titleSetupPrivacy);
		title.setTypeface(mTypeface_bi);
		
		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_privacy, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		btnForward.setOnClickListener(new OnClickListener() {
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

	@Override
	public void onResume() {
		super.onResume();
		if (btnBackward != null && btnForward != null) {
			updateUI();
		}
	}
}
