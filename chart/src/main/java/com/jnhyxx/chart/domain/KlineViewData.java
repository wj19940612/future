package com.jnhyxx.chart.domain;

import android.util.SparseArray;

public class KlineViewData {

    /**
     * closePrice : 1179.2
     * day : 2016-12-03
     * maxPrice : 1180.3
     * minPrice : 1168.4
     * openPrice : 1174.3
     * time : 2016-12-03 06:01:33
     * timeStamp : 1480716093535
     */

    private float closePrice;
    private float maxPrice;
    private float minPrice;
    private float openPrice;
    private long nowVolume;
    private String day;
    private String time;
    private long timeStamp;

    // local cache data
    private SparseArray<Float> movingAverages;

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNowVolume() {
        return nowVolume;
    }

    public void addMovingAverage(int movingAverageKey, float movingAverageValue) {
        if (movingAverages == null) {
            movingAverages = new SparseArray<>();
        }
        movingAverages.put(movingAverageKey, Float.valueOf(movingAverageValue));
    }

    public float getMovingAverage(int movingAverageKey) {
        if (movingAverages != null) {
            Float movingAverageValue = movingAverages.get(movingAverageKey);
            if (movingAverageValue != null) {
                return movingAverageValue.floatValue();
            }
            return 0f;
        }
        return 0f;
    }

    @Override
    public String toString() {
        return "KlineViewData{" +
                "closePrice=" + closePrice +
                ", maxPrice=" + maxPrice +
                ", minPrice=" + minPrice +
                ", openPrice=" + openPrice +
                ", time='" + time + '\'' +
                ", day='" + day + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
