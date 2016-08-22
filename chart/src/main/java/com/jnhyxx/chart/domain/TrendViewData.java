package com.jnhyxx.chart.domain;

public class TrendViewData {

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * cu1610,37230.0,20160815101000
     */

    private String contractId;
    private float lastPrice;
    private String date;

    public TrendViewData(String contractId, float lastPrice, String date) {
        this.contractId = contractId;
        this.lastPrice = lastPrice;
        this.date = date;
    }

    public String getHHmm() {
        if (date.length() >= 12) {
            return date.substring(8, 10) + ":" + date.substring(10, 12);
        }
        return "";
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
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
