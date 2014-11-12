package babyfon.activities;

import babyfon.activities.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
