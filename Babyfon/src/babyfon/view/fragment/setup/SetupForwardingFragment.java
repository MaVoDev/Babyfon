package babyfon.view.fragment.setup;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.OverviewFragment;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class SetupForwardingFragment extends Fragment {

	// Define ui elements
	private Button btnBackward;
	private Button btnForward;
	private CheckBox chkboxCall;
	private CheckBox chkboxSMS;
	private TextView subtitle;
	private TextView title;
	private TextView infoText;
	private RadioButton radioForwardSMS;
	private RadioButton radioInfoSMS;

	// Fragments
	private SetupCompleteBabyModeFragment nextFragment;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public SetupForwardingFragment(Context mContext) {
		nextFragment = new SetupCompleteBabyModeFragment(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

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

		if (mSharedPrefs.getForwardingSMSInfo() || mSharedPrefs.getForwardingSMS()) {
			chkboxSMS.setChecked(true);
			radioForwardSMS.setEnabled(true);
			radioInfoSMS.setEnabled(true);
			if (mSharedPrefs.getForwardingSMS()) {
				radioForwardSMS.setChecked(true);
				radioInfoSMS.setChecked(false);
			} else {
				radioForwardSMS.setChecked(false);
				radioInfoSMS.setChecked(true);
			}
		} else {
			chkboxSMS.setChecked(false);
			radioForwardSMS.setEnabled(false);
			radioInfoSMS.setEnabled(false);
			radioForwardSMS.setChecked(false);
			radioInfoSMS.setChecked(true);
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
		btnBackward = (Button) view.findViewById(R.id.btn_backward_setup_forwarding);
		btnBackward.setTypeface(mTypeface_i);
		btnForward = (Button) view.findViewById(R.id.btn_forward_setup_forwarding);
		btnForward.setTypeface(mTypeface_i);

		// Initialize Checkboxes
		chkboxCall = (CheckBox) view.findViewById(R.id.chkbox_forwarding_Call);
		chkboxCall.setTypeface(mTypeface_i);
		chkboxSMS = (CheckBox) view.findViewById(R.id.chkbox_forwarding_SMS);
		chkboxSMS.setTypeface(mTypeface_i);

		// Initialize RadioButtons
		radioForwardSMS = (RadioButton) view.findViewById(R.id.forward_sms);
		radioForwardSMS.setTypeface(mTypeface_i);
		radioInfoSMS = (RadioButton) view.findViewById(R.id.forward_sms_info);
		radioInfoSMS.setTypeface(mTypeface_i);

		// Initialize TextViews
		subtitle = (TextView) view.findViewById(R.id.subtitle_setup_forward);
		subtitle.setTypeface(mTypeface_i);
		title = (TextView) view.findViewById(R.id.title_forwarding);
		title.setTypeface(mTypeface_bi);
		infoText = (TextView) view.findViewById(R.id.text_forwarding);
		infoText.setTypeface(mTypeface_i);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setup_forwarding, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);

		btnBackward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
				ft.replace(R.id.frame_container, new SetupConnectionFragment(mContext), null).addToBackStack(null)
						.commit();
			}
		});

		btnForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (chkboxCall.isChecked()) {
					mSharedPrefs.setForwardingCallInfoTemp(true);
				} else {
					mSharedPrefs.setForwardingCallInfoTemp(false);
				}

				if (chkboxSMS.isChecked()) {
					if (radioInfoSMS.isChecked()) {
						mSharedPrefs.setForwardingSMSInfoTemp(true);
						mSharedPrefs.setForwardingSMSTemp(false);
					} else {
						mSharedPrefs.setForwardingSMSTemp(true);
						mSharedPrefs.setForwardingSMSInfoTemp(false);
					}
				} else {
					mSharedPrefs.setForwardingSMSInfoTemp(false);
					mSharedPrefs.setForwardingSMSTemp(false);
				}

				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
				ft.replace(R.id.frame_container, nextFragment, null).addToBackStack(null).commit();
			}
		});

		chkboxSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					radioForwardSMS.setEnabled(true);
					radioInfoSMS.setEnabled(true);
				} else {
					radioForwardSMS.setEnabled(false);
					radioInfoSMS.setEnabled(false);
				}
			}
		});

		onBackPressed(view, mFragmentManager);

		return view;
	}

	public void onBackPressed(View view, final FragmentManager mFragmentManager) {
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() != KeyEvent.ACTION_DOWN)
					return true;

				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					new AlertDialog.Builder(getActivity())
							.setTitle(mContext.getString(R.string.dialog_title_cancel_setup))
							.setMessage(mContext.getString(R.string.dialog_message_cancel_setup))
							.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
							.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											if (mSharedPrefs.getDeviceMode() == 0) {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container, new OverviewFragment(mContext),
																null).addToBackStack(null).commit();
											} else if (mSharedPrefs.getDeviceMode() == 1) {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container,
																new BabyMonitorFragment(mContext), null)
														.addToBackStack(null).commit();
											} else {
												mFragmentManager
														.beginTransaction()
														.replace(R.id.frame_container,
																new SetupStartFragment(mContext), null)
														.addToBackStack(null).commit();
											}
											mSharedPrefs.setConnectivityTypeTemp(-1);
										}
									}).create().show();
					break;
				}
				return true;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnBackward != null && btnForward != null) {
			updateUI();
		}
	}
}
