package com.flash.light;

import android.util.Log;
import android.widget.Toast;

import android.os.SystemClock;
import android.os.PowerManager;

import android.app.Service;
import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class AlarmReceiver extends BroadcastReceiver {

  private static AlarmManager am;
  private static PendingIntent pIntent;

  private static final String TAG = "FlashLight AlmReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {

    Log.i(TAG, "Entering onReceive()");

    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
    wl.acquire();

    Intent i = new Intent(context.getApplicationContext(), SMSService.class);
    PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 1, i, PendingIntent.FLAG_ONE_SHOT);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);

    wl.release();

  }

}
