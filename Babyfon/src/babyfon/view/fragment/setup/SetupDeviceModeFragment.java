package babyfon.view.fragment.setup;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.setup.babymode.SetupConnectionBabyModeFragment;
import babyfon.view.fragment.setup.parentmode.SetupConnectionParentsModeFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupDeviceModeFragment extends Fragment {

	// Define UI elements
	private Button btnForward;
	private ImageView imgViewBabyMode;
	private ImageView imgViewParentsMode;
	private TextView title;

	private SharedPrefs mSharedPrefs;

	private SetupConnectionBabyModeFragment connectionBabyFragment;
	private SetupConnectionParentsModeFragment connectionParentsFragment;

	private Context mContext;

	// Constructor
	public SetupDeviceModeFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);
		connectionBabyFragment = new SetupConnectionBabyModeFragment(mContext);
		connectionParentsFragment = new SetupConnectionParentsModeFragment(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnForward.setBackgroundResource(R.drawable.btn_selector_female);
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

		// Initialize ImageViews
		imgViewBabyMode = (ImageView) view.findViewById(R.id.imgbtn_baby);
		imgViewParentsMode = (ImageView) view.findViewById(R.id.imgbtn_parents);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_device_mode);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_device_mode, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);

		imgViewBabyMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setDeviceModeTemp(0);
				mFragmentManager.beginTransaction().replace(R.id.frame_container, connectionBabyFragment, null)
						.addToBackStack(null).commit();
			}
		});

		imgViewParentsMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setDeviceModeTemp(1);
				mFragmentManager.beginTransaction().replace(R.id.frame_container, connectionParentsFragment, null)
						.addToBackStack(null).commit();
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