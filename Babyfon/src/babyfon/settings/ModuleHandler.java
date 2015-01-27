package babyfon.settings;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import babyfon.audio.AudioRecorder;
import babyfon.connectivity.sms.SMSReceiver;
import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.performance.Battery;
import babyfon.performance.ConnectivityStateCheck;
import babyfon.view.activity.MainActivity;

public class ModuleHandler {

	private Context mContext;

	private static final String TAG = ModuleHandler.class.getCanonicalName();

	public ModuleHandler(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * Register battery receiver
	 */
	public void registerBattery() {
		if (MainActivity.mBattery == null) {
			Log.i(TAG, "Try to register battery receiver...");
			MainActivity.mBattery = new Battery(mContext);
			if (MainActivity.mBattery.register()) {
				Log.d(TAG, "Battery receiver registered.");
			} else {
				Log.e(TAG, "Error: Can't register battery receiver.");
			}
		} else {
			Log.e(TAG, "Battery receiver is still registered.");
		}
	}

	/**
	 * Unregister battery receiver
	 */
	public void unregisterBattery() {
		if (MainActivity.mBattery != null) {
			Log.i(TAG, "Try to unregister battery receiver...");
			if (MainActivity.mBattery.unregister()) {
				MainActivity.mBattery = null;
				Log.d(TAG, "Battery receiver unregistered.");
			} else {
				Log.e(TAG, "Error: Can't unregister battery receiver.");
			}
		} else {
			Log.e(TAG, "Can't unregister battery: The receiver wasn't registerd or has been unregisterd.");
		}
	}

	/**
	 * Register SMS receiver
	 */
	public void registerSMS() {
		if (MainActivity.mIntentFilter == null) {
			MainActivity.mIntentFilter = new IntentFilter();
			MainActivity.mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		}

		if (MainActivity.mSmsReceiver == null) {
			Log.i(TAG, "Try to register sms receiver...");
			MainActivity.mSmsReceiver = new SMSReceiver(mContext);
			try {
				mContext.registerReceiver(MainActivity.mSmsReceiver, MainActivity.mIntentFilter);
				Log.d(TAG, "SMS receiver registered.");
			} catch (Exception e) {
				Log.e(TAG, "Error: Can't register sms receiver.");
			}
		} else {
			Log.e(TAG, "SMS receiver is still registered.");
		}
	}

	/**
	 * Unregister sms receiver
	 */
	public void unregisterSMS() {
		if (MainActivity.mSmsReceiver != null) {
			Log.i(TAG, "Try to unregister sms receiver...");
			try {
				mContext.unregisterReceiver(MainActivity.mSmsReceiver);
				MainActivity.mSmsReceiver = null;
				MainActivity.mIntentFilter = null;
				Log.d(TAG, "SMS receiver unregistered.");
			} catch (Exception e) {
				Log.e(TAG, "Error: Can't unregister sms receiver.");
			}
		} else {
			Log.e(TAG, "Can't unregister sms: The receiver wasn't registerd or has been unregisterd.");
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
				Log.d(TAG, "TCP receiver is running.");
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
				Log.d(TAG, "TCP receiver closed.");
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
				Log.d(TAG, "UDP receiver is running.");
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
				Log.d(TAG, "UDP receiver closed.");
			} else {
				Log.e(TAG, "Error: Can't close UDP receiver.");
			}
		} else {
			Log.e(TAG, "Can't close UDP receiver: The receiver wasn't active or has been stopped.");
		}
	}

	/**
	 * Start remote checker thread
	 */
	public void startRemoteCheck() {
		// if (MainActivity.mConnectivityStateCheck == null) {
		Log.i(TAG, "Try to start remote checker thread...");
		MainActivity.mConnectivityStateCheck = new ConnectivityStateCheck(mContext);
		if (MainActivity.mConnectivityStateCheck.startConnectivityStateThread()) {
			Log.d(TAG, "Remote checker thread is running.");
		} else {
			Log.e(TAG, "Error: Can't start remote checker thread.");
		}

		// } else {
		// Log.e(TAG, "Remote checker thread is still running.");
		// }
	}

	/**
	 * Start remote checker thread
	 */
	public void stopRemoteCheck() {
		if (MainActivity.mConnectivityStateCheck != null) {
			Log.i(TAG, "Try to stop remote checker thread...");
			if (MainActivity.mConnectivityStateCheck.stopConnectivityStateThread()) {
				Log.d(TAG, "Remote checker thread stopped.");
			} else {
				Log.e(TAG, "Error: Can't stop remote checker thread.");
			}
		} else {
			Log.e(TAG, "Can't stop thread: Remote checker thread wasn't running or has been stopped.");
		}
	}

	/**
	 * Stops the AudioPlayer
	 */
	public void stopAudioPlayer() {
		// if (MainActivity.mConnection != null)
		// MainActivity.mConnection.setOnReceiveDataListener(null);

		if (MainActivity.mAudioPlayer != null) {
			MainActivity.mAudioPlayer.stopPlaying();
			MainActivity.mAudioPlayer = null;
		}
	}

	/**
	 * Start AudioRecorder
	 */
	public void startAudioRecorder() {
		// MainActivity.mAudioRecorder = new AudioRecorder(MainActivity.mConnection);
		MainActivity.mAudioRecorder.startRecording();
	}

	/**
	 * Stop AudioRecorder
	 */
	public void stopAudioRecorder() {
		if (MainActivity.mAudioRecorder != null) {
			MainActivity.mAudioRecorder.cleanUp();
			MainActivity.mAudioRecorder = null;
		}
	}
}
