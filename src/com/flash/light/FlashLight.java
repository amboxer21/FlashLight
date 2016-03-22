package com.flash.light;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.Toast;
import android.widget.Button;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View.OnClickListener;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;

public class FlashLight extends Activity implements SurfaceHolder.Callback {

  private Camera mCam;
  private boolean isFlashOn;
  private Parameters params;
  private Button flashLight;
  private boolean hasCameraFlash;
  private SurfaceView surfaceView;
  private SurfaceHolder surfaceHolder;

  private static final String TAG = FlashLight.class.getSimpleName();

  /*@Override
  public void onStart() {
    super.onStart();
  }*/

  @Override
  public void onStop() {
    super.onStop();
    mCam.stopPreview();
    mCam.release();
  }

  @Override
  public void onPause() {
    super.onPause();
    mCam.stopPreview();
  }

  @Override
  public void onResume() {
    super.onResume();
    getCamera();
    if(isFlashOn) {
      flashLightOn();
    }
  }

  /*@Override
  public void onDestroy() {
    super.onDestroy();
  }*/

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

    isFlashOn = false;
    flashLight = (Button)findViewById(R.id.flashLight);
    flashLight.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        try {
          toggle();
        }
        catch(Exception e) {
          e.printStackTrace();
        }
      }

    });
  }

  public void toggle() {
    try {
      if(!(isFlashOn)) {
        flashLightOn();
        flashLight.setText("OFF");
      }
      else {
        flashLightOff();
        flashLight.setText("ON");
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void getCamera() {
      try {
        mCam = Camera.open();
        params = mCam.getParameters();
      }
      catch(Exception e) {
        e.printStackTrace();
      }
  }

  public void flashLightOn() {
    if(mCam == null) {
      Toast.makeText(this, "Camera not found.", Toast.LENGTH_LONG).show();
      return;
    }
    try {
      if(!(isFlashOn)) {
        params = mCam.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCam.setParameters(params);
        mCam.startPreview();
        isFlashOn = true;
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void flashLightOff() {
    try {
      if(isFlashOn) {
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

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { 
    // Empty method
  }

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCam.setPreviewDisplay(holder); 
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    mCam.stopPreview();
  } 

}
