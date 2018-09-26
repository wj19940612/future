package com.jnhyxx.html5.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jnhyxx.html5.R;


public class CustomToast {

    private Toast mToast;

    private static class Instance {
        static CustomToast customToast = new CustomToast();
    }

    public static CustomToast getInstance() {
        return Instance.customToast;
    }

    public void showText(Context context, String content) {
        if (mToast == null) {
            mToast = new Toast(context);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
        TextView toastText = (TextView) view.findViewById(R.id.toastText);
        toastText.setText(content);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(view);
        mToast.show();
    }

    public void showText(Context context, int resId) {
        showText(context, context.getString(resId));
    }
}
