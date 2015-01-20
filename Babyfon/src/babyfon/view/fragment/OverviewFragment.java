package babyfon.view.fragment;

import java.util.Timer;
import java.util.TimerTask;

import babyfon.Generator;
import babyfon.Message;
import babyfon.init.R;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
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

	private boolean isActive;

	// Timer
	private Timer timer;

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

		updateUI();
	}

	public void updateUI() {
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

		// connectivity type
		if (mSharedPrefs.getConnectivityType() == 1) {
			// bluetooth
			connectivityState.setText(R.string.bluetooth);
		} else if (mSharedPrefs.getConnectivityType() == 2) {
			// wifi
			connectivityState.setText(R.string.wifi);
		} else {
			// wifi direct
			connectivityState.setText(R.string.wifip2p);
		}

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

		// password
		passwordState.setText(mSharedPrefs.getPassword());
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
				final CharSequence[] items = { mContext.getString(R.string.wifi), mContext.getString(R.string.wifip2p),
						mContext.getString(R.string.bluetooth) };

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(mContext.getString(R.string.dialog_title_connectivity));

				int checkedOption = -1;

				if (mSharedPrefs.getConnectivityType() == 1) {
					checkedOption = 2;
				}

				if (mSharedPrefs.getConnectivityType() == 2) {
					checkedOption = 0;
				}

				if (mSharedPrefs.getConnectivityType() == 3) {
					checkedOption = 1;
				}

				builder.setSingleChoiceItems(items, checkedOption, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							mSharedPrefs.setConnectivityType(2);
							if (mSharedPrefs.getRemoteAddress() == null) {
								mModuleHandler.startUDPReceiver();
							}
							break;
						case 1:
							mSharedPrefs.setConnectivityType(3);
							mModuleHandler.stopUDPReceiver();
							break;
						case 2:
							mSharedPrefs.setConnectivityType(1);
							mModuleHandler.stopUDPReceiver();
							break;
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
							mSharedPrefs.setForwardingSMS(false);
							mSharedPrefs.setForwardingSMSInfo(false);
							break;
						case 1:
							mModuleHandler.registerSMS();
							mSharedPrefs.setForwardingSMSInfo(true);
							mSharedPrefs.setForwardingSMS(false);
							break;
						case 2:
							mModuleHandler.registerSMS();
							mSharedPrefs.setForwardingSMS(true);
							mSharedPrefs.setForwardingSMSInfo(false);
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
		changeMode.setOnClickListener(new View.OnClickListener() {
			String title, message;

			@Override
			public void onClick(View v) {
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
