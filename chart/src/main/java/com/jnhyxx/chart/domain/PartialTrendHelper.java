package com.jnhyxx.chart.domain;

import android.util.Log;

import com.jnhyxx.chart.TrendView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PartialTrendHelper {

    private static final int INTERVAL_HOURS = 6;

    private String openMarketTime;
    private String[] customOpenMarketTimeArray;
    private TrendViewData lastTrendData;

    public void setLastTrendData(TrendViewData lastData) {
        this.lastTrendData = lastData;
    }

    public TrendViewData getLastTrendData() {
        return lastTrendData;
    }

    public void setOpenMarketTime(String openMarketTime) {
        this.openMarketTime = openMarketTime;
    }

    public String[] getPartialOpenMarketTime() {
        customOpenMarketTimeArray = processOpenMarketTime();
        return createPartialOpenMarketTime();
    }

    private String[] createPartialOpenMarketTime() {
        Log.d("TEST", "createPartialOpenMarketTime: " + lastTrendData);
        if (lastTrendData != null) {
            String[] partialOpenMarketTimes = new String[2];
            for (int i = 0; i < customOpenMarketTimeArray.length; i += 2) {
                String lastDataTime = format(lastTrendData.getDate(), TrendViewData.DATE_FORMAT, "HH:mm");
                if (TrendView.Util.isBetweenTimes(customOpenMarketTimeArray[i], customOpenMarketTimeArray[i + 1], lastDataTime)) {
                    partialOpenMarketTimes[0] = customOpenMarketTimeArray[i];
                    partialOpenMarketTimes[1] = customOpenMarketTimeArray[i + 1];
                    return partialOpenMarketTimes;
                }
            }
        }
        return new String[0];
    }

    private String[] processOpenMarketTime() {
        String[] openMarketTimes = openMarketTime.split(";");
        if (openMarketTimes.length == 2) {
            return createSubOpenMarketTimeArray(openMarketTimes[0], openMarketTimes[1]);
        }
        return openMarketTimes;
    }

    private String[] createSubOpenMarketTimeArray(String startTime, String endTime) {
        int diffHour = TrendView.Util.getDiffMinutes(startTime, endTime) / 60;
        int numOfSegment = diffHour / INTERVAL_HOURS + (diffHour % INTERVAL_HOURS > 3 ? 1 : 0);
        String[] newStartEnd = new String[numOfSegment * 2];
        newStartEnd[0] = startTime;
        newStartEnd[newStartEnd.length - 1] = endTime;
        for (int i = 2; i < newStartEnd.length - 1; i += 2) {
            newStartEnd[i] = addMinutes(startTime, 60 * INTERVAL_HOURS * (i / 2), "HH:mm");
            newStartEnd[i - 1] = newStartEnd[i];
        }
        return newStartEnd;
    }

    public String[] getDisplayMarketTimes() {
        if (customOpenMarketTimeArray != null && customOpenMarketTimeArray.length > 0) {
            return createPartialOpenMarketTime();
        }
        return getPartialOpenMarketTime();
    }

    private String addMinutes(String date, int min, String format) {
        if (date.length() != format.length()) {
            return date;
        }
        SimpleDateFormat parser = new SimpleDateFormat(format);
        try {
            long originDate = parser.parse(date).getTime();
            long finalDate = originDate + min * 60 * 1000;
            return parser.format(new Date(finalDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private String format(String time, String fromFormat, String toFormat) {
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
}
