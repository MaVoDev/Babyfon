package babyfon.view.fragment.overview;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OverviewParentsFragment extends Fragment {

	// Define UI elements
	private TextView title;
	// private TextView tvConnectivityType;
	// private TextView tvDeviceMode;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public OverviewParentsFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_overview_parentsmode);
		title.setTypeface(mTypeface);

		// Initialize TextViews
		// tvConnectivityType = (TextView)
		// view.findViewById(R.id.tv_connectivityType);
		// if (mSharedPrefs.getConnectivityType() == 1) {
		// tvConnectivityType.setText(R.string.connect_bluetooth);
		// } else if (mSharedPrefs.getConnectivityType() == 2) {
		// tvConnectivityType.setText(R.string.connect_wifi);
		// } else if (mSharedPrefs.getConnectivityType() == 3) {
		// tvConnectivityType.setText(R.string.connect_wifip2p);
		// } else {
		// tvConnectivityType.setText(R.string.connect_null);
		// }
		//
		// tvDeviceMode = (TextView) view.findViewById(R.id.tv_deviceMode);
		// tvDeviceMode.setText(R.string.mode_parents);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_overview_parentsmode, container, false);

		initUiElements(view);

		return view;
	}
}
