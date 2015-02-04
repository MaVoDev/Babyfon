package babyfon.view.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import babyfon.Message;
import babyfon.Notification;
import babyfon.audio.AudioRecorder;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.setup.SetupConnectionFragment;

public class BabyMonitorFragment extends Fragment {

	private int mNoiseThreshold = 50;
	private static final String TAG = BabyMonitorFragment.class.getCanonicalName();
	// Define UI elements
	private ImageView kickRemote;
	private ImageView remoteOnlineState;
	private ImageView batteryEdit;
	private ImageView baby;
	private ProgressBar noiseLevel;
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
	private TextView noiseActivateText;
	private TextView title;

	private LinearLayout layoutRemote;

	private long currentTime;
	private long lastTime;

	private int batteryLevel;

	private int noiseCounter = 0;

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	// Timer
	private Timer timer;

	private Context mContext;

	// protected boolean mFragmentActive = false;

	// Constructor
	public BabyMonitorFragment(Context mContext) {
		// WORKAROUND
		((MainActivity) MainActivity.getContext()).setFragmentForId(this, "BabyMonitorFragment");

		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {

		if (mSharedPrefs.getConnectivityType() == 2) {
			if (mSharedPrefs.isRemoteOnline()) {

			} else {
				noiseLevel.setProgress(0);
				remoteOnlineState.setImageResource(android.R.drawable.presence_away);
			}
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

		if (mSharedPrefs.isNoiseActivated()) {
			noiseActivateText.setVisibility(View.GONE);
			noiseLevel.setVisibility(View.VISIBLE);
		} else {
			noiseActivateText.setVisibility(View.VISIBLE);
			noiseLevel.setVisibility(View.GONE);
		}

		// TODO hier kommt die Abfrage für den Noise State zur Progressbar rein

		// update batterie state
		if (mSharedPrefs.getRemoteAddress() != null) {
			if (batteryEdit != null) {
				if (batteryState != null) {
					if (mSharedPrefs.isRemoteOnline()) {
						batteryLevel = mSharedPrefs.getBatteryLevel();

						if (batteryLevel > 0) {
							if (batteryLevel > 75) {
								batteryEdit.setImageResource(R.drawable.batt100);
							} else if (batteryLevel > 50) {
								batteryEdit.setImageResource(R.drawable.batt75);
							} else if (batteryLevel > 25) {
								batteryEdit.setImageResource(R.drawable.batt45);
							} else if (batteryLevel > 5) {
								batteryEdit.setImageResource(R.drawable.batt15);
							} else {
								batteryEdit.setImageResource(R.drawable.batt00);
							}
							batteryState.setText(batteryLevel + "%");
						} else {
							batteryState.setText("n/a");
							batteryEdit.setImageResource(R.drawable.battna);
						}
					} else {
						batteryState.setText("n/a");
						batteryEdit.setImageResource(R.drawable.battna);
					}
				} else {
					batteryState.setText("n/a");
					batteryEdit.setImageResource(R.drawable.battna);
				}
			}
		} else {
			batteryState.setText("n/a");
			batteryEdit.setImageResource(R.drawable.battna);
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

		// Initialize CompoundButton
		hearEdit = (CompoundButton) view.findViewById(R.id.babymonitor_hear_edit);
		hearEdit.setTypeface(mTypeface_n);
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
		noiseActivateText = (TextView) view.findViewById(R.id.babymonitor_text_noise_activate);
		noiseActivateText.setTypeface(mTypeface_b);

		// Initialize Layouts
		layoutRemote = (LinearLayout) view.findViewById(R.id.babymonitor_layout_remote);

		if (hearEdit.isChecked()) {
			hearState.setText(mContext.getString(R.string.enabled));
		} else {
			hearState.setText(mContext.getString(R.string.disabled));
		}

		talkText = (TextView) view.findViewById(R.id.babymonitor_text_talk);
		talkText.setTypeface(mTypeface_b);
		talkState = (TextView) view.findViewById(R.id.babymonitor_talk_state);
		talkState.setTypeface(mTypeface_n);

		if (mSharedPrefs.isHearActivated()) {
			hearEdit.setChecked(true);
			hearState.setText(mContext.getString(R.string.enabled));
		} else {
			hearEdit.setChecked(false);
			hearState.setText(mContext.getString(R.string.disabled));
		}

		if (mSharedPrefs.isTalkActivated()) {
			talkEdit.setChecked(true);
			talkState.setText(mContext.getString(R.string.enabled));
		} else {
			talkEdit.setChecked(false);
			talkState.setText(mContext.getString(R.string.disabled));
		}

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_babymonitor, container, false);

		final FragmentManager mFragmentManager = getFragmentManager();

		initUiElements(view);

		startUiUpdateThread();

		noiseActivateText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setNoiseActivated(true);
			}
		});

