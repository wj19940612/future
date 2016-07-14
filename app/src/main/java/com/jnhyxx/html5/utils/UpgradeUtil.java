package com.jnhyxx.html5.utils;

import android.content.Context;
import android.util.Log;

import com.jnhyxx.html5.net.Api;
import com.johnz.kutils.AppInfo;
import com.umeng.onlineconfig.OnlineConfigAgent;

public class UpgradeUtil {

    private static final String TAG = "TEST";

    public static final String VERSION_CODE = "version_code";
    public static final String FORCE_UPDATE = "force_update";
    public static final String UPDATE_LOG = "update_log";
    public static final String DOWNLOAD_URI = "download_uri";
    public static final String DOMAIN = "domain";
    public static final String DEBUG = "debug";
    public static final String DEBUG_DEVICE = "debug_device";

    public static final String ACTION_UPGRADE_COMPLETE = "com.jnhyxx.html5.ACTION_UPGRADE_COMPLETE";

    public static void log(Context context) {
        Log.d(TAG, "log: newVersion " + getVersionCode(context));
        Log.d(TAG, "log: isForceUpgrade " + isForceUpgrade(context));
        Log.d(TAG, "log: upgrade log " + getUpdateLog(context));
        Log.d(TAG, "log: download uri " + getDownloadURI(context));
        Log.d(TAG, "log: domain " + getDomain(context));
        Log.d(TAG, "log: debug " + isDebug(context));
        Log.d(TAG, "log: debug device " + getDebugDevice(context));
    }

    public static boolean hasNewVersion(Context context) {
        if (isDebug(context)) {
            String debugDevice = getDebugDevice(context);
            String currentDeviceId = AppInfo.getDeviceHardwareId(context);

            Log.d(TAG, "CurrentDevice: " + currentDeviceId);

            if (debugDevice.equals(currentDeviceId)) {
                ToastUtil.show("Debug Device");
                return hasNewVersionCode(context);
            }
        } else {
            return hasNewVersionCode(context);
        }
        return false;
    }

    private static boolean hasNewVersionCode(Context context) {
        String domain = getDomain(context);
        if (domain.equals(Api.HOST)) {
            String newVersion = getVersionCode(context);
            String currentVersion = AppInfo.getVersionName(context);
            if (newVersion.compareTo(currentVersion) > 0) {
                return true;
            }
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

    public static boolean isDebug(Context context) {
        String debug = OnlineConfigAgent.getInstance().getConfigParams(context, DEBUG);
        if (debug.equals("0")) {
            return false;
        }
        return true;
    }

    public static String getDebugDevice(Context context) {
        return OnlineConfigAgent.getInstance().getConfigParams(context, DEBUG_DEVICE);
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

    public static String getDomain(Context context) {
        return OnlineConfigAgent.getInstance().getConfigParams(context, DOMAIN);
    }
}
