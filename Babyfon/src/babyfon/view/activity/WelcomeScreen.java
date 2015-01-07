package babyfon.view.activity;

import babyfon.connectivity.wifi.TCPReceiver;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.init.R;
import babyfon.performance.Battery;
import babyfon.settings.SharedPrefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class WelcomeScreen extends Activity {

	// Modules
	private Battery mBattery;

	// Receiver
	private TCPReceiver mTCPReceiver;
	private UDPReceiver mUDPReceiver;

	private SharedPrefs mSharedPrefs;

	@Override
	protected void onStart() {
		super.onStart();

		mSharedPrefs = new SharedPrefs(this);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_welcome);

		// Layout related to the gender of the baby
		if (mSharedPrefs.getGender().equals("boy")) {
			// Set background color
			layout.setBackgroundResource(R.drawable.bg_male);
		} else {
			// Set background color
			layout.setBackgroundResource(R.drawable.bg_female);
		}

		// Load default modules
		// TCP Receiver
		if (mSharedPrefs.getDeviceMode() != -1) {
			// Load mode modules
			if (mSharedPrefs.getDeviceMode() == 0) {
				// Load baby modules
				// Battery
				// Call
				// SMS
				// UDP Receiver
			} else {
				// Load parents modules
				// Battery (off)
				// Call (off)
				// SMS (off)
				// UDP Receiver (off)
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen);

		Thread background = new Thread() {
			public void run() {

				try {
					sleep(1000);

					Intent intent = new Intent(getBaseContext(), MainActivity.class);
					startActivity(intent);

					finish();

				} catch (Exception e) {

				}
			}
		};
		background.start();
	}
}
