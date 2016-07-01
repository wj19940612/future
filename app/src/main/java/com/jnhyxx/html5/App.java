package com.jnhyxx.html5;

import android.app.Application;
import android.content.Context;

import com.jnhyxx.umenglibrary.UmengLib;
import com.wo.main.WP_App;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        if (Variant.isApp1()) {
            try {
                WP_App.on_AppInit(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        UmengLib.init(sContext);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
