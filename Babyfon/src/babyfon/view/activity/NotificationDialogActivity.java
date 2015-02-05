package babyfon.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import babyfon.init.R;
import babyfon.settings.SharedPrefs;

public class NotificationDialogActivity extends Activity {

	private SharedPrefs mSharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		setContentView(R.layout.layout_activity_notificationdialog);

		mSharedPrefs = new SharedPrefs(this);

		initUI();

		// start ringtone notification
		if (mSharedPrefs.getSoundNotificationState()) {
			Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			MainActivity.mRingtone = RingtoneManager.getRingtone(this, soundNotification);
			MainActivity.mRingtone.play();
		}

		// start vibration notification
		if (mSharedPrefs.getVibrateNotificationState()) {
			MainActivity.mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 0, 1000, 1000 };
			MainActivity.mVibrator.vibrate(pattern, 0);
		}

	}

	private void initUI() {

		final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_out);

		// Parent Layout
		LinearLayout dialogLayout = (LinearLayout) findViewById(R.id.baby_alert_dialog);

		// Init Buttons
		Button openAppBtn = (Button) findViewById(R.id.btn_dismiss_dialog);
		Button dismissBtn = (Button) findViewById(R.id.btn_open_app);

		// Initialize ImageView
		ImageView babyCry = (ImageView) findViewById(R.id.cry_baby_image);
		babyCry.startAnimation(animationFadeIn);

		// Initialize TextView
		TextView babyName = (TextView) findViewById(R.id.cry_baby_name);
		babyName.setText(mSharedPrefs.getName() + getString(R.string.is_awake));
		// TextView subtitle = (TextView) findViewById(R.id.cry_subtitle);

		if (mSharedPrefs.getGender() == 0) {
			openAppBtn.setBackgroundResource(R.drawable.btn_selector_male);
			dismissBtn.setBackgroundResource(R.drawable.btn_selector_male);
			babyCry.setImageResource(R.drawable.cry_male);
			dialogLayout.setBackgroundResource(R.drawable.bg_male_cry);
		} else {
			openAppBtn.setBackgroundResource(R.drawable.btn_selector_female);
			dismissBtn.setBackgroundResource(R.drawable.btn_selector_female);
			babyCry.setImageResource(R.drawable.cry_female);
			dialogLayout.setBackgroundResource(R.drawable.bg_female_cry);
		}

		// Set Typeface
		Typeface mTypeface_b = Typeface.createFromAsset(getAssets(), "fonts/BOOKOSBI.TTF");
		babyName.setTypeface(mTypeface_b);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stop();
	}

	public void stop() {

		mSharedPrefs.setNoiseActivated(true);

		// stop vibration notification
		if (MainActivity.mVibrator != null) {
			MainActivity.mVibrator.cancel();
			MainActivity.mVibrator = null;
		}

		// stop ringtone notification
		if (MainActivity.mRingtone != null) {
			MainActivity.mRingtone.stop();
		}
	}

	public void openApp(View view) {
		stop();

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		this.finish();
	}

	public void dismiss(View view) {
		this.finish();
	}

}
