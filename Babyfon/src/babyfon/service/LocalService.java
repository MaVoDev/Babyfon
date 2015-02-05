package babyfon.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import babyfon.Message;
import babyfon.audio.AudioDetection;
import babyfon.audio.AudioPlayer;
import babyfon.audio.AudioRecorder;
import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.ConnectionInterface.OnReceiveDataListener;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.activity.NotificationDialogActivity;
import babyfon.view.fragment.BabyMonitorFragment;

public class LocalService extends Service {
	private static final String TAG = LocalService.class.getCanonicalName();

	private static final int NOTIFICATION_ID = 0;

	private NotificationManager mNM;

	private SharedPrefs mSharedPrefs;
	private Context mContext;

	private AudioPlayer mAudioPlayer;
	private AudioRecorder mAudioRecorder;
	private ConnectionInterface mConnection;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.service_notification_msg;

	/**
	 * Class for clients to access. Because we know this service always runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		public LocalService getService() {
			return LocalService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "Service->onCreate()...");

		mContext = MainActivity.getContext();
		mSharedPrefs = new SharedPrefs(getApplicationContext());

		// Damit Service nicht beendet wird bei zu wenig Speicher, starten mit startForeground
		startForeground(NOTIFICATION_ID, createServiceNotification());

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// if (mConnection == null) {
		initBtConnection();

		// Disconnect Handler wieder unregistern w‰hrend des setups!
		// mConnection.registerDisconnectHandler();
		// }

		// if (mConnection == null)
		// mConnection = new BluetoothConnection();

		if (mAudioPlayer == null)
			mAudioPlayer = new AudioPlayer();

		// Display a notification about us starting. We put an icon in the status bar.
		showNotification();
	}

	public void initBtConnection() {
		mConnection = new BluetoothConnection(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service->onStartCommand(): Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private Notification createServiceNotification() {

		// Orientiert an http://developer.android.com/training/notify-user/build-notification.html

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("My notification").setContentText("Hello World!");

		Intent resultIntent = new Intent(this, MainActivity.class);

		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		return mBuilder.build();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, LocalService.class.getSimpleName() + "->onDestroy()");

		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		// Verbindung schlieﬂen
		if (mConnection != null)
			mConnection.stopConnection();

		if (mAudioPlayer != null) {
			mAudioPlayer.stopPlaying();
		}

		if (mAudioRecorder != null) {
			mAudioRecorder.stopRecording();
			mAudioRecorder.cleanUp();
		}

		// Tell the user we stopped.
		// Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
//		Toast.makeText(this, "Service stopped.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * ÷ffnet einen Socket, der auf einen Client wartet, der sich mit ihm verbindet.
	 */
	public void startServer() {

		// BABY MODE

		mConnection.startServer();

		mConnection.setOnReceiveDataListener(new OnReceiveDataListener() {

			@Override
			public void onReceiveDataListener(byte[] bData, byte type, int bytesRead) {
				// mAudioPlayer.playData(bData);

				if (type == 1) {
					// PLay Audio
					mAudioPlayer.playData(bData, bytesRead);
				} else {
					new Message(mContext).handleIncomingMessage(new String(bData, 0, bytesRead));
				}
			}
		});
	}

	/**
	 * Stellt eine Verbindung zu dem angegebenen Ger‰t her.
	 * 
	 * @param address
	 *            Adresse des Ger‰tes zu dem verbunden werden soll.
	 */
	public void connectTo(String address) {

		// PARENTS MODE

		mConnection.connectToAdress(address);

		final AudioDetection audioDetection = new AudioDetection();

		audioDetection.setThreshold(36);
		
		Log.i(TAG, "Register new onReceiveDataListener for Bluetooth connection...");
		mConnection.setOnReceiveDataListener(new OnReceiveDataListener() {

			@Override
			public void onReceiveDataListener(byte[] bData, byte type, int bytesRead) {

				if (type == 1) {
					if (mSharedPrefs.isHearActivated()) {
						// PLay Audio
						// mAudioPlayer.playData(bData);
						mAudioPlayer.playData(bData, bytesRead);
					}
					// Update stuff on UI
					if (MainActivity.getContext() != null) {

						BabyMonitorFragment babyMonitorFragment = (BabyMonitorFragment) ((MainActivity) MainActivity.getContext())
								.getFragmentById("BabyMonitorFragment");

						if (babyMonitorFragment != null) {
							// if (babyMonitorFragment.isVisible()) {
							if (babyMonitorFragment.isOnScreen()) {
								// App im Vordergrund
								Log.i(TAG, "Updating volume in BabyMonitorFragment...");
								babyMonitorFragment.updateVolume(AudioDetection.calculateVolume(bData, bytesRead));
							} else {
								if (mSharedPrefs.isNoiseActivated()) {
									Log.i(TAG, "Check if Baby is Screaming in background...");

									// App nicht im Vordergrund
									if (audioDetection.isBabyScreaming(AudioDetection.calculateVolume(bData, bytesRead))) {
										Intent intent = new Intent(getApplicationContext(), NotificationDialogActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

										startActivity(intent);
										mSharedPrefs.setNoiseActivated(false);
									}
								}
							}
						}

					}

				} else {
					new Message(mContext).handleIncomingMessage(new String(bData, 0, bytesRead));
				}
			}
		});
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.service_notification_msg);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LocalServiceActivities.Controller.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.service_label), text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

	public ConnectionInterface getConnection() {
		return mConnection;
	}

	public void startRecording() {
		Log.v(TAG, "Start Recording on Service...");
		if (mAudioRecorder == null)
			mAudioRecorder = new AudioRecorder(mContext, this);
		mAudioRecorder.startRecording();
	}

	public void stopRecording() {
		Log.v(TAG, "Stop Recording on Service...");
		if (mAudioRecorder != null)
			mAudioRecorder.stopRecording();
	}

	public void stopAudioPlaying() {
		if (mAudioPlayer != null)
			mAudioPlayer.stopPlaying();
	}
}