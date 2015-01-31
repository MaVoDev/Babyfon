package babyfon.view;

import babyfon.view.activity.MainActivity;
import android.content.Context;
import android.widget.Toast;

public class Output {

	/**
	 * Displays a toast on screnn
	 * 
	 * @param mContext
	 *            : context
	 * @param text
	 *            : text to be displayed
	 * @param duration
	 *            : duration of the displaying (0 = Toast.LENGTH_SHORT, 1 =
	 *            Toast.LENGTH_LONG)
	 */
	public void toast(final Context mContext, final String text, final int duration) {
		((MainActivity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(mContext, text, duration);
				toast.show();
			}
		});
	}
}
