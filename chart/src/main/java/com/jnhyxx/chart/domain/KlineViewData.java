package com.jnhyxx.chart.domain;

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
}
