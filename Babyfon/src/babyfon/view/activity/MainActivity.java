package babyfon.view.activity;

import babyfon.init.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import babyfon.adapter.NavigationDrawerListAdapter;
import babyfon.connectivity.call.CallReceiver;
import babyfon.connectivity.sms.SMSReceiver;
import babyfon.connectivity.wifi.StreamSender;
import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.model.NavigationDrawerItemModel;
import babyfon.performance.Battery;
import babyfon.performance.Sound;
import babyfon.settings.ModuleHandler;
import babyfon.settings.SharedPrefs;
import babyfon.view.fragment.AbsenceFragment;
import babyfon.view.fragment.BabyMonitorFragment;
import babyfon.view.fragment.overview.OverviewBabyFragment;
import babyfon.view.fragment.overview.OverviewParentsFragment;
import babyfon.view.fragment.setup.SetupDeviceModeFragment;
import babyfon.view.fragment.setup.SetupStartFragment;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// Modules
	public static Battery mBattery;
	public static TCPReceiver mTCPReceiver;
	public static UDPReceiver mUDPReceiver;

	private ModuleHandler mModuleHandler;

	Map<String, Fragment> mFragmentMap = new HashMap<String, Fragment>();

	// Navigation Drawer Title
	private CharSequence drawerTitle;

	// App Title/Name
	private CharSequence appTitle;

	// Slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private OverviewBabyFragment mOverviewBabyFragment;

	private ArrayList<NavigationDrawerItemModel> items;
	private NavigationDrawerListAdapter adapter;

	private SharedPrefs mSharedPrefs;

	public void handleModules() {
		if (mSharedPrefs.getDeviceMode() != -1) {
			if (mSharedPrefs.getConnectivityType() == 2) {
				mModuleHandler.startTCPReceiver();
			}
		}

		if (mSharedPrefs.getRemoteAddress() == null) {
			mModuleHandler.startUDPReceiver();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mOverviewBabyFragment = new OverviewBabyFragment(this);

		new StreamSender(this);

		mSharedPrefs = new SharedPrefs(this);
		mModuleHandler = new ModuleHandler(this);

		handleModules();

		new CallReceiver(this).missedCalls();

		new SMSReceiver(this); // TODO Only baby mode

		appTitle = drawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.navigation_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		items = new ArrayList<NavigationDrawerItemModel>();

		// Listenelement: Übersicht
		items.add(new NavigationDrawerItemModel(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Listenelement: Babymonitor
		items.add(new NavigationDrawerItemModel(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Listenelement: Anrufe und Nachrichten in Abwesenheit
		items.add(new NavigationDrawerItemModel(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), true, "0"));
		// Listenelement: Einrichtungsassistent
		items.add(new NavigationDrawerItemModel(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavigationDrawerListAdapter(getApplicationContext(), items);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Drawer Layout, Drawer Icon, Drawer Name (Drawer open, close)
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
				R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(appTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// Set Typeface
				getActionBar().setTitle(drawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Checke the mode
		if (mSharedPrefs.getDeviceMode() != -1) {
			// Show first fragment view: OverviewFragment
			displayView(0);
		} else {
			// Show first fragment view: SetupFragment
			displayView(3);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mSharedPrefs.getRemoteAddress() != null) {
			new babyfon.Message(this).send(this.getString(R.string.MESSAGE_SYSTEM_EXIT));
		}

		if (mSharedPrefs.getDeviceMode() == 0) {
			new Sound(this).soundOn();
		}

		mModuleHandler.unregisterBattery();
		mModuleHandler.stopTCPReceiver();
		mModuleHandler.stopUDPReceiver();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mSharedPrefs.getDeviceMode() == 0) {
			if (mSharedPrefs.getNumberOfConnections() < mSharedPrefs.getNumberOfAllowedConnections()) {
				mModuleHandler.startUDPReceiver();
			} else {
				if (mSharedPrefs.getNumberOfConnections() > 0) {
					mModuleHandler.registerBattery();
				}
			}
			new Sound(this).mute();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		ActionBar actionBar = getActionBar();
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
	}

	/**
	 * Aktion bei Betätiung der BackPressed-Taste bei aktiver MainActivity.
	 */
	@Override
	public void onBackPressed() {
		// Dialogfenster zur Abfrage, ob die App wirklich beendet werden soll.
		new AlertDialog.Builder(this).setIcon(null).setTitle(getString(R.string.dialog_title_exit))
				.setMessage(getString(R.string.dialog_message_exit))
				.setPositiveButton(getString(R.string.dialog_button_yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
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
			if (mSharedPrefs.getDeviceMode() == 0) {
				// Baby mode
				fragment = new OverviewBabyFragment(this);
			} else {
				// Parents mode
				fragment = new OverviewParentsFragment(this);
			}
		} else if (id.equals("OverviewFragmentBaby")) {
			// Baby mode
			fragment = new OverviewBabyFragment(this);
		} else if (id.equals("BabymonitorFragment")) {
			// Open babymonitor
			fragment = new BabyMonitorFragment(this);
		} else if (id.equals("AbsenceFragment")) {
			// Open absence
			fragment = new AbsenceFragment(this);
		} else if (id.equals("SetupFragment")) {
			// Open setup
			if (mSharedPrefs.getDeviceMode() != -1) {
				// Baby or parents mode is active
				fragment = new SetupDeviceModeFragment(this);
			} else {
				// No mode
				fragment = new SetupStartFragment(this);
			}
		}

		mFragmentMap.put(id, fragment);

		return fragment;
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		String id = null;
		switch (position) {
		case 0:
			id = "OverviewFragment";
			break;
		case 1:
			id = "BabymonitorFragment";
			break;
		case 2:
			id = "AbsenceFragment";
			break;
		case 3:
			id = "SetupFragment";
			break;

		default:
			break;
		}

		fragment = mFragmentMap.get(id);
		if (fragment == null) {
			fragment = createFragmentById(id);
		}
		// TODO hier ist der Fehler beim Abschließen des Setups
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
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
		getActionBar().setTitle(appTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
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
}
