package com.justdrive.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class MarshmellowPermissions {

  public static Activity activity;
  private static boolean start = false;

  private static final String[] START_PERMS = {
    android.Manifest.permission.READ_PHONE_STATE,
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION};

  public MarshmellowPermissions(Activity activity) {
    this.activity = activity;
  }

  public boolean hasPermissionsForSendSms() {
    int result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
    if(result == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean hasPermissionsForReadPhoneState() {
    int result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
    if(result == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean hasPermissionsForAccessFineLocation() {
    int result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    if(result == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean hasPermissionsForCoarseLocation() {
    int result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
    if(result == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean hasPermissionsForStartup() {
    for(String perms: START_PERMS) {
      int result = ActivityCompat.checkSelfPermission(activity, perms);
      if(result == PackageManager.PERMISSION_GRANTED) {
        start = true;
      }
      else {
        start = false;
      }
    }
    return start;
  }

}
