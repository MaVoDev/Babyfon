package babyfon.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import babyfon.Message;
import babyfon.audio.AudioPlayer;
import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.ConnectionInterface.OnReceiveDataListener;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;

public class LocalService extends Service {
	private static final String TAG = "LocalService";

	private static final int NOTIFICATION_ID = 0;

	private NotificationManager mNM;

	private AudioPlayer mAudioPlayer;
	private ConnectionInterface mConnection;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.service_started;

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

		// Damit Service nicht beendet wird bei zu wenig Speicher, starten mit startForeground
		startForeground(NOTIFICATION_ID, createServiceNotification());

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		initBtConnection();

		// if (mConnection == null)
		// mConnection = new BluetoothConnection();

		if (mAudioPlayer == null)
			mAudioPlayer = new AudioPlayer();

		// Display a notification about us starting. We put an icon in the status bar.
		showNotification();
	}

	public void initBtConnection() {
		mConnection = new BluetoothConnection();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
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

		if (mAudioPlayer != null)
			mAudioPlayer.stopPlaying();

		// Tell the user we stopped.
		// Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "Service stopped.", Toast.LENGTH_SHORT).show();
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

		mConnection.startServer();

		mConnection.setOnReceiveDataListener(new OnReceiveDataListener() {

			@Override
			public void onReceiveDataListener(byte[] bData, byte type, int bytesRead) {
				// mAudioPlayer.playData(bData);

				if (type == 1)
					// PLay Audio
					;
				else
					new Message(MainActivity.getContext()).handleIncomingMessage(new String(bData, 0, bytesRead));
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

		mConnection.connectToAdress(address);

		mConnection.setOnReceiveDataListener(new OnReceiveDataListener() {

			@Override
			public void onReceiveDataListener(byte[] bData, byte type, int bytesRead) {

				if (type == 1)
					// PLay Audio
					mAudioPlayer.playData(bData);
				else
					new Message(getApplicationContext()).handleIncomingMessage(new String(bData, 0, bytesRead));
			}
		});
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.service_started);

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
}