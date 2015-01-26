package babyfon.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import babyfon.audio.AudioPlayer;
import babyfon.connectivity.ConnectionInterface.OnReceiveDataListener;
import babyfon.connectivity.bluetooth.BluetoothConnection;
import babyfon.init.R;
import babyfon.view.activity.MainActivity;

public class LocalService extends Service {
	private NotificationManager mNM;

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
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting. We put an icon in the status bar.
		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

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

	private BluetoothConnection mConnection;

	/**
	 * Stellt eine Verbindung zu dem angegebenen Gerät her.
	 * 
	 * @param address
	 *            Adresse des Gerätes zu dem verbunden werden soll.
	 */
	public void connectTo(String address) {
		mConnection = new BluetoothConnection(getApplicationContext());
		mConnection.connectToAdress(address);

		final AudioPlayer audioPlayer = new AudioPlayer();

		mConnection.setOnReceiveDataListener(new OnReceiveDataListener() {

			@Override
			public void onReceiveDataListener(byte[] bData, byte type, int bytesRead) {
				audioPlayer.playData(bData);
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
}