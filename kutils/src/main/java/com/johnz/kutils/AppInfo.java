package com.johnz.kutils;

import android.content.Context;
import android.content.pm.PackageInfo;

public class AppInfo {

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
}
