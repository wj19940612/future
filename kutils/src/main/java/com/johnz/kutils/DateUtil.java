package com.johnz.kutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String format(String time, String fromFormat, String toFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(toFormat);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isInThisYear(String time, String fromFormat) {
        if (time.length() != fromFormat.length()) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            Calendar today = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String addOneMinute(String date, String format) {
        if (date.length() != format.length()) {
            return date;
        }
        SimpleDateFormat parser = new SimpleDateFormat(format);
        try {
            long originDate = parser.parse(date).getTime();
            long finalDate = originDate + 60 * 1000; // 1 min
            return parser.format(new Date(finalDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
