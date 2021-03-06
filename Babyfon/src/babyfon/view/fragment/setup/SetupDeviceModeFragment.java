package babyfon.view.fragment.setup;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class SetupDeviceModeFragment extends Fragment {

	// Define UI elements
	private Button btnForward;
	private RadioButton radioBaby;
	private RadioButton radioParents;
	private TextView title;
	private TextView infoText;

	private SharedPrefs mSharedPrefs;

	private SetupConnectionFragment nextFragment;

	private Context mContext;

	// Constructor
	public SetupDeviceModeFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
		nextFragment = new SetupConnectionFragment(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnForward.setBackgroundResource(R.drawable.btn_selector_female);
		}

		// Update radio buttons
		if (mSharedPrefs.getDeviceModeTemp() == 1) {
			radioBaby.setChecked(false);
			radioParents.setChecked(true);
		} else {
			radioBaby.setChecked(true);
			radioParents.setChecked(false);
		}
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface_bi = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");
		Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");

		// Initialize Buttons
		btnForward = (Button) view.findViewById(R.id.btn_forwardSetupDeviceMode);
		btnForward.setTypeface(mTypeface_i);

		// Initialize Checkboxes
		radioBaby = (RadioButton) view.findViewById(R.id.radio_baby);
		radioBaby.setTypeface(mTypeface_i);
		radioParents = (RadioButton) view.findViewById(R.id.radio_parents);
		radioParents.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_device_mode);
		title.setTypeface(mTypeface_bi);
		infoText = (TextView) view.findViewById(R.id.text_device_mode);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_device_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (radioBaby.isChecked()) {
					mSharedPrefs.setDeviceModeTemp(0);
				} else {
					mSharedPrefs.setDeviceModeTemp(1);
				}

				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
				ft.replace(R.id.frame_container, nextFragment, null).addToBackStack(null).commit();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnForward != null) {
			updateUI();
		}
	}
}
