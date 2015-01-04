package babyfon.view.fragment;

import babyfon.init.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeviceModeFragment extends Fragment {

	// Define UI elements
	private Button buttonBabyMode;
	private Button buttonParentMode;

	private ConnectivityFragment connectivityFragment;

	// Constructor
	public DeviceModeFragment(Context mContext) {
		connectivityFragment = new ConnectivityFragment(mContext);
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {

		// Initialize Buttons
		buttonBabyMode = (Button) view.findViewById(R.id.button_baby_mode);
		buttonParentMode = (Button) view.findViewById(R.id.button_parent_mode);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_device_mode, container, false);

		initUiElements(view);

		buttonBabyMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDeviceMode(0);
			}
		});

		buttonParentMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDeviceMode(1);
			}
		});
		return view;
	}

	public void setDeviceMode(int deviceMode) {
		FragmentManager fragmentManager = getFragmentManager();

		Bundle bundle = new Bundle();
		bundle.putInt("deviceMode", deviceMode);
		connectivityFragment.setArguments(bundle);
		fragmentManager.beginTransaction().replace(R.id.frame_container, connectivityFragment, null)
				.addToBackStack(null).commit();
	}
}