package com.jnhyxx.html5.utils;

import android.widget.Toast;

import com.jnhyxx.html5.App;

public class ToastUtil {

    public static void show(String message) {
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void show(int messageId) {
        Toast.makeText(App.getAppContext(), messageId, Toast.LENGTH_SHORT).show();
    }
}