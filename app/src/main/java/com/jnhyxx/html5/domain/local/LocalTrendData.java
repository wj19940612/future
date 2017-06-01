package com.jnhyxx.html5.domain.local;

import com.jnhyxx.chart.TrendView;
import com.jnhyxx.chart.domain.TrendViewData;
import com.johnz.kutils.DateUtil;

public class LocalTrendData {

    private static final int INTERVAL_HOURS = 6;

    private String rawData;
    private TrendViewData lastData;

    private String openMarketTime;
    private String[] customOpenMarketTimeArray;

    public void setLastData(TrendViewData lastData) {
        this.lastData = lastData;
    }

    public LocalTrendData(String openMarketTime) {
        this.openMarketTime = openMarketTime;
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

    public String getOpenMarketTime() {
        customOpenMarketTimeArray = processOpenMarketTime();
        return createOpenMarketTime();
    }

    private String createOpenMarketTime() {
        if (lastData != null) {
            for (int i = 0; i < customOpenMarketTimeArray.length; i += 2) {
                String lastDataTime = DateUtil.format(lastData.getDate(), TrendViewData.DATE_FORMAT, "HH:mm");
                if (TrendView.Util.isBetweenTimes(customOpenMarketTimeArray[i], customOpenMarketTimeArray[i + 1], lastDataTime)) {
                    return customOpenMarketTimeArray[i] + ";" + customOpenMarketTimeArray[i + 1];
                }
            }
        }
        return "";
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
            newStartEnd[i] = DateUtil.addMinutes(startTime, 60 * INTERVAL_HOURS * (i / 2), "HH:mm");
            newStartEnd[i - 1] = newStartEnd[i];
        }
        return newStartEnd;
    }

    public String getDisplayMarketTimes() {
        if (customOpenMarketTimeArray != null && customOpenMarketTimeArray.length > 0) {
            return createOpenMarketTime();
        }
        return getOpenMarketTime();
    }
}
