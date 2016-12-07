package com.jnhyxx.html5.utils.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

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
import java.math.RoundingMode;
import java.util.List;

public class HoldingOrderPresenter {

    private static final String TAG = "VolleyHttp";

    private static final int LOAD_DATA = 0;
    private static final int QUERY_DATA = 1;
    private static final int RISK_CONTROL = 2;
    private static final int CLOSE_POSITION = 3;
    private static final int UPDATE_ONLY = 4;

    private static class QueryJob {
        public int count;
        public boolean startQuery;
        public int varietyId;
        public int fundType;

        public void set(int varietyId, int fundType) {
            this.varietyId = varietyId;
            this.fundType = fundType;
        }
    }

    public HoldingOrderPresenter(IHoldingOrderView iHoldingOrderView) {
        mIHoldingOrderView = iHoldingOrderView;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                getHoldingList(msg.what, msg.arg1, msg.obj);
            }
        };
        mQueryJob = new QueryJob();
    }

    private void getHoldingList(final int what, final int varietyId, final Object obj) {
        Log.d(TAG, "getHoldingList: varietyId: " + mQueryJob.varietyId);
        final API api = API.Order.getHoldingOrderList(mQueryJob.varietyId, mQueryJob.fundType);
        api.setCallback(new Callback2<Resp<List<HoldingOrder>>, List<HoldingOrder>>() {
            @Override
            public void onRespSuccess(List<HoldingOrder> holdingOrderList) {
                if (mPause) return;
                Log.d(TAG, "getHoldingList finished: varietyId: " + mQueryJob.varietyId
                        + ", startQuery: " + mQueryJob.startQuery);

                mHoldingOrderList = holdingOrderList;
                onShowHoldingOrderList(holdingOrderList);

                if (what == LOAD_DATA) {
                    Log.d(TAG, "onRespSuccess: LOAD_DATA");
                    setFullMarketData(mMarketData, mQueryJob.varietyId);
                    startQuery();
                }

                if (what == QUERY_DATA && mQueryJob.startQuery) {
                    Log.d(TAG, "onRespSuccess: QUERY_DATA: " + mQueryJob.count);
                    mQueryJob.count++;
                    query();
                }

                if (what == RISK_CONTROL && varietyId == mQueryJob.varietyId) {
                    Log.d(TAG, "onRespSuccess: RISK_CONTROL");
                    String closingOrderIds = (String) obj;
                    String[] showIds = closingOrderIds.split(";");
                    boolean refresh = false;
                    for (HoldingOrder order : holdingOrderList) {
                        for (String showId : showIds) {
                            if (showId.equals(order.getShowId())
                                    && order.getOrderStatus() == HoldingOrder.ORDER_STATUS_HOLDING) {
                                refresh = true;
                            }
                        }
                    }
                    if (refresh) {
                        Log.d(TAG, "继续风控刷新");
                        Message message = mHandler.obtainMessage(RISK_CONTROL, varietyId, -1, closingOrderIds);
                        mHandler.sendMessageDelayed(message, 2 * 1000);
                    } else {
                        if (mQueryJob.startQuery && mHandler.hasMessages(QUERY_DATA)) {
                            stopQuery();
                            startQuery();
                        } else if (!mQueryJob.startQuery) {
                            startQuery();
                        }
                    }
                }

                if (what == CLOSE_POSITION && varietyId == mQueryJob.varietyId) {
                    Log.d(TAG, "onRespSuccess: CLOSE_POSITION");
                    if (mQueryJob.startQuery && mHandler.hasMessages(QUERY_DATA)) {
                        stopQuery();
                        startQuery();
                    } else if (!mQueryJob.startQuery) {
                        startQuery();
                    }
                }

                if (what == UPDATE_ONLY) {
                    Log.d(TAG, "onRespSuccess: UPDATE_ONLY");
                }
            }
        });
        if (what == LOAD_DATA || what == UPDATE_ONLY || what == RISK_CONTROL) {
            api.fireSync();
        } else {
            api.fire();
        }
    }

    public void startQuery() {
        Log.d(TAG, "startQuery: ");
        mQueryJob.startQuery = true;
        query();
    }

    public void query() {
        boolean refresh = false;
        for (HoldingOrder holdingOrder : mHoldingOrderList) {
            int orderStatus = holdingOrder.getOrderStatus();
            // 存在处理中的订单,买处理中(代持有),卖处理中(平仓中), 使用 "策略" 刷新持仓数据
            if (orderStatus == HoldingOrder.ORDER_STATUS_PAID_UNHOLDING
                    || orderStatus == HoldingOrder.ORDER_STATUS_CLOSING) {
                refresh = true;
                break;
            }
        }

        Log.d(TAG, "need query?: " + refresh + ", varietyId: " + mQueryJob.varietyId +
                ", queryList.size: " + mHoldingOrderList.size());

        if (refresh && mQueryJob.startQuery) {
            queryDelay();
        } else {
            stopQuery();
        }
    }

    public void stopQuery() {
        Log.d(TAG, "stopQuery: ");
        mQueryJob.startQuery = false;
        mQueryJob.count = 0;
        mHandler.removeMessages(QUERY_DATA);
    }

    /**
     * 查询策略:
     * <p/>
     * 500ms 间隔, 刷新10次
     * 2s 间隔, 刷新20次(10 + 20)
     * 8s 间隔, 刷新30次(30 + 30)
     * 32s 间隔, 刷新60次(60 + 60)
     */
    private void queryDelay() {
        if (mHandler == null) return;

        Message message = mHandler.obtainMessage(QUERY_DATA, mQueryJob.varietyId, -1);
        if (mQueryJob.count == 0) {
            mHandler.sendMessage(message);
        } else if (mQueryJob.count < 10) {
            mHandler.sendMessageDelayed(message, 5 * 100);
        } else if (mQueryJob.count < 30) {
            mHandler.sendMessageDelayed(message, 2 * 1000);
        } else if (mQueryJob.count < 60) {
            mHandler.sendMessageDelayed(message, 8 * 1000);
        } else if (mQueryJob.count < 120) {
            mHandler.sendMessageDelayed(message, 32 * 1000);
        }
    }

    public void loadHoldingOrderList(final int varietyId, final int fundType) {
        if (!LocalUser.getUser().isLogin()) return;

        Log.d(TAG, "loadHoldingOrderList: " + varietyId + ", fund: " + fundType);

        mQueryJob.set(varietyId, fundType);
        mQueryJob.startQuery = false;
        mHandler.sendEmptyMessage(LOAD_DATA);
    }

    public void updateHolingOrderListOnly() {
        mHandler.sendEmptyMessage(UPDATE_ONLY);
    }

    private FullMarketData mMarketData;
    private List<HoldingOrder> mHoldingOrderList;

    private IHoldingOrderView mIHoldingOrderView;
    private Handler mHandler;
    private QueryJob mQueryJob;
    private boolean mPause;

    public void onResume() {
        mPause = false;
    }

    public void onPause() {
        mPause = true;
        if (mHoldingOrderList != null) {
            mHoldingOrderList.clear();
        }
        mMarketData = null;
        stopQuery();
    }

    public void onDestroy() {
        mHandler = null;
    }

    public void closeAllHoldingPositions() {
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
            API.Order.closeAllHoldingOrders(showIds.toString(), mQueryJob.fundType, unwindPrices.toString())
                    .setCallback(new Callback1<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            setOrderListStatus(HoldingOrder.ORDER_STATUS_CLOSING, mHoldingOrderList);
                            onSubmitAllHoldingPositionsCompleted(resp.getMsg());
                            mHandler.sendMessage(mHandler.obtainMessage(CLOSE_POSITION, mQueryJob.varietyId, -1));
                        }
                    }).fire();
        }
    }

    private void setOrderListStatus(int orderStatus, List<HoldingOrder> holdingOrderList) {
        for (HoldingOrder holdingOrder : holdingOrderList) {
            holdingOrder.setOrderStatus(orderStatus);
        }
    }

    public void closePosition(final HoldingOrder order) {
        double unwindPrice = 0;
        if (mMarketData != null) {
            if (order.getDirection() == HoldingOrder.DIRECTION_LONG) {
                unwindPrice = mMarketData.getBidPrice();
            } else {
                unwindPrice = mMarketData.getAskPrice();
            }
        }

        API.Order.closeHoldingOrder(order.getShowId(), mQueryJob.fundType, unwindPrice)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        order.setOrderStatus(HoldingOrder.ORDER_STATUS_CLOSING);
                        onSubmitHoldingOrderCompleted(order);
                        mHandler.sendMessage(mHandler.obtainMessage(CLOSE_POSITION, mQueryJob.varietyId, -1));
                    }
                }).fireSync();
    }

    public void clearData() {
        mMarketData = null;
        if (mHoldingOrderList != null) {
            mHoldingOrderList.clear();
        }
    }

    public void setFullMarketData(FullMarketData marketData, int varietyId) {
        mMarketData = marketData;

        BigDecimal totalProfit = new BigDecimal(0);
        boolean hasHoldingOrders = false;
        double ratio = 0;
        boolean refresh = false;
        StringBuilder closingOrderIds = new StringBuilder();

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
                    BigDecimal bigDecimalStopProfit = new BigDecimal(holdingOrder.getStopWin());
                    if (orderStatus == HoldingOrder.ORDER_STATUS_HOLDING && diff.compareTo(bigDecimalStopProfit) >= 0) {
                        refresh = true;
                        holdingOrder.setOrderStatus(HoldingOrder.ORDER_STATUS_CLOSING);
                        closingOrderIds.append(holdingOrder.getShowId()).append(";");
                    }
                    if (orderStatus == HoldingOrder.ORDER_STATUS_HOLDING && diff.compareTo(bigDecimalStopLoss) <= 0) {
                        refresh = true;
                        holdingOrder.setOrderStatus(HoldingOrder.ORDER_STATUS_CLOSING);
                        closingOrderIds.append(holdingOrder.getShowId()).append(";");
                    }

                    totalProfit = totalProfit.add(diff);
                }
            }
        }


        onShowTotalProfit(hasHoldingOrders, totalProfit.doubleValue(), ratio);

        if (refresh) { // 触及风控刷新
            Log.d(TAG, "触及风控刷新");
            String showIds = closingOrderIds.deleteCharAt(closingOrderIds.length() - 1).toString();
            mHandler.sendMessage(mHandler.obtainMessage(RISK_CONTROL, varietyId, -1, showIds));
            onRiskControlTriggered(showIds);
        }
    }

    private void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (mIHoldingOrderView != null) {
            mIHoldingOrderView.onShowTotalProfit(hasHoldingOrders, totalProfit, ratio);
        }
    }

    private void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
        if (mIHoldingOrderView != null) {
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

    private void onRiskControlTriggered(String showIds) {
        if (mIHoldingOrderView != null) {
            mIHoldingOrderView.onRiskControlTriggered(showIds);
        }
    }
}
