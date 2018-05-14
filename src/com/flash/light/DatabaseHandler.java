package com.justdrive.app;

import java.util.List;
import java.util.ArrayList;
 
import android.content.Context;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
 
  private static final int DATABASE_VERSION      = 1;
 
  private static final String KEY_ID             = "id";
  private static final String KEY_EMAIL          = "email";
  private static final String KEY_APP_PASSWORD   = "app_password";
  private static final String KEY_PH_NO          = "phone_number";
  private static final String KEY_U_PH_NO        = "user_phone_number";
  private static final String KEY_TN_PH_NO       = "teen_phone_number";

  private static final String TABLE_CONTACTS     = "contacts";
  private static final String DATABASE_NAME      = "contactsManager";
 
  public DatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
 
  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
      + KEY_ID + " INTEGER PRIMARY KEY," 
      + KEY_EMAIL + " TEXT,"
      + KEY_PH_NO + " TEXT," 
      + KEY_TN_PH_NO + " TEXT," 
      + KEY_APP_PASSWORD + " TEXT,"
      + KEY_U_PH_NO + " TEXT" + ")";
    db.execSQL(CREATE_CONTACTS_TABLE);
  }
 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
    onCreate(db);
  }

  void addContact(Contact contact) {
    SQLiteDatabase db = this.getWritableDatabase();
 
    ContentValues values = new ContentValues();
    values.put(KEY_EMAIL, contact.getEmail()); 
    values.put(KEY_PH_NO, contact.getPhoneNumber()); 
    values.put(KEY_TN_PH_NO, contact.getTeenPhoneNumber()); 
    values.put(KEY_APP_PASSWORD, contact.getAppPassword()); 
    values.put(KEY_U_PH_NO, contact.getMPhoneNumber()); 
 
    db.insert(TABLE_CONTACTS, null, values);
    db.close(); 
  }
 
  Contact getContact(int id) {
    SQLiteDatabase db = this.getReadableDatabase();
 
    Cursor cursor = db.query(TABLE_CONTACTS, 
      new String[] { KEY_ID, KEY_EMAIL, KEY_PH_NO, KEY_TN_PH_NO, KEY_APP_PASSWORD, KEY_U_PH_NO }, KEY_ID + "=?",
      new String[] { String.valueOf(id) }, null, null, null, null);
      if (cursor != null) {
        cursor.moveToFirst();
      }
 
      Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
        cursor.getString(1), 
        cursor.getString(2), 
        cursor.getString(3), 
        cursor.getString(4),
        cursor.getString(5));

      return contact;
  }
     
  public List<Contact> getAllContacts() {

    List<Contact> contactList = new ArrayList<Contact>();

    String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
    SQLiteDatabase db  = this.getWritableDatabase();
    Cursor cursor      = db.rawQuery(selectQuery, null);
 
    if (cursor.moveToFirst()) {
      do {
        Contact contact = new Contact();
        contact.setID(Integer.parseInt(cursor.getString(0)));
        contact.setEmail(cursor.getString(1));
        contact.setPhoneNumber(cursor.getString(2));
        contact.setTeenPhoneNumber(cursor.getString(3));
        contact.setAppPassword(cursor.getString(4));
        contact.setMPhoneNumber(cursor.getString(5));
        contactList.add(contact);
      } while (cursor.moveToNext());
    }
 
    return contactList;
  }
 
  public int updateContact(Contact contact) {
    SQLiteDatabase db    = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_EMAIL, contact.getEmail());
    values.put(KEY_PH_NO, contact.getPhoneNumber());
    values.put(KEY_TN_PH_NO, contact.getTeenPhoneNumber());
    values.put(KEY_APP_PASSWORD, contact.getAppPassword());
    values.put(KEY_U_PH_NO, contact.getMPhoneNumber());
 
    return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
      new String[] { String.valueOf(contact.getID()) });
  }
 
  public void deleteContact(Contact contact) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
      new String[] { String.valueOf(contact.getID()) });
    db.close();
  }

  public int getContactsCount() {
    String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);
    cursor.close();
 
    return cursor.getCount();
  }
 
}
