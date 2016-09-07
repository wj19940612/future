package com.jnhyxx.html5.domain.market;

/**
 * 行情简要数据
 */
public class MarketData {

    /**
     * floatPricePoint : -0.45%
     * varietyType : HSI
     * lastPrice : 22770
     */

    private String floatPricePoint;
    private String varietyType;
    private double lastPrice;


    public String getUnsignedPercentage() {
        if (floatPricePoint.startsWith("-") || floatPricePoint.startsWith("+")) {
            return floatPricePoint.substring(1, floatPricePoint.length());
        }
        return floatPricePoint;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getPercentage() {
        return floatPricePoint;
    }

    public void setPercentage(String percentage) {
        this.floatPricePoint = percentage;
    }

    public String getVarietyType() {
        return varietyType;
    }

    public void setVarietyType(String varietyType) {
        this.varietyType = varietyType;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "floatPricePoint='" + floatPricePoint + '\'' +
                ", varietyType='" + varietyType + '\'' +
                ", lastPrice=" + lastPrice +
                '}';
    }
}
