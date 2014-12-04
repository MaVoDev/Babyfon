package babyfon.view.activity;

import babyfon.init.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_welcome_screen);

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		finish();
	}
}
