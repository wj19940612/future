package com.jnhyxx.html5;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.jnhyxx.html5.net.API;
import com.jnhyxx.umenglibrary.UmengLib;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

public class App extends MultiDexApplication {

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
        MobclickAgent.setCatchUncaughtExceptions(!BuildConfig.DEBUG);

        if (!BuildConfig.DEBUG) {
            handleUncaughtException();
        }
    }

    private void handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Preference.get().setForeground(false);
                System.exit(1);
            }
        });
    }

    public static Context getAppContext() {
        return sContext;
    }
}
