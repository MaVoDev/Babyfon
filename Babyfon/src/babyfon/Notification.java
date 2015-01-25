package babyfon;

import java.util.logging.Handler;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class Notification {

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	public Notification(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	/**
	 * Start notifications
	 */
	public void start() {
		// start display notification
		final Animation animationFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_in_out);

		MainActivity.overlay = (FrameLayout) ((MainActivity) mContext).findViewById(R.id.baby_alert);
		MainActivity.overlay.setVisibility(View.VISIBLE);

		// Initialize ImageView
		ImageView babyCry = (ImageView) ((MainActivity) mContext).findViewById(R.id.cry_baby_image);
		babyCry.startAnimation(animationFadeIn);

		// Initialize TextView
		TextView babyName = (TextView) ((MainActivity) mContext).findViewById(R.id.cry_baby_name);
		babyName.setText(mSharedPrefs.getName() + mContext.getString(R.string.is_awake));
		TextView subtitle = (TextView) ((MainActivity) mContext).findViewById(R.id.cry_subtitle);

		if (mSharedPrefs.getGender() == 0) {
			// boy is crying
			MainActivity.overlay.setBackgroundResource(R.drawable.bg_male_cry);
			babyCry.setImageResource(R.drawable.cry_male);
		} else {
			// girl is crying
			MainActivity.overlay.setBackgroundResource(R.drawable.bg_female_cry);
			babyCry.setImageResource(R.drawable.cry_female);

		}

		// Set Typeface
		Typeface mTypeface_b = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");
		Typeface mTypeface_n = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");
		babyName.setTypeface(mTypeface_b);
		subtitle.setTypeface(mTypeface_n);

		// start ringtone notification
		if (mSharedPrefs.getSoundNotificationState()) {
			Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			MainActivity.mRingtone = RingtoneManager.getRingtone(mContext, soundNotification);
			MainActivity.mRingtone.play();
		}

		// start vibration notification
		if (mSharedPrefs.getVibrateNotificationState()) {
			MainActivity.mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 0, 1000, 1000 };
			MainActivity.mVibrator.vibrate(pattern, 0);
		}

		final GestureDetector gestureDetector = new GestureDetector(mContext, new GestureListener());
		MainActivity.overlay.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});
	}

	/**
	 * Stop notifications
	 */
	public void stop() {
		// stop display notification
		if (MainActivity.overlay != null) {
			MainActivity.overlay.setVisibility(View.GONE);
		}

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

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		// On double click
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			stop();
			return true;
		}
	}
}
