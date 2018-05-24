package com.flash.light;

import android.content.Context;
import android.content.ContentResolver;

import android.net.Uri;
import android.util.Log;
import android.database.Cursor;
import android.provider.ContactsContract;

public class Contacts {

  public static final String TAG = "FlashLight Contacts";

  public String getContactName(final String number, Context context) {
    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

    String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };

    String contactName = "";
    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

    if(cursor != null) {
      if(cursor.moveToFirst()) {
        contactName = cursor.getString(0);
      }
      cursor.close();
    }

    Log.d(TAG, "getContactName() return contactName " + contactName);
    return contactName;
  }
}
