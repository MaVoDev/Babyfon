package babyfon.settings;

import babyfon.init.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class SharedPrefs {

	private final String APP_PREFS = "babyfon_prefs";

	private Resources mResources;
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	public SharedPrefs(Context mContext) {
		this.mResources = mContext.getResources();
		this.mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Activity.MODE_PRIVATE);
		this.editor = mSharedPreferences.edit();
	}

	/*
	 * == General =====================================
	 */

	// == Connectivity =================================

	/**
	 * Get the active connectivity type.
	 * 
	 * @return int: -1 = no type, 0 = auto, 1 = Bluetooth, 2 = Wi-Fi, 3 = Wi-Fi
	 *         Direct
	 */
	public int getConnectivityType() {
		return mSharedPreferences.getInt("connectivity_type", -1);
	}

	/**
	 * Set the active connectivity type.
	 * 
	 * @param int type: -1 = no type, 0 = auto, 1 = Bluetooth, 2 = Wi-Fi, 3 =
	 *        Wi-Fi Direct
	 */
	public void setConnectivityType(int type) {
		editor.putInt("connectivity_type", type).commit();
	}

	// == Device mode =================================

	/**
	 * Get the mode of the device.
	 * 
	 * @return int: -1 = no mode, 0 = baby mode, 1 = parent mode
	 */
	public int getDeviceMode() {
		return mSharedPreferences.getInt("device_mode", -1);
	}

	/**
	 * Set the mode of the device.
	 * 
	 * @param int mode: -1 = no mode, 0 = baby mode, 1 = parent mode
	 */
	public void setDeviceMode(int mode) {
		editor.putInt("device_mode", mode).commit();
	}

	// == Password =================================

	/**
	 * Get the password for the connection.
	 * 
	 * @return int: -1 = no password
	 */
	public int getPassword() {
		return mSharedPreferences.getInt("password", -1);
	}

	/**
	 * Set the password for the connection.
	 * 
	 * @param int password: -1 = no password
	 */
	public void setPassword(int password) {
		editor.putInt("password", password).commit();
	}

	// == Port TCP ====================================

	/**
	 * Get the tcp port.
	 * 
	 * @return int: tcp port
	 */
	public int getTCPPort() {
		return mSharedPreferences.getInt("port_tcp", mResources.getInteger(R.integer.sharedprefs_default_port_tcp));
	}

	// == Port UDP ====================================

	/**
	 * Get the udp port.
	 * 
	 * @return int: udp port
	 */
	public int getUDPPort() {
		return mSharedPreferences.getInt("port_tcp", mResources.getInteger(R.integer.sharedprefs_default_port_udp));
	}

	/*
	 * == Settings ====================================
	 */

	// == Gender of the baby ========================

	/**
	 * Get the gender of the baby.
	 * 
	 * @return int: 0 = boy, 1 = girl
	 */
	public int getGender() {
		return mSharedPreferences.getInt("gender", 0);
	}

	/**
	 * Set the gender of the baby.
	 * 
	 * @param int gender: 0 = boy, 1 = girl
	 */
	public void setGender(int gender) {
		editor.putInt("gender", gender).commit();
	}

	// == Name of the baby ===========================

	/**
	 * Get the name of the baby.
	 * 
	 * @return String: name of the baby
	 */
	public String getName() {
		return mSharedPreferences.getString("baby_name", mResources.getString(R.string.pref_default_baby_name));
	}

	/**
	 * Set the name of the baby.
	 * 
	 * @param String
	 *            name: name of the baby
	 */
	public void setName(String name) {
		editor.putString("baby_name", name).commit();
	}

	// == Notification display ========================

	/**
	 * Get the state of display notification.
	 * 
	 * @return boolean: true = enabled, false = disabled
	 */
	public boolean getDisplayNotificationState() {
		return mSharedPreferences.getBoolean("notification_display_state", true);
	}

	/**
	 * Set the state of display notification.
	 * 
	 * @param boolean isEnabled: state of display notification
	 */
	public void setDisplayNotificationState(boolean isEnabled) {
		editor.putBoolean("notification_display_state", isEnabled).commit();
	}

	// == Notification sound ==========================

	/**
	 * Get the state of sound notification.
	 * 
	 * @return boolean: true = enabled, false = disabled
	 */
	public boolean getSoundNotificationState() {
		return mSharedPreferences.getBoolean("notification_sound_state", true);
	}

	/**
	 * Set the state of sound notification.
	 * 
	 * @param boolean isEnabled: state of sound notification
	 */
	public void setSoundNotificationState(boolean isEnabled) {
		editor.putBoolean("notification_sound_state", isEnabled).commit();
	}

	// == Notification vibrate ========================

	/**
	 * Get the state of vibrate notification.
	 * 
	 * @return boolean: true = enabled, false = disabled
	 */
	public boolean getVibrateNotificationState() {
		return mSharedPreferences.getBoolean("notification_vibrate_state", true);
	}

	/**
	 * Set the state of vibrate notification.
	 * 
	 * @param boolean isEnabled: state of vibrate notification
	 */
	public void setVibrateNotificationState(boolean isEnabled) {
		editor.putBoolean("notification_vibrate_state", isEnabled).commit();
	}

	/*
	 * == Setup =====================================
	 */

	// == Privacy: Call =================================

	/**
	 * Get the state of call privacy.
	 * 
	 * @return boolean: true = send forward, false = don't send forward
	 */
	public boolean getPrivacyCall() {
		return mSharedPreferences.getBoolean("privacy_call", false);
	}

	/**
	 * Set the state of call privacy.
	 * 
	 * @param boolean state: state of call privacy
	 */
	public void setPrivacyCall(boolean state) {
		editor.putBoolean("privacy_call", state).commit();
	}

	// == Privacy: sms =================================

	/**
	 * Get the state of sms privacy.
	 * 
	 * @return boolean: true = send forward, false = don't send forward
	 */
	public boolean getPrivacySMS() {
		return mSharedPreferences.getBoolean("privacy_sms", false);
	}

	/**
	 * Set the state of sms privacy.
	 * 
	 * @param boolean state: state of call privacy
	 */
	public void setPrivacySMS(boolean state) {
		editor.putBoolean("privacy_sms", state).commit();
	}
}
