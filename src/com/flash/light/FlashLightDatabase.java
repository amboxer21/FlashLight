package com.flash.light; 
 
public class FlashLightDatabase {
     
  int _id;

  String _hide;

  public FlashLightDatabase() { }
     
  public FlashLightDatabase(int id, String hide) {
    this._id   = id;
    this._hide = hide;
  }
     
  public int getID() {
    return this._id;
  }
     
  public void setID(int id) {
    this._id = id;
  }

  public String getHide() {
    return this._hide;
  }

  public void setHide(String hide) {
    this._hide = hide;
  }

}
