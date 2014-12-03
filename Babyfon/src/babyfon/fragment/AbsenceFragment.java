package babyfon.fragment;

import babyfon.activity.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AbsenceFragment extends Fragment {

	public AbsenceFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_absence, container, false);

		return rootView;
	}
}
