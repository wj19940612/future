package com.jnhyxx.html5.domain.order;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户查看持仓接口model
 */
public class HoldingOrder extends AbsOrder implements Parcelable {

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
    /**
     * 方向：1： 买涨 0： 买跌
     */
    private int direction;
    private double eachPointMoney;
    private int handsNum;
    /**
     * 订单状态 0:待支付，1：已支付，待持仓 2：持仓中 3 ：平仓处理中
     */
    private int orderStatus;
    private long orderTime;
    private double ratio;
    private double realAvgPrice;
    private double realMarketVal;
    private String showId;
    private double stopLossPrice; //止损金额
    private double stopLossMoney; //止损价格
    private double stopWinPrice;
    private double stopWinMoney;

    /**
     * 平仓价      （人民币）
     */
    private double unwindAvgPrice;

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

    public double getEachPointMoney() {
        return eachPointMoney;
    }

    public void setEachPointMoney(double eachPointMoney) {
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

    public double getRealMarketVal() {
        return realMarketVal;
    }

    public void setRealMarketVal(double realMarketVal) {
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

    public double getUnwindAvgPrice() {
        return unwindAvgPrice;
    }

    public void setUnwindAvgPrice(double unwindAvgPrice) {
        this.unwindAvgPrice = unwindAvgPrice;
    }

    public double getStopLossMoney() {
        return stopLossMoney;
    }

    public double getStopWinMoney() {
        return stopWinMoney;
    }

    @Override
    public String toString() {
        return "HoldingOrder{" +
                "buyTime=" + buyTime +
                ", currencyUnit='" + currencyUnit + '\'' +
                ", direction=" + direction +
                ", eachPointMoney=" + eachPointMoney +
                ", handsNum=" + handsNum +
                ", orderStatus=" + orderStatus +
                ", orderTime=" + orderTime +
                ", ratio=" + ratio +
                ", realAvgPrice=" + realAvgPrice +
                ", realMarketVal=" + realMarketVal +
                ", showId='" + showId + '\'' +
                ", stopLossPrice=" + stopLossPrice +
                ", stopLossMoney=" + stopLossMoney +
                ", stopWinPrice=" + stopWinPrice +
                ", stopWinMoney=" + stopWinMoney +
                ", unwindAvgPrice=" + unwindAvgPrice +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.buyTime);
        dest.writeString(this.currencyUnit);
        dest.writeInt(this.direction);
        dest.writeDouble(this.eachPointMoney);
        dest.writeInt(this.handsNum);
        dest.writeInt(this.orderStatus);
        dest.writeLong(this.orderTime);
        dest.writeDouble(this.ratio);
        dest.writeDouble(this.realAvgPrice);
        dest.writeDouble(this.realMarketVal);
        dest.writeString(this.showId);
        dest.writeDouble(this.stopLossPrice);
        dest.writeDouble(this.stopLossMoney);
        dest.writeDouble(this.stopWinPrice);
        dest.writeDouble(this.stopWinMoney);
        dest.writeDouble(this.unwindAvgPrice);
    }

    public HoldingOrder() {
    }

    protected HoldingOrder(Parcel in) {
        this.buyTime = in.readLong();
        this.currencyUnit = in.readString();
        this.direction = in.readInt();
        this.eachPointMoney = in.readDouble();
        this.handsNum = in.readInt();
        this.orderStatus = in.readInt();
        this.orderTime = in.readLong();
        this.ratio = in.readDouble();
        this.realAvgPrice = in.readDouble();
        this.realMarketVal = in.readDouble();
        this.showId = in.readString();
        this.stopLossPrice = in.readDouble();
        this.stopLossMoney = in.readDouble();
        this.stopWinPrice = in.readDouble();
        this.stopWinMoney = in.readDouble();
        this.unwindAvgPrice = in.readDouble();
    }

    public static final Parcelable.Creator<HoldingOrder> CREATOR = new Parcelable.Creator<HoldingOrder>() {
        @Override
        public HoldingOrder createFromParcel(Parcel source) {
            return new HoldingOrder(source);
        }

        @Override
        public HoldingOrder[] newArray(int size) {
            return new HoldingOrder[size];
        }
    };
}
