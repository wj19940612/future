package com.johnz.kutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public class Launcher {

    public static final String EX_PAYLOAD = "payload";

    private static Launcher sInstance;

    private Context mContext;
    private Intent mIntent;

    private Launcher() {
        mIntent = new Intent();
    }

    public static Launcher with(Context context, Class<?> clazz) {
        sInstance = new Launcher();
        sInstance.mContext = context;
        sInstance.mIntent.setClass(context, clazz);
        return sInstance;
    }

    public Launcher putExtra(String key, int value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, String value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, boolean value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, Serializable data) {
        mIntent.putExtra(key, data);
        return this;
    }

    public Launcher setFlags(int flag) {
        mIntent.setFlags(flag);
        return this;
    }

    public void execute() {
        mContext.startActivity(mIntent);
    }

    public void executeForResult(int requestCode) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            activity.startActivityForResult(mIntent, requestCode);
        }
    }
}
