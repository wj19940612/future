package com.jnhyxx.html5.domain.market;

import com.jnhyxx.html5.domain.local.SubmittedOrder;

/**
 * Created by ${wangJie} on 2016/11/29.
 * 闪电下单状态
 */

public class ProductLightningOrderStatus {

    public static final int PAY_TYPE = 1;

    /**
     * assetsId : 1
     * varietyId : 2
     * handsNum : 1
     * stopLossPrice : 10
     * stopWinPrice : 180
     * marginMoney : 92
     * fees : 10
     * ratio : 7
     */

    /**
     * 支付方式   0：积分 1：现金
     */
    private int payType;
    /**
     * 配资id
     */
    private int assetsId;
    /**
     * 品种id
     */
    private int varietyId;
    /**
     * 手数
     */
    private int handsNum;
    /**
     * 止损金额
     */
    private double stopLossPrice;
    /**
     * 止盈金额
     */
    private double stopWinPrice;
    /**
     * 保证金
     */
    private double marginMoney;
    /**
     * 手续费
     */
    private double fees;
    /**
     * 费率
     */
    private double ratio;


    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(int assetsId) {
        this.assetsId = assetsId;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
    }

    public int getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(int varietyId) {
        this.varietyId = varietyId;
    }

    public double getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public double getStopWinPrice() {
        return stopWinPrice;
    }

    public void setStopWinPrice(double stopWinPrice) {
        this.stopWinPrice = stopWinPrice;
    }

    public double getMarginMoney() {
        return marginMoney;
    }

    public void setMarginMoney(double marginMoney) {
        this.marginMoney = marginMoney;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "ProductLightningOrderStatus{" +
                "payType=" + payType +
                ", assetsId=" + assetsId +
                ", varietyId=" + varietyId +
                ", handsNum=" + handsNum +
                ", stopLossPrice=" + stopLossPrice +
                ", stopWinPrice=" + stopWinPrice +
                ", marginMoney=" + marginMoney +
                ", fees=" + fees +
                ", ratio=" + ratio +
                '}';
    }

    public boolean compareDataWithWeb(ProductLightningOrderStatus productLightningOrderStatus) {
        if (productLightningOrderStatus == null) {
            return false;
        }
        if (productLightningOrderStatus.getFees() == getFees()
                && productLightningOrderStatus.getAssetsId() == getAssetsId()
                && productLightningOrderStatus.getHandsNum() == getHandsNum()
                && productLightningOrderStatus.getMarginMoney() == getMarginMoney()
                && productLightningOrderStatus.getRatio() == getRatio()
                && productLightningOrderStatus.getStopLossPrice() == getStopLossPrice()
                && productLightningOrderStatus.getStopWinPrice() == getStopWinPrice()) {
            return true;
        }
        return false;
    }

    public ProductLightningOrderStatus setSubmittedOrder(SubmittedOrder submittedOrder){
        
    }
}
