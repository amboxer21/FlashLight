package com.flash.light; 
 
public class FlashLightDatabase {
     
  int _id;

  String _hide;
  String _email;
  String _phone_number;

  public FlashLightDatabase() { }
     
  public FlashLightDatabase(int id, String hide, String email, String phone_number) {
    this._id   = id;
    this._hide = hide;
    this._email = email;
    this._phone_number = phone_number;
  }
     
  public int getID() {
    return this._id;
  }

  public String getHide() {
    return this._hide;
  }

  public String getEmail() {
    return this._email;
  }

  public String getPhoneNumber() {
    return this._phone_number;
  }
     
  public void setID(int id) {
    this._id = id;
  }

  public void setHide(String hide) {
    this._hide = hide;
  }

  public void setEmail(String email) {
    this._email = email;
  }

  public void setPhoneNumber(String phone_number) {
    this._phone_number = phone_number;
  }

}
