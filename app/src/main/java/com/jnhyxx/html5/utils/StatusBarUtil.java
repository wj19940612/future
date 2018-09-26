package com.jnhyxx.html5.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MR.YANG on 2017/2/15.
 */

public class StatusBarUtil {
    public static void addStatusBarView(ViewGroup view, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View statusBarView = new View(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(context));
            statusBarView.setBackgroundColor(Color.BLACK);
            view.addView(statusBarView, 0, lp);
        }
    }

    public static void addStatusBarView(ViewGroup view, Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View statusBarView = new View(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(context));
            statusBarView.setBackgroundColor(color);
            view.addView(statusBarView, 0, lp);
        }
    }

    public static void setTitleBar(View toolBar, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolBar.setPadding(toolBar.getPaddingLeft(), getStatusBarHeight(context),
                    toolBar.getPaddingRight(), toolBar.getPaddingBottom());
        }
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
