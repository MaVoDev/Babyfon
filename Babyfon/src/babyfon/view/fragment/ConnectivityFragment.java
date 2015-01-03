package babyfon.view.fragment;

import babyfon.SharedPrefs;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ConnectivityFragment extends Fragment {

	private Context mContext;

	int connectivityType;

	public ConnectivityFragment(Context mContext) {
		this.mContext = mContext;
		System.out.println("Modus: " + new SharedPrefs(mContext).getDeviceMode());
	}

	/**
	 * Liest die ausgewählte Verbindungsart aus.
	 * 
	 * @param checkId
	 *            ausgewählte Verbindungsart
	 */
	private void getConnectivityType(int checkedId) {
		switch (checkedId) {
		case R.id.radioConnectivityBluetooth:
			// Bluetooth ausgewählt
			connectivityType = 1;
			break;
		case R.id.radioConnectivityWifi:
			// Wi-Fi ausgewählt
			connectivityType = 2;
			break;
		case R.id.radioConnectivityWifiDirect:
			// Wi-Fi Direct ausgewählt
			connectivityType = 3;
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connectivity, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		RadioGroup radioConnectivity = (RadioGroup) view.findViewById(R.id.radioConnectivity);
		getConnectivityType(radioConnectivity.getCheckedRadioButtonId());

		radioConnectivity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId repräsentiert die ausgewählte Option.
				getConnectivityType(checkedId);
			}
		});

		Button button = (Button) view.findViewById(R.id.button_forward_connectivity);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt("connectivityType", connectivityType);
				ConnectionFragment connectionFragment = new ConnectionFragment(mContext);
				connectionFragment.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionFragment, null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}
}
