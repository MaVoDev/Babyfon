package babyfon.view.fragment;

import babyfon.init.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BabymonitorFragment extends Fragment {

	public BabymonitorFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_babymonitor, container, false);

		return view;
	}
}
