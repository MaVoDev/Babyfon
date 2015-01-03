package babyfon.view.fragment;

import babyfon.init.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BabymonitorFragment extends Fragment {

	private static final String TAG = BabymonitorFragment.class
			.getCanonicalName();

	public String batteryLevel = "n/a";

	private View view;

	private TextView textBatteryLevel;

	public BabymonitorFragment() {

	}

	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.fragment_babymonitor, container, false);
		textBatteryLevel = (TextView) view.findViewById(R.id.textBatteryLevel);
		updateUI();
		return view;
	}

	public void updateUI() {
		Log.i(TAG, "Updating Fragment! (Fragment == null: "
				+ (textBatteryLevel == null) + ")");

		if (textBatteryLevel != null)
			textBatteryLevel.setText(batteryLevel);
	}
}
