package com.jnhyxx.html5;

import android.app.Application;
import android.content.Context;

import com.jnhyxx.umenglibrary.UmengLib;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
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
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.setCatchUncaughtExceptions(!BuildConfig.DEBUG);
        initPushHandlers();
    }

    private void initPushHandlers() {
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
            }
        };
        PushAgent.getInstance(sContext).setMessageHandler(messageHandler);

        UmengNotificationClickHandler clickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage uMessage) {

            }
        };
        PushAgent.getInstance(sContext).setNotificationClickHandler(clickHandler);
    }
    public static Context getAppContext() {
        return sContext;
    }
}
