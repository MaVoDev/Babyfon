package babyfon.view.fragment;

import babyfon.init.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SetupFragment extends Fragment {

	private DeviceModeFragment mDeviceModeFragment;

	// Constructor
	public SetupFragment(Context mContext) {
		mDeviceModeFragment = new DeviceModeFragment(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setup, container, false);

		final FragmentManager fragmentManager = getFragmentManager();
		
		Button button = (Button) view.findViewById(R.id.button_forward_setup);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.frame_container, mDeviceModeFragment, null)
						.addToBackStack(null).commit();
			}
		});
		
		

		return view;
	}
}
