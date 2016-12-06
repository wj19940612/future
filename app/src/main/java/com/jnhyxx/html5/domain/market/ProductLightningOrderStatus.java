package com.jnhyxx.html5.domain.market;

import android.util.Log;

import com.jnhyxx.html5.domain.order.FuturesFinancing;

import java.util.List;

/**
 * Created by ${wangJie} on 2016/11/29.
 * 闪电下单状态
 */

public class ProductLightningOrderStatus {
    private static final String TAG = "ProductLightningOrderSt";

    public static final String KEY_LIGHTNING_ORDER_IS_OPEN = "KEY_LIGHTNING_ORDER_IS_OPEN";


    public static final int TYPE_BUY_LONG = 1;
    public static final int TYPE_SELL_SHORT = 0;

    //表示闪电下单按钮打开同意协议的fragment的标志
    public static final int TAG_OPEN_ARRGE_FRAGMENT_PAGE = 333;

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
     * 提交订单的时候的止盈点数
     */
    private int stopProfitPoint;

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

    public int getStopProfitPoint() {
        return stopProfitPoint;
    }

    public void setStopProfitPoint(int stopProfitPoint) {
        this.stopProfitPoint = stopProfitPoint;
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
                ", stopProfitPoint=" + stopProfitPoint +
                '}';
    }

    public boolean compareDataWithWeb(FuturesFinancing futuresFinancing) {
        if (futuresFinancing.getAssets() == null && futuresFinancing.getAssets().isEmpty())
            return false;
        List<FuturesFinancing.AssetsBean> assets = futuresFinancing.getAssets();
        if (futuresFinancing.getRatio() == getRatio()) {
            for (int i = 0; i < assets.size(); i++) {
                if (getAssetsId() == assets.get(i).getAssetsId()) {
                    FuturesFinancing.AssetsBean assetsBean = assets.get(i);
                    Log.d(TAG, "配资方案  assetsBean " + assetsBean.toString());
                    if (assetsBean.getFees() * getHandsNum() == getFees() &&
                            assetsBean.getStopLossBeat() == getStopLossPrice() &&
                            assetsBean.getMarginBeat() * getHandsNum() == getMarginMoney() &&
                            assetsBean.getHandsMultiple().contains(String.valueOf(getHandsNum())) &&
                            assetsBean.getStopWinBeats().containsKey(String.valueOf(getStopProfitPoint())) &&
                            assetsBean.getStopWinBeats().get(String.valueOf(getStopProfitPoint())) == getStopWinPrice()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
