package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.domain.order.StopProfitLossConfig;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.BlurEngine;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.StopProfitLossPicker;
import com.johnz.kutils.FinanceUtil;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SetStopProfitLossFragment extends BaseFragment {

    public interface Callback {
        void onSetStopProfitLossFragmentCloseTriggered();

        void onSetStopProfitLossFragmentConfirmed(HoldingOrder order, double newStopLossPrice, double newStopProfitPrice);
    }

    private static final String ORDER = "order";
    private static final String STOP_CONFIG = "stopConfig";

    @BindView(R.id.emptyClickArea)
    View mEmptyClickArea;

    @BindView(R.id.buyOrSell)
    TextView mBuyOrSell;
    @BindView(R.id.hands)
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

    @BindView(R.id.stopLossPicker)
    StopProfitLossPicker mStopLossPicker;
    @BindView(R.id.stopProfitPicker)
    StopProfitLossPicker mStopProfitPicker;

    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.confirmSetting)
    TextView mConfirmSetting;

    private HoldingOrder mHoldingOrder;
    private Product mProduct;
    private int mFundType;
    private String mFundUnit;
    private FullMarketData mMarketData;
    private StopProfitLossConfig mStopProfitLossConfig;

    private Unbinder mBinder;
    private BlurEngine mBlurEngine;
    private Callback mCallback;
    private boolean mIsShowing;

    @OnClick({R.id.emptyClickArea, R.id.cancel, R.id.confirmSetting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.emptyClickArea:
                if (mCallback != null) {
                    mCallback.onSetStopProfitLossFragmentCloseTriggered();
                }
                break;
            case R.id.cancel:
                if (mCallback != null) {
                    mCallback.onSetStopProfitLossFragmentCloseTriggered();
                }
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.SET_STOP_PROFIT_STOP_LOSS_CANCEL);
                break;
            case R.id.confirmSetting:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.SET_STOP_PROFIT_STOP_LOSS_OK);
                double newStopLossPrice = mStopLossPicker.getPrice();
                double newStopProfitPrice = mStopProfitPicker.getPrice();
                if (mCallback != null) {
                    mCallback.onSetStopProfitLossFragmentConfirmed(mHoldingOrder, newStopLossPrice, newStopProfitPrice);
                }
                break;
        }
    }

    private NettyHandler mNettyHandler = new NettyHandler<FullMarketData>() {
        @Override
        public void onReceiveData(FullMarketData data) {
            mMarketData = data;
            if (mIsShowing || !isVisible()) return;
            updateLastPriceAndProfit();
            updatePricePickers();
        }
    };

    private void updatePricePickers() {
        if (mMarketData == null) return;

        if (mHoldingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
            mStopLossPicker.setLastPrice(mMarketData.getBidPrice());
            mStopProfitPicker.setLastPrice(mMarketData.getBidPrice());
        } else {
            mStopLossPicker.setLastPrice(mMarketData.getAskPrice());
            mStopProfitPicker.setLastPrice(mMarketData.getAskPrice());
        }
    }

    private void updateLastPriceAndProfit() {
        if (mMarketData == null) return;

        int priceScale = mProduct.getPriceDecimalScale();
        int profitScale = mProduct.getLossProfitScale();
        double ratio = mHoldingOrder.getRatio();
        BigDecimal eachPointMoney = new BigDecimal(mHoldingOrder.getEachPointMoney());
        BigDecimal diff;
        if (mHoldingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
            mLastPrice.setText(FinanceUtil.formatWithScale(mMarketData.getBidPrice(), priceScale));
            diff = FinanceUtil.subtraction(mMarketData.getBidPrice(), mHoldingOrder.getRealAvgPrice());
        } else {
            mLastPrice.setText(FinanceUtil.formatWithScale(mMarketData.getAskPrice(), priceScale));
            diff = FinanceUtil.subtraction(mHoldingOrder.getRealAvgPrice(), mMarketData.getAskPrice());
        }
        diff = diff.multiply(eachPointMoney);

        String lossProfitStr;
        String lossProfitRmbStr;
        double diffRmb = diff.multiply(new BigDecimal(ratio)).doubleValue();
        if (diff.doubleValue() >= 0) {
            mLossProfit.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
            lossProfitStr = "+" + FinanceUtil.formatWithScale(diff.doubleValue(), profitScale);
        } else {
            mLossProfit.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenPrimary));
            lossProfitStr = FinanceUtil.formatWithScale(diff.doubleValue(), profitScale);
        }
        mLossProfit.setText(lossProfitStr);
        if (mProduct.isForeign()) {
            lossProfitRmbStr = "(" + FinanceUtil.formatWithScale(Math.abs(diffRmb)) + mFundUnit + ")";
            mLossProfitRmb.setText(lossProfitRmbStr);
        } else {
            mLossProfitRmb.setText("");
        }
    }

    public static SetStopProfitLossFragment newInstance(Product product, int fundType,
                                                        HoldingOrder order,
                                                        FullMarketData marketData,
                                                        StopProfitLossConfig stopProfitLossConfig) {
        SetStopProfitLossFragment fragment = new SetStopProfitLossFragment();
        Bundle args = new Bundle();
        args.putParcelable(Product.EX_PRODUCT, product);
        args.putInt(Product.EX_FUND_TYPE, fundType);
        args.putParcelable(FullMarketData.EX_MARKET_DATA, marketData);
        args.putParcelable(ORDER, order);
        args.putParcelable(STOP_CONFIG, stopProfitLossConfig);
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
                    + " must implement SetStopProfitLossFragment.Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = getArguments().getParcelable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
            mFundUnit = (mFundType == Product.FUND_TYPE_CASH ? Unit.YUAN : Unit.GOLD);
            mMarketData = getArguments().getParcelable(FullMarketData.EX_MARKET_DATA);
            mHoldingOrder = getArguments().getParcelable(ORDER);
            mStopProfitLossConfig = getArguments().getParcelable(STOP_CONFIG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBlurEngine = new BlurEngine(container, R.color.blackHalfTransparent);
        View view = inflater.inflate(R.layout.fragment_set_profit_loss, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurEngine.onResume();
        NettyClient.getInstance().addNettyHandler(mNettyHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBlurEngine.onDestroyView();
        mBinder.unbind();
        mNettyHandler = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHoldingOrderViews();
        initStopProfitLossPickers();

        updatePricePickers();
        updateLastPriceAndProfit();
    }

    private void initHoldingOrderViews() {
        mHands.setText(mHoldingOrder.getHandsNum() + "手");
        if (mHoldingOrder.getDirection() == HoldingOrder.DIRECTION_LONG) {
            mBuyOrSell.setText(R.string.buy_long);
            mBuyOrSell.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
            mHands.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
        } else {
            mBuyOrSell.setText(R.string.sell_short);
            mBuyOrSell.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenPrimary));
            mHands.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenPrimary));
        }
        mBuyPrice.setText(FinanceUtil.formatWithScale(mHoldingOrder.getRealAvgPrice(), mProduct.getPriceDecimalScale()));

        String stopProfit = FinanceUtil.formatWithScale(mHoldingOrder.getStopWinMoney(), mProduct.getPriceDecimalScale())
                + "  (" + FinanceUtil.formatWithScale(mHoldingOrder.getStopWin(), mProduct.getLossProfitScale())
                + mProduct.getCurrencyUnit() + ")";
        mStopProfit.setText(stopProfit);

        String stopLoss = FinanceUtil.formatWithScale(mHoldingOrder.getStopLossMoney(), mProduct.getPriceDecimalScale())
                + "  (" + FinanceUtil.formatWithScale(mHoldingOrder.getStopLoss(), mProduct.getLossProfitScale())
                + mProduct.getCurrencyUnit() + ")";
        mStopLoss.setText(stopLoss);
        mLossProfit.setText("+0");
        mLossProfitRmb.setText("(0" + mFundUnit + ")");
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
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mIsShowing = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private void initStopProfitLossPickers() {
        mStopLossPicker.setConfig(new StopProfitLossPicker.Config(
                true,
                mHoldingOrder.getDirection(),
                mHoldingOrder.getRealAvgPrice(),
                mStopProfitLossConfig.getBeatFewPoints(),
                mHoldingOrder.getStopLossMoney(), mHoldingOrder.getStopWinMoney(),
                mStopProfitLossConfig.getFirstStopLossPrice(), mStopProfitLossConfig.getHighestStopProfitPrice(),
                mStopProfitLossConfig.getStopLoseOffsetPoint(), mStopProfitLossConfig.getStopWinOffsetPoint(),
                mProduct.getEachPointMoney(),
                mProduct.getPriceDecimalScale(),
                mProduct.getLossProfitScale()
        ));
        mStopProfitPicker.setConfig(new StopProfitLossPicker.Config(
                false,
                mHoldingOrder.getDirection(),
                mHoldingOrder.getRealAvgPrice(),
                mStopProfitLossConfig.getBeatFewPoints(),
                mHoldingOrder.getStopLossMoney(), mHoldingOrder.getStopWinMoney(),
                mStopProfitLossConfig.getFirstStopLossPrice(), mStopProfitLossConfig.getHighestStopProfitPrice(),
                mStopProfitLossConfig.getStopLoseOffsetPoint(), mStopProfitLossConfig.getStopWinOffsetPoint(),
                mProduct.getEachPointMoney(),
                mProduct.getPriceDecimalScale(),
                mProduct.getLossProfitScale()
        ));
    }

    public HoldingOrder getBeingSetOrder() {
        return mHoldingOrder;
    }
}
