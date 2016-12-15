package com.jnhyxx.html5.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.jnhyxx.html5.App;

public class ToastUtil {

    public static void curt(String message) {
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void curt(int messageId) {
        Toast.makeText(App.getAppContext(), messageId, Toast.LENGTH_SHORT).show();
    }

    public static void curt(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    public static void show(String message) {
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void show(int messageId) {
        Toast.makeText(App.getAppContext(), messageId, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
    }

    public static void center(int messageId, int yOffsetResId) {
        Toast toast = Toast.makeText(App.getAppContext(), messageId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,
                App.getAppContext().getResources().getDimensionPixelSize(yOffsetResId));
        toast.show();
    }
}