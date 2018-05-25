package com.flash.light; 

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

import java.util.Date;
import android.telephony.TelephonyManager;

public abstract class PhoneCallReceiver extends BroadcastReceiver {

  private static Date callStartTime;
  private static boolean isIncoming;
  private static String savedNumber;  
  private static int lastState = TelephonyManager.CALL_STATE_IDLE;


  @Override
  public void onReceive(Context context, Intent intent) {

  if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
  }
  else {

    String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
    String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

    int state = 0;
    if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
      state = TelephonyManager.CALL_STATE_IDLE;
    }
    else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
      state = TelephonyManager.CALL_STATE_OFFHOOK;
    }
    else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
      state = TelephonyManager.CALL_STATE_RINGING;
    }

    onCallStateChanged(context, state, number);

    }
  }

  protected abstract void onIncomingCallReceived(Context ctx, String number, Date start);
  protected abstract void onIncomingCallAnswered(Context ctx, String number, Date start);
  protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);
  protected abstract void onOutgoingCallStarted(Context ctx, String number, Date start);      
  protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);
  protected abstract void onMissedCall(Context ctx, String number, Date start);


  //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
  //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
  public void onCallStateChanged(Context context, int state, String number) {
    if(lastState == state){
      //No change, debounce extras
      return;
    }
    switch (state) {
      case TelephonyManager.CALL_STATE_RINGING:
        isIncoming = true;
        callStartTime = new Date();
        savedNumber = number;
        onIncomingCallReceived(context, number, callStartTime);
      break;
      case TelephonyManager.CALL_STATE_OFFHOOK:
        if(lastState != TelephonyManager.CALL_STATE_RINGING) {
          isIncoming = false;
          callStartTime = new Date();
          onOutgoingCallStarted(context, savedNumber, callStartTime);                     
        }
        else {
          isIncoming = true;
          callStartTime = new Date();
          onIncomingCallAnswered(context, savedNumber, callStartTime); 
        }

      break;
      case TelephonyManager.CALL_STATE_IDLE:
        if(lastState == TelephonyManager.CALL_STATE_RINGING) {
          onMissedCall(context, savedNumber, callStartTime);
        }
        else if(isIncoming) {
          onIncomingCallEnded(context, savedNumber, callStartTime, new Date());                       
        }
        else {
          onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());                                               
        }
      break;
    }

    lastState = state;

  }
}
