package babyfon;

import babyfon.init.R;
import android.content.Context;
import android.widget.Toast;

public class MessageHandler {

	Context context;

	public MessageHandler(Context context) {
		this.context = context;
	}

	public void getError(String errMsg) {
		String errorMessage;
		if (errMsg.equals(context.getString(R.string.WIFI_STATE_ERROR))) {
			// Wi-Fi ist inaktiv
			errorMessage = context.getString(R.string.ERRMSG_WIFI_STATE_ERROR);
		} else if (errMsg.equals(context.getString(R.string.WIFI_CONNECTION_ERROR))) {
			// Wi-Fi ist mit keinem Netzwerk verbunden
			errorMessage = context.getString(R.string.ERRMSG_WIFI_CONNECTION_ERROR);
		} else {
			// Unbekannter Fehler
			errorMessage = context.getString(R.string.ERRMSG_UNKNOWN_ERROR);
		}
		Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
	}
}
