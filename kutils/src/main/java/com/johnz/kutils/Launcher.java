package com.johnz.kutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Launcher {

    public interface PreExecuteListener {
        void preExecute(Intent intent);
    }

    private static Launcher sInstance;

    private Context mContext;
    private Intent mIntent;
    private PreExecuteListener mPreExecuteListener;

    private Launcher() {
        mIntent = new Intent();
    }

    public static Launcher with(Context context) {
        sInstance = new Launcher();
        sInstance.mContext = context;
        return sInstance;
    }

    public static Launcher with(Context context, Class<?> clazz) {
        sInstance = new Launcher();
        sInstance.mContext = context;
        sInstance.mIntent.setClass(context, clazz);
        return sInstance;
    }

    public Launcher launch(Class<?> clazz) {
        mIntent.setClass(mContext, clazz);
        return this;
    }

    public Launcher setPreExecuteListener(PreExecuteListener listener) {
        mPreExecuteListener = listener;
        return this;
    }

    public void execute() {
        if (mPreExecuteListener != null) {
            mPreExecuteListener.preExecute(mIntent);
        }
        mContext.startActivity(mIntent);
    }

    public void executeForResult(int requestCode) {
        if (mPreExecuteListener != null) {
            mPreExecuteListener.preExecute(mIntent);
        }
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            activity.startActivityForResult(mIntent, requestCode);
        }
    }

}
