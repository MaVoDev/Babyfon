package babyfon.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import babyfon.view.activity.MainActivity;

public class Output {

	/**
	 * Displays a toast on screen
	 * 
	 * @param text
	 *            : text to be displayed
	 * @param duration
	 *            : duration of the displaying (0 = Toast.LENGTH_SHORT, 1 =
	 *            Toast.LENGTH_LONG)
	 */
	public void toast(final String text, final int duration) {
		((MainActivity) MainActivity.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(MainActivity.getContext(), text, duration);
				toast.show();
			}
		});
	}

	/**
	 * Displays a simple dialog with one button
	 * 
	 * @param title
	 *            : title to be displayed
	 * @param message
	 *            : message to be displayed
	 * @param buttonText
	 *            : button text
	 */
	public 	void simpleDialog(final String title, final String message, final String buttonText) {
		((MainActivity) MainActivity.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getContext());
				builder.setTitle(title).setMessage(message).setCancelable(false)
						.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do nothing, just close dialog
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
}
