package babyfon.view.fragment;

import babyfon.init.R;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ConnectionFragment extends Fragment {

	// private Context context;
	
	private ImageView iconConnectivity;
	private TextView titleConnectivity;

	public ConnectionFragment(Context context) {
		// this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connection, container, false);

		iconConnectivity = (ImageView) view.findViewById(R.id.iconConnectivity);
		titleConnectivity = (TextView) view.findViewById(R.id.titleConnectivity);
		
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			switch (bundle.getInt("connectivityType", -1)) {
			case 1:
				initViewBluetooth();
				break;
			case 2:
				initViewBWifi();
				break;
			case 3:
				initViewBWifiDirect();
				break;
			}
		}
		return view;
	}

	public void initViewBluetooth() {
		iconConnectivity.setImageResource(R.drawable.ic_bluetooth);
		titleConnectivity.setText(getString(R.string.bluetooth));
	}

	public void initViewBWifi() {
		iconConnectivity.setImageResource(R.drawable.ic_wifi);
		titleConnectivity.setText(getString(R.string.wifi));
	}

	public void initViewBWifiDirect() {
		iconConnectivity.setImageResource(R.drawable.ic_wifidirect);
		titleConnectivity.setText(getString(R.string.wifip2p));
	}
}
