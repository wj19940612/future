package com.jnhyxx.html5;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.jnhyxx.html5.activity.MainActivity;
import com.jnhyxx.html5.activity.PopupDialogActivity;
import com.jnhyxx.html5.utils.NotificationUtil;
import com.jnhyxx.umenglibrary.UmengLib;
import com.johnz.kutils.Launcher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.wo.main.WP_App;

import java.util.Map;

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
        handleUncaughtException();
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

    private void initPushHandlers() {
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            @Override
            public void dealWithNotificationMessage(Context context, final UMessage uMessage) {
                final Map<String, String> extra = uMessage.extra;
                if (NotificationUtil.isImportant(extra)) {
                    if (Preference.get().isForeground()) {
                        UTrack.getInstance(sContext).trackMsgClick(uMessage); // Record click event
                        showPopupDialog(context, uMessage, extra);
                        return;
                    }
                }
                super.dealWithNotificationMessage(context, uMessage);
            }
        };
        PushAgent.getInstance(sContext).setMessageHandler(messageHandler);

        UmengNotificationClickHandler clickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage uMessage) {
                final Map<String, String> extra = uMessage.extra;
                final String messageType = NotificationUtil.getMessageType(extra);
                if (NotificationUtil.isSystemMessage(extra)) {
                    Launcher.with(context, MainActivity.class)
                            .setPreExecuteListener(new Launcher.PreExecuteListener() {
                                @Override
                                public void preExecute(Intent intent) {
                                    final String messageId = NotificationUtil.getMessageId(extra);
                                    intent.putExtra(NotificationUtil.MESSAGE_ID, messageId)
                                            .putExtra(NotificationUtil.MESSAGE_TYPE, messageType)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                            }).execute();

                } else if (NotificationUtil.isTradeMessage(uMessage.extra)) {
                    Launcher.with(context, MainActivity.class)
                            .setPreExecuteListener(new Launcher.PreExecuteListener() {
                                @Override
                                public void preExecute(Intent intent) {
                                    intent.putExtra(NotificationUtil.MESSAGE_TYPE, messageType)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                            }).execute();
                }
            }
        };
        PushAgent.getInstance(sContext).setNotificationClickHandler(clickHandler);
    }

    private void showPopupDialog(Context context, final UMessage uMessage, Map<String, String> extra) {
        final String messageId = NotificationUtil.getMessageId(extra);
        final String messageType = NotificationUtil.getMessageType(extra);
        Launcher.with(context, PopupDialogActivity.class)
                .setPreExecuteListener(new Launcher.PreExecuteListener() {
                    @Override
                    public void preExecute(Intent intent) {
                        intent.putExtra(NotificationUtil.MESSAGE_ID, messageId)
                                .putExtra(NotificationUtil.MESSAGE_TYPE, messageType)
                                .putExtra(PopupDialogActivity.TITLE, uMessage.title)
                                .putExtra(PopupDialogActivity.MESSAGE, uMessage.text)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }).execute();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
