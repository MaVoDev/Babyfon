package com.example.android.navigationdrawerexample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

public class TestFragment2 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_test_2, container, false);

		// ProgressBar
		final ProgressBar prgsBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

		// Button
		Button btn1 = (Button) rootView.findViewById(R.id.button1);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (prgsBar.isShown())
					prgsBar.setVisibility(View.INVISIBLE);
				else
					prgsBar.setVisibility(View.VISIBLE);
			}
		});

		return rootView;
	}
}
