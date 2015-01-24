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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SetupStartFragment extends Fragment {

	// Define UI elements
	private Button btnForward;
	private TextView title;
	private TextView infoText;

	private SetupDeviceModeFragment nextFragment;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public SetupStartFragment(Context mContext) {
		nextFragment = new SetupDeviceModeFragment(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
			btnForward.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnForward.setBackgroundResource(R.drawable.btn_selector_female);
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
		btnForward = (Button) view.findViewById(R.id.btn_forward_setup_start);
		btnForward.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_setup_start);
		title.setTypeface(mTypeface_bi);
		infoText = (TextView) view.findViewById(R.id.text_setup_start);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_setup, container, false);

		final FragmentManager fragmentManager = getFragmentManager();

		initUiElements(view);

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// fragmentManager.beginTransaction().replace(R.id.frame_container, nextFragment, null)
				// .addToBackStack(null).commit();

				FragmentTransaction ft = fragmentManager.beginTransaction();

				// TODO an API 10 anpassen
//				ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
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