		noiseActivateText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharedPrefs.setNoiseActivated(true);
			}
		});

		// kick remote
		kickRemote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity()).setTitle(mContext.getString(R.string.dialog_title_kick_remote))
						.setMessage(mContext.getString(R.string.dialog_message_kick_remote))
						.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
						.setPositiveButton(mContext.getString(R.string.dialog_button_yes), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								mModuleHandler.stopRemoteCheck();

								mSharedPrefs.setRemoteAddress(null);
								mSharedPrefs.setRemoteName(null);
								mSharedPrefs.setRemoteOnlineState(false);

								if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
									mModuleHandler.unregisterSMS();
								}

								new Message(mContext).send(mContext.getString(R.string.BABYFON_MSG_SYSTEM_DISCONNECTED));

								if (mSharedPrefs.getConnectivityType() == 1) {
									MainActivity.mBoundService.getConnection().stopConnection();
								}

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
					mSharedPrefs.setHearActivated(true);
				} else {
					// checked false
					hearState.setText(mContext.getString(R.string.disabled));
					mSharedPrefs.setHearActivated(false);
				}

			}
		});

		// talk to baby
		talkEdit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// checked true
					if (mSharedPrefs.isNoiseActivated()) {
						mSharedPrefs.setNoiseActivated(false);
					}
					startRecorder();
					talkState.setText(mContext.getString(R.string.enabled));
					mSharedPrefs.setTalkActivated(true);
				} else {
					stopRecorder();
					// checked false
					talkState.setText(mContext.getString(R.string.disabled));
					mSharedPrefs.setTalkActivated(false);
				}
			}
		});

		// change remote in fragment
		layoutRemote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSharedPrefs.getRemoteAddress() == null) {
					mSharedPrefs.setDeviceModeTemp(1);
					FragmentTransaction ft = mFragmentManager.beginTransaction();
					ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
					ft.replace(R.id.frame_container, new SetupConnectionFragment(mContext), null).addToBackStack(null).commit();
				}
			}
		});

		return view;
	}

	public void updateVolume(final int calculateVolume) {
		if (mSharedPrefs.isNoiseActivated() && !mSharedPrefs.isTalkActivated()) {
			final int level = calculateVolume;

			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					noiseLevel.setProgress(level);
				}
			});

			if (level > mNoiseThreshold) {
				if (currentTime == 0 && lastTime == 0) {
					currentTime = System.currentTimeMillis();
					lastTime = System.currentTimeMillis();
				} else {
					currentTime = System.currentTimeMillis();
				}

				if ((currentTime - lastTime) > 5000) {
					noiseCounter = 0;
				} else {
					noiseCounter++;
				}
				lastTime = currentTime;
			}

			if (noiseCounter > 10) {
				noiseCounter = 0;
				((MainActivity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new Notification(mContext).start();
					}
				});
				mSharedPrefs.setNoiseActivated(false);
			}
		}
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
		Log.e(TAG, "BabyMonitor->onResume()");

		super.onResume();

		if (mSharedPrefs.getConnectivityType() == 1) {
			mNoiseThreshold = 80;
		} else {
			mNoiseThreshold = 50;
		}

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

		// mFragmentActive = true;
	}

	@Override
	public void onPause() {
		Log.e(TAG, "BabyMonitor->onPause()");

		super.onPause();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		// mFragmentActive = false;

	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "BabyMonitor->onDestroy()");

		super.onDestroy();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void startRecorder() {
		if (mSharedPrefs.getConnectivityType() == 1) {
			MainActivity.mBoundService.startRecording();
		} else {
			if (MainActivity.mAudioRecorder == null) {
				MainActivity.mAudioRecorder = new AudioRecorder(mContext, MainActivity.mBoundService);
			}
			MainActivity.mAudioRecorder.startRecording();
		}
	}

	public void stopRecorder() {
		if (mSharedPrefs.getConnectivityType() == 1) {
			MainActivity.mBoundService.stopRecording();
		} else {
			if (MainActivity.mAudioRecorder != null) {
				MainActivity.mAudioRecorder.stopRecording();
			}
		}

	}
}
