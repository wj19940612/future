package com.jnhyxx.html5.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 王杰 on 2016/8/29.
 * 公共方法的utils
 */

public class CommonMethodUtils {
    private static final String TAG = "CommonMethodUtils";
    private static WeakReference<Context> sContext;

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        sContext = new WeakReference<Context>(context);
        Context mContext = sContext.get();
        if (mContext instanceof Activity) {
            WindowManager windowManager = ((Activity) mContext).getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            int width = outMetrics.widthPixels;
            return width;
        }
        return -1;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (context instanceof Activity) {
            WindowManager windowManager = ((Activity) context).getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            int heightPixels = outMetrics.heightPixels;
            return heightPixels;
        }
        return -1;
    }

    /**
     * 判断输入数字是否为手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();

    }
}

