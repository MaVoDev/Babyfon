package babyfon.view.fragment.setup;

import babyfon.init.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SetupStartFragment extends Fragment {

	private SetupDeviceModeFragment mDeviceModeFragment;

	// Constructor
	public SetupStartFragment(Context mContext) {
		mDeviceModeFragment = new SetupDeviceModeFragment(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_setup, container, false);

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
