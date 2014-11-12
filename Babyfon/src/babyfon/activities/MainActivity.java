package babyfon.activities;

import babyfon.detection.Noise;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getCanonicalName();
	Noise noise;
	private ProgressBar bar;
	private TextView tv;
	private Button btn1;
	private Button btn2;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);

		editText = (EditText) findViewById(R.id.editText1);

		bar = (ProgressBar) findViewById(R.id.progressBar);
		tv = (TextView) findViewById(R.id.textView1);

		// noise = new Noise(bar, tv);
		// noise = new Noise(bar, tv);
		noise = new Noise(this);
	}

	public void changeThreshold(View v) {
		int threshold = Integer.parseInt(editText.getText().toString());
		noise.setThreshold(threshold);

		Toast.makeText(this, "Threshold changed to: " + threshold, Toast.LENGTH_SHORT).show();
	}

	public void recButtonClicked(View v) {

		Log.i(TAG, "Button clicked!");

		if (!noise.isRecording()) {
			noise.startRecording();
			btn1.setText("Stop Rec");
		} else {
			noise.stopRecording();
			btn1.setText("Start Rec");
		}

	}

	public ProgressBar getBar() {
		return bar;
	}

	public TextView getTextView() {
		return tv;
	}

}
