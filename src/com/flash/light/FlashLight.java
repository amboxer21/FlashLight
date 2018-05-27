package com.flash.light;

import android.util.Log;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.MenuInflater;
import android.view.SurfaceHolder;
import android.view.View.OnClickListener;

import android.widget.Toast;
import android.widget.Button;
import android.widget.ToggleButton;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.ServiceConnection;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatCallback;

public class FlashLight extends Activity implements SurfaceHolder.Callback, AppCompatCallback {

  private static final String TAG = "FlashLight FlashLight";

  private Camera mCam;
  private Parameters params;
  private ToggleButton flashLight;
  private SurfaceView surfaceView;
  private Messenger mService = null;
  private SurfaceHolder surfaceHolder;

  private boolean mBound;
  private boolean hasCameraFlash;
  private boolean isBound   = false;
  private boolean isFlashOn = false;
  private static long backPressedTime = 0;

  private static Message mtn;
  private static Message msg;
  private static AppCompatDelegate delegate;
  private static ComponentName componentName;
  private static PackageManager packageManager;

  public void isServiceBound() {
    isBound = getApplicationContext().bindService(new Intent(getApplicationContext(),
      FlashLightService.class), mConnection, Context.BIND_AUTO_CREATE );
    if(isBound) {
      getApplicationContext().unbindService(mConnection);
    }
  }

  public void toast(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
  }

  private ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
      mBound   = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mService = new Messenger(service);
      mBound   = true;
    }
  };

  @Override
  public void onBackPressed() {
    long mTime = System.currentTimeMillis();
    if(mTime - backPressedTime > 2000) {
      backPressedTime = mTime;
      Toast.makeText(this, "Press back again to close app.", Toast.LENGTH_SHORT).show();
    }
    else {
      finish();
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  private AppCompatDelegate getDelegate() {
    if (delegate == null) {
      delegate = AppCompatDelegate.create(this, this);
    }

  return delegate;
  }

  public boolean supportRequestWindowFeature(int featureId) {
    return getDelegate().requestWindowFeature(featureId);
  }

  public void invalidateOptionsMenu() {
    getDelegate().invalidateOptionsMenu();
  }

  @Override
  public void onSupportActionModeStarted(ActionMode mode) { }

  @Override
  public void onSupportActionModeFinished(ActionMode mode) { }

  public ActionMode startSupportActionMode(ActionMode.Callback callback) {
    return getDelegate().startSupportActionMode(callback);
  }

  @Nullable
  @Override
  public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
    return null;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      case R.id.configureMenu:
        Intent configureIntent = new Intent(getApplicationContext(), Configure.class);
        startActivityForResult(configureIntent, 0);
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    try {
      isServiceBound();
      if(mCam != null) {
        mCam.stopPreview();
        mCam.release();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putBoolean("isFlashOn", isFlashOn);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    isFlashOn = savedInstanceState.getBoolean("isFlashOn");
  }

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  } 

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    if(!isMyServiceRunning(FlashLightService.class)) {
      Intent serviceIntent = new Intent(getApplicationContext(), FlashLightService.class);
      startService(serviceIntent);
      getApplicationContext().bindService(new Intent(getApplicationContext(), FlashLightService.class), mConnection,
        Context.BIND_AUTO_CREATE);
    }

    delegate = AppCompatDelegate.create(this, this);
    delegate.onCreate(savedInstanceState);
    delegate.setContentView(R.layout.main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);

    delegate.setSupportActionBar(toolbar);
    delegate.getSupportActionBar().setDisplayShowTitleEnabled(true);

    if(savedInstanceState != null) {
      isFlashOn = savedInstanceState.getBoolean("isFlashOn");
    }

    surfaceView = (SurfaceView)findViewById(R.id.preview);
    surfaceHolder = surfaceView.getHolder();
    surfaceHolder.addCallback(FlashLight.this);
    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    hasCameraFlash = getApplicationContext().getPackageManager()
      .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    if(!(hasCameraFlash)) {
      toast("Camera does not have flash feature.");
      return;
    }
    else {
      getCamera();
    }

    flashLight = (ToggleButton)findViewById(R.id.flashLight);
    flashLight.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        try {
          if(!(isFlashOn)) {
            params = mCam.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            mCam.setParameters(params);
            mCam.startPreview();
            isFlashOn = true;
          }
          else {
            params = mCam.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCam.setParameters(params);
            mCam.stopPreview();
            isFlashOn = false;
          }
        }
        catch(Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public void getCamera() throws NullPointerException {
    try {
      if(mCam == null) {
        mCam = Camera.open();
      }
    }
    catch(NullPointerException e) {
      e.printStackTrace();
    }
  }

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCam.setPreviewDisplay(holder);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

  public void surfaceDestroyed(SurfaceHolder holder) { } 

}
