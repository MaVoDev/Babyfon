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
import android.widget.TextView;

public class CompleteSetupFragment extends Fragment {

	// Define UI elements
	private Button btnCompleteSetup;
	private TextView tvPassword;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public CompleteSetupFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {

		// Initialize Button
		btnCompleteSetup = (Button) view.findViewById(R.id.btn_complete_setup);

		// Initialize TextViews
		tvPassword = (TextView) view.findViewById(R.id.tv_password);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_complete_setup, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		tvPassword.setText(getRandomPassword() + "");

		// OnClickListener for the Button btnCompleteSetup
		btnCompleteSetup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.frame_container, new OverviewFragment(mContext), null)
						.addToBackStack(null).commit();
			}
		});

		return view;
	}

	public int getRandomPassword() {
		// Generating a password between 1000 and 10000
		int password = (int) Math.floor(Math.random() * 9000 + 1000);

		// Saving the password in SharedPreferences
		mSharedPrefs.setPassword(password);

		return password;
	}
}
