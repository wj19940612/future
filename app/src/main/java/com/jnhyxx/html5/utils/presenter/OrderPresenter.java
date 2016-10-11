package com.jnhyxx.html5.utils.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.FinanceUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderPresenter {

    private static OrderPresenter sInstance;

    public OrderPresenter() {
        mHandler = new Handler();
        mScheduleJob = new ScheduleJob();
        mIHoldingOrderViewList = new ArrayList<>();
    }

    public static OrderPresenter getInstance() {
        if (sInstance == null) {
            sInstance = new OrderPresenter();
        }
        return sInstance;
    }

    private List<IHoldingOrderView> mIHoldingOrderViewList;
    private List<HoldingOrder> mHoldingOrderList;

    private int mVarietyId;
    private int mFundType;
    private FullMarketData mMarketData;

    /**
     * 刷新策略:
     * <p/>
     * 2s 间隔, 刷新5次
     * 4s 间隔, 刷新10次
     * 8s 间隔, 刷新20次
     */
    private class ScheduleJob {
        public boolean performing;
        public int count;
    }

    private Handler mHandler;
    private ScheduleJob mScheduleJob;

    public interface IHoldingOrderView {

        void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList);

        void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio);
    }

    public void register(IHoldingOrderView iHoldingOrderView) {
        mIHoldingOrderViewList.add(iHoldingOrderView);
    }

    public void unregister(IHoldingOrderView iHoldingOrderView) {
        mIHoldingOrderViewList.remove(iHoldingOrderView);
    }

    public List<HoldingOrder> getHoldingOrderList() {
        return mHoldingOrderList;
    }

    public void closeAllHoldingPositions(int fundType) {
//        for (final HoldingOrder holdingOrder : mHoldingOrderList) {
//
//            double unwindPrice = 0;
//            if (mMarketData != null) {
//                if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
//                    unwindPrice = mMarketData.getBidPrice();
//                } else {
//                    unwindPrice = mMarketData.getAskPrice();
//                }
//            }
//
//            requestCloseHoldingOrder(fundType, holdingOrder, unwindPrice);
//        }


        StringBuffer showIds = new StringBuffer();
        StringBuffer unwindPrices = new StringBuffer();
        for (int i = 0; i < mHoldingOrderList.size(); i++) {
            HoldingOrder holdingOrder = mHoldingOrderList.get(i);
            if (holdingOrder != null) {
                double orderUnwindPrice = getOneKeyHoldingOrderUnwindPrice(holdingOrder);
                unwindPrices.append(orderUnwindPrice + ",");
                showIds.append(holdingOrder.getShowId() + ",");
            }
        }
        if (!TextUtils.isEmpty(showIds.toString()) && !TextUtils.isEmpty(unwindPrices.toString())) {
            requestAllHoldingOrder(showIds.toString(), fundType, unwindPrices.toString());
        }
    }

    private double getOneKeyHoldingOrderUnwindPrice(HoldingOrder holdingOrder) {
        double unwindPrice = 0;
        if (mMarketData != null) {
            if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
                unwindPrice = mMarketData.getBidPrice();
            } else {
                unwindPrice = mMarketData.getAskPrice();
            }
        }
        return unwindPrice;
    }

    //一键平仓
    public void requestAllHoldingOrder(String showIds, int payType, String unwindPrices) {
        API.Order.aKeyHoldingOrder(showIds, payType, unwindPrices).setCallback(new Callback1<Resp<JsonObject>>() {

            @Override
            protected void onRespSuccess(Resp<JsonObject> resp) {
                for (int i = 0; i < mHoldingOrderList.size(); i++) {
                    HoldingOrder holdingOrder = mHoldingOrderList.get(i);
                    holdingOrder.setOrderStatus(HoldingOrder.ORDER_STATUS_CLOSING);
                }
            }
        }).fire();
    }

    private void requestCloseHoldingOrder(int fundType, final HoldingOrder holdingOrder, double unwindPrice) {
        API.Order.closeHoldingOrder(holdingOrder.getShowId(), fundType, unwindPrice)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        holdingOrder.setOrderStatus(HoldingOrder.ORDER_STATUS_CLOSING);
                    }
                }).fire();
    }

    public void closePosition(int fundType, HoldingOrder order) {
        double unwindPrice = 0;
        if (mMarketData != null) {
            if (order.getDirection() == HoldingOrder.DIRECTION_LONG) {
                unwindPrice = mMarketData.getBidPrice();
            } else {
                unwindPrice = mMarketData.getAskPrice();
            }
        }
        requestCloseHoldingOrder(fundType, order, unwindPrice);
    }

    public void setFullMarketData(FullMarketData marketData) {
        if (mHoldingOrderList == null) {
            mHoldingOrderList = new ArrayList<>();
        }

        mMarketData = marketData;
        BigDecimal totalProfit = new BigDecimal(0);
        boolean hasHoldingOrders = false;
        double ratio = 0;
        boolean refreshSchedule = false;
        boolean refresh = false;

        for (HoldingOrder holdingOrder : mHoldingOrderList) {
            int orderStatus = holdingOrder.getOrderStatus();
            if (orderStatus >= HoldingOrder.ORDER_STATUS_HOLDING && orderStatus < HoldingOrder.ORDER_STATUS_SETTLED) {
                // 持仓中、卖处理中的订单
                hasHoldingOrders = true;
                ratio = holdingOrder.getRatio();

                BigDecimal eachPointMoney = new BigDecimal(holdingOrder.getEachPointMoney());
                BigDecimal diff;
                if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_SHORT) {
                    diff = FinanceUtil.subtraction(marketData.getBidPrice(), holdingOrder.getRealAvgPrice());
                } else {
                    diff = FinanceUtil.subtraction(holdingOrder.getRealAvgPrice(), marketData.getAskPrice());
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
                refreshSchedule = true;
            }
        }

        onViewShowTotalProfit(hasHoldingOrders, totalProfit.doubleValue(), ratio);

        if (refresh) { // 触及风控刷新
            loadHoldingOrderList(mVarietyId, mFundType);
        }

        if (refreshSchedule && mHandler != null) {
            startScheduleJob();
        } else {
            mScheduleJob.count = 0;
        }
    }

    private void onViewShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (mIHoldingOrderViewList != null) {
            for (IHoldingOrderView iHoldingOrderView : mIHoldingOrderViewList) {
                iHoldingOrderView.onShowTotalProfit(hasHoldingOrders, totalProfit, ratio);
            }
        }
    }

    private void onViewShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
        if (mIHoldingOrderViewList != null) {
            for (IHoldingOrderView iHoldingOrderView : mIHoldingOrderViewList) {
                iHoldingOrderView.onShowHoldingOrderList(holdingOrderList);
            }
        }
    }

    private void startScheduleJob() {
        if (mScheduleJob.performing) return;

        mScheduleJob.performing = true;
        if (mScheduleJob.count < 5) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshHoldingOrderList();
                }
            }, mScheduleJob.count == 0 ? 0 : 2 * 1000); // 第一次不延时
        } else if (mScheduleJob.count < 10) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshHoldingOrderList();
                }
            }, 4 * 1000);
        } else if (mScheduleJob.count < 20) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshHoldingOrderList();
                }
            }, 8 * 1000);
        }
        mScheduleJob.count++;
    }

    private void refreshHoldingOrderList() {
        API.Order.getHoldingOrderList(mVarietyId, mFundType)
                .setCallback(new Callback2<Resp<List<HoldingOrder>>, List<HoldingOrder>>() {
                    @Override
                    public void onRespSuccess(List<HoldingOrder> holdingOrderList) {
                        mScheduleJob.performing = false;
                        mHoldingOrderList = holdingOrderList;
                        onViewShowHoldingOrderList(mHoldingOrderList);
                    }
                }).fire();
    }

    public void loadHoldingOrderList(int varietyId, int fundType) {
        if (!LocalUser.getUser().isLogin()) return;

        mVarietyId = varietyId;
        mFundType = fundType;

        API.Order.getHoldingOrderList(varietyId, fundType)
                .setCallback(new Callback2<Resp<List<HoldingOrder>>, List<HoldingOrder>>() {
                    @Override
                    public void onRespSuccess(List<HoldingOrder> holdingOrderList) {
                        mHoldingOrderList = holdingOrderList;
                        onViewShowHoldingOrderList(holdingOrderList);
                    }
                }).fire();
    }
}
