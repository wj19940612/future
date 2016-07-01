package com.jnhyxx.html5.utils;

import android.content.Context;
import android.util.Log;

import com.johnz.kutils.AppInfo;
import com.umeng.onlineconfig.OnlineConfigAgent;

public class UpgradeUtil {

    private static final String TAG = "TEST";

    public static final String VERSION_CODE = "version_code";
    public static final String FORCE_UPDATE = "force_update";
    public static final String UPDATE_LOG = "update_log";
    public static final String DOWNLOAD_URI = "download_uri";

    private static void log(Context context) {
        Log.d(TAG, "log: update" + isForceUpgrade(context));
        Log.d(TAG, "log: log" + getUpdateLog(context));
        Log.d(TAG, "log: download" + getDownloadURI(context));
    }

    public static boolean hasNewVersion(Context context) {
        log(context);

        String newVersion = OnlineConfigAgent.getInstance().getConfigParams(context, VERSION_CODE);
        String currentVersion = AppInfo.getVersionName(context);
        Log.d(TAG, "hasNewVersion: " + newVersion);
        if (newVersion.compareTo(currentVersion) > 0) {
            return true;
        }
        return false;
    }

    public static boolean isForceUpgrade(Context context) {
        String forceUpgrade = OnlineConfigAgent.getInstance().getConfigParams(context, FORCE_UPDATE);
        if (forceUpgrade.equals("0")) {
            return false;
        }
        return true;
    }

    public static String getUpdateLog(Context context) {
        return OnlineConfigAgent.getInstance().getConfigParams(context, UPDATE_LOG);
    }

    public static String getDownloadURI(Context context) {
        return OnlineConfigAgent.getInstance().getConfigParams(context, DOWNLOAD_URI);
    }

    public static String getVersionCode(Context context) {
        return OnlineConfigAgent.getInstance().getConfigParams(context, VERSION_CODE);
    }

}
