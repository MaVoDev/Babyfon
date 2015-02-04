package babyfon.view.activity;

import babyfon.init.R;
import babyfon.view.fragment.setup.SetupSearchDevicesFragment;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class OverlayActivity extends Activity {

	private static OverlayActivity instance;
	public static boolean isCreated = false;

	public static OverlayActivity getInstance() {
		// if (instance == null)
		// instance = new OverlayActivity();

		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OverlayActivity.instance = this;

		setContentView(R.layout.overlay_wait);

		Typeface mTypeface_i = Typeface.createFromAsset(this.getAssets(), "fonts/BOOKOSI.TTF");
		TextView waitingMsg = (TextView) findViewById(R.id.waiting_msg);
		waitingMsg.setTypeface(mTypeface_i);

		isCreated = true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		isCreated = false;

		// Gehe 1 Seite zurück im Setup
		SetupSearchDevicesFragment fragment = (SetupSearchDevicesFragment) ((MainActivity) MainActivity.getContext())
				.getFragmentById("SetupSearchDevicesFragment");
		fragment.goBack();

	}

	public void dismiss() {
		this.finish();

		isCreated = false;

		SetupSearchDevicesFragment fragment = (SetupSearchDevicesFragment) ((MainActivity) MainActivity.getContext())
				.getFragmentById("SetupSearchDevicesFragment");

		if (fragment.isVisible())
			fragment.initViewBluetooth();

	}

}
