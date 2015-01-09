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

public class BabymonitorFragment extends Fragment {

	// Define UI elements
	private TextView textTitleBabymonitor;

	public String batteryLevel = "n/a";

	private TextView textBatteryLevel;
	
	private Context mContext;

	// Constructor
	public BabymonitorFragment(Context mContext) {
		this.mContext = mContext;
	}

	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
		updateUI();
	}

	public void updateUI() {
		if (textBatteryLevel != null)
			textBatteryLevel.setText(batteryLevel);
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
		textTitleBabymonitor = (TextView) view.findViewById(R.id.text_titleBabymonitor);
		textTitleBabymonitor.setTypeface(mTypeface);
		
		textBatteryLevel = (TextView) view.findViewById(R.id.textBatteryLevel);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.layout_babymonitor, container, false);

		initUiElements(view);
		updateUI();
		
		return view;
	}
}
