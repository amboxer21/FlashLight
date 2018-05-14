package com.flash.light;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
 
public class Eula {
    private String EULA_PREFIX = "eula";
    private Activity mContext;
 
    public Eula(Activity context) {
        mContext = context;
    }
 
    private PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }
 
    public void show() {
        PackageInfo versionInfo = getPackageInfo();
 
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
 
        boolean bAlreadyAccepted = prefs.getBoolean(eulaKey, false);
        if (bAlreadyAccepted == false) {
 
            String title = mContext.getString(R.string.app_name) + " v"
                    + versionInfo.versionName;
 
            String message = mContext.getString(R.string.eula_string);
 
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
 
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.accept,
                            new Dialog.OnClickListener() {
 
                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = prefs
                                            .edit();
                                    editor.putBoolean(eulaKey, true);
                                    editor.commit();
 
                                    dialogInterface.dismiss();
 
                                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new Dialog.OnClickListener() {
 
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    mContext.finish();
                                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                }
 
                            });
            builder.create().show();
        }
    }
}
