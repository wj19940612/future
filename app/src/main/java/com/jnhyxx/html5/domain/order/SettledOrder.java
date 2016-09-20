package com.jnhyxx.html5.domain.order;

import java.io.Serializable;

public class SettledOrder extends AbsOrder implements Serializable {

    /**
     * batchNoTime : 0
     * currencyUnit : 美元
     * direction : 0
     * handsNum : 1
     * orderStatus : 4
     * orderTime : 1474342989000
     * ratio : 6.65
     * realAvgPrice : 43.64
     * realMarketVal : 43640
     * sellTime : 1474343063000
     * showId : 11mj
     * stopLossPrice : 10
     * stopWinPrice : 10
     * unwindAvgPrice : 43.65
     * unwindType : 2
     * winOrLoss : -10
     */

    private int batchNoTime;
    private String currencyUnit;
    private int direction;
    private int handsNum;
    private int orderStatus;
    private long orderTime;
    private double ratio;
    private double realAvgPrice;
    private int realMarketVal;
    private long sellTime;
    private String showId;
    private int stopLossPrice;
    private int stopWinPrice;
    private double unwindAvgPrice;
    private int unwindType;
    private int winOrLoss;

    public int getBatchNoTime() {
        return batchNoTime;
    }

    public void setBatchNoTime(int batchNoTime) {
        this.batchNoTime = batchNoTime;
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

    public long getSellTime() {
        return sellTime;
    }

    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public int getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(int stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public int getStopWinPrice() {
        return stopWinPrice;
    }

    public void setStopWinPrice(int stopWinPrice) {
        this.stopWinPrice = stopWinPrice;
    }

    public double getUnwindAvgPrice() {
        return unwindAvgPrice;
    }

    public void setUnwindAvgPrice(double unwindAvgPrice) {
        this.unwindAvgPrice = unwindAvgPrice;
    }

    public int getUnwindType() {
        return unwindType;
    }

    public void setUnwindType(int unwindType) {
        this.unwindType = unwindType;
    }

    public int getWinOrLoss() {
        return winOrLoss;
    }

    public void setWinOrLoss(int winOrLoss) {
        this.winOrLoss = winOrLoss;
    }
}
