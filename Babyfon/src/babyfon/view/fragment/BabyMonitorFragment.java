package babyfon.view.fragment;

import java.util.Timer;
import java.util.TimerTask;

import babyfon.Message;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class BabyMonitorFragment extends Fragment {

	// Define UI elements
	private ImageView kickRemote;
	private ImageView remoteOnlineState;
	private ImageView batteryEdit;
	private ImageView baby;
	private ProgressBar noiseLevel;
//	private Switch hearEdit;
//	private Switch talkEdit;
	private CompoundButton hearEdit;
	private CompoundButton talkEdit;
	private TextView remoteText;
	private TextView remoteState;
	private TextView batteryText;
	private TextView batteryState;
	private TextView hearText;
	private TextView hearState;
	private TextView talkText;
	private TextView talkState;
	private TextView noiseText;
	private TextView title;

	private int batteryLevelInt;
	private String batteryLevelString;

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	// Timer
	private Timer timer;

	private Context mContext;

	// Constructor
	public BabyMonitorFragment(Context mContext) {
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	// public void setBatteryLevel(String batteryLevel) {
	// this.batteryLevel = batteryLevel;
	// updateUI();
	// }

	public void updateUI() {
		if (mSharedPrefs.isRemoteOnline()) {

		} else {
			remoteOnlineState.setImageResource(android.R.drawable.presence_away);
		}

		if (mSharedPrefs.getRemoteAddress() != null) {
			// remote host is connected
			kickRemote.setVisibility(View.VISIBLE);
			kickRemote.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
			remoteState.setText(mSharedPrefs.getRemoteName());
			if (mSharedPrefs.isRemoteOnline()) {
				remoteOnlineState.setImageResource(android.R.drawable.presence_online);
			} else {
				remoteOnlineState.setImageResource(android.R.drawable.presence_away);
			}
		} else {
			// remote host isn't connected
			kickRemote.setVisibility(View.INVISIBLE);
			remoteState.setText(R.string.overview_connected_device_false);
			remoteOnlineState.setImageResource(android.R.drawable.presence_invisible);
		}

		// TODO hier kommt die Abfrage für den Noise State zur Progressbar rein

		if (batteryEdit != null) {
			if (batteryState != null) {
				if (mSharedPrefs.isRemoteOnline()) {

					batteryLevelString = mSharedPrefs.getBatteryLevel();

					if (batteryLevelString != null) {
						batteryLevelInt = Integer.parseInt(batteryLevelString);
						if (batteryLevelInt > 75) {
							batteryEdit.setImageResource(R.drawable.batt100);
						} else if (batteryLevelInt > 50) {
							batteryEdit.setImageResource(R.drawable.batt75);
						} else if (batteryLevelInt > 25) {
							batteryEdit.setImageResource(R.drawable.batt45);
						} else if (batteryLevelInt > 5) {
							batteryEdit.setImageResource(R.drawable.batt15);
						} else {
							batteryEdit.setImageResource(R.drawable.batt00);
						}
						batteryState.setText(batteryLevelString + "%");
					} else {
						batteryLevelString = "n/a";
						batteryEdit.setImageResource(R.drawable.batt00);
					}
				} else {
					batteryLevelString = "n/a";
					batteryEdit.setImageResource(R.drawable.batt00);
				}
				batteryState.setText(batteryLevelString + "%");
			}
		} else {
		}

		hearText.setText(mSharedPrefs.getName() + " zuhören");
		talkText.setText("Mit " + mSharedPrefs.getName() + " reden");
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface_n = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");
		Typeface mTypeface_b = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");

		// Initialize ImageViews
		kickRemote = (ImageView) view.findViewById(R.id.babymonitor_delete_remote);
		remoteOnlineState = (ImageView) view.findViewById(R.id.babymonitor_online_state);
		batteryEdit = (ImageView) view.findViewById(R.id.babymonitor_battery_edit);
		baby = (ImageView) view.findViewById(R.id.babymonitor_baby);
		if (mSharedPrefs.getGender() == 0) {
			baby.setImageResource(R.drawable.baby_male);
		} else {
			baby.setImageResource(R.drawable.baby_female);
		}

		// Initialize Progressbar
		noiseLevel = (ProgressBar) view.findViewById(R.id.babymonitor_edit_noise_level);

		// Initialize Switchen
//		hearEdit = (Switch) view.findViewById(R.id.babymonitor_hear_edit);
		hearEdit = (CompoundButton) view.findViewById(R.id.babymonitor_hear_edit);
		hearEdit.setTypeface(mTypeface_n);
//		talkEdit = (Switch) view.findViewById(R.id.babymonitor_talk_edit);
		talkEdit = (CompoundButton) view.findViewById(R.id.babymonitor_talk_edit);
		talkEdit.setTypeface(mTypeface_n);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_baby_monitor);
		title.setTypeface(mTypeface_b);
		remoteText = (TextView) view.findViewById(R.id.babymonitor_text_remote);
		remoteText.setTypeface(mTypeface_b);
		remoteState = (TextView) view.findViewById(R.id.babymonitor_remote);
		remoteState.setTypeface(mTypeface_n);
		batteryText = (TextView) view.findViewById(R.id.babymonitor_text_battery);
		batteryText.setTypeface(mTypeface_b);
		batteryState = (TextView) view.findViewById(R.id.babymonitor_battery_state);
		batteryState.setTypeface(mTypeface_n);
		hearText = (TextView) view.findViewById(R.id.babymonitor_text_hear);
		hearText.setTypeface(mTypeface_b);
		hearState = (TextView) view.findViewById(R.id.babymonitor_hear_state);
		hearState.setTypeface(mTypeface_n);
		noiseText = (TextView) view.findViewById(R.id.babymonitor_text_noise_level);
		noiseText.setTypeface(mTypeface_b);

		if (hearEdit.isChecked()) {
			hearState.setText(mContext.getString(R.string.enabled));
		} else {
			hearState.setText(mContext.getString(R.string.disabled));
		}

		talkText = (TextView) view.findViewById(R.id.babymonitor_text_talk);
		talkText.setTypeface(mTypeface_b);
		talkState = (TextView) view.findViewById(R.id.babymonitor_talk_state);
		talkState.setTypeface(mTypeface_n);

		if (talkEdit.isChecked()) {
			talkState.setText(mContext.getString(R.string.enabled));
		} else {
			talkState.setText(mContext.getString(R.string.disabled));
		}

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_babymonitor, container, false);

		initUiElements(view);

		startUiUpdateThread();

		// kick remote
		kickRemote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
						.setTitle(mContext.getString(R.string.dialog_title_kick_remote))
						.setMessage(mContext.getString(R.string.dialog_message_kick_remote))
						.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
						.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										mModuleHandler.stopRemoteCheck();

										mSharedPrefs.setRemoteAddress(null);
										mSharedPrefs.setRemoteName(null);
										mSharedPrefs.setRemoteOnlineState(false);

										if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
											mModuleHandler.unregisterSMS();
										}

										new Message(mContext).send(mContext
												.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));
										updateUI();
									}
								}).create().show();
			}
		});

		// listen to the baby
		hearEdit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// checked true
					hearState.setText(mContext.getString(R.string.enabled));
				} else {
					// checked false
					hearState.setText(mContext.getString(R.string.disabled));
				}

			}
		});

		// talk to baby
		talkEdit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// checked true
					talkState.setText(mContext.getString(R.string.enabled));
				} else {
					// checked false
					talkState.setText(mContext.getString(R.string.disabled));
				}

			}
		});

		return view;
	}

	public void startUiUpdateThread() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						updateUI();
					}
				});
			}
		}, 0, 1000);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (timer == null) {
			timer = new Timer();
		}
		startUiUpdateThread();

		if (baby != null) {
			if (mSharedPrefs.getGender() == 0) {
				baby.setImageResource(R.drawable.baby_male);
			} else {
				baby.setImageResource(R.drawable.baby_female);
			}

		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
