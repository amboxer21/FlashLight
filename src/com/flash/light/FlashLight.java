package com.flash.light;

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;

import android.view.View;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View.OnClickListener;

import android.widget.Toast;
import android.widget.Button;
import android.widget.ToggleButton;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class FlashLight extends Activity implements SurfaceHolder.Callback {

  private Camera mCam;
  private Parameters params;
  private ToggleButton flashLight;
  private SurfaceView surfaceView;
  private SurfaceHolder surfaceHolder;

  private boolean hasCameraFlash;
  private boolean isFlashOn = false;

  @Override
  public void onDestroy() {
    super.onDestroy();
    mCam.stopPreview();
    mCam.release();
  }

  @Override
  public void onStop() {
    super.onPause();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    //Toast.makeText(getApplicationContext(), "" + isFlashOn, Toast.LENGTH_LONG).show();
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

  public void hideAppIcon(Context context) {
    PackageManager p = context.getPackageManager();
    ComponentName componentName = new ComponentName(this, com.flash.light.FlashLight.class); 
    p.setComponentEnabledSetting(componentName,
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    hideAppIcon(getApplicationContext());

    if(savedInstanceState != null) {
      isFlashOn = savedInstanceState.getBoolean("isFlashOn");
      //Toast.makeText(getApplicationContext(), "1:" + isFlashOn, Toast.LENGTH_LONG).show();
    }

    surfaceView = (SurfaceView)findViewById(R.id.preview);
    surfaceHolder = surfaceView.getHolder();
    surfaceHolder.addCallback(FlashLight.this);
    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    hasCameraFlash = getApplicationContext().getPackageManager()
      .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    if(!(hasCameraFlash)) {
      Toast.makeText(this, "Camera does not have flash feature.", Toast.LENGTH_LONG).show();
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
          mCam.release();
	  Log.e("FlashLight","onClick Exception e: " + e.toString());
          e.printStackTrace();
        }
      }
    });
  }

  public void getCamera() {
      try {
        if(mCam == null) {
          mCam = Camera.open();
          params = mCam.getParameters();
        }
      }
      catch(Exception e) {
        e.printStackTrace();
      }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCam.setPreviewDisplay(holder);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) { } 

}
