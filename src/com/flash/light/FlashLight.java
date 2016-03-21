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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    surfaceView = (SurfaceView)findViewById(R.id.preview);
    surfaceHolder = surfaceView.getHolder();
    surfaceHolder.addCallback(this);
    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    hasCameraFlash = getApplicationContext().getPackageManager()
      .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    if(!(hasCameraFlash)) {
      Toast.makeText(this, "Camera does not have flash feature.", Toast.LENGTH_LONG).show();
      return;
    }
    else {
      getCamera();
      Toast.makeText(this, "Opened Camera.", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "flashLightOn()", Toast.LENGTH_LONG).show();
      }
      else {
        flashLightOff();
        flashLight.setText("ON");
        Toast.makeText(this, "flashLightOff()", Toast.LENGTH_LONG).show();
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

    if(!(isFlashOn)) {
      params = mCam.getParameters();
      params.setFlashMode(Parameters.FLASH_MODE_TORCH);
      mCam.setParameters(params);
      mCam.startPreview();
      isFlashOn = true;
      Toast.makeText(this, "isFlashOn = true", Toast.LENGTH_LONG).show();
    }
  }

  public void flashLightOff() {
    try {
      if(isFlashOn) {
        params = mCam.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCam.setParameters(params);
        mCam.stopPreview();
        mCam.release();
        isFlashOn = false;
        Toast.makeText(this, "isFlashOn = false", Toast.LENGTH_LONG).show();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.d(TAG,"surfaceChanged()");
  }

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCam.setPreviewDisplay(holder); 
      Toast.makeText(this, "Setting preview.", Toast.LENGTH_LONG).show();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    Toast.makeText(this, "Preview stopped.", Toast.LENGTH_LONG).show();
    mCam.stopPreview();
  } 

}
