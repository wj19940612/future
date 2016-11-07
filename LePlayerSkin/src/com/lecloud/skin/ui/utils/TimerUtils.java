package com.lecloud.skin.ui.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class TimerUtils {

    // TODO 用完没有释放
    private static StringBuilder formatBuilder;
    private static Formatter formatter;
    private static SimpleDateFormat sdf;
    private static SimpleDateFormat sdf2;

    /**
     * 初始化formatter
     */
    private static void initTextFormatter() {
        if (formatBuilder == null) {
            formatBuilder = new StringBuilder();
            formatter = new Formatter(formatBuilder, Locale.getDefault());
        }
    }

    /**
     * 
     * @param times
     *            时间为秒
     * @return
     */
    public static String stringForTime(int times) {
        initTextFormatter();

        int totalSeconds = times;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

//    public static String stringForTime(long times) {
//        if (String.valueOf(times).length() < String.valueOf(System.currentTimeMillis()).length()) {
//            times = times * 1000;
//        }
//        if (sdf == null) {
//            sdf = new SimpleDateFormat("HH:mm:ss");
//        }
//        return sdf.format(times);
//    }

    public static Date stringToTimestamp(String time) {
        if (sdf2 == null) {
            sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        }
        Date date = null;
        try {
            date = sdf2.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date timestampToDate(long timestamp, boolean alignToimeMillis) {
        /**
         * 服务器给出的时间戳是10位，需要*1000
         */
        if (String.valueOf(timestamp).length() < String.valueOf(System.currentTimeMillis()).length() && alignToimeMillis) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(timestamp);
        sdf.format(date);
        return date;
    }
    
    public static String timeToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
