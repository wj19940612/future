package com.jnhyxx.html5.domain.order;

import java.io.Serializable;

public class OrderDetail implements Serializable {

    /**
     * buyTime : 1472615097000
     * buyType : 0
     * contractsCode : CL1610
     * handsNum : 1
     * id : 111
     * marginMoney : 180
     * realAvgPrice : 92
     * sellTime : 1472621755000
     * unwindAvgPrice : 92
     * unwindType : 0
     * userFees : 10
     */

    private long buyTime;
    private int buyType;
    private String contractsCode;
    private int handsNum;
    private int showId;
    private double marginMoney;
    private int realAvgPrice;
    private long sellTime;
    private int unwindAvgPrice;
    private int unwindType;
    private double userFees;

    public long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public int getBuyType() {
        return buyType;
    }

    public void setBuyType(int buyType) {
        this.buyType = buyType;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public double getMarginMoney() {
        return marginMoney;
    }

    public void setMarginMoney(double marginMoney) {
        this.marginMoney = marginMoney;
    }

    public int getRealAvgPrice() {
        return realAvgPrice;
    }

    public void setRealAvgPrice(int realAvgPrice) {
        this.realAvgPrice = realAvgPrice;
    }

    public long getSellTime() {
        return sellTime;
    }

    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }

    public int getUnwindAvgPrice() {
        return unwindAvgPrice;
    }

    public void setUnwindAvgPrice(int unwindAvgPrice) {
        this.unwindAvgPrice = unwindAvgPrice;
    }

    public int getUnwindType() {
        return unwindType;
    }

    public void setUnwindType(int unwindType) {
        this.unwindType = unwindType;
    }

    public double getUserFees() {
        return userFees;
    }

    public void setUserFees(double userFees) {
        this.userFees = userFees;
    }
}
