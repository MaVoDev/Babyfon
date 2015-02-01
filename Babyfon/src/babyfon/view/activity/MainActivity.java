package babyfon.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import babyfon.Notification;
import babyfon.adapter.NavigationDrawerListAdapter;
import babyfon.audio.AudioPlayer;
import babyfon.audio.AudioRecorder;
import babyfon.connectivity.phone.CallStateListener;
import babyfon.connectivity.phone.SMSReceiver;
import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.init.R;
import babyfon.model.NavigationDrawerItemModel;
import babyfon.performance.Battery;
import babyfon.performance.ConnectivityStateCheck;
import babyfon.performance.Sound;
import babyfon.service.LocalService;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.AbsenceFragment;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.OverviewFragment;
import babyfon.view.fragment.setup.SetupDeviceModeFragment;
import babyfon.view.fragment.setup.SetupSearchDevicesFragment;
import babyfon.view.fragment.setup.SetupStartFragment;

public class MainActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	public static AudioPlayer mAudioPlayer;

	// Module objects
	public static Battery mBattery;
	public static TCPReceiver mTCPReceiver;
	public static UDPReceiver mUDPReceiver;
	public static SMSReceiver mSmsReceiver;
	public static ConnectivityStateCheck mConnectivityStateCheck;

	// Notification objects
	public static Vibrator mVibrator;
	public static Ringtone mRingtone;
	public static FrameLayout overlay;

	public static AudioRecorder mAudioRecorder;

	public static IntentFilter mIntentFilterSms;
	public static IntentFilter mIntentFilterCall;

	private ModuleHandler mModuleHandler;
	private SharedPrefs mSharedPrefs;

	Map<String, Fragment> mFragmentMap = new HashMap<String, Fragment>();

	// Navigation Drawer Title
	private CharSequence drawerTitle;

	// App Title/Name
	private CharSequence appTitle;

	// Slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	// Timer
	private Timer timerNavigationDrawer;
	private Timer timerConnectivityState;
	private Timer timerCloseDrawer;

	private ArrayList<NavigationDrawerItemModel> items;
	private NavigationDrawerListAdapter adapter;

	private int counter;

	private static final String TAG = MainActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// TODO TEST
		mContext = this;
		// ---------

		// TelephonyManager class object to register one listner
		TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// Register listener for LISTEN_CALL_STATE
		mTelephonyManager.listen(new CallStateListener(this), PhoneStateListener.LISTEN_CALL_STATE);

		mAudioPlayer = new AudioPlayer();
		mModuleHandler = new ModuleHandler(this);
		mSharedPrefs = new SharedPrefs(this);

		initNavigationDrawer();

		startNavigationDrawerUpdateThread();

		displayView(0);

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// TODO: WORKAROUND UM DRAWER ERRORS ZU FIXEN, ALTER ERSTMAL
		// AUSKOMMENTIERT; VS!
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, android.R.string.ok, android.R.string.no);

		// if (mSharedPrefs.getConnectivityType() == 1) {
		// Zum Service verbinden / Service starten
		doBindService();
		// }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// TODO: F‹R TESTZWECKE SERVICE WIEDER AUSMACHEN, WENN APP GESCHLOSSEN WIRD
		doUnbindService();
		mModuleHandler.stopRemoteCheck();

		if (timerNavigationDrawer != null) {
			timerNavigationDrawer.cancel();
			timerNavigationDrawer = null;
		}

		if (timerConnectivityState != null) {
			timerConnectivityState.cancel();
			timerConnectivityState = null;
		}

		if (mSharedPrefs.getRemoteAddress() != null) {
			new babyfon.Message(this).send(this.getString(R.string.BABYFON_MSG_SYSTEM_AWAY));
		}

		if (mSharedPrefs.getDeviceMode() == 0) {
			new Sound(this).soundOn();
			mModuleHandler.unregisterBattery();
			if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
				mModuleHandler.unregisterSMS();
			}
		}

		if (mSharedPrefs.getConnectivityType() == 2) {
			mModuleHandler.stopUDPReceiver();
			mModuleHandler.stopTCPReceiver();
		}

		if (MainActivity.mAudioRecorder != null) {
			MainActivity.mAudioRecorder.stopRecording();
		}

		if (mSharedPrefs.getRemoteAddress() != null) {
			mModuleHandler.stopRemoteCheck();
		}

		new Notification(this).stop();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (timerNavigationDrawer == null) {
			timerNavigationDrawer = new Timer();
		}
		initNavigationDrawer();

		if (mSharedPrefs.getRemoteAddress() != null) {
			if (mSharedPrefs.getConnectivityType() == 2) {
				mModuleHandler.startRemoteCheck();
			}
			new babyfon.Message(this).send(this.getString(R.string.BABYFON_MSG_CONNECTION_HELLO) + ";" + mSharedPrefs.getHostAddress()
					+ ";" + mSharedPrefs.getPassword());
			if (mSharedPrefs.getDeviceMode() == 0) {
				mModuleHandler.registerBattery();
				if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
					mModuleHandler.registerSMS();
				}
			}
		} else {
			if (mSharedPrefs.getDeviceMode() == 0) {
				mSharedPrefs.setRemoteOnlineState(false);

				new Sound(this).mute();
			}
		}
		if (mSharedPrefs.getConnectivityType() == 2) {
			mModuleHandler.startUDPReceiver();
			mModuleHandler.startTCPReceiver();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			// Wenn sich der Navigation Drawer nicht automatisch schlieﬂen
			// sollte
			timerCloseDrawer = new Timer();
			timerCloseDrawer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {
							mDrawerLayout.closeDrawer(mDrawerList);
							timerCloseDrawer.cancel();
							timerCloseDrawer = null;
						}
					});
				}
			}, 500, 1000);
		}

		if (mSharedPrefs.getRemoteAddress() != null) {
			mModuleHandler.stopRemoteCheck();
		}

		timerNavigationDrawer.cancel();
		timerNavigationDrawer = null;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mSharedPrefs.getConnectivityType() != 3) {
			if (mSharedPrefs.getConnectivityType() == 2) {
				mModuleHandler.startTCPReceiver();
				mModuleHandler.startUDPReceiver();
			}

			if (mSharedPrefs.getConnectivityType() != 1) { // BEI BT NICHT!
				if (mSharedPrefs.getRemoteAddress() != null) {
					if (mSharedPrefs.isNoiseActivated()) {
						if (MainActivity.mAudioRecorder == null) {
							// MainActivity.mAudioRecorder = new AudioRecorder(this, mBoundService.getConnection());
							MainActivity.mAudioRecorder = new AudioRecorder(this, null);
							MainActivity.mAudioRecorder.startRecording();
						}
					} else {
						if (MainActivity.mAudioRecorder != null) {
							MainActivity.mAudioRecorder.stopRecording();
						}
					}
				}
			}
		}

		if (mSharedPrefs.getForwardingSMS() || mSharedPrefs.getForwardingSMSInfo()) {
			mModuleHandler.registerSMS();
		}

		if (timerNavigationDrawer == null) {
			timerNavigationDrawer = new Timer();
		}
		startNavigationDrawerUpdateThread();

		ActionBar actionBar = getSupportActionBar();

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_container);

		// Layout related to the gender of the baby
		if (mSharedPrefs.getGender() == 0) {
			// Set action bar color
			actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar_male));
			// Set layout background color
			frameLayout.setBackgroundResource(R.drawable.bg_male);
			// Set navigation drawer background
			mDrawerList.setBackgroundResource(R.drawable.bg_navigation_drawer_male);
		} else {
			// Set action bar color
			actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar_female));
			// Set layout background color
			frameLayout.setBackgroundResource(R.drawable.bg_female);
			// Set navigation drawer background
			mDrawerList.setBackgroundResource(R.drawable.bg_navigation_drawer_female);
		}

		if (mSharedPrefs.getConnectivityType() == 2) {
			if (mSharedPrefs.getRemoteAddress() != null) {
				mModuleHandler.startRemoteCheck();
			}
		}
	}

	/**
	 * Aktion bei Bet‰tiung der BackPressed-Taste bei aktiver MainActivity.
	 */
	@Override
	public void onBackPressed() {
		// Dialogfenster zur Abfrage, ob die App wirklich beendet werden soll.
		new AlertDialog.Builder(this).setIcon(null).setTitle(getString(R.string.dialog_title_exit))
				.setMessage(getString(R.string.dialog_message_exit))
				.setPositiveButton(getString(R.string.dialog_button_yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.finish();
					}

				}).setNegativeButton(getString(R.string.dialog_button_no), null).show();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// TODO WORKAROUND F‹R FEHLER IN SetupSearchDevices
	public void setFragmentForId(Fragment fragment, String id) {
		mFragmentMap.put(id, fragment);
	}

	public Fragment getFragmentById(String id) {

		Fragment fragment = mFragmentMap.get(id);
		if (fragment == null) {
			fragment = createFragmentById(id);
		}

		return fragment;
	}

	private Fragment createFragmentById(String id) {
		Fragment fragment = null;

		if (id.equals("OverviewFragment")) {
			// Open overview
			fragment = new OverviewFragment(this);
		} else if (id.equals("OverviewFragmentBaby")) {
			// Baby mode
			fragment = new OverviewFragment(this);
		} else if (id.equals("BabyMonitorFragment")) {
			// Open babymonitor
			fragment = new BabyMonitorFragment(this);
		} else if (id.equals("AbsenceFragment")) {
			// Open absence
			fragment = new AbsenceFragment(this);
		} else if (id.equals("SetupSearchDevicesFragment")) {
			// Open setup search devices
			fragment = new SetupSearchDevicesFragment(this);
		} else if (id.equals("SetupFragment")) {
			// Open setup
			if (mSharedPrefs.getDeviceMode() != -1) {
				// Baby or parents mode is active
				fragment = new SetupDeviceModeFragment(this);
			} else {
				// No mode
				fragment = new SetupStartFragment(this);
			}
		} else if (id.equals("Settings")) {
			Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(intent);
		}

		// doesn't store the setup start screen
		if (mSharedPrefs.getDeviceMode() != -1) {
			mFragmentMap.put(id, fragment);
		}

		return fragment;
	}

	public void loadStoredSetupOptions() {
		mSharedPrefs.setDeviceModeTemp(mSharedPrefs.getDeviceMode());
		mSharedPrefs.setConnectivityTypeTemp(mSharedPrefs.getConnectivityType());
		mSharedPrefs.setForwardingSMSInfoTemp(mSharedPrefs.getForwardingSMSInfo());
		mSharedPrefs.setForwardingSMSTemp(mSharedPrefs.getForwardingSMS());
		mSharedPrefs.setForwardingCallInfoTemp(mSharedPrefs.getForwardingCallInfo());
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		String id = null;
		switch (position) {
		case 0:
			if (mSharedPrefs.getDeviceMode() == -1) {
				id = "SetupFragment";
			}

			if (mSharedPrefs.getDeviceMode() == 0) {
				id = "OverviewFragment";
			}
			if (mSharedPrefs.getDeviceMode() == 1) {
				id = "BabyMonitorFragment";
			}
			break;
		case 1:
			if (mSharedPrefs.getDeviceMode() == -1) {
				id = "Settings";
			}
			if (mSharedPrefs.getDeviceMode() == 0) {
				loadStoredSetupOptions();
				id = "SetupFragment";
			}
			if (mSharedPrefs.getDeviceMode() == 1) {
				id = "AbsenceFragment";
			}
			break;
		case 2:
			if (mSharedPrefs.getDeviceMode() == 0) {
				id = "Settings";
			}
			if (mSharedPrefs.getDeviceMode() == 1) {
				loadStoredSetupOptions();
				id = "SetupFragment";
			}
			break;
		case 3:
			if (mSharedPrefs.getDeviceMode() == 1) {
				id = "Settings";
			}
			break;

		default:
			break;
		}

		fragment = mFragmentMap.get(id);
		if (fragment == null) {
			fragment = createFragmentById(id);
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		appTitle = title;
		getSupportActionBar().setTitle(appTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void initNavigationDrawer() {
		appTitle = drawerTitle = getTitle();

		if (mSharedPrefs.getDeviceMode() == 0) {
			// load slide menu items for baby mode
			navMenuTitles = getResources().getStringArray(R.array.navigation_drawer_items_baby_mode);
			// nav drawer icons from resources for baby mode
			navMenuIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons_baby_mode);
		} else if (mSharedPrefs.getDeviceMode() == 1) {
			// load slide menu items for baby mode
			navMenuTitles = getResources().getStringArray(R.array.navigation_drawer_items_parents_mode);
			// nav drawer icons from resources for baby mode
			navMenuIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons_parents_mode);
		} else {
			// load slide menu items for baby mode
			navMenuTitles = getResources().getStringArray(R.array.navigation_drawer_items_no_mode);
			// nav drawer icons from resources for baby mode
			navMenuIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons_no_mode);
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		items = new ArrayList<NavigationDrawerItemModel>();

		if (mSharedPrefs.getDeviceMode() == 0) {
			// Listenelement: ‹bersicht
			items.add(new NavigationDrawerItemModel(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
			// Listenelement: Einrichtungsassistent
			items.add(new NavigationDrawerItemModel(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
			// Listenelement: Einstellungen
			items.add(new NavigationDrawerItemModel(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		} else if (mSharedPrefs.getDeviceMode() == 1) {
			// Listenelement: Babymonitor
			items.add(new NavigationDrawerItemModel(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
			// Listenelement: Anrufe und Nachrichten in Abwesenheit
			items.add(new NavigationDrawerItemModel(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true, String.valueOf(counter)));
			// Listenelement: Einrichtungsassistent
			items.add(new NavigationDrawerItemModel(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
			// Listenelement: Einstellungen
			items.add(new NavigationDrawerItemModel(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		} else {
			// Listenelement: Einrichtungsassistent
			items.add(new NavigationDrawerItemModel(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
			// Listenelement: Einstellungen
			items.add(new NavigationDrawerItemModel(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavigationDrawerListAdapter(getApplicationContext(), items);
		mDrawerList.setAdapter(adapter);

		// Drawer Layout, Drawer Icon, Drawer Name (Drawer open, close)
		// mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
		// R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
		// public void onDrawerClosed(View view) {
		// getActionBar().setTitle(appTitle);
		// // calling onPrepareOptionsMenu() to show action bar icons
		// invalidateOptionsMenu();
		// }
		//
		// public void onDrawerOpened(View drawerView) {
		// // Set Typeface
		// getActionBar().setTitle(drawerTitle);
		// // calling onPrepareOptionsMenu() to hide action bar icons
		// invalidateOptionsMenu();
		// }
		// };

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void startNavigationDrawerUpdateThread() {
		if (timerNavigationDrawer == null) {
			timerNavigationDrawer = new Timer();
		}

		counter = mSharedPrefs.getCallSMSCounter();

		timerNavigationDrawer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (mSharedPrefs.getTempMode() != mSharedPrefs.getDeviceMode()) {
							mSharedPrefs.setTempMode(mSharedPrefs.getDeviceMode());
							initNavigationDrawer();
							Log.d(TAG, "Navigation Drawer changed.");
						}
					}
				});
			}
		}, 0, 1000);
	}

	//
	// --------------------------------------- Service Zeugs (LOCAL)
	//

	public static LocalService mBoundService;
	private static Context mContext;
	private boolean mIsBound = false;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((LocalService.LocalBinder) service).getService();

			// Tell the user about this for our demo.
			Log.i(TAG, "Service connected with app...");
			Toast.makeText(MainActivity.this, "Service connected.", Toast.LENGTH_SHORT).show();

			// if (MainActivity.mAudioRecorder != null) {
			// MainActivity.mAudioRecorder.stopRecording();
			// MainActivity.mAudioRecorder = null;
			// }
			// MainActivity.mAudioRecorder = new AudioRecorder(MainActivity.this, mBoundService);

			// MainActivity.mAudioRecorder.startRecording();

			// TODO: einbauen:
			// Verbinden mit aktuelle gepairtem Device...
			// mBoundService.connectTo("CC:96:A0:41:34:3E");
			// mBoundService.connectTo(mSharedPrefs.getRemoteAddress());
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
			Toast.makeText(MainActivity.this, "Service disconnected.", Toast.LENGTH_SHORT).show();
		}
	};

	public void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).

		// bindService(new Intent(MainActivity.this, LocalService.class),
		// mServiceConnection, Context.BIND_AUTO_CREATE);

		Intent serviceIntent = new Intent(MainActivity.this, LocalService.class);

		startService(serviceIntent);
		bindService(serviceIntent, mServiceConnection, 0);
		// bindService(serviceIntent, mServiceConnection,
		// Context.BIND_AUTO_CREATE);

		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			Log.i(TAG, "Unbind service...");

			// Detach our existing connection.
			unbindService(mServiceConnection);
			mIsBound = false;

			Log.i(TAG, "Stop service...");
			// Stop service
			Intent serviceIntent = new Intent(MainActivity.this, LocalService.class);
			stopService(serviceIntent);
		}
	}

	//
	// --------------------------------------- / Service Zeugs (LOCAL) ENDE
	//

	public static Context getContext() {
		if (mContext == null)
			return null;
		else
			return mContext;

	}
}
