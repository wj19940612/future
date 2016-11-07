package com.lecloud.skin.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ScreenBrightnessManager {

    private static final String TAG = "ScreenBrightnessManager";

    private ScreenBrightnessManager() {

    }

    /**
     * 获得当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static int getScreenMode(Context context) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    public static int getScreenBrightness(Context context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void setScreenMode(Context context, int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    public static void saveScreenBrightness(Context context, int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    public static void setScreenBrightness(Activity activity, int paramInt) {
        if (null == activity || paramInt < 0 || paramInt > 255) {
            return;
        }

        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow
                .getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }
}
