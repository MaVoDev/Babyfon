package babyfon.view.fragment;

import java.util.Timer;
import java.util.TimerTask;

import babyfon.Generator;
import babyfon.Message;
import babyfon.Output;
import babyfon.audio.AudioRecorder;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverviewFragment extends Fragment {

	// Define UI elements
	private ImageView activeState;
	private ImageView changeCall;
	private ImageView changeConnectivity;
	private ImageView changeMode;
	private ImageView changeSMS;
	private ImageView kickRemote;
	private ImageView refreshPassword;
	private ImageView remoteOnlineState;
	private TextView title;
	private TextView remoteText;
	private TextView remoteState;
	private TextView passwordText;
	private TextView passwordState;
	private TextView smsText;
	private TextView smsState;
	private TextView callText;
	private TextView callState;
	private TextView connectivityText;
	private TextView connectivityState;
	private TextView modeText;
	private TextView modeState;

	private LinearLayout layoutPassword;
	private LinearLayout layoutCall;
	private LinearLayout layoutSms;
	private LinearLayout layoutChangeMode;

	private View layoutPasswordSeparator;
	private View layoutCallSeparator;
	private View layoutSmsSeparator;

	private boolean isActive;
	private boolean isCountdownActive = false;

	// Timer
	private Timer updateTimer;

	int countdown;

	// Alert Dialogs
	private AlertDialog callDialog;
	private AlertDialog connectivityDialog;
	private AlertDialog smsDialog;

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public OverviewFragment(Context mContext) {
		mModuleHandler = new ModuleHandler(mContext);
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
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

		// Initialize ImageViews
		activeState = (ImageView) view.findViewById(R.id.overview_active_state);
		changeCall = (ImageView) view.findViewById(R.id.overview_edit_call);
		changeConnectivity = (ImageView) view.findViewById(R.id.overview_edit_connectivity);
		changeMode = (ImageView) view.findViewById(R.id.overview_edit_mode);
		changeSMS = (ImageView) view.findViewById(R.id.overview_edit_sms);
		kickRemote = (ImageView) view.findViewById(R.id.overview_delete_remote);
		refreshPassword = (ImageView) view.findViewById(R.id.overview_edit_password);
		remoteOnlineState = (ImageView) view.findViewById(R.id.overview_online_state);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_overview_babymode);
		title.setTypeface(mTypeface_bi);
		remoteText = (TextView) view.findViewById(R.id.overview_text_remote);
		remoteText.setTypeface(mTypeface_bi);
		remoteState = (TextView) view.findViewById(R.id.overview_remote);
		remoteState.setTypeface(mTypeface_i);
		passwordText = (TextView) view.findViewById(R.id.overview_text_password);
		passwordText.setTypeface(mTypeface_bi);
		passwordState = (TextView) view.findViewById(R.id.overview_password);
		passwordState.setTypeface(mTypeface_i);
		smsText = (TextView) view.findViewById(R.id.overview_text_sms);
		smsText.setTypeface(mTypeface_bi);
		smsState = (TextView) view.findViewById(R.id.overview_sms);
		smsState.setTypeface(mTypeface_i);
		callText = (TextView) view.findViewById(R.id.overview_text_call);
		callText.setTypeface(mTypeface_bi);
		callState = (TextView) view.findViewById(R.id.overview_call);
		callState.setTypeface(mTypeface_i);
		connectivityText = (TextView) view.findViewById(R.id.overview_text_connectivity);
		connectivityText.setTypeface(mTypeface_bi);
		connectivityState = (TextView) view.findViewById(R.id.overview_connectivity);
		connectivityState.setTypeface(mTypeface_i);
		modeText = (TextView) view.findViewById(R.id.overview_text_mode);
		modeText.setTypeface(mTypeface_bi);
		modeState = (TextView) view.findViewById(R.id.overview_mode);
		modeState.setTypeface(mTypeface_i);

		// Initialize Layouts
		layoutPassword = (LinearLayout) view.findViewById(R.id.overview_layout_password);
		layoutCall = (LinearLayout) view.findViewById(R.id.overview_layout_call);
		layoutSms = (LinearLayout) view.findViewById(R.id.overview_layout_sms);
		layoutChangeMode = (LinearLayout) view.findViewById(R.id.overview_layout_edit_mode);

		// Initialize Views
		layoutPasswordSeparator = (View) view.findViewById(R.id.overview_layout_password_separator);
		layoutCallSeparator = (View) view.findViewById(R.id.overview_layout_call_separator);
		layoutSmsSeparator = (View) view.findViewById(R.id.overview_layout_sms_separator);

		updateUI();
	}

	public void updateUI() {
		if (mSharedPrefs.getConnectivityType() == 1) {
			updateBluetoothMode();
		}

		if (mSharedPrefs.getConnectivityType() == 2) {
			updateWiFiMode();
		}

		if (mSharedPrefs.getConnectivityType() == 3) {
			updateCallMode();
		}

		// connectivity type
		if (mSharedPrefs.getConnectivityType() == 1) {
			// bluetooth
			connectivityState.setText(R.string.bluetooth);
		} else if (mSharedPrefs.getConnectivityType() == 2) {
			// wifi
			connectivityState.setText(R.string.wifi);
		} else {
			// call
			connectivityState.setText(R.string.by_call);
		}
	}

	public void updateBluetoothMode() {
		layoutCall.setVisibility(View.VISIBLE);
		layoutCallSeparator.setVisibility(View.VISIBLE);
		layoutSms.setVisibility(View.VISIBLE);
		layoutSmsSeparator.setVisibility(View.VISIBLE);
		layoutPassword.setVisibility(View.VISIBLE);
		layoutPasswordSeparator.setVisibility(View.VISIBLE);

		isActive = mSharedPrefs.getActiveStateBabyMode();
		if (isActive) {
			// baby mode is enabled
			changeMode.setImageResource(android.R.drawable.ic_media_pause);
			modeState.setText(R.string.enabled);
			activeState.setImageResource(android.R.drawable.presence_online);
		} else {
			// baby mode is disabled
			changeMode.setImageResource(android.R.drawable.ic_media_play);
			modeState.setText(R.string.disabled);
			activeState.setImageResource(android.R.drawable.presence_invisible);
		}

		if (mSharedPrefs.isRemoteOnline()) {

		} else {
			remoteOnlineState.setImageResource(android.R.drawable.presence_away);
		}

		remoteText.setText(mContext.getString(R.string.overview_connected_device));
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

		// password
		passwordState.setText(mSharedPrefs.getPassword());

		modeText.setText(mContext.getString(R.string.overview_baby_mode_state));

		// sms state
		if (mSharedPrefs.getForwardingSMS()) {
			smsState.setText(R.string.radio_forward);
		} else if (mSharedPrefs.getForwardingSMSInfo()) {
			smsState.setText(R.string.radio_only_notify);
		} else {
			smsState.setText(R.string.radio_send_false);
		}

		// call state
		if (mSharedPrefs.getForwardingCallInfo()) {
			callState.setText(R.string.radio_only_notify);
		} else {
			callState.setText(R.string.radio_send_false);
		}

		if (mSharedPrefs.isNoiseActivated()) {
			startRecorder();
		} else {
			stopRecorder();
		}
	}

	public void updateWiFiMode() {
		layoutCall.setVisibility(View.VISIBLE);
		layoutCallSeparator.setVisibility(View.VISIBLE);
		layoutSms.setVisibility(View.VISIBLE);
		layoutSmsSeparator.setVisibility(View.VISIBLE);
		layoutPassword.setVisibility(View.VISIBLE);
		layoutPasswordSeparator.setVisibility(View.VISIBLE);

		isActive = mSharedPrefs.getActiveStateBabyMode();
		if (isActive) {
			// baby mode is enabled
			changeMode.setImageResource(android.R.drawable.ic_media_pause);
			modeState.setText(R.string.enabled);
			activeState.setImageResource(android.R.drawable.presence_online);
		} else {
			// baby mode is disabled
			changeMode.setImageResource(android.R.drawable.ic_media_play);
			modeState.setText(R.string.disabled);
			activeState.setImageResource(android.R.drawable.presence_invisible);
		}

		if (mSharedPrefs.isRemoteOnline()) {

		} else {
			remoteOnlineState.setImageResource(android.R.drawable.presence_away);
		}

		remoteText.setText(mContext.getString(R.string.overview_connected_device));
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

		// password
		passwordState.setText(mSharedPrefs.getPassword());

		modeText.setText(mContext.getString(R.string.overview_baby_mode_state));

		// sms state
		if (mSharedPrefs.getForwardingSMS()) {
			smsState.setText(R.string.radio_forward);
		} else if (mSharedPrefs.getForwardingSMSInfo()) {
			smsState.setText(R.string.radio_only_notify);
		} else {
			smsState.setText(R.string.radio_send_false);
		}

		// call state
		if (mSharedPrefs.getForwardingCallInfo()) {
			callState.setText(R.string.radio_only_notify);
		} else {
			callState.setText(R.string.radio_send_false);
		}

		if (mSharedPrefs.isNoiseActivated()) {
			startRecorder();
		} else {
			stopRecorder();
		}
	}

	public void updateCallMode() {
		layoutCall.setVisibility(View.INVISIBLE);
		layoutCallSeparator.setVisibility(View.INVISIBLE);
		layoutSms.setVisibility(View.INVISIBLE);
		layoutSmsSeparator.setVisibility(View.INVISIBLE);
		layoutPassword.setVisibility(View.INVISIBLE);
		layoutPasswordSeparator.setVisibility(View.INVISIBLE);

		remoteText.setText(mContext.getString(R.string.overview_text_call_device));
		if (mSharedPrefs.getPhoneNumber() != null && !mSharedPrefs.getPhoneNumber().equals("")) {
			remoteOnlineState.setImageResource(android.R.drawable.presence_online);
			remoteState.setText(mSharedPrefs.getPhoneNumber());
		} else {
			remoteOnlineState.setImageResource(android.R.drawable.presence_invisible);
			remoteState.setText(mContext.getString(R.string.overview_no_number));
		}

		if (isCountdownActive) {
			activeState.setImageResource(android.R.drawable.presence_invisible);
			changeMode.setImageResource(android.R.drawable.ic_media_pause);
		} else {
			if (mSharedPrefs.isNoiseActivated()) {
				changeMode.setImageResource(android.R.drawable.ic_media_pause);
				modeState.setText(R.string.enabled);
				activeState.setImageResource(android.R.drawable.presence_online);
			} else {
				changeMode.setImageResource(android.R.drawable.ic_media_play);
				modeState.setText(R.string.disabled);
				activeState.setImageResource(android.R.drawable.presence_invisible);
			}
		}
		modeText.setText(mContext.getString(R.string.overview_baby_noise_state));

		if (mSharedPrefs.getPhoneNumber() == null && mSharedPrefs.getPhoneNumber().equals("")) {
			isCountdownActive = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_overview, container, false);

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

										mModuleHandler.unregisterBattery();
										mModuleHandler.startUDPReceiver();

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

		// refresh password
		refreshPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
						.setTitle(mContext.getString(R.string.dialog_title_refresh_password))
						.setMessage(mContext.getString(R.string.dialog_message_refresh_password))
						.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
						.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										String password = new Generator().getRandomPassword();
										mSharedPrefs.setPassword(password);
										updateUI();
										new Message(mContext).send(mContext
												.getString(R.string.BABYFON_MSG_SYSTEM_PWCHANGED)
												+ ";"
												+ mSharedPrefs.getPassword());
									}
								}).create().show();
			}
		});

		// change connectivity
		changeConnectivity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = { mContext.getString(R.string.wifi),
						mContext.getString(R.string.bluetooth), mContext.getString(R.string.by_call) };

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(mContext.getString(R.string.dialog_title_connectivity));

				int checkedOption = -1;

				if (mSharedPrefs.getConnectivityType() == 1) {
					checkedOption = 1;
				}

				if (mSharedPrefs.getConnectivityType() == 2) {
					checkedOption = 0;
				}

				if (mSharedPrefs.getConnectivityType() == 3) {
					checkedOption = 2;
				}

				builder.setSingleChoiceItems(items, checkedOption, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							mSharedPrefs.setConnectivityType(2);
							if (mSharedPrefs.getRemoteAddress() == null) {
								mModuleHandler.startUDPReceiver();
								mModuleHandler.startTCPReceiver();
							}
							break;
						case 1:
							mSharedPrefs.setConnectivityType(1);
							mModuleHandler.stopTCPReceiver();
							mModuleHandler.stopUDPReceiver();
							break;
						case 2:
							mSharedPrefs.setConnectivityType(3);
							mModuleHandler.stopTCPReceiver();
							mModuleHandler.stopUDPReceiver();
							break;
						}

						if (mSharedPrefs.getConnectivityType() != item) {
							isCountdownActive = false;
							stopRecorder();
						}

						updateUI();
						connectivityDialog.dismiss();
					}
				});
				connectivityDialog = builder.create();
				connectivityDialog.show();
			}
		});

		// change sms
		changeSMS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = { mContext.getString(R.string.radio_send_false),
						mContext.getString(R.string.radio_only_notify), mContext.getString(R.string.radio_forward) };

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(mContext.getString(R.string.dialog_title_sms));

				int checkedOption = -1;

				if (mSharedPrefs.getForwardingSMS()) {
					checkedOption = 2;
				} else if (mSharedPrefs.getForwardingSMSInfo()) {
					checkedOption = 1;
				} else {
					checkedOption = 0;
				}

				builder.setSingleChoiceItems(items, checkedOption, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							mModuleHandler.unregisterSMS();
							mSharedPrefs.setForwardingSMSInfo(false);
							mSharedPrefs.setForwardingSMS(false);
							break;
						case 1:
							mModuleHandler.registerSMS();
							mSharedPrefs.setForwardingSMSInfo(true);
							mSharedPrefs.setForwardingSMS(false);
							break;
						case 2:
							mModuleHandler.registerSMS();
							mSharedPrefs.setForwardingSMSInfo(false);
							mSharedPrefs.setForwardingSMS(true);
							break;
						}
						updateUI();
						smsDialog.dismiss();
					}
				});
				smsDialog = builder.create();
				smsDialog.show();
			}
		});

		// change call
		changeCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = { mContext.getString(R.string.radio_send_false),
						mContext.getString(R.string.radio_only_notify) };

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(mContext.getString(R.string.dialog_title_call));

				int checkedOption = -1;

				if (mSharedPrefs.getForwardingCallInfo()) {
					checkedOption = 1;
				} else {
					checkedOption = 0;
				}

				builder.setSingleChoiceItems(items, checkedOption, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							mSharedPrefs.setForwardingCallInfo(false);
							break;
						case 1:
							mSharedPrefs.setForwardingCallInfo(true);
							break;
						}
						updateUI();
						callDialog.dismiss();
					}
				});
				callDialog = builder.create();
				callDialog.show();
			}
		});

		// change mode
		layoutChangeMode.setOnClickListener(new View.OnClickListener() {
			String title, message;

			@Override
			public void onClick(View v) {
				if (mSharedPrefs.getConnectivityType() == 3) {
					if (mSharedPrefs.isNoiseActivated()) {
						isCountdownActive = false;
						new Output().toast(mContext, mContext.getString(R.string.noise_countdown_stop), 0);
						mSharedPrefs.setNoiseActivated(false);
						stopRecorder();
					} else {
						if (mSharedPrefs.getPhoneNumber() != null && !mSharedPrefs.getPhoneNumber().equals("")) {
							countdown = 30;
							isCountdownActive = true;
							new Output().toast(mContext, mContext.getString(R.string.noise_countdown_start), 0);
							mSharedPrefs.setNoiseActivated(true);
						} else {
							new Output().toast(mContext, mContext.getString(R.string.no_phone_number), 1);
						}
					}
				} else {
					if (isActive) {
						// baby mode is active
						title = mContext.getString(R.string.dialog_title_change_baby_mode_state_to_false);
						message = mContext.getString(R.string.dialog_message_change_baby_mode_state_to_false);
					} else {
						// baby mode is not active
						title = mContext.getString(R.string.dialog_title_change_baby_mode_state_to_true);
						message = mContext.getString(R.string.dialog_message_change_baby_mode_state_to_true);
					}
					new AlertDialog.Builder(getActivity())
							.setTitle(title)
							.setMessage(message)
							.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
							.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											if (isActive) {
												// enabled -> disabled
												mSharedPrefs.setActiveStateBabyMode(false);
												if (mSharedPrefs.getRemoteAddress() != null) {
													new Message(mContext).send(mContext
															.getString(R.string.BABYFON_MSG_SYSTEM_AWAY));
													mModuleHandler.unregisterBattery();
													if (mSharedPrefs.getForwardingSMS()
															|| mSharedPrefs.getForwardingSMSInfo()) {
														mModuleHandler.unregisterSMS();
													}
												} else {
													mModuleHandler.stopUDPReceiver();
												}
											} else {
												// disabled -> enabled
												mSharedPrefs.setActiveStateBabyMode(true);
												if (mSharedPrefs.getRemoteAddress() != null) {
													mModuleHandler.registerBattery();
													if (mSharedPrefs.getForwardingSMS()
															|| mSharedPrefs.getForwardingSMSInfo()) {
														mModuleHandler.registerSMS();
													}
													new Message(mContext).send(mContext
															.getString(R.string.BABYFON_MSG_SYSTEM_REJOIN));
												} else {
													mModuleHandler.startUDPReceiver();
												}
											}
											updateUI();
										}
									}).create().show();
				}
			}
		});

		return view;
	}

	public void startUiUpdateThread() {
		if (updateTimer == null) {
			updateTimer = new Timer();
		}
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (isCountdownActive) {
					new Task().run();
				}
				((MainActivity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}, 0, 1000);
	}

	public void startRecorder() {
		isCountdownActive = false;
		if (MainActivity.mAudioRecorder == null) {
			MainActivity.mAudioRecorder = new AudioRecorder(mContext, MainActivity.mConnection);
		}
		MainActivity.mAudioRecorder.startRecording();
	}

	public void stopRecorder() {
		if (MainActivity.mAudioRecorder != null) {
			MainActivity.mAudioRecorder.stopRecording();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (updateTimer == null) {
			updateTimer = new Timer();
		}
		startUiUpdateThread();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
	}

	/**
	 * Countdown Timer
	 */
	class Task implements Runnable {
		public void run() {
			countdown--;
			((MainActivity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (isCountdownActive) {
						if (countdown > 0) {
							if (countdown != 1) {
								modeState.setText("Aktiv in: " + countdown + " " + mContext.getString(R.string.seconds));
							} else {
								modeState.setText("Aktiv in: " + countdown + " " + mContext.getString(R.string.second));
							}
						} else {
							startRecorder();
						}
					}
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
