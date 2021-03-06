package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.FuturesFinancing;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.OrderConfigurationSelector;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.StrUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PlaceOrderFragment extends BaseFragment {

    public interface Callback {
        void onPlaceOrderFragmentConfirmBtnClick(SubmittedOrder submittedOrder);

        void onPlaceOrderFragmentEmptyAreaClick();

        void onPlaceOrderFragmentShow();

        void onPlaceOrderFragmentExited();
    }

    private static final String TYPE = "longOrShort";
    public static final int TYPE_BUY_LONG = 1;
    public static final int TYPE_SELL_SHORT = 0;


    @BindView(R.id.emptyClickArea)
    View mEmptyClickArea;

    @BindView(R.id.tradeQuantitySelector)
    OrderConfigurationSelector mTradeQuantitySelector;
    @BindView(R.id.touchStopLossSelector)
    OrderConfigurationSelector mTouchStopLossSelector;
    @BindView(R.id.touchStopProfitSelector)
    OrderConfigurationSelector mTouchStopProfitSelector;

    @BindView(R.id.margin)
    TextView mMargin;
    @BindView(R.id.tradeFee)
    TextView mTradeFee;

    @BindView(R.id.rateAndMarketTime)
    TextView mRateAndMarketTime;

    @BindView(R.id.totalTobePaid)
    TextView mTotalTobePaid;
    @BindView(R.id.lastBidAskPrice)
    TextView mLastBidAskPrice;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;
    @BindView(R.id.lastBidAskPriceBg)
    LinearLayout mLastBidAskPriceBg;
    @BindView(R.id.bottomSplitLine)
    View mBottomSplitLine;

    private int mLongOrShort;
    private Product mProduct;
    private int mFundType;
    private FuturesFinancing mFuturesFinancing;
    private SubmittedOrder mSubmittedOrder;
    private FullMarketData mMarketData;
    private ExchangeStatus mExchangeStatus;
    private boolean mIsShowing;

    private Unbinder mBinder;
    private Callback mCallback;

    private NettyHandler mNettyHandler = new NettyHandler<FullMarketData>() {
        @Override
        public void onReceiveData(FullMarketData data) {
            mMarketData = data;
            if (mIsShowing || !isVisible()) return;
            updateMarketDataRelatedView();
            updateSubmittedOrder();
        }
    };

    public static PlaceOrderFragment newInstance(int longOrShort, Product product, int fundType,
                                                 FullMarketData marketData) {
        PlaceOrderFragment fragment = new PlaceOrderFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, longOrShort);
        args.putParcelable(Product.EX_PRODUCT, product);
        args.putInt(Product.EX_FUND_TYPE, fundType);
        args.putParcelable(FullMarketData.EX_MARKET_DATA, marketData);
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
                    + " must implement PlaceOrderFragment.Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLongOrShort = getArguments().getInt(TYPE, 0);
            mProduct = getArguments().getParcelable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
            mMarketData = getArguments().getParcelable(FullMarketData.EX_MARKET_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_order, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSubmittedOrder = new SubmittedOrder(mProduct.getVarietyId(), mLongOrShort);

        mTradeQuantitySelector.setOnItemSelectedListener(new OrderConfigurationSelector.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                if (configuration instanceof FuturesFinancing.TradeQuantity) {
                    FuturesFinancing.TradeQuantity tradeQuantity = (FuturesFinancing.TradeQuantity) configuration;
                    mSubmittedOrder.setHandsNum(tradeQuantity.getQuantity());
                    updateMarginTradeFeeAndTotal(tradeQuantity);
                }
            }
        });
        mTouchStopLossSelector.setOnItemSelectedListener(new OrderConfigurationSelector.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                if (configuration instanceof FuturesFinancing.StopLoss) {

                    FuturesFinancing.StopLoss stopLoss = (FuturesFinancing.StopLoss) configuration;

                    // 设置止盈
                    List<FuturesFinancing.StopProfit> stopProfitList = stopLoss.getStopProfitList();
                    mTouchStopProfitSelector.setOrderConfigurationList(stopProfitList);

                    // 设置手数
                    List<FuturesFinancing.TradeQuantity> tradeQuantityList = stopLoss.getTradeQuantityList();
                    mTradeQuantitySelector.setOrderConfigurationList(tradeQuantityList);

                    mSubmittedOrder.setAssetsId(stopLoss.getAssetsBean().getAssetsId());
                }
            }
        });
        mTouchStopProfitSelector.setOnItemSelectedListener(new OrderConfigurationSelector.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                if (configuration instanceof FuturesFinancing.StopProfit) {
                    FuturesFinancing.StopProfit stopProfit = (FuturesFinancing.StopProfit) configuration;
                    mSubmittedOrder.setStopProfitPoint(stopProfit.getStopProfitPoint());
                }
            }
        });

        //获取期货配资方案
        API.Order.getFuturesFinancing(mProduct.getVarietyId(), mFundType).setTag(TAG)
                .setCallback(new Callback2<Resp<FuturesFinancing>, FuturesFinancing>() {
                    @Override
                    public void onRespSuccess(FuturesFinancing futuresFinancing) {
                        mFuturesFinancing = futuresFinancing;
                        if (mIsShowing) return;
                        updatePlaceOrderViews();
                    }
                }).fire();


        API.Order.getExchangeTradeStatus(mProduct.getExchangeId(), mProduct.getVarietyType()).setTag(TAG)
                .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                    @Override
                    public void onRespSuccess(ExchangeStatus exchangeStatus) {
                        mExchangeStatus = exchangeStatus;
                        if (mIsShowing) return;
                        updateRateAndMarketTimeView();
                    }
                }).fire();

        updateMarketDataRelatedView();
        updateSubmittedOrder();
        updateBuyAskPriceBgAndConfirmBtn();
    }


    private void updateSubmittedOrder() {
        if (mSubmittedOrder != null && mMarketData != null) {
            if (mSubmittedOrder.getDirection() == TYPE_BUY_LONG) {
                mSubmittedOrder.setOrderPrice(mMarketData.getAskPrice());
            } else {
                mSubmittedOrder.setOrderPrice(mMarketData.getBidPrice());
            }
        }
    }

    private void updateMarketDataRelatedView() {
        if (mMarketData != null) {
            mLastBidAskPrice.setText(mLongOrShort == TYPE_BUY_LONG ?
                    FinanceUtil.formatWithScale(mMarketData.getAskPrice(), mProduct.getPriceDecimalScale()) :
                    FinanceUtil.formatWithScale(mMarketData.getBidPrice(), mProduct.getPriceDecimalScale()));
        }
    }

    private void updateBuyAskPriceBgAndConfirmBtn() {
        mConfirmButton.setText(mLongOrShort == TYPE_BUY_LONG ?
                R.string.confirm_buy_long : R.string.confirm_sell_short);
        mLastBidAskPriceBg.setBackgroundResource(mLongOrShort == TYPE_BUY_LONG ?
                R.color.redPrimary : R.color.greenPrimary);
        mConfirmButton.setBackgroundResource(mLongOrShort == TYPE_BUY_LONG ?
                R.drawable.btn_red_primary : R.drawable.btn_green_primary);
        mBottomSplitLine.setBackgroundColor(mLongOrShort == TYPE_BUY_LONG ?
                Color.parseColor("#FD6C73") : Color.parseColor("#47D690"));
    }

    @Override
    public void onResume() {
        super.onResume();
        NettyClient.getInstance().addNettyHandler(mNettyHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
    }

    private void updateRateAndMarketTimeView() {
        if (mProduct.isForeign()) {
            mRateAndMarketTime.setText(getString(R.string.currency_converter,
                    "1" + mProduct.getCurrencyUnit() + "=" + mProduct.getRatio() + Unit.YUAN));
        }

        String marketTimeStr;
        if (mExchangeStatus.isTradeable()) {
            marketTimeStr = getString(R.string.prompt_holding_position_time_to_then_close,
                    mExchangeStatus.getNextTime());
        } else {
            marketTimeStr = getString(R.string.prompt_next_trade_time_is,
                    mExchangeStatus.getNextTime());
        }
        String rateAndMarketTimeStr = mRateAndMarketTime.getText().toString();
        if (TextUtils.isEmpty(rateAndMarketTimeStr)) {
            rateAndMarketTimeStr = marketTimeStr;
        } else {
            rateAndMarketTimeStr += "  " + marketTimeStr;
        }
        mRateAndMarketTime.setText(rateAndMarketTimeStr);
    }

    private void updateMarginTradeFeeAndTotal(FuturesFinancing.TradeQuantity tradeQuantity) {
        // DOMESTIC
        String marginWithSign = mProduct.getSign() + FinanceUtil.formatWithScaleNoZero(tradeQuantity.getMargin());
        String tradeFeeWithSign = mProduct.getSign() + FinanceUtil.formatWithScaleNoZero(tradeQuantity.getFee());
        String totalWithSign = mProduct.getSign() + FinanceUtil.formatWithScaleNoZero(tradeQuantity.getMargin() + tradeQuantity.getFee());
        mMargin.setText(marginWithSign);
        mTradeFee.setText(tradeFeeWithSign);
        mTotalTobePaid.setText(totalWithSign);

        if (mProduct.isForeign() && mFuturesFinancing != null) {
            double ratio = mFuturesFinancing.getRatio();
            String marginRmb = "  ( " + Unit.SIGN_CNY +
                    FinanceUtil.formatWithScaleNoZero(tradeQuantity.getMargin() * ratio) + " )";
            mMargin.setText(
                    StrUtil.mergeTextWithColor(marginWithSign, marginRmb, Color.parseColor("#666666"))
            );
            String tradeFeeRmb = "  ( " + Unit.SIGN_CNY +
                    FinanceUtil.formatWithScaleNoZero(tradeQuantity.getFee() * ratio) + " )";
            mTradeFee.setText(
                    StrUtil.mergeTextWithColor(tradeFeeWithSign, tradeFeeRmb, Color.parseColor("#666666"))
            );
            String totalRmb = Unit.SIGN_CNY
                    + FinanceUtil.formatWithScaleNoZero((tradeQuantity.getMargin() + tradeQuantity.getFee()) * mProduct.getRatio());
            String totalForeign = "  ( " + totalWithSign + " )";
            mTotalTobePaid.setText(
                    StrUtil.mergeTextWithColor(totalRmb, totalForeign, Color.parseColor("#666666"))
            );
        }
    }

    private void updatePlaceOrderViews() {
        // 设置止损
        mFuturesFinancing.sort();
        mTouchStopLossSelector.setOrderConfigurationList(mFuturesFinancing.getStopLossList(mProduct));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCallback != null) {
            mCallback.onPlaceOrderFragmentExited();
        }
        mCallback = null;
    }

    @OnClick({R.id.emptyClickArea, R.id.confirmButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.emptyClickArea:
                if (mCallback != null) {
                    mCallback.onPlaceOrderFragmentEmptyAreaClick();
                }
                break;
            case R.id.confirmButton:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.BUY_RISE_OR_BUY_DROP_CONFIRM);
                if (mCallback != null) {
                    mCallback.onPlaceOrderFragmentConfirmBtnClick(mSubmittedOrder);
                }
                break;
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation animation;

        if (enter) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
            animation.setAnimationListener(new EnterAnimListener());
        } else {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        }

        return animation;
    }

    private class EnterAnimListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            mIsShowing = true;
            if (mCallback != null) {
                mCallback.onPlaceOrderFragmentShow();
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mIsShowing = false;

            if (mFuturesFinancing != null) {
                updatePlaceOrderViews();
            }
            if (mExchangeStatus != null) {
                updateRateAndMarketTimeView();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
