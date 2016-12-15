package com.jnhyxx.html5.domain.order;

import android.util.Log;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;

import java.util.List;

/**
 * Created by ${wangJie} on 2016/11/29.
 * 闪电下单状态
 */

public class LightningOrderAsset {

    private static final String TAG = "ProductLightningOrderSt";


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
        return "LightningOrderAsset{" +
                "payType=" + payType +
                ", assetsId=" + assetsId +
                ", varietyId=" + varietyId +
                ", handsNum=" + handsNum +
                ", stopLossPrice=" + stopLossPrice +
                ", stopWinPrice=" + stopWinPrice +
                ", stopProfitPoint=" + stopProfitPoint +
                ", marginMoney=" + marginMoney +
                ", fees=" + fees +
                ", ratio=" + ratio +
                '}';
    }

    /**
     * 本地的闪电下单数据和产品的配资进行对比，如果不相同，则闪电下单失效
     *
     * @param futuresFinancing 产品配资方案
     * @return
     */
    public boolean isValid(FuturesFinancing futuresFinancing) {
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

    //获取止损的选择索引
    public int getSelectStopLossIndex(FuturesFinancing futuresFinancing) {
        int stopLossIndex = 0;
        if (futuresFinancing != null && !futuresFinancing.getAssets().isEmpty()) {
            List<FuturesFinancing.AssetsBean> assets = futuresFinancing.getAssets();
            for (int i = 0; i < assets.size(); i++) {
                if (assets.get(i).getStopLossBeat() == getStopLossPrice()) {
                    stopLossIndex = i;
                    break;
                }
            }
        }
        return stopLossIndex;
    }

    //获取被选择的手数
    public int getSelectHandNum(Product product, FuturesFinancing futuresFinancing) {
        int defaultIndex = 0;
        if (futuresFinancing != null) {
            //获取止损集合
            List<FuturesFinancing.StopLoss> stopLossList = futuresFinancing.getStopLossList(product);
            List<FuturesFinancing.TradeQuantity> tradeQuantityList = stopLossList.get(getSelectStopLossIndex(futuresFinancing)).getTradeQuantityList();
            for (int i = 0; i < tradeQuantityList.size(); i++) {//
                if (tradeQuantityList.get(i).getQuantity() == getHandsNum()) {
                    defaultIndex = i;
                    break;
                }
            }
        }
        return defaultIndex;
    }

    //获取被选择的止盈数据
    public int getSelectStopProfit(Product product, FuturesFinancing futuresFinancing) {
        int selectIndex = 0;
        if (futuresFinancing != null) {
            List<FuturesFinancing.StopLoss> stopLossList = futuresFinancing.getStopLossList(product);
            List<FuturesFinancing.StopProfit> stopProfitList = stopLossList.get(getSelectStopLossIndex(futuresFinancing)).getStopProfitList();
            for (int i = 0; i < stopProfitList.size(); i++) {
                if (stopProfitList.get(i).getStopProfit() == getStopWinPrice()) {
                    Log.d(TAG, " i " + i + " " + stopProfitList.get(i).getStopProfit());
                    selectIndex = i;
                    break;
                }
            }
        }
        return selectIndex;
    }

    /**
     * 本地存储闪电下单数据
     * @param product
     * @param fundType
     * @param lightningOrderAsset
     */
    public static void setLocalLightningOrder(Product product, int fundType, LightningOrderAsset lightningOrderAsset) {
        String lightningOrderKey = createLightningOrderKey(product, fundType);
        Preference.get().setLightningOrderAsset(lightningOrderKey, lightningOrderAsset);
    }

    /**
     * 获取本地的闪电数据
     * @param product
     * @param fundType
     * @return
     */
    public static LightningOrderAsset getLocalLightningOrderAsset(Product product, int fundType) {
        String lightningOrderKey = createLightningOrderKey(product, fundType);
        return Preference.get().getLightningOrderAsset(lightningOrderKey);
    }

    private static String createLightningOrderKey(Product product, int fundType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(product.getVarietyId());
        stringBuilder.append(LocalUser.getUser().getPhone());
        stringBuilder.append(fundType);
        return stringBuilder.toString();
    }

    public SubmittedOrder getSubmittedOrder(Product product, int fundType, int buyType, FullMarketData fullMarketData) {
        SubmittedOrder submittedOrder = new SubmittedOrder(product.getVarietyId(), buyType, SubmittedOrder.SUBMIT_TYPE_LIGHTNING_ORDER);
        if (fullMarketData != null) {
            submittedOrder.setOrderPrice(buyType == TYPE_BUY_LONG ? fullMarketData.getAskPrice() : fullMarketData.getBidPrice());
        }
        submittedOrder.setPayType(fundType);
        submittedOrder.setAssetsId(getAssetsId());
        submittedOrder.setHandsNum(getHandsNum());
        submittedOrder.setStopProfitPoint(getStopProfitPoint());
        return submittedOrder;
    }

    /**
     * 判断闪电下单是否开启
     *
     * @param product  产品
     * @param fundType 支付方式
     * @return
     */
    public static boolean isLightningOrderOpened(Product product, int fundType) {
        return getLocalLightningOrderAsset(product, fundType) != null;
    }
}
