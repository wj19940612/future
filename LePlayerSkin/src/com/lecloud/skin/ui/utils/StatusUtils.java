package com.lecloud.skin.ui.utils;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;

/**
 * 得到当前电量 wifi 时间的信息 并进行格式化
 * @date 2014-12-17
 */
public class StatusUtils {
    /**
     * 得到当前电量的值
     * @param context
     * @return 返回电量的百分比
     */
    public static int getBatteryStatus(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, 1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        if (isCharging) {
            return 120;
        }
        int current = batteryStatus.getExtras().getInt("level");// 获得当前电量
        int total = batteryStatus.getExtras().getInt("scale");// 获得总电量
        int percent = current * 100 / total;
        return percent;
    }
    public StatusUtils(Context context ) {
    	batteryStatus = context.registerReceiver(null, ifilter);
    	batteryBundle = batteryStatus.getExtras();
    	cv = context.getContentResolver();
    	strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
    	connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
    
    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatus;
    Bundle batteryBundle;
    /**
     * 得到当前电量的值，使用非静态方法进行执行速度优化
     * @return
     */
    public int getBatteryStatus(){
    	 int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, 1);
         boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
         if (isCharging) {
             return 120;
         }
         int current = batteryBundle.getInt("level");// 获得当前电量
         int total = batteryBundle.getInt("scale");// 获得总电量
         int percent = current * 100 / total;
         return percent;
    }

    /**
     * 得到当前事件 并格式化为 12:11形式
     * @return
     */
    public static String getCurrentTime(Context context) {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = 0;
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat != null && strTimeFormat.equals("24")) {
            hour = c.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = c.get(Calendar.HOUR);
        }

        return String.format("%02d", hour) + ":"
                + String.format("%02d", minute);
    }
    /**
     * 得到当前时间非静态方法。进行优化
     * @param context
     * @return
     */
    Calendar c = Calendar.getInstance();
    ContentResolver cv;
    String strTimeFormat;
    public String getCurrentTime() {
    	c.setTimeInMillis(System.currentTimeMillis());
    	int minute = c.get(Calendar.MINUTE);
    	int hour = 0;
    	if (strTimeFormat != null && strTimeFormat.equals("24")) {
    		hour = c.get(Calendar.HOUR_OF_DAY);
    	} else {
    		hour = c.get(Calendar.HOUR);
    	}
    	
    	return String.format("%02d", hour) + ":"	+ String.format("%02d", minute);
    }

    /**
     * 得到当前wifi的状态
     * @param context
     * @return
     */
    public static int getWiFistate(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return 70;
                    }
                }
            }
        }
        return 100;
    }
    /**
     * 得到当前wifi的状态，非静态方法
     * @param context
     * @return
     */
    ConnectivityManager connectivity;
    NetworkInfo[] info;
    public int getWiFistate() {
    	if (connectivity != null) {
    		info = connectivity.getAllNetworkInfo();
    		if (info != null) {
    			for (int i = 0; i < info.length; i++) {
    				if (info[i].getTypeName().equals("WIFI")&& info[i].isConnected()) {
    					return 70;
    				}
    			}
    		}
    	}
    	return 100;
    }
    
    /**
	 * 格式化时间字符串
	 * 
	 * @param timeMs
	 *            秒
	 * @return 返回格式00:00:00
	 */
    public static String stringForTime(int timeMs) {

		StringBuilder formatBuilder = new StringBuilder();
		Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

		try {
			int totalSeconds = timeMs ;

			int seconds = totalSeconds % 60;
			int minutes = (totalSeconds / 60) % 60;
			int hours = totalSeconds / 3600;

			formatBuilder.setLength(0);

			return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
		} finally {
			formatter.close();
		}
	}
}
