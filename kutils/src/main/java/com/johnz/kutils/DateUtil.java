package com.johnz.kutils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String FORMAT_YEAR = "yyyy年MM月dd日 HH:mm";
    public static String FORMAT_NOT_SECOND = "MM月dd日 HH:mm";

    public static String format(long time, String toFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(toFormat);
        return dateFormat.format(new Date(time));
    }

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

    public static boolean isToday(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isToday(date, todayDate);
    }

    public static boolean isToday(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isTomorrow(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isTomorrow(date, todayDate);
    }

    public static boolean isTomorrow(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        todayCalendar.add(Calendar.DAY_OF_YEAR, 1);
        return isToday(calendar.getTime(), todayCalendar.getTime());
    }

    public static boolean isYesterday(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isYesterday(date, todayDate);
    }

    private static boolean isYesterday(Date date, Date todayDate) {
        Calendar toadyCalendar = Calendar.getInstance();
        toadyCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        toadyCalendar.add(Calendar.DAY_OF_YEAR, -1);
        return isToday(calendar.getTime(), toadyCalendar.getTime());
    }

    public static boolean isNextWeek(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isNextWeek(date, todayDate);
    }

    public static boolean isNextWeek(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        todayCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        return calendar.get(Calendar.WEEK_OF_YEAR) == todayCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isInThisYear(long time) {
        Date date = new Date(time);
        return isInThisYear(date);
    }

    public static boolean isInThisYear(String time, String fromFormat) {
        if (time.length() != fromFormat.length()) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            return isInThisYear(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInThisYear(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
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

    public static String addMinutes(String date, int min, String format) {
        if (date.length() != format.length()) {
            return date;
        }
        SimpleDateFormat parser = new SimpleDateFormat(format);
        try {
            long originDate = parser.parse(date).getTime();
            long finalDate = originDate + min * 1000; // 1 min
            return parser.format(new Date(finalDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDayOfWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String result = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                result = "一";
                break;
            case Calendar.TUESDAY:
                result = "二";
                break;
            case Calendar.WEDNESDAY:
                result = "三";
                break;
            case Calendar.THURSDAY:
                result = "四";
                break;
            case Calendar.FRIDAY:
                result = "五";
                break;
            case Calendar.SATURDAY:
                result = "六";
                break;
            case Calendar.SUNDAY:
                result = "日";
                break;
            default:
                break;
        }
        return result;
    }

    public static String format(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    /**
     * 将日期格式转化为时间(秒数)
     *
     * @param time
     * @return
     */
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 判断 time1 是否比 time2 晚了 milliseconds, 即 time1 - time2 <= milliseconds && time1 - time2 >= 0
     *
     * @param time1
     * @param time2
     * @param milliseconds
     * @return
     */
    public static boolean isLessThanTimeInterval(long time1, long time2, long milliseconds) {
        long diff = time1 - time2;
        Log.d("TAG", "isLessThanTimeInterval: " + diff);
        if (diff >= 0 && diff <= milliseconds) {
            return true;
        }
        return false;
    }

    /**
     * @param startTime 最新的时间
     * @param endTime
     * @return
     */
    public static boolean isTimeBetweenFiveMin(long startTime, long endTime) {
        int difference = (int) (startTime - endTime) / (60 * 1000);
        Log.d("dateUtil", "相差数据" + difference + "  开始的时间" + DateUtil.format(startTime) + "  比较时间" + DateUtil.format(endTime));

        return 5 < difference;
    }

    /**
     * 判断指定时间和当前时间是否小于minute分钟
     *
     * @param txtDate 指定的时间
     * @return
     */
    public static boolean isTimeMatchFiveMin(String txtDate) {
        return isTimeMatchFiveMin(txtDate, 1);
    }

    public static boolean isTimeMatchFiveMin(String txtDate, int minute) {
        if (TextUtils.isEmpty(txtDate)) {
            return false;
        }
        if (minute == 0) {
            minute = 5;
        }
        try {
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
            final Date workDay = format.parse(txtDate);

            final Calendar c1 = Calendar.getInstance();
            final Calendar c2 = Calendar.getInstance();

            final Date currTime = new Date();

            c1.setTime(workDay);
            c2.setTime(currTime);

            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                    && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)) {
                if (Math.abs(c1.get(Calendar.MINUTE) - c2.get(Calendar.MINUTE)) < minute) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 格式化时间  如果是当天 则显示18:20
     *            如果是昨天 则 昨天 18:20
     *            其他的   12月12日 12:20
     *            不是今年  则 2015年12月18日
     *
     * @param createTime
     * @return
     */
    public static String getFormatTime(long createTime) {
        long systemTime = System.currentTimeMillis();
        if (DateUtil.isInThisYear(createTime)) {
            if (DateUtil.isToday(createTime, systemTime)) {
                return DateUtil.format(createTime, "HH:mm");
            } else if (DateUtil.isYesterday(createTime, systemTime)) {
                return DateUtil.format(createTime, "昨天  " + "HH:mm");
            } else {
                return DateUtil.format(createTime, DateUtil.FORMAT_NOT_SECOND);
            }
        } else {
            return DateUtil.format(createTime, DateUtil.FORMAT_YEAR);
        }
    }
}
