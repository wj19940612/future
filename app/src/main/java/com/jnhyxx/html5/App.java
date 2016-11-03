package com.jnhyxx.html5;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.DeviceInfoUtil;
import com.jnhyxx.umenglibrary.UmengLib;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

public class App extends MultiDexApplication {
    private static final String TAG = "App";
    private static Context sContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        API.init(sContext.getCacheDir());
        CookieManger.getInstance().init(sContext.getFilesDir());

        UmengLib.init(sContext);

        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.setCatchUncaughtExceptions(BuildConfig.DEBUG);

        if (!BuildConfig.DEBUG) {
            handleUncaughtException();
        }

        String deviceInfo = DeviceInfoUtil.getDeviceInfo(this);
        Log.d(TAG, "设备信息" + deviceInfo);
    }

    private void handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                MobclickAgent.reportError(App.getAppContext(), ex);
                MobclickAgent.onKillProcess(App.getAppContext());
                Preference.get().setForeground(false);
                /**
                 * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据。
                 */
                System.exit(1);

            }
        });
    }

    public static Context getAppContext() {
        return sContext;
    }

}
