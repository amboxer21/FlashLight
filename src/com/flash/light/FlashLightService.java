package com.flash.light; 

import android.app.Service;
import android.app.Activity;

import android.content.Intent;
import android.content.Context;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import android.util.Log;
import android.support.annotation.Nullable;

public class FlashLightService extends Service {

  private static final String TAG = "FlashLight FLService";

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
  }

  final Messenger mMessenger = new Messenger(new IncomingHandler());

  static class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder();
  }

  public void onCreate(Context context, Intent intent) {
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
    try { }
    catch(NullPointerException e) { }
    return START_STICKY;
  }

}
