package com.jnhyxx.html5.domain.local;

import android.util.Log;

import com.jnhyxx.chart.TrendView;
import com.johnz.kutils.DateUtil;

public class LocalTrendData {

    private String rawData;
    private boolean isForeign;
    private String openMarketTime;
    private String[] openMarketTimeArray;

    public LocalTrendData(String openMarketTime, boolean foreign) {
        this.openMarketTime = openMarketTime;
        this.isForeign = foreign;

    }

    public void setForeign(boolean foreign) {
        isForeign = foreign;
    }

    public void setOpenMarketTime(String openMarketTime) {
        this.openMarketTime = openMarketTime;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getOpenMarketTime(long currentTime) {
        openMarketTimeArray = processOpenMarketTime();

        for (int i = 0; i < openMarketTimeArray.length; i += 2) {
                
        }

        return openMarketTime;
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
        Log.d("TEST", "st: " + startTime + ", endTime: " + endTime + ". createSubOpenMarketTimeArray: " + diffHour);
        int numOfSegment = diffHour / 6 + (diffHour % 6 > 3 ? 1 : 0);
        Log.d("TEST", "numOfSegment: " + numOfSegment);
        String[] newStartEnd = new String[numOfSegment * 2];
        newStartEnd[0] = startTime;
        newStartEnd[newStartEnd.length - 1] = endTime;
        for (int i = 1; i < newStartEnd.length - 1; i += 2) {
            newStartEnd[i] = DateUtil.addMinutes(startTime, 60 * 6 * i, "HH:mm");
            newStartEnd[i + 1] = newStartEnd[i];
        }
        return newStartEnd;
    }

    public String getDisplayMarketTimes(long currentTime) {
        return null;
    }
}
