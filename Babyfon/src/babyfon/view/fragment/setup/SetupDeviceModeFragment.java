package babyfon.view.fragment.setup;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.setup.parentmode.SetupSearchDevicesFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupDeviceModeFragment extends Fragment {

	// Define UI elements
	private Button btnBackward;
	private Button btnForward;
	private ImageView imgViewBabyMode;
	private ImageView imgViewParentsMode;
	private TextView title;

	private SharedPrefs mSharedPrefs;

	// TODO: TEST VS!
	// WIEDER ALTE REIN!
	// private SetupConnectionBabyFragment connectionBabyFragment;
	// private SetupConnectionParentsFragment connectionParentsFragment;
	private SetupSearchDevicesFragment connectionBabyFragment;
	private SetupSearchDevicesFragment connectionParentsFragment;

	private Context mContext;

	// Constructor
	public SetupDeviceModeFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		// TODO: TEST VS!
		// WIEDER ALTE REIN!
		// connectionBabyFragment = new SetupConnectionBabyFragment(mContext);
		// connectionParentsFragment = new SetupConnectionParentsFragment(mContext);
		connectionBabyFragment = new SetupSearchDevicesFragment(mContext);
		connectionParentsFragment = new SetupSearchDevicesFragment(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnBackward.setBackgroundResource(R.drawable.btn_selector_male);
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnBackward.setBackgroundResource(R.drawable.btn_selector_female);
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
		btnBackward = (Button) view.findViewById(R.id.btn_backwardSetupDeviceMode);
		btnBackward.setTypeface(mTypeface_i);
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

		View view = inflater.inflate(R.layout.setup_devicemode, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		imgViewBabyMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setDeviceMode(0);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionBabyFragment, null).addToBackStack(null)
						.commit();
			}
		});

		imgViewParentsMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setDeviceMode(1);
				fragmentManager.beginTransaction().replace(R.id.frame_container, connectionParentsFragment, null).addToBackStack(null)
						.commit();
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnBackward != null && btnForward != null) {
			updateUI();
		}
	}
}
