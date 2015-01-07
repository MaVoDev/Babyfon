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
	private TextView tvConnectivityType;
	private TextView tvDeviceMode;

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
		tvConnectivityType = (TextView) view.findViewById(R.id.tv_connectivityType);
		if (mSharedPrefs.getConnectivityType() == 1) {
			tvConnectivityType.setText(R.string.connect_bluetooth);
		} else if (mSharedPrefs.getConnectivityType() == 2) {
			tvConnectivityType.setText(R.string.connect_wifi);
		} else if (mSharedPrefs.getConnectivityType() == 3) {
			tvConnectivityType.setText(R.string.connect_wifip2p);
		} else {
			tvConnectivityType.setText(R.string.connect_null);
		}
		
		tvDeviceMode = (TextView) view.findViewById(R.id.tv_deviceMode);
		tvDeviceMode.setText(R.string.mode_baby);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_overview_baby, container, false);

		initUiElements(view);

		return view;
	}
}