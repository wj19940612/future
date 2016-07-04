package com.johnz.kutils;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class FileSystem {

    /**
     * ExternalStorage: Traditionally SD card, or a built-in storage in device that is distinct from
     * the protected internal storage and can be mounted as a filesystem on a computer.
     */
    private static boolean sExternalStorageAvailable = false;
    private static boolean sExternalStorageWriteable = false;

    public static boolean isExteralStorageAvailable() {
        updateExternalStorageState();
        return sExternalStorageAvailable;
    }

    public static boolean isExternalStorageWriteable() {
        updateExternalStorageState();
        return sExternalStorageWriteable;
    }

    private static void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            sExternalStorageAvailable = true;
            sExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            sExternalStorageAvailable = true;
            sExternalStorageWriteable = false;
        } else {
            sExternalStorageAvailable = false;
            sExternalStorageWriteable = false;
        }
    }

    public static void registerExternalStorageWatcher(Activity activity, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        if (activity != null) {
            activity.registerReceiver(receiver, filter);
        }
    }

    public static void unregisterExternalStorageWatcher(Activity activity, BroadcastReceiver receiver) {
        if (activity != null) {
            try {
                activity.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) { // throw when receiver not register
                e.printStackTrace();
            }
        }
    }

    public static boolean isStoragePermissionGranted(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        }
    }
}
