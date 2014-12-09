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

public class ConnectivityFragment extends Fragment {

	Context context;

	public ConnectivityFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connectivity, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		Button button = (Button) view.findViewById(R.id.button_forward_connectivity);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConnectionFragment connectionFragment = new ConnectionFragment(context);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionFragment, null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}
}
