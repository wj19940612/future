package com.jnhyxx.html5.domain.market;

public class TrendViewData {
    /**
     * cu1610,37230.0,20160815101000
     */

    private String instrumentId;
    private float lastPrice;
    private String date;

    public TrendViewData(String instrumentId, float lastPrice, String date) {
        this.instrumentId = instrumentId;
        this.lastPrice = lastPrice;
        this.date = date;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
