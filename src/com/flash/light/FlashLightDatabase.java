package com.justdrive.app; 
 
public class Contact {
     
  int _id;

  String _email;
  String _app_password;
  String _phone_number;
  String _teen_phone_number;
  String _user_phone_number;

  public Contact(){ 

  }
     
  public Contact(int id, String email, String phone_number,
    String teen_phone_number, String app_password, String user_phone_number){
      this._id                = id;
      this._email             = email;
      this._phone_number      = phone_number;
      this._teen_phone_number = teen_phone_number;
      this._app_password      = app_password;
      this._user_phone_number = user_phone_number;
  }
     
  public int getID(){
    return this._id;
  }
     
  public void setID(int id){
    this._id = id;
  }

  public String getEmail(){
    return this._email;
  }

  public void setEmail(String email){
    this._email = email;
  }

  public String getAppPassword(){
    return this._app_password;
  }
     
  public void setAppPassword(String app_password){
    this._app_password = app_password;
  }
     
  public String getPhoneNumber(){
    return this._phone_number;
  }
     
  public void setPhoneNumber(String phone_number){
    this._phone_number = phone_number;
  }

  public String getTeenPhoneNumber(){
    return this._teen_phone_number;
  }

  public void setTeenPhoneNumber(String teen_phone_number){
    this._teen_phone_number = teen_phone_number;
  }

  public String getMPhoneNumber(){
    return this._user_phone_number;
  }

  public void setMPhoneNumber(String user_phone_number){
    this._user_phone_number = user_phone_number;
  }

}
