package babyfon.view.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

	private SharedPrefs mSharedPrefs;

	// Typeface
	Typeface mTypeface_b;
	Typeface mTypeface_n;

	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mTypeface_b = Typeface.createFromAsset(this.getAssets(), "fonts/BOOKOSBI.TTF");
		mTypeface_n = Typeface.createFromAsset(this.getAssets(), "fonts/BOOKOSI.TTF");

		mSharedPrefs = new SharedPrefs(this);
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// Add 'Baby' preferences.
		addPreferencesFromResource(R.xml.pref_baby);

		// Add 'Notification' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_notifications);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("baby_name"));
		bindPreferenceSummaryToValue(findPreference("gender_list"));
		bindPreferenceSummaryToValue(findPreference("choose_ringtone"));

		// Define checkbox (Display notification)
		final CheckBoxPreference cbpDisplayNotification = (CheckBoxPreference) getPreferenceManager().findPreference(
				"enable_display_notification");

		// Define listener for checkbox (display notification)
		cbpDisplayNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object state) {

				boolean isEnabled = ((Boolean) state).booleanValue();
				Log.d("Babyfon", "Pref " + preference.getKey() + " changed to " + isEnabled);

				// Store value in SharedPreferences
				mSharedPrefs.setDisplayNotificationState(isEnabled);
				return true;
			}
		});

		// Define checkbox (Sound notification)
		final CheckBoxPreference cbpSoundNotification = (CheckBoxPreference) getPreferenceManager().findPreference(
				"enable_sound_notification");

		// Define listener for checkbox (vibrate notification)
		cbpSoundNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object state) {

				boolean isEnabled = ((Boolean) state).booleanValue();
				Log.d("Babyfon", "Pref " + preference.getKey() + " changed to " + isEnabled);

				// Store value in SharedPreferences
				mSharedPrefs.setSoundNotificationState(isEnabled);

				return true;
			}
		});

		// Define checkbox (Vibrate notification)
		final CheckBoxPreference cbpVibrateNotification = (CheckBoxPreference) getPreferenceManager().findPreference(
				"enable_vibrate_notification");

		// Define listener for checkbox (vibrate notification)
		cbpVibrateNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object state) {

				boolean isEnabled = ((Boolean) state).booleanValue();
				Log.d("Babyfon", "Pref " + preference.getKey() + " changed to " + isEnabled);

				// Store value in SharedPreferences
				mSharedPrefs.setVibrateNotificationState(isEnabled);

				return true;
			}
		});

		// Define list (Gender)
		final ListPreference lpGender = (ListPreference) getPreferenceManager().findPreference("gender_list");

		// Define listener for list (Gender)
		lpGender.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object state) {

				// Update the summary of the gender in the ui.
				sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, state);

				int gender = Integer.parseInt(state.toString());
				Log.d("Babyfon", "Pref " + preference.getKey() + " changed to " + gender);

				// Store value in SharedPreferences
				mSharedPrefs.setGender(gender);

				return true;
			}
		});

		// Define edittext (Name)
		final EditTextPreference etpName = (EditTextPreference) getPreferenceManager().findPreference("baby_name");

		// Define listener for edittext (Name)
		etpName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object state) {

				// Update the summary of the name in the ui.
				sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, state);

				String name = state.toString();
				Log.d("Babyfon", "Pref " + preference.getKey() + " changed to " + name);

				// Store value in SharedPreferences
				mSharedPrefs.setName(name);
				System.out.println(mSharedPrefs.getName());

				return true;
			}
		});
	}

	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);
				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}
			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {

		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
				.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_baby);

			bindPreferenceSummaryToValue(findPreference("baby_name"));
			bindPreferenceSummaryToValue(findPreference("gender_list"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);

			bindPreferenceSummaryToValue(findPreference("choose_ringtone"));
		}
	}
}
