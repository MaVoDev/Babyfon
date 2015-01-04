package babyfon.connectivity.call;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class CallLog extends android.provider.CallLog {

	private Context mContext;

	public CallLog(Context mContext) {
		this.mContext = mContext;
	}

	public void test() {
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection,
					selectionArgs, sortOrder);
			while (cursor.moveToNext()) {
				String callName = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
				String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
				String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
				String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
				String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
				if (Integer.parseInt(callType) == 3 && Integer.parseInt(isCallNew) > 0) {
					System.out.println(callName + ": " + callNumber);
				}
			}
		} catch (Exception ex) {
			System.out.println("ERROR: " + ex.toString());
		} finally {
			cursor.close();
		}
	}
	// public void test() {
	// String[] strFields = { android.provider.CallLog.Calls.CACHED_NAME,
	// android.provider.CallLog.Calls.NUMBER,
	// android.provider.CallLog.Calls.DATE, android.provider.CallLog.Calls.TYPE
	// };
	// String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
	//
	// Cursor cursor =
	// mContext.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
	// strFields,
	// null, null, strOrder);
	//
	// if (cursor.moveToFirst()) {
	// do {
	// boolean missed = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
	// == CallLog.Calls.MISSED_TYPE;
	//
	// if (missed) {
	// String name =
	// cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
	// String number =
	// cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
	//
	// int date = cursor.getColumnIndex(CallLog.Calls.DATE);
	// String callDate = cursor.getString(date);
	// Date callDayTime = new Date(Long.valueOf(callDate));
	//
	// if (name == null) {
	// name = mContext.getString(R.string.call_name_unknown);
	// }
	//
	// if (number.length() == 0) {
	// number = mContext.getString(R.string.call_number_unknown);
	// }
	//
	// Log.d("PhoneLog", name + ": " + number + " at time: " + callDayTime);
	// }
	//
	// } while (cursor.moveToNext());
	// }
	// }
	//
	// public void createString(String name, String number) {
	// String str = name + ";" + number;
	// }
}