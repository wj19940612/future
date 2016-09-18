package com.jnhyxx.html5.domain.order;

public class HoldingOrder extends AbsOrder {

    /**
     * buyTime : 1473821298000
     * currencyUnit : 美元
     * direction : 1
     * eachPointMoney : 1000
     * handsNum : 1
     * orderStatus : 3
     * orderTime : 1473821404000
     * ratio : 6.65
     * realAvgPrice : 45.66
     * realMarketVal : 45660
     * showId : 1185
     * stopLossPrice : 200
     * stopWinPrice : 180
     */

    private long buyTime;
    private String currencyUnit;
    private int direction;
    private int eachPointMoney;
    private int handsNum;
    private int orderStatus;
    private long orderTime;
    private double ratio;
    private double realAvgPrice;
    private int realMarketVal;
    private String showId;
    private double stopLossPrice;
    private double stopWinPrice;

    public long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getEachPointMoney() {
        return eachPointMoney;
    }

    public void setEachPointMoney(int eachPointMoney) {
        this.eachPointMoney = eachPointMoney;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRealAvgPrice() {
        return realAvgPrice;
    }

    public void setRealAvgPrice(double realAvgPrice) {
        this.realAvgPrice = realAvgPrice;
    }

    public int getRealMarketVal() {
        return realMarketVal;
    }

    public void setRealMarketVal(int realMarketVal) {
        this.realMarketVal = realMarketVal;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public double getStopLoss() {
        return stopLossPrice;
    }

    public void setStopLoss(int stopLoss) {
        this.stopLossPrice = stopLoss;
    }

    public double getStopWin() {
        return stopWinPrice;
    }

    public void setStopWin(int stopWin) {
        this.stopWinPrice = stopWin;
    }
}
