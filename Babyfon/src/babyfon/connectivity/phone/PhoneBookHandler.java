package babyfon.connectivity.phone;

import babyfon.init.R;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class PhoneBookHandler {

	private ContentResolver mContentResolver;

	private Context mContext;

	public PhoneBookHandler(Context mContext) {
		mContentResolver = mContext.getContentResolver();

		this.mContext = mContext;
	}

	public String getContactName(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = mContentResolver.query(uri, new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cursor == null) {
			return null;
		}
		String contactName = null;
		if (cursor.moveToFirst()) {
			contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (contactName == null) {
			contactName = mContext.getString(R.string.unknownContact);
		}
		
		return contactName;
	}
}
