package babyfon.view.fragment.setup;

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

public class SetupDeviceModeFragment extends Fragment {

	// Define UI elements
	private Button buttonBabyMode;
	private Button buttonParentMode;

	private SharedPrefs mSharedPrefs;

	private SetupConnectionBabyFragment connectionBabyFragment;
	private SetupConnectionParentsFragment connectionParentsFragment;

	// Constructor
	public SetupDeviceModeFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
		connectionBabyFragment = new SetupConnectionBabyFragment(mContext);
		connectionParentsFragment = new SetupConnectionParentsFragment(mContext);
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {

		// Initialize Buttons
		buttonBabyMode = (Button) view.findViewById(R.id.btn_babyMode);
		buttonParentMode = (Button) view.findViewById(R.id.btn_parentMode);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_device_mode, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		buttonBabyMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setDeviceMode(0);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionBabyFragment, null)
						.addToBackStack(null).commit();
			}
		});

		buttonParentMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setDeviceMode(1);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionParentsFragment, null)
						.addToBackStack(null).commit();
			}
		});
		return view;
	}
}