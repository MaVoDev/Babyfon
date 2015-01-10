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
	 * == Network settings =====================================
	 */

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
	 * == Setup settings =====================================
	 */

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

	/**
	 * Get the temporary mode of the device.
	 * 
	 * @return int: -1 = no mode, 0 = baby mode, 1 = parent mode
	 */
	public int getDeviceModeTemp() {
		return mSharedPreferences.getInt("device_mode_temp", -1);
	}

	/**
	 * Set the temporary mode of the device.
	 * 
	 * @param int temporary mode: -1 = no mode, 0 = baby mode, 1 = parent mode
	 */
	public void setDeviceModeTemp(int mode) {
		editor.putInt("device_mode_temp", mode).commit();
	}

	// == Bluetooth shared state =================================

	/**
	 * Get the shared state of Bluetooth.
	 * 
	 * @return boolean: false = not shared, true = shared
	 */
	public boolean getBluetoothSharedState() {
		return mSharedPreferences.getBoolean("bluetooth_shared_state", false);
	}

	/**
	 * Set the shared state of Bluetooth.
	 * 
	 * @param boolean state: false = not shared, true = shared
	 */
	public void setBluetoothSharedState(boolean state) {
		editor.putBoolean("bluetooth_shared_state", state).commit();
	}

	/**
	 * Get the temporary shared state of Bluetooth.
	 * 
	 * @return int: false = not shared, true = shared
	 */
	public boolean getBluetoothSharedStateTemp() {
		return mSharedPreferences.getBoolean("bluetooth_shared_state_temp", false);
	}

	/**
	 * Set the temporary shared state of Bluetooth.
	 * 
	 * @param boolean temporary state: false = not shared, true = shared
	 */
	public void setBluetoothSharedStateTemp(boolean state) {
		editor.putBoolean("bluetooth_shared_state_temp", state).commit();
	}

	// == Wi-Fi shared state =================================

	/**
	 * Get the shared state of Wi-Fi.
	 * 
	 * @return boolean: false = not shared, true = shared
	 */
	public boolean getWiFiSharedState() {
		return mSharedPreferences.getBoolean("wifi_shared_state", false);
	}

	/**
	 * Set the shared state of Wi-Fi.
	 * 
	 * @param boolean state: false = not shared, true = shared
	 */
	public void setWiFiSharedState(boolean state) {
		editor.putBoolean("wifi_shared_state", state).commit();
	}

	/**
	 * Get the temporary shared state of Wi-Fi.
	 * 
	 * @return boolean: false = not shared, true = shared
	 */
	public boolean getWiFiSharedStateTemp() {
		return mSharedPreferences.getBoolean("wifi_shared_state_temp", false);
	}

	/**
	 * Set the temporary shared state of Wi-Fi.
	 * 
	 * @param boolean temporary state: false = not shared, true = shared
	 */
	public void setWiFiSharedStateTemp(boolean state) {
		editor.putBoolean("wifi_shared_state_temp", state).commit();
	}

	// == Wi-Fi Direct shared state =================================

	/**
	 * Get the shared state of Wi-Fi Direct.
	 * 
	 * @return boolean: false = not shared, true = shared
	 */
	public boolean getWiFiDirectSharedState() {
		return mSharedPreferences.getBoolean("wifi_direct_shared_state", false);
	}

	/**
	 * Set the shared state of Wi-Fi Direct.
	 * 
	 * @param boolean state: false = not shared, true = shared
	 */
	public void setWiFiDirectSharedState(boolean state) {
		editor.putBoolean("wifi_direct_shared_state", state).commit();
	}

	/**
	 * Get the temporary shared state of Wi-Fi Direct.
	 * 
	 * @return boolean: false = not shared, true = shared
	 */
	public boolean getWiFiDirectSharedStateTemp() {
		return mSharedPreferences.getBoolean("wifi_direct_shared_state_temp", false);
	}

	/**
	 * Set the temporary shared state of Wi-Fi Direct.
	 * 
	 * @param boolean temporary state: false = not shared, true = shared
	 */
	public void setWiFiDirectSharedStateTemp(boolean state) {
		editor.putBoolean("wifi_direct_shared_state_temp", state).commit();
	}

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

	/**
	 * Get the temporary state of call privacy.
	 * 
	 * @return boolean: true = send forward, false = don't send forward
	 */
	public boolean getPrivacyCallTemp() {
		return mSharedPreferences.getBoolean("privacy_call_temp", false);
	}

	/**
	 * Set the temporary state of call privacy.
	 * 
	 * @param boolean state: temporary state of call privacy
	 */
	public void setPrivacyCallTemp(boolean state) {
		editor.putBoolean("privacy_call_temp", state).commit();
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

	/**
	 * Get the temporary state of sms privacy.
	 * 
	 * @return boolean: true = send forward, false = don't send forward
	 */
	public boolean getPrivacySMSTemp() {
		return mSharedPreferences.getBoolean("privacy_sms_temp", false);
	}

	/**
	 * Set the temporary state of sms privacy.
	 * 
	 * @param boolean temporary state: temporary state of call privacy
	 */
	public void setPrivacySMSTemp(boolean state) {
		editor.putBoolean("privacy_sms_temp", state).commit();
	}

	// == Password =================================

	/**
	 * Get the password for the connection.
	 * 
	 * @return String: -1 = no password
	 */
	public String getPassword() {
		return mSharedPreferences.getString("password", null);
	}

	/**
	 * Set the password for the connection.
	 * 
	 * @param String
	 *            password: -1 = no password
	 */
	public void setPassword(String password) {
		editor.putString("password", password).commit();
	}

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

	/**
	 * Get the temporary connectivity type.
	 * 
	 * @return int: -1 = no type, 0 = auto, 1 = Bluetooth, 2 = Wi-Fi, 3 = Wi-Fi
	 *         Direct
	 */
	public int getConnectivityTypeTemp() {
		return mSharedPreferences.getInt("connectivity_type_temp", -1);
	}

	/**
	 * Set the temporary connectivity type.
	 * 
	 * @param int temporary type: -1 = no type, 0 = auto, 1 = Bluetooth, 2 =
	 *        Wi-Fi, 3 = Wi-Fi Direct
	 */
	public void setConnectivityTypeTemp(int type) {
		editor.putInt("connectivity_type_temp", type).commit();
	}
}
