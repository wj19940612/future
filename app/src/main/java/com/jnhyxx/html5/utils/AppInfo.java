package com.jnhyxx.html5.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.jnhyxx.html5.App;

public class AppInfo {

    /**
     * 获取版本名，例如 1.0.1
     * @return version name
     */
    public static String getVersionName() {
        String versionName = "";
        try {
            Context context = App.getAppContext();
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
