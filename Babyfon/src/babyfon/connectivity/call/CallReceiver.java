package babyfon.connectivity.call;

import babyfon.init.R;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public class CallReceiver extends CallLog {

	private Context mContext;

	public CallReceiver(Context mContext) {
		this.mContext = mContext;
	}

	public void missedCalls() {
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection, selectionArgs,
					sortOrder);
			while (cursor.moveToNext()) {
				String callName = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
				String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
				String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
				// Call type (missed call = 3)
				String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
				String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));

				if (Integer.parseInt(callType) == 3 && Integer.parseInt(isCallNew) > 0) {
					// If call type is missed call and the call notification is
					// unread

					if (callName == null) {
						// Call name is null if call number is unknown -> set
						// default name
						callName = mContext.getString(R.string.call_name_unknown);
					}

					if (callNumber.length() == 0) {
						// Call number is 0 if call number if the number was not
						// send -> set default info text
						callNumber = mContext.getString(R.string.call_number_unknown);
					}

					// Send call information
					System.out.println(callName + ": " + callNumber);
				}
			}
		} catch (Exception ex) {

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
}