package babyfon.view.fragment;

import babyfon.init.R;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BabyMonitorFragment extends Fragment {

	// Define UI elements
	private TextView title;

	public String batteryLevel;

	private TextView textBatteryLevel;

	private Context mContext;

	// Constructor
	public BabyMonitorFragment(Context mContext) {
		this.mContext = mContext;
	}

	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
		updateUI();
	}

	public void updateUI() {
		if (textBatteryLevel != null) {
			textBatteryLevel.setText(batteryLevel);
		}
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");
		Typeface mTypeface_bi = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_baby_monitor);
		title.setTypeface(mTypeface_bi);

		textBatteryLevel = (TextView) view.findViewById(R.id.textBatteryLevel);
		textBatteryLevel.setTypeface(mTypeface_i);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_babymonitor, container, false);

		initUiElements(view);
		updateUI();

		return view;
	}
}
