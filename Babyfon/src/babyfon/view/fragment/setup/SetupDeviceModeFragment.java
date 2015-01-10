package babyfon.view.fragment.setup;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.setup.babymode.SetupConnectionBabyFragment;
import babyfon.view.fragment.setup.parentmode.SetupConnectionParentsFragment;
import babyfon.view.fragment.setup.parentmode.SetupSearchDevicesFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupDeviceModeFragment extends Fragment {

	// Define UI elements
	private ImageView imgViewBabyMode;
	private ImageView imgViewParentsMode;
	private TextView textTitleDeviceMode;

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

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");

		// Initialize ImageViews
		imgViewBabyMode = (ImageView) view.findViewById(R.id.imgbtn_baby);
		imgViewParentsMode = (ImageView) view.findViewById(R.id.imgbtn_parents);

		// Initialize TextViews
		textTitleDeviceMode = (TextView) view.findViewById(R.id.text_titleDeviceMode);
		textTitleDeviceMode.setTypeface(mTypeface);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_setup_devicemode, container, false);

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
}