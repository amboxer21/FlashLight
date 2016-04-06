package com.flash.light;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.Toast;
import android.widget.Button;
import android.graphics.Color;
import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;

public class FlashLight extends Activity implements SurfaceHolder.Callback {

  private Camera mCam;
  private Parameters params;
  private ToggleButton flashLight;
  private SurfaceView surfaceView;
  private SurfaceHolder surfaceHolder;

<<<<<<< HEAD
  private boolean hasCameraFlash;
  private boolean isFlashOn = false;
=======
  private boolean isFlashOn;
  private boolean hasCameraFlash;
>>>>>>> 7002b16ffee677546305596cd0fae8c9b4de66b2

  @Override
  public void onDestroy() {
    super.onDestroy();
    mCam.stopPreview();
    mCam.release();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

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

  public void initCamera() {
      try {
        if(mCam == null) {
          mCam = Camera.open();
          params = mCam.getParameters();
        }
        /*if(!(mCam == null)) {
          mCam.reconnect();
        }*/
      }
      catch(Exception e) {
        e.printStackTrace();
      }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { 
    // Empty method
  }

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCam.setPreviewDisplay(holder);
<<<<<<< HEAD
=======
      getCamera();
>>>>>>> 7002b16ffee677546305596cd0fae8c9b4de66b2
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // Not needed since we are using a service to run the main activity.
    // Otherwise this is where we would stop and release the camera.
    //mCam.stopPreview();
    //mCam.release();
  } 

}
