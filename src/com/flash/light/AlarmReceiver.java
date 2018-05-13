package com.flash.light;

import android.app.Service;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.os.PowerManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

public class AlarmReceiver extends BroadcastReceiver {

  private static AlarmManager am;
  private static PendingIntent pIntent;

  @Override
  public void onReceive(Context context, Intent intent) {

    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
    wl.acquire();

    Intent serviceIntent = new Intent(context, SMSService.class);
    context.startService(serviceIntent);

    wl.release();

  }

}
