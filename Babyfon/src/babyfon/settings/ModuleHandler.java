package babyfon.settings;

import android.content.Context;
import android.util.Log;
import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.performance.Battery;
import babyfon.view.activity.MainActivity;

public class ModuleHandler {

	private Context mContext;

	private static final String TAG = ModuleHandler.class.getCanonicalName();

	public ModuleHandler(Context mContext) {
		this.mContext = mContext;
	}

	public void registerBattery() {
		if (MainActivity.mBattery == null) {
			Log.i(TAG, "Try to register battery receiver.");
			MainActivity.mBattery = new Battery(mContext);
			if (MainActivity.mBattery.register()) {
				Log.i(TAG, "Battery receiver registered.");
			} else {
				Log.e(TAG, "Error: Can't register battery receiver.");
			}
		} else {
			Log.e(TAG, "Battery receiver is still registered.");
		}
	}

	public void unregisterBattery() {
		if (MainActivity.mBattery != null) {
			Log.i(TAG, "Try to unregister battery receiver...");
			if (MainActivity.mBattery.unregister()) {
				MainActivity.mBattery = null;
				Log.i(TAG, "Battery receiver unregistered.");
			} else {
				Log.e(TAG, "Error: Can't unregister battery receiver.");
			}
		} else {
			Log.e(TAG, "Can't unregister battery: The receiver wasn't registerd or has been unregisterd.");
		}
	}

	/**
	 * Start TCP receiver
	 */
	public void startTCPReceiver() {
		if (MainActivity.mTCPReceiver == null) {
			Log.i(TAG, "Try to start TCP receiver...");
			MainActivity.mTCPReceiver = new TCPReceiver(mContext);
			if (MainActivity.mTCPReceiver.start()) {
				Log.i(TAG, "TCP receiver is running.");
			} else {
				Log.e(TAG, "Error: Can't start TCP receiver.");
			}
		} else {
			Log.e(TAG, "TCP receiver is still running.");
		}
	}

	/**
	 * Stop TCP receiver
	 */
	public void stopTCPReceiver() {
		if (MainActivity.mTCPReceiver != null) {
			Log.i(TAG, "Try to stop TCP receiver...");
			if (MainActivity.mTCPReceiver.stop()) {
				MainActivity.mTCPReceiver = null;
				Log.i(TAG, "TCP receiver closed.");
			} else {
				Log.e(TAG, "Error: Can't close TCP receiver.");
			}
		} else {
			Log.e(TAG, "Can't close UDP receiver: The receiver wasn't active or has been stopped.");
		}
	}

	/**
	 * Start UDP receiver
	 */
	public void startUDPReceiver() {
		if (MainActivity.mUDPReceiver == null) {
			Log.i(TAG, "Try to start UDP receiver...");
			MainActivity.mUDPReceiver = new UDPReceiver(mContext);
			if (MainActivity.mUDPReceiver.start()) {
				Log.i(TAG, "UDP receiver is running.");
			} else {
				Log.e(TAG, "Error: Can't start UDP receiver.");
			}
		} else {
			Log.e(TAG, "UDP receiver is still running.");
		}
	}

	/**
	 * Stop UDP receiver
	 */
	public void stopUDPReceiver() {
		if (MainActivity.mUDPReceiver != null) {
			Log.i(TAG, "Try to stop UDP receiver...");
			if (MainActivity.mUDPReceiver.stop()) {
				MainActivity.mUDPReceiver = null;
				Log.i(TAG, "UDP receiver closed.");
			} else {
				Log.e(TAG, "Error: Can't close UDP receiver.");
			}
		} else {
			Log.e(TAG, "Can't close UDP receiver: The receiver wasn't active or has been stopped.");
		}
	}
}
