package com.jnhyxx.html5.domain.market;

/**
 * 行情简要数据
 */
public class MarketBrief {

    /**
     * code : PP
     * percentage : -0.14%
     * lastPrice : 8281.0
     */

    private String code;
    private String percentage;
    private double lastPrice;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }
}
