package com.flash.light;

import java.util.List;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;

import android.widget.Toast;
import android.widget.EditText;
import android.widget.RelativeLayout;

import android.content.Intent;
import android.content.ComponentName;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.MenuInflater;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatCallback;

public class Configure extends Activity implements AppCompatCallback {

  private static EditText editPhoneNumber;
  private static EditText editHideKeyword;
  private static EditText editEmailAddress;
  private static EditText editUnhideKeyword;

  private static String sPhoneNumber;
  private static String sHideKeyword;
  private static String sEmailAddress;
  private static String sUnhideKeyword;

  public  static String sPhoneNumberDb;
  public  static String sHideKeywordDb;
  public  static String sEmailAddressDb;
  public  static String sUnhideKeywordDb;

  private static AppCompatDelegate delegate;
  private static ComponentName componentName;
  private static DatabaseHandler databaseHandler;


  private float x1, x2;
  private static int count = 0;
  static final int MIN_DISTANCE = 150;
  private static final int max_tries  = 3;

  private static String email_address;
  private static String action  = "create";
  private static final String TAG = "FlashLight Configure";

  public void toast(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
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

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  public void onBackPressed() {

    sPhoneNumber   = editPhoneNumber.getText().toString();
    sHideKeyword   = editHideKeyword.getText().toString();
    sEmailAddress  = editEmailAddress.getText().toString();
    sUnhideKeyword = editUnhideKeyword.getText().toString();

    if(sHideKeyword.isEmpty()) {
      sHideKeyword = "";
      Log.d(TAG, "onBackPressed() sHideKeyword.isEmpty()");
    }
    if(sUnhideKeyword.isEmpty()) {
      sUnhideKeyword = "";
      Log.d(TAG, "onBackPressed() sUnhideKeyword.isEmpty()");
    }

    if(!sPhoneNumber.isEmpty() && !sEmailAddress.isEmpty() && getDatabaseInfo().equals("update")) {
      if(!sEmailAddress.equals(sEmailAddressDb) || !sPhoneNumber.equals(sPhoneNumberDb)) {
        toast("Updating DB now!");
        databaseHandler.updateFlashLightDatabase(new FlashLightDatabase(1, "yes", sEmailAddress, sPhoneNumber, sHideKeyword, sUnhideKeyword));
      }
    }
    else if(!sPhoneNumber.isEmpty() && !sEmailAddress.isEmpty() && getDatabaseInfo().equals("create")) {
      toast("Creating DB now!");
      databaseHandler.addFlashLightDatabase(new FlashLightDatabase(1, "yes", sEmailAddress, sPhoneNumber, sHideKeyword, sUnhideKeyword));
    }
    finish();
    super.onBackPressed();
  }

  @Nullable
  public String getDatabaseInfo() throws NullPointerException {

    String database_action = "null";

    try {
      List<FlashLightDatabase> flashLightDatabase = databaseHandler.getAllFlashLightDatabase();
      for(FlashLightDatabase fldb : flashLightDatabase) {
        sPhoneNumberDb   = fldb.getPhoneNumber();
        sHideKeywordDb   = fldb.getHideKeyword();
        sEmailAddressDb  = fldb.getEmailAddress();
        sUnhideKeywordDb = fldb.getUnhideKeyword();
      }
      if(sEmailAddressDb != null) {
        database_action = "update";
      }
      else if(sEmailAddressDb == null && flashLightDatabase != null) {
        database_action = "create";
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "getDatabaseInfo() database_action " + database_action);
    return database_action;
  }

  @Nullable
  public String emailAddress() throws NullPointerException {

    String email_address = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        email_address = sEmailAddressDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "emailAddress() email_address " + email_address);
    return email_address;
  }

  @Nullable
  public String phoneNumber() throws NullPointerException {

    String phone_number = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        phone_number = sPhoneNumberDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "phoneNumber() phone_number " + phone_number);
    return phone_number;
  }

  @Nullable
  public String hideKeyword() throws NullPointerException {

    String hide_keyword = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        hide_keyword = sHideKeywordDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "hideKeyword() hide_keyword " + hide_keyword);
    return hide_keyword;
  }

  @Nullable
  public String unhideKeyword() throws NullPointerException {

    String unhide_keyword = "null";

    try {
      if(!getDatabaseInfo().equals("null")) {
        unhide_keyword = sUnhideKeywordDb;
      }
    }
    catch(NullPointerException e) { }

    Log.d(TAG, "unhideKeyword() unhide_keyword " + unhide_keyword);
    return unhide_keyword;

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.configure);

    delegate = AppCompatDelegate.create(this, this);
    delegate.onCreate(savedInstanceState);
    delegate.setContentView(R.layout.configure);

    Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar_configure);

    delegate.setSupportActionBar(toolbar);
    delegate.getSupportActionBar().setDisplayShowTitleEnabled(false);

    editPhoneNumber   = (EditText)findViewById(R.id.edit_phone_number);
    editEmailAddress  = (EditText)findViewById(R.id.edit_email_address);
    editHideKeyword   = (EditText)findViewById(R.id.edit_hide_keyword);
    editUnhideKeyword = (EditText)findViewById(R.id.edit_unhide_keyword);

    databaseHandler   = new DatabaseHandler(getApplicationContext());

    if(!getDatabaseInfo().equals("null")) {
      editPhoneNumber.setText(sPhoneNumber);
      editEmailAddress.setText(sEmailAddress);
      editHideKeyword.setText(sHideKeyword);
      editUnhideKeyword.setText(sUnhideKeyword);
    }

  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    switch(event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        x1 = event.getX();
      break;
      case MotionEvent.ACTION_UP:
        x2 = event.getX();
        float deltaX = x2 - x1;

        if(Math.abs(deltaX) > MIN_DISTANCE && x2 > x1) {
          /*Intent intent = new Intent(Configure.this, FlashLight.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
          startActivity(intent);*/
          finish();
        }
      break;
    }
    return super.onTouchEvent(event);
  }

}
