package com.johnz.kutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String DEFAULT_FORMAT = "yyyy-MM-dd hh:mm:ss";

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

    /**
     * get diff minutes bewteen endDate and startDate. endDate - startDate
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDiffMinutes(String startDate, String endDate, String format) {
        long diff = 0;
        try {
            SimpleDateFormat parser = new SimpleDateFormat(format);
            long start = parser.parse(startDate).getTime();
            long end = parser.parse(endDate).getTime();

            if (startDate.compareTo(endDate) <= 0) { // eg. 09:00 <= 09:10
                diff = end - start;
            } else { // eg. 21:00 ~ 01:00, we should change 01:00 to 25:00
                diff = end + 24 * 60 * 60 * 1000 - start;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return (int) (diff / (60 * 1000));
        }
    }

    /**
     * check if time is between time1 and time2
     *
     * @param time1
     * @param time2
     * @param time
     * @return
     */
    public static boolean isBetweenTimes(String time1, String time2, String time) {
        if (time1.compareTo(time2) <= 0) {
            return time.compareTo(time1) >= 0 && time.compareTo(time2) <= 0;
        } else {
            return time.compareTo(time1) >= 0 || time.compareTo(time2) <= 0;
        }
    }
}
