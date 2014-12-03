package babyfon.fragment;

import babyfon.activity.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConnectivityFragment extends Fragment {

	public ConnectivityFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connectivity, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		Button button = (Button) view.findViewById(R.id.button_forward_connectivity);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConnectionFragment connectionFragment = new ConnectionFragment();
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionFragment, null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}
}
