package com.flash.light;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.Toast;
import android.widget.Button;
import android.content.Context;
import android.hardware.Camera;
import android.view.View.OnClickListener;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;

public class FlashLight extends Activity {

  private Camera mCam;
  private boolean isFlashOn;
  private Parameters params;
  private Button flashLight;

  private static final String TAG = FlashLight.class.getSimpleName();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    boolean hasCameraFlash = getApplicationContext().getPackageManager()
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
        Toast.makeText(this, "flashLightOn()", Toast.LENGTH_LONG).show();
      }
      else {
        flashLightOff();
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
        //params = mCam.getParameters();
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
        //mCam = null;
        isFlashOn = false;
        Toast.makeText(this, "isFlashOn = false", Toast.LENGTH_LONG).show();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

}
