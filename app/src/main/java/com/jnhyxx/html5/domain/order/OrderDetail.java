package com.jnhyxx.html5.domain.order;

import java.io.Serializable;

public class OrderDetail implements Serializable {

    /**
     * batchNoTime : 0
     * buyTime : 1473660116000
     * buyType : 0
     * contractsCode : GC1610
     * handsNum : 1
     * marginMoney : 270
     * realAvgPrice : 1328.4
     * sellTime : 1473660160000
     * showId : 117v
     * unwindAvgPrice : 1330
     * unwindType : 2
     * userFees : 10
     */

    private int batchNoTime;
    private long buyTime;
    private int buyType;
    private String contractsCode;
    private int handsNum;
    private double marginMoney;
    private double realAvgPrice;
    private long sellTime;
    private String showId;
    private double unwindAvgPrice;
    private int unwindType;
    private double userFees;

    public int getBatchNoTime() {
        return batchNoTime;
    }

    public void setBatchNoTime(int batchNoTime) {
        this.batchNoTime = batchNoTime;
    }

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

    public double getRealAvgPrice() {
        return realAvgPrice;
    }

    public void setRealAvgPrice(double realAvgPrice) {
        this.realAvgPrice = realAvgPrice;
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

    public void setUnwindAvgPrice(int unwindAvgPrice) {
        this.unwindAvgPrice = unwindAvgPrice;
    }

    public int getUnwindType() {
        return unwindType;
    }

    public void setUnwindType(int unwindType) {
        this.unwindType = unwindType;
    }

    public double getMarginMoney() {
        return marginMoney;
    }

    public double getUnwindAvgPrice() {
        return unwindAvgPrice;
    }

    public double getUserFees() {
        return userFees;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "batchNoTime=" + batchNoTime +
                ", buyTime=" + buyTime +
                ", buyType=" + buyType +
                ", contractsCode='" + contractsCode + '\'' +
                ", handsNum=" + handsNum +
                ", marginMoney=" + marginMoney +
                ", realAvgPrice=" + realAvgPrice +
                ", sellTime=" + sellTime +
                ", showId='" + showId + '\'' +
                ", unwindAvgPrice=" + unwindAvgPrice +
                ", unwindType=" + unwindType +
                ", userFees=" + userFees +
                '}';
    }
}
