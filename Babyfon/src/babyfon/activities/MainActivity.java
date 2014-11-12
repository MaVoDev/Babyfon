package babyfon.activities;

import babyfon.detection.Noise;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getCanonicalName();
	Noise noise;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		noise = new Noise();
	}

	public void buttonClicked(View v) {

		Log.i(TAG, "Button clicked!");

		if (!noise.isRecording())
			noise.startListening();
		else
			noise.stopRecording();
	}

}
