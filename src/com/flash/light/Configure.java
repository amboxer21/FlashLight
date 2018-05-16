package com.flash.light;

import java.util.List;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;

import android.widget.Toast;
import android.widget.EditText;

import android.content.Intent;
import android.content.ComponentName;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.MenuInflater;
import android.view.View.OnTouchListener;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatCallback;

public class Configure extends Activity implements AppCompatCallback, OnTouchListener {

  private static EditText editTextPhoneNumber;
  private static EditText editTextEmailAddress;

  private static String sPhoneNumber;
  private static String sEmailAddress;

  private static String sPhoneNumberDb;
  private static String sEmailAddressDb;

  private static DatabaseHandler db;
  private static AppCompatDelegate delegate;
  private static ComponentName componentName;

  private static String action = "create";

  public void toast(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
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
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  public boolean isEmpty(String string) {
    if(string.length() == 0 || string.isEmpty() || string == null || string == "") {
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    sPhoneNumber  = editTextPhoneNumber.getText().toString();
    sEmailAddress = editTextEmailAddress.getText().toString();
    toast("sEmailAddress " + sEmailAddress);
    /*if(!isEmpty(sPhoneNumber) && !isEmpty(sEmailAddress) && action == "update") {
      toast("Updating DB now!");
      Log.d("FlashLight onBackPress()","sEmailAddress " + sEmailAddress);
      Log.d("FlashLight onBackPress()","sPhoneNumber " + sPhoneNumber);
      db.updateFlashLightDatabase(new FlashLightDatabase(1, "no", sEmailAddress, sPhoneNumber));
    }
    else if(!isEmpty(sPhoneNumber) && !isEmpty(sEmailAddress) && action == "create") {
      toast("Creating DB now!");
      db.addFlashLightDatabase(new FlashLightDatabase(1, "no", sEmailAddress, sPhoneNumber));
    }*/
    finish();
  }

  public void getDatabaseInfo()  {

    List<FlashLightDatabase> flashLightDatabase = db.getAllFlashLightDatabase();

    if(flashLightDatabase == null) {
      return;
    }

    for(FlashLightDatabase fldb : flashLightDatabase) {
      sPhoneNumberDb  = fldb.getPhoneNumber();
      sEmailAddressDb = fldb.getEmailAddress();
    }

    if(sEmailAddressDb != null) {
      //editTextPhoneNumber.setText(sPhoneNumber);
      //editTextEmailAddress.setText(sEmailAddress);
      action = "update";
    }
    else {
      action = "create";
    }

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.configure);

    editTextPhoneNumber  = (EditText)findViewById(R.id.editPhoneNumber);
    editTextEmailAddress = (EditText)findViewById(R.id.editEmailAddress);

    db = new DatabaseHandler(Configure.this); 
    
    getDatabaseInfo();

    //editTextPhoneNumber.setText("sPhoneNumber");
    //editTextEmailAddress.setText("sEmailAddress");

    //hideAppIcon(getApplicationContext());

    delegate = AppCompatDelegate.create(this, this);
    delegate.onCreate(savedInstanceState);
    delegate.setContentView(R.layout.configure);

    Toolbar toolbar   = (Toolbar) findViewById(R.id.action_toolbar_configure);

    delegate.setSupportActionBar(toolbar);
    delegate.getSupportActionBar().setDisplayShowTitleEnabled(false);

    editTextEmailAddress.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return false;
      }
    });

    editTextPhoneNumber.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        sEmailAddress  = editTextEmailAddress.getText().toString();
        Log.d("FlashLight sEmailAddress ", "" + sEmailAddress);
        return false;
      }
    });

  }

  public boolean onTouch(View v, MotionEvent event) {
    return true;
  }

}
