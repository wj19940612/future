package com.johnz.kutils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AppInfo {

    public interface Meta {
        String UMENG_CHANNEL = "UMENG_CHANNEL";
    }

    /**
     * 获取版本名，例如 1.0.1
     * @return version name
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取 manifest 里面的 meta-data
     * @param context
     * @param name
     * @return
     */
    public static String getMetaData(Context context, String name) {
        PackageManager packageManager = context.getPackageManager();
        String result = "";
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            result = info.metaData.get(name).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int rest = context.checkSelfPermission(permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDeviceHardwareId(Context context) {
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String typePrefix = "DEVICE_ID_";
            String deviceId = tm.getDeviceId();

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = android.os.Build.SERIAL;
                typePrefix = "SERIAL_";
            }

            if (TextUtils.isEmpty(deviceId)) {
                android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                String mac = wifi.getConnectionInfo().getMacAddress();
                deviceId = mac;
                typePrefix = "MAC_";
            }

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                typePrefix = "ANDROID_ID_";
            }

            Log.d("AppInfo", "getDeviceHardwareId: " + (typePrefix + deviceId));
            return SecurityUtil.md5Encrypt(typePrefix + deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
