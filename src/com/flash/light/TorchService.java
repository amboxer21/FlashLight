package com.flash.light;

import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.content.Context;

public class TorchService extends Service {

  public void onCreate(Context context, Intent intent) {
    try {
      if (intent.getAction() != null) {
        intent = new Intent(context, FlashLight.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
