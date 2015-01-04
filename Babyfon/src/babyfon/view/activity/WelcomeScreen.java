package babyfon.view.activity;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class WelcomeScreen extends Activity {

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
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen);

		Thread background = new Thread() {
			public void run() {

				try {
					sleep(5000);

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
