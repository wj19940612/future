package com.jnhyxx.html5.utils.presenter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.activity.TradeActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.fragment.order.HoldingFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.FinanceUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class HoldingOrderPresenter {

    private static final String TAG = "VolleyHttp";

    public HoldingOrderPresenter(IHoldingOrderView iHoldingOrderView) {
        mIHoldingOrderView = iHoldingOrderView;
        mHandler = new Handler();
    }

    private IHoldingOrderView mIHoldingOrderView;

    private FullMarketData mMarketData;
    private List<HoldingOrder> mHoldingOrderList;
    private Handler mHandler;
    private int mCounter;
    private boolean mIsQuerying;

    public interface IHoldingOrderView {

        void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList);

        void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio);

        void onSubmitAllHoldingPositionsCompleted(String message);

        void onSubmitHoldingOrderCompleted(HoldingOrder holdingOrder);

        void onRiskControlTriggered();
    }

    public void onPause() {
        mHandler.removeCallbacksAndMessages(null);
        mMarketData = null;
        mCounter = 0;
        if (mHoldingOrderList != null) {
            mHoldingOrderList.clear();
        }
    }

    public void onDestroy() {
        mHandler = null;
    }

    public void closeAllHoldingPositions(final int varietyId, final int fundType) {
        StringBuilder showIds = new StringBuilder();
        StringBuilder unwindPrices = new StringBuilder();
        for (final HoldingOrder holdingOrder : mHoldingOrderList) {
            if (holdingOrder.getOrderStatus() == HoldingOrder.ORDER_STATUS_HOLDING) {
                double unwindPrice = 0;
                if (mMarketData != null) {
                    if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
                        unwindPrice = mMarketData.getBidPrice();
                    } else {
                        unwindPrice = mMarketData.getAskPrice();
                    }
                }
                showIds.append(holdingOrder.getShowId()).append(",");
                unwindPrices.append(unwindPrice).append(",");
            }
        }

        if (showIds.length() > 0) {
            showIds.deleteCharAt(showIds.length() - 1);
            unwindPrices.deleteCharAt(unwindPrices.length() - 1);
        }

        if (!TextUtils.isEmpty(showIds.toString())) {
            API.Order.closeAllHoldingOrders(showIds.toString(), fundType, unwindPrices.toString())
                    .setCallback(new Callback1<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            setOrderListStatus(HoldingOrder.ORDER_STATUS_CLOSING, mHoldingOrderList);
                            onSubmitAllHoldingPositionsCompleted(resp.getMsg());
                            loadHoldingOrderList(varietyId, fundType);
                        }
                    }).fire();
        }
    }

    private void setOrderListStatus(int orderStatus, List<HoldingOrder> holdingOrderList) {
        for (HoldingOrder holdingOrder : holdingOrderList) {
            holdingOrder.setOrderStatus(orderStatus);
        }
    }

    public void closePosition(final int varietyId, final int fundType, final HoldingOrder order) {
        double unwindPrice = 0;
        if (mMarketData != null) {
            if (order.getDirection() == HoldingOrder.DIRECTION_LONG) {
                unwindPrice = mMarketData.getBidPrice();
            } else {
                unwindPrice = mMarketData.getAskPrice();
            }
        }

        API.Order.closeHoldingOrder(order.getShowId(), fundType, unwindPrice)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        order.setOrderStatus(HoldingOrder.ORDER_STATUS_CLOSING);
                        onSubmitHoldingOrderCompleted(order);
                        loadHoldingOrderList(varietyId, fundType);
                    }
                }).fireSync();
    }

    public void clearData() {
        mMarketData = null;
        mHoldingOrderList.clear();
    }

    public void setFullMarketData(FullMarketData marketData, int varietyId, int fundType) {
        mMarketData = marketData;

        BigDecimal totalProfit = new BigDecimal(0);
        boolean hasHoldingOrders = false;
        double ratio = 0;
        boolean refresh = false;

        if (marketData != null && mHoldingOrderList != null) {
            for (HoldingOrder holdingOrder : mHoldingOrderList) {
                int orderStatus = holdingOrder.getOrderStatus();
                if (orderStatus >= HoldingOrder.ORDER_STATUS_HOLDING && orderStatus < HoldingOrder.ORDER_STATUS_SETTLED) {
                    // 持仓中、卖处理中的订单
                    hasHoldingOrders = true;
                    ratio = holdingOrder.getRatio();

                    BigDecimal eachPointMoney = new BigDecimal(holdingOrder.getEachPointMoney());
                    BigDecimal diff;
                    if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
                        diff = FinanceUtil.subtraction(mMarketData.getBidPrice(), holdingOrder.getRealAvgPrice());
                    } else {
                        diff = FinanceUtil.subtraction(holdingOrder.getRealAvgPrice(), mMarketData.getAskPrice());
                    }
                    diff = diff.multiply(eachPointMoney).setScale(4, RoundingMode.HALF_EVEN);

                    BigDecimal bigDecimalStopLoss = FinanceUtil.multiply(holdingOrder.getStopLoss(), -1);
                    if (diff.compareTo(new BigDecimal(holdingOrder.getStopWin())) >= 0) {
                        refresh = true;
                    }
                    if (diff.compareTo(bigDecimalStopLoss) <= 0) {
                        refresh = true;
                    }
                    totalProfit = totalProfit.add(diff);
                }
            }
        }


        onShowTotalProfit(hasHoldingOrders, totalProfit.doubleValue(), ratio);

        if (refresh) { // 触及风控刷新
            Log.d(TAG, "触及风控刷新");
            loadHoldingOrderList(varietyId, fundType);
            onRiskControlTriggered();
        }
    }

    private void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (mIHoldingOrderView != null) {
            mIHoldingOrderView.onShowTotalProfit(hasHoldingOrders, totalProfit, ratio);
        }
    }

    private void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
        if (mIHoldingOrderView != null) {

            if (mIHoldingOrderView instanceof TradeActivity) {
                Log.d(TAG, "onShowHoldingOrderList: " + "TradeActivity");
            } else if (mIHoldingOrderView instanceof HoldingFragment) {
                Log.d(TAG, "onShowHoldingOrderList: " + "HoldingFragment");
            }

            mIHoldingOrderView.onShowHoldingOrderList(holdingOrderList);
        }
    }

    private void onSubmitAllHoldingPositionsCompleted(String message) {
        if (mIHoldingOrderView != null) {
            mIHoldingOrderView.onSubmitAllHoldingPositionsCompleted(message);
        }
    }

    private void onSubmitHoldingOrderCompleted(HoldingOrder holdingOrder) {
        if (mIHoldingOrderView != null) {
            mIHoldingOrderView.onSubmitHoldingOrderCompleted(holdingOrder);
        }
    }

    private void onRiskControlTriggered() {
        if (mIHoldingOrderView != null) {
            mIHoldingOrderView.onRiskControlTriggered();
        }
    }

    public void loadHoldingOrderList(final int varietyId, final int fundType) {
        if (!LocalUser.getUser().isLogin()) return;

        Log.d(TAG, "loadHoldingOrderList: " + varietyId + ", fund: " + fundType);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        API.Order.getHoldingOrderList(varietyId, fundType)
                .setCallback(new Callback2<Resp<List<HoldingOrder>>, List<HoldingOrder>>() {
                    @Override
                    public void onRespSuccess(List<HoldingOrder> holdingOrderList) {
                        Log.d(TAG, "loadHoldingOrderList finished: varietyId: " + varietyId);
                        mHoldingOrderList = holdingOrderList;

                        onShowHoldingOrderList(holdingOrderList);
                        setFullMarketData(mMarketData, varietyId, fundType);
                        queryHoldingOrderListAndUpdate(varietyId, fundType, holdingOrderList);
                    }
                }).fire();
    }

    /**
     * 查询策略:
     * <p/>
     * 500ms 间隔, 刷新10次
     * 2s 间隔, 刷新20次(10 + 20)
     * 8s 间隔, 刷新30次(30 + 30)
     * 32s 间隔, 刷新60次(60 + 60)
     *
     * @param varietyId
     * @param fundType
     */
    private void doQueryJobDelay(final int varietyId, final int fundType, final List<HoldingOrder> holdingOrderList) {
        if (mHandler == null) return;

        if (mCounter < 10) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryHoldingOrderListAndUpdate(varietyId, fundType, holdingOrderList);
                }
            }, 5 * 100);
        } else if (mCounter < 30) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryHoldingOrderListAndUpdate(varietyId, fundType, holdingOrderList);
                }
            }, 2 * 1000);
        } else if (mCounter < 60) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryHoldingOrderListAndUpdate(varietyId, fundType, holdingOrderList);
                }
            }, 8 * 1000);
        } else if (mCounter < 120) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryHoldingOrderListAndUpdate(varietyId, fundType, holdingOrderList);
                }
            }, 32 * 1000);
        }
    }

    private void queryHoldingOrderListAndUpdate(final int varietyId, final int fundType, List<HoldingOrder> holdingOrderList) {
        boolean refresh = false;
        for (HoldingOrder holdingOrder : holdingOrderList) {
            int orderStatus = holdingOrder.getOrderStatus();
            // 存在处理中的订单,买处理中(代持有),卖处理中(平仓中), 使用 "策略" 刷新持仓数据
            if (orderStatus == HoldingOrder.ORDER_STATUS_PAID_UNHOLDING
                    || orderStatus == HoldingOrder.ORDER_STATUS_CLOSING) {
                refresh = true;
                break;
            }
        }

        Log.d(TAG, "queryHoldingOrderListAndUpdate before start: " + refresh + ", varietyId: " + varietyId + ", queryList.Size: " + holdingOrderList.size());

        if (refresh) {
            API.Order.getHoldingOrderList(varietyId, fundType)
                    .setCallback(new Callback2<Resp<List<HoldingOrder>>, List<HoldingOrder>>() {
                        @Override
                        public void onRespSuccess(List<HoldingOrder> holdingOrderList) {
                            mHoldingOrderList = holdingOrderList;

                            mCounter++;

                            Log.d(TAG, "queryHoldingOrderListAndUpdate finished count: " + mCounter + ", varietyId: " + varietyId);

                            onShowHoldingOrderList(holdingOrderList);

                            doQueryJobDelay(varietyId, fundType, holdingOrderList);
                        }
                    }).fire();
        } else {
            mCounter = 0;
        }
    }
}
