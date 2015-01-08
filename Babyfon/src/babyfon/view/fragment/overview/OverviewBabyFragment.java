package babyfon.view.fragment.overview;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OverviewBabyFragment extends Fragment {

	// Define UI elements
	private TextView tvDeviceMode;
	private TextView tvPrivacyCall;
	private TextView tvPrivacySMS;

	private SharedPrefs mSharedPrefs;

	public OverviewBabyFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {

		// Initialize TextViews
		tvDeviceMode = (TextView) view.findViewById(R.id.tv_overviewBabyModeDeviceMode);
		tvDeviceMode.setText(R.string.mode_baby);

		tvPrivacyCall = (TextView) view.findViewById(R.id.tv_overviewBabyModePrivacyCall);
		if (mSharedPrefs.getPrivacyCall()) {
			tvPrivacyCall.setText(R.string.privacy_call_no);
		} else {
			tvPrivacyCall.setText(R.string.privacy_call_yes);
		}

		tvPrivacySMS = (TextView) view.findViewById(R.id.tv_overviewBabyModePrivacySMS);
		if (mSharedPrefs.getPrivacySMS()) {
			tvPrivacySMS.setText(R.string.privacy_sms_no);
		} else {
			tvPrivacySMS.setText(R.string.privacy_sms_yes);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_overview_babymode, container, false);

		initUiElements(view);

		return view;
	}
}
