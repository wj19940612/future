package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.domain.order.StopProfitLossActive;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.FontUtil;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.presenter.HoldingOrderPresenter;
import com.jnhyxx.html5.utils.presenter.IHoldingOrderView;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.buyOrSell;
import static com.jnhyxx.html5.R.id.hands;

public class HoldingFragment extends BaseFragment implements IHoldingOrderView<HoldingOrder> {

    public interface Callback {
        void onClosePositionEventTriggered(String showIds);
        void onSetStopProfitLossClick(HoldingOrder order);
    }

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(R.id.totalProfitAndUnit)
    TextView mTotalProfitAndUnit;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.oneKeyClosePositionBtn)
    TextView mOneKeyClosePositionBtn;
    @BindView(android.R.id.empty)
    LinearLayout mEmpty;
    @BindView(R.id.lossProfitArea)
    LinearLayout mLossProfitArea;
    @BindView(R.id.totalProfitRmb)
    TextView mTotalProfitRmb;

    private Unbinder mBinder;

    private Product mProduct;
    private int mFundType;
    private HoldingOrderAdapter mHoldingOrderAdapter;
    private String mFundUnit;
    private boolean mShowStopProfitLoss;

    private HoldingOrderPresenter mPresenter;
    private Callback mCallback;

    private NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveData(FullMarketData data) {
            mPresenter.setFullMarketData(data, mProduct.getVarietyId());
            if (mHoldingOrderAdapter != null) {
                mHoldingOrderAdapter.setFullMarketData(data);
            }
            if (mHoldingOrderAdapter != null && mHoldingOrderAdapter.getCount() > 0) {
                updateHoldingOrderVisibleItems(data);
            }
        }
    };

    private void updateHoldingOrderVisibleItems(FullMarketData data) {
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        for (int i = first; i <= last; i++) {
            HoldingOrder holdingOrder = (HoldingOrder) mHoldingOrderAdapter.getItem(i);
            if (holdingOrder != null) {
                View itemView = mList.getChildAt(i - mList.getFirstVisiblePosition());
                TextView lastPrice = ButterKnife.findById(itemView, R.id.lastPrice);
                TextView lossProfit = ButterKnife.findById(itemView, R.id.lossProfit);
                TextView lossProfitRmb = ButterKnife.findById(itemView, R.id.lossProfitRmb);

                int priceScale = mProduct.getPriceDecimalScale();
                int profitScale = mProduct.getLossProfitScale();
                double ratio = holdingOrder.getRatio();
                BigDecimal eachPointMoney = new BigDecimal(holdingOrder.getEachPointMoney());
                BigDecimal diff;
                if (holdingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
                    lastPrice.setText(FinanceUtil.formatWithScale(data.getBidPrice(), priceScale));
                    diff = FinanceUtil.subtraction(data.getBidPrice(), holdingOrder.getRealAvgPrice());
                } else {
                    lastPrice.setText(FinanceUtil.formatWithScale(data.getAskPrice(), priceScale));
                    diff = FinanceUtil.subtraction(holdingOrder.getRealAvgPrice(), data.getAskPrice());
                }
                diff = diff.multiply(eachPointMoney);

                String lossProfitStr;
                String lossProfitRmbStr;
                double diffRmb = diff.multiply(new BigDecimal(ratio)).doubleValue();
                if (diff.doubleValue() >= 0) {
                    lossProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
                    lossProfitStr = "+" + FinanceUtil.formatWithScale(diff.doubleValue(), profitScale);
                } else {
                    lossProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenPrimary));
                    lossProfitStr = FinanceUtil.formatWithScale(diff.doubleValue(), profitScale);
                }
                lossProfit.setText(lossProfitStr);
                if (mProduct.isForeign()) {
                    lossProfitRmbStr = "(" + FinanceUtil.formatWithScale(Math.abs(diffRmb)) + mFundUnit + ")";
                    lossProfitRmb.setText(lossProfitRmbStr);
                }
            }
        }
    }

    public static HoldingFragment newInstance(Product product, int fundType) {
        HoldingFragment fragment = new HoldingFragment();
        Bundle args = new Bundle();
        args.putParcelable(Product.EX_PRODUCT, product);
        args.putInt(Product.EX_FUND_TYPE, fundType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HoldingFragment.Call");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = getArguments().getParcelable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
            mFundUnit = (mFundType == Product.FUND_TYPE_CASH ? Unit.YUAN : Unit.GOLD);
            mPresenter = new HoldingOrderPresenter(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holding, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
        mPresenter.loadHoldingOrderList(mProduct.getVarietyId(), mFundType);
        NettyClient.getInstance().addNettyHandler(mNettyHandler);
        getStopProfitLossActive();
    }

    private void getStopProfitLossActive() {
        API.Order.getStopProfitLossActive(mProduct.getVarietyId(), mFundType).setTag(TAG)
                .setCallback(new com.jnhyxx.html5.net.Callback<Resp<StopProfitLossActive>>() {
                    @Override
                    public void onReceive(Resp<StopProfitLossActive> stopProfitLossConfigResp) {
                        if (stopProfitLossConfigResp.isSuccess() && stopProfitLossConfigResp.hasData()) {
                            mShowStopProfitLoss = stopProfitLossConfigResp.getData().isActive();
                            if (mHoldingOrderAdapter != null) {
                                mHoldingOrderAdapter.setShowStopProfitLoss(mShowStopProfitLoss);
                            }
                        }
                    }
                }).fire();
    }

    @Override
    public void onPause() {
        super.onPause();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
        mPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mNettyHandler = null;
        mBinder.unbind();
        mPresenter.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FontUtil.setTt0173MFont(mTotalProfit);

        mList.setEmptyView(mEmpty);
        mTotalProfitAndUnit.setText(getString(R.string.holding_position_total_profit_and_unit,
                mProduct.getCurrencyUnit()));
    }

    @Override
    public void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
        if (holdingOrderList != null) {
            if (mHoldingOrderAdapter == null) {
                mHoldingOrderAdapter = new HoldingOrderAdapter(getContext(), mProduct, mFundUnit, holdingOrderList);
                mHoldingOrderAdapter.setCallback(new HoldingOrderAdapter.Callback() {
                    @Override
                    public void onItemClosePositionClick(HoldingOrder order) {
                        mPresenter.closePosition(order);
                        onClosePositionEventTriggered(order.getShowId());
                    }

                    @Override
                    public void onSetStopProfitLossClick(HoldingOrder order) {
                        if (mCallback != null) {
                            mCallback.onSetStopProfitLossClick(order);
                        }
                    }
                });
                mList.setAdapter(mHoldingOrderAdapter);
            } else {
                mHoldingOrderAdapter.setHoldingOrderList(holdingOrderList);
            }
            mHoldingOrderAdapter.setShowStopProfitLoss(mShowStopProfitLoss);
        }
    }

    private void onClosePositionEventTriggered(String showIds) {
        if (mCallback != null) {
            mCallback.onClosePositionEventTriggered(showIds);
        }
    }

    @Override
    public void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (hasHoldingOrders) {
            if (mLossProfitArea.getVisibility() == View.GONE) {
                mLossProfitArea.setVisibility(View.VISIBLE);
            }

            int scale = mProduct.getLossProfitScale();
            String totalProfitStr;
            String totalProfitRmbStr;
            if (totalProfit >= 0) {
                mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
                mOneKeyClosePositionBtn.setBackgroundResource(R.drawable.btn_red_one_key_close);
                totalProfitStr = "+" + FinanceUtil.formatWithScale(totalProfit, scale);
            } else {
                mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenPrimary));
                mOneKeyClosePositionBtn.setBackgroundResource(R.drawable.btn_green_one_key_close);
                totalProfitStr = FinanceUtil.formatWithScale(totalProfit, scale);
            }
            mTotalProfit.setText(totalProfitStr);
            if (mProduct.isForeign()) {
                totalProfitRmbStr = FinanceUtil.formatWithScale(Math.abs(totalProfit * ratio));
                mTotalProfitRmb.setText("(" + totalProfitRmbStr + mFundUnit + ")");
            }
        } else {
            if (mLossProfitArea.getVisibility() == View.VISIBLE) {
                mLossProfitArea.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSubmitAllHoldingPositionsCompleted(String message) {
        SmartDialog.with(getActivity(),
                getString(R.string.sell_order_submit_successfully) + "\n" + message)
                .setPositive(R.string.ok)
                .show();
        if (mHoldingOrderAdapter != null) {
            mHoldingOrderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSubmitHoldingOrderCompleted(HoldingOrder holdingOrder) {
        ToastUtil.center(R.string.sell_order_submit_successfully, R.dimen.toast_offset);
        if (mHoldingOrderAdapter != null) {
            mHoldingOrderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRiskControlTriggered(String showIds) {
        onClosePositionEventTriggered(showIds);
    }

    @OnClick(R.id.oneKeyClosePositionBtn)
    public void onClick() {
        mPresenter.closeAllHoldingPositions();
        onClosePositionEventTriggered("");
    }

    public void updateHoldingOrderList() {
        if (mPresenter != null) {
            mPresenter.updateHolingOrderListOnly();
        }
    }

    static class HoldingOrderAdapter extends BaseAdapter {

        public interface Callback {
            void onItemClosePositionClick(HoldingOrder order);
            void onSetStopProfitLossClick(HoldingOrder order);
        }

        private Context mContext;
        private Product mProduct;
        private String mFundUnit;

        private List<HoldingOrder> mHoldingOrderList;
        private FullMarketData mFullMarketData;
        private Callback mCallback;
        private boolean mShowStopProfitLoss;

        public HoldingOrderAdapter(Context context, Product product, String fundUnit, List<HoldingOrder> holdingOrderList) {
            mContext = context;
            mProduct = product;
            mFundUnit = fundUnit;
            mHoldingOrderList = holdingOrderList;
        }

        public void setFullMarketData(FullMarketData fullMarketData) {
            mFullMarketData = fullMarketData;
        }

        public void setShowStopProfitLoss(boolean showStopProfitLoss) {
            mShowStopProfitLoss = showStopProfitLoss;
        }

        public void setHoldingOrderList(List<HoldingOrder> holdingOrderList) {
            mHoldingOrderList = holdingOrderList;
            notifyDataSetChanged();
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public int getCount() {
            return mHoldingOrderList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position >= mHoldingOrderList.size()) return null;
            return mHoldingOrderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_holding_order, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData((HoldingOrder) getItem(position), mContext,
                    mProduct, mFundUnit,
                    mFullMarketData, mShowStopProfitLoss,
                    mCallback);

            return convertView;
        }

        static class ViewHolder {
            @BindView(buyOrSell)
            TextView mBuyOrSell;
            @BindView(hands)
            TextView mHands;
            @BindView(R.id.lossProfit)
            TextView mLossProfit;
            @BindView(R.id.lossProfitRmb)
            TextView mLossProfitRmb;
            @BindView(R.id.buyPrice)
            TextView mBuyPrice;
            @BindView(R.id.stopProfit)
            TextView mStopProfit;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.stopLoss)
            TextView mStopLoss;
            @BindView(R.id.closePositionButton)
            TextView mClosePositionButton;
            @BindView(R.id.orderStatus)
            TextView mOrderStatus;
            @BindView(R.id.setStopLossStopProfit)
            TextView mSetStopLossStopProfit;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final HoldingOrder item, Context context,
                                    Product product, String fundUnit,
                                    FullMarketData data, boolean showStopProfitLoss,
                                    final Callback callback) {

                mBuyPrice.setText(FinanceUtil.formatWithScale(item.getRealAvgPrice(), product.getPriceDecimalScale()));
                String stopProfit = FinanceUtil.formatWithScale(item.getStopWinMoney(), product.getPriceDecimalScale())
                        + "  (" + FinanceUtil.formatWithScale(item.getStopWin(), product.getLossProfitScale())
                        + product.getCurrencyUnit() + ")";
                mStopProfit.setText(stopProfit);
                String stopLoss = FinanceUtil.formatWithScale(item.getStopLossMoney(), product.getPriceDecimalScale())
                        + "  (" + FinanceUtil.formatWithScale(item.getStopLoss(), product.getLossProfitScale())
                        + product.getCurrencyUnit() + ")";
                mStopLoss.setText(stopLoss);
                mHands.setText(item.getHandsNum() + "手");
                mClosePositionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onItemClosePositionClick(item);
                        }
                    }
                });
                mSetStopLossStopProfit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onSetStopProfitLossClick(item);
                        }
                    }
                });
                if (item.getDirection() == HoldingOrder.DIRECTION_LONG) {
                    mBuyOrSell.setText(R.string.buy_long);
                    mBuyOrSell.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                    mHands.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                } else {
                    mBuyOrSell.setText(R.string.sell_short);
                    mBuyOrSell.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                    mHands.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                }
                if (item.getOrderStatus() == HoldingOrder.ORDER_STATUS_HOLDING) {
                    mClosePositionButton.setVisibility(View.VISIBLE);
                    mOrderStatus.setVisibility(View.GONE);
                } else {
                    mClosePositionButton.setVisibility(View.GONE);
                    mOrderStatus.setVisibility(View.VISIBLE);
                    if (item.getOrderStatus() < HoldingOrder.ORDER_STATUS_HOLDING) {
                        mOrderStatus.setText(R.string.buying);
                    } else if (item.getOrderStatus() > HoldingOrder.ORDER_STATUS_HOLDING) {
                        mOrderStatus.setText(R.string.selling);
                    }
                }

                if (showStopProfitLoss && item.getOrderStatus() == HoldingOrder.ORDER_STATUS_HOLDING) {
                    mSetStopLossStopProfit.setVisibility(View.VISIBLE);
                } else {
                    mSetStopLossStopProfit.setVisibility(View.GONE);
                }

                // views will change
                if (data != null) {
                    int priceScale = product.getPriceDecimalScale();
                    int profitScale = product.getLossProfitScale();
                    double ratio = item.getRatio();
                    BigDecimal eachPointMoney = new BigDecimal(item.getEachPointMoney());
                    BigDecimal diff;
                    if (item.getDirection() == HoldingOrder.DIRECTION_LONG) {
                        mLastPrice.setText(FinanceUtil.formatWithScale(data.getBidPrice(), priceScale));
                        diff = FinanceUtil.subtraction(data.getBidPrice(), item.getRealAvgPrice());
                    } else {
                        mLastPrice.setText(FinanceUtil.formatWithScale(data.getAskPrice(), priceScale));
                        diff = FinanceUtil.subtraction(item.getRealAvgPrice(), data.getAskPrice());
                    }
                    diff = diff.multiply(eachPointMoney);

                    String lossProfitStr;
                    String lossProfitRmbStr;
                    double diffRmb = diff.multiply(new BigDecimal(ratio)).doubleValue();
                    if (diff.doubleValue() >= 0) {
                        mLossProfit.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        lossProfitStr = "+" + FinanceUtil.formatWithScale(diff.doubleValue(), profitScale);
                    } else {
                        mLossProfit.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                        lossProfitStr = FinanceUtil.formatWithScale(diff.doubleValue(), profitScale);
                    }
                    mLossProfit.setText(lossProfitStr);
                    if (product.isForeign()) {
                        lossProfitRmbStr = "(" + FinanceUtil.formatWithScale(Math.abs(diffRmb)) + fundUnit + ")";
                        mLossProfitRmb.setText(lossProfitRmbStr);
                    } else {
                        mLossProfitRmb.setText("");
                    }
                } else {
                    mLastPrice.setText("");
                    mLossProfit.setText("");
                    mLossProfitRmb.setText("");
                }
            }
        }
    }
}
