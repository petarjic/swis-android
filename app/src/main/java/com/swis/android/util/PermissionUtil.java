package com.swis.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Nitesh Siingh(Killer)on 10/31/2016.
 */
public class PermissionUtil {
    public static final int CAMERA_REQUEST_CODE = 2;
    public static final int SMS_REQUEST_CODE = 1;
    public static final int LOCATION_REQUEST_CODE = 0;
    public static final int CAL_PERMISSION = 100;
    public static final int SDCARD_REQUEST_CODE = 200;
    public static final int CONTACTS_REQUEST_CODE = 300;
    public static final int CONTACTS_REQUEST_CODE_TWO = 301;

    public static boolean checkLocationPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_REQUEST_CODE);
    }

    public static boolean checkSmsPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkAllPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_SMS}, SMS_REQUEST_CODE);
    }

    public static void requestAllPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE
        , Manifest.permission.ACCESS_FINE_LOCATION}, SMS_REQUEST_CODE);
    }

    public static boolean checkCameraPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestCamersPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
    }

    public static void requestContactPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST_CODE);
    }

    public static void requestContactPermissionEmail(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST_CODE_TWO);
    }
    public static boolean checkContactsPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean checkSDCardPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestSDCardPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SDCARD_REQUEST_CODE);
    }
}
