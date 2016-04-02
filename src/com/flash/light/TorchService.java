package com.flash.light;

import android.os.IBinder;
import android.app.Service;
import android.content.Intent;

public class TorchService extends Service {

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      FlashLight flashLight = new FlashLight();
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
