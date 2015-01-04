package babyfon.view.fragment;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OverviewFragment extends Fragment {

	// Define UI elements
	private TextView tvConnectivityType;
	private TextView tvDeviceMode;

	private SharedPrefs mSharedPrefs;

	public OverviewFragment(Context mContext) {
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
		tvDeviceMode = (TextView) view.findViewById(R.id.tv_deviceMode);

		// Set values (tvConnectivityType)
		if (mSharedPrefs.getConnectivityType() == 1) {
			tvConnectivityType.setText(R.string.connect_bluetooth);
		} else if (mSharedPrefs.getConnectivityType() == 2) {
			tvConnectivityType.setText(R.string.connect_wifi);
		} else if (mSharedPrefs.getConnectivityType() == 3) {
			tvConnectivityType.setText(R.string.connect_wifip2p);
		} else {
			tvConnectivityType.setText(R.string.connect_null);
		}

		// Set values (tvDeviceMode)
		if (mSharedPrefs.getDeviceMode() == 0) {
			tvDeviceMode.setText(R.string.mode_baby);
		} else if (mSharedPrefs.getDeviceMode() == 1) {
			tvDeviceMode.setText(R.string.mode_parents);
		} else {
			tvDeviceMode.setText(R.string.mode_null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_overview, container, false);

		initUiElements(view);

		return view;
	}
}
