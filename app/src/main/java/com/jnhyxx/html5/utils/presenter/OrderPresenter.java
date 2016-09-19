package com.jnhyxx.html5.utils.presenter;

import android.os.Handler;

import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.FinanceUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderPresenter {

    private IHoldingOrderView mHoldingOrderView;
    private boolean mBindActivity;

    private static List<HoldingOrder> sHoldingOrderList;
    private int mVarietyId;
    private int mFundType;

    /**
     * 刷新策略:
     * <p/>
     * 2s 间隔, 刷新5次
     * 4s 间隔, 刷新10次
     * 8s 间隔, 刷新20次
     */
    private int mRefreshCount;
    private Handler mHandler;

    public interface IHoldingOrderView {
        void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList);

        void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio);
    }

    public OrderPresenter(IHoldingOrderView holdingOrderView) {
        mHoldingOrderView = holdingOrderView;
        mBindActivity = true;
        mHandler = new Handler();
    }

    public void onResume() {
        mBindActivity = true;
    }

    public void onPause() {
        mBindActivity = false;
    }

    public void setAskBidPrices(double askPrice, double bidPrice) {
        if (sHoldingOrderList == null) {
            sHoldingOrderList = new ArrayList<>();
        }

        BigDecimal totalProfit = new BigDecimal(0);
        boolean hasHoldingOrders = false;
        double ratio = 0;
        boolean refreshDelay = false;
        boolean refresh = false;

        for (HoldingOrder holdingOrder : sHoldingOrderList) {
            int orderStatus = holdingOrder.getOrderStatus();
            if (orderStatus >= HoldingOrder.ORDER_STATUS_HOLDING && orderStatus < HoldingOrder.ORDER_STATUS_SETTLED) {
                // 持仓中、卖处理中的订单
                hasHoldingOrders = true;
                ratio = holdingOrder.getRatio();

                BigDecimal eachPointMoney = new BigDecimal(holdingOrder.getEachPointMoney());
                BigDecimal diff;
                if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
                    diff = FinanceUtil.subtraction(bidPrice, holdingOrder.getRealAvgPrice());
                } else {
                    diff = FinanceUtil.subtraction(holdingOrder.getRealAvgPrice(), askPrice);
                }
                diff = diff.multiply(eachPointMoney);

                if (diff.doubleValue() >= holdingOrder.getStopWin()) {
                    refresh = true;
                }
                if (diff.doubleValue() <= holdingOrder.getStopLoss() * -1) {
                    refresh = true;
                }

                totalProfit = totalProfit.add(diff);
            }

            // 存在处理中的订单,买处理中(代持有),卖处理中(平仓中), 使用 "策略" 刷新持仓数据
            if (orderStatus == HoldingOrder.ORDER_STATUS_PAID_UNHOLDING
                    || orderStatus == HoldingOrder.ORDER_STATUS_CLOSING) {
                refreshDelay = true;
            }
        }

        if (mHoldingOrderView != null && mBindActivity) {
            mHoldingOrderView.onShowTotalProfit(hasHoldingOrders, totalProfit.doubleValue(), ratio);
        }

        if (refresh) { // 触及风控刷新
            loadHoldingOrderList(mVarietyId, mFundType);
        }

        if (refreshDelay && mHandler != null) {
            if (mRefreshCount++ < 5) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadHoldingOrderList(mVarietyId, mFundType);
                    }
                }, 2 * 1000);
            } else if (mRefreshCount++ < 10) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadHoldingOrderList(mVarietyId, mFundType);
                    }
                }, 4 * 1000);
            } else if (mRefreshCount++ < 20) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadHoldingOrderList(mVarietyId, mFundType);
                    }
                }, 8 * 1000);
            }
        }
    }

    public void loadHoldingOrderList(int varietyId, int fundType) {
        if (!LocalUser.getUser().isLogin()) return;

        mVarietyId = varietyId;
        mFundType = fundType;

        API.Order.getHoldingOrderList(varietyId, fundType)
                .setCallback(new Callback2<Resp<List<HoldingOrder>>, List<HoldingOrder>>() {
                    @Override
                    public void onRespSuccess(List<HoldingOrder> holdingOrderList) {
                        sHoldingOrderList = holdingOrderList;
                        if (mHoldingOrderView != null && mBindActivity) {
                            mHoldingOrderView.onShowHoldingOrderList(sHoldingOrderList);
                        }
                    }
                }).fire();

    }
}
