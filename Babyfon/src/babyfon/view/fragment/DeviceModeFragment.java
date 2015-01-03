package babyfon.view.fragment;

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

public class DeviceModeFragment extends Fragment {

	private Context mContext;
	private SharedPrefs mSharedPrefs;

	public DeviceModeFragment(Context mContext) {
		this.mContext = mContext;
		this.mSharedPrefs = new SharedPrefs(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_device_mode, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		Button buttonBabyMode = (Button) view.findViewById(R.id.button_baby_mode);
		buttonBabyMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setze den Gerätemodus auf 0 (Babymodus)
				mSharedPrefs.setDeviceMode(0);
				ConnectivityFragment connectivityFragment = new ConnectivityFragment(mContext);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectivityFragment, null)
						.addToBackStack(null).commit();
			}
		});

		Button buttonParentMode = (Button) view.findViewById(R.id.button_parent_mode);
		buttonParentMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Setze den Gerätemodus auf 1 (Elternmodus)
				mSharedPrefs.setDeviceMode(1);
				ConnectivityFragment connectivityFragment = new ConnectivityFragment(mContext);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectivityFragment, null)
						.addToBackStack(null).commit();
			}
		});
		return view;
	}
}