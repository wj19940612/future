package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.FuturesFinancing;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.BlurEngine;
import com.jnhyxx.html5.view.BuySellVolumeLayout;
import com.jnhyxx.html5.view.OrderConfigurationSelector;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.StrUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PlaceOrderFragment extends BaseFragment {

    private static final String TYPE = "longOrShort";
    public static final int TYPE_BUY_LONG = 1;
    public static final int TYPE_SELL_SHORT = 0;

    @BindView(R.id.tradeQuantitySelector)
    OrderConfigurationSelector mTradeQuantitySelector;
    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.buySellVolumeLayout)
    BuySellVolumeLayout mBuySellVolumeLayout;
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
    @BindView(R.id.emptyClickArea)
    View mEmptyClickArea;
    @BindView(R.id.bottomSplitLine)
    View mBottomSplitLine;
    @BindView(R.id.marketCloseText)
    TextView mMarketCloseText;
    @BindView(R.id.marketOpenArea)
    RelativeLayout mMarketOpenArea;

    private int mLongOrShort;
    private Product mProduct;
    private int mFundType;
    private FuturesFinancing mFuturesFinancing;
    private SubmittedOrder mSubmittedOrder;
    private FullMarketData mMarketData;

    private Unbinder mBinder;
    private BlurEngine mBlurEngine;
    private Callback mCallback;

    public static PlaceOrderFragment newInstance(int longOrShort, Product product, int fundType) {
        PlaceOrderFragment fragment = new PlaceOrderFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, longOrShort);
        args.putSerializable(Product.EX_PRODUCT, product);
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
                    + " must implement OnBuyBtnClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLongOrShort = getArguments().getInt(TYPE, 0);
            mProduct = (Product) getArguments().getSerializable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBlurEngine = new BlurEngine(container, R.color.blackHalfTransparent);
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

        API.Order.getFuturesFinancing(mProduct.getVarietyId(), mFundType).setTag(TAG)
                .setCallback(new Callback2<Resp<FuturesFinancing>, FuturesFinancing>() {
                    @Override
                    public void onRespSuccess(FuturesFinancing futuresFinancing) {
                        mFuturesFinancing = futuresFinancing;
                        updatePlaceOrderViews();
                    }
                }).fire();

        updateRateAndMarketTimeView();
        updateBuyAskPriceBgAndConfirmBtn();
    }

    public void setMarketData(FullMarketData data) {
        mMarketData = data;
        updateMarketDataRelatedView();
    }

    private void updateMarketDataRelatedView() {
        if (!isAdded() || mMarketData == null) return;

        mBuySellVolumeLayout.setVolumes(mMarketData.getAskVolume(), mMarketData.getBidVolume());
        updateLastPriceView(mMarketData);
        mLastBidAskPrice.setText(mLongOrShort == TYPE_BUY_LONG ?
                FinanceUtil.formatWithScale(mMarketData.getAskPrice(), mProduct.getPriceDecimalScale()) :
                FinanceUtil.formatWithScale(mMarketData.getBidPrice(), mProduct.getPriceDecimalScale()));
    }

    private void updateLastPriceView(FullMarketData data) {
        mLastPrice.setText(FinanceUtil.formatWithScale(data.getLastPrice(), mProduct.getPriceDecimalScale()));
        double priceChangeValue = data.getLastPrice() - data.getPreSetPrice();
        double priceChangePercent = priceChangeValue / data.getPreSetPrice() * 100;
        int textColor;
        if (priceChangeValue >= 0) {
            textColor = ContextCompat.getColor(getActivity(), R.color.redPrimary);
            mLastPrice.setTextColor(textColor);
            mPriceChange.setTextColor(textColor);
            String priceChangeStr = "+" + FinanceUtil.formatWithScale(priceChangeValue, mProduct.getPriceDecimalScale())
                    + "\n+" + FinanceUtil.formatWithScale(priceChangePercent) + "%";
            mPriceChange.setText(priceChangeStr);
        } else {
            textColor = ContextCompat.getColor(getActivity(), R.color.greenPrimary);
            mLastPrice.setTextColor(textColor);
            mPriceChange.setTextColor(textColor);
            String priceChangeStr = FinanceUtil.formatWithScale(priceChangeValue, mProduct.getPriceDecimalScale())
                    + "\n" + FinanceUtil.formatWithScale(priceChangePercent) + "%";
            mPriceChange.setText(priceChangeStr);
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
        mBlurEngine.onResume();
    }

    private void updateRateAndMarketTimeView() {
        if (mProduct.isForeign()) {
            mRateAndMarketTime.setText(getString(R.string.currency_converter,
                    "1" + mProduct.getCurrencyUnit() + "=" + mProduct.getRatio() + FinanceUtil.UNIT_YUAN));
        }

        API.Order.getExchangeTradeStatus(mProduct.getExchangeId(), mProduct.getVarietyType()).setTag(TAG)
                .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                    @Override
                    public void onRespSuccess(ExchangeStatus exchangeStatus) {
                        String marketTimeStr;
                        if (exchangeStatus.isTradeable()) {
                            marketTimeStr = getString(R.string.prompt_holding_position_time_to_then_close,
                                    exchangeStatus.getNextTime());

                            mMarketOpenArea.setVisibility(View.VISIBLE);
                            mMarketCloseText.setVisibility(View.GONE);
                        } else {
                            marketTimeStr = getString(R.string.prompt_next_trade_time_is,
                                    exchangeStatus.getNextTime());

                            mMarketOpenArea.setVisibility(View.GONE);
                            mMarketCloseText.setVisibility(View.VISIBLE);
                            mMarketCloseText.setText(marketTimeStr);
                        }
                        String rateAndMarketTimeStr = mRateAndMarketTime.getText().toString();
                        if (TextUtils.isEmpty(rateAndMarketTimeStr)) {
                            rateAndMarketTimeStr = marketTimeStr;
                        } else {
                            rateAndMarketTimeStr += "  " + marketTimeStr;
                        }
                        mRateAndMarketTime.setText(rateAndMarketTimeStr);
                    }
                }).fire();
    }

    private void updateMarginTradeFeeAndTotal(FuturesFinancing.TradeQuantity tradeQuantity) {
        // DOMESTIC
        String marginWithSign = mProduct.getSign() + tradeQuantity.getMargin();
        String tradeFeeWithSign = mProduct.getSign() + tradeQuantity.getFee();
        String totalWithSign = mProduct.getSign() + (tradeQuantity.getMargin() + tradeQuantity.getFee());
        mMargin.setText(marginWithSign);
        mTradeFee.setText(tradeFeeWithSign);
        mTotalTobePaid.setText(totalWithSign);

        if (mProduct.isForeign() && mFuturesFinancing != null) {
            double ratio = mFuturesFinancing.getRatio();
            String marginRmb = "  ( " + FinanceUtil.UNIT_SIGN_CNY +
                    FinanceUtil.formatWithScale(tradeQuantity.getMargin() * ratio) + " )";
            mMargin.setText(
                    StrUtil.mergeTextWithColor(marginWithSign, marginRmb, Color.parseColor("#666666"))
            );
            String tradeFeeRmb = "  ( " + FinanceUtil.UNIT_SIGN_CNY +
                    FinanceUtil.formatWithScale(tradeQuantity.getFee() * ratio) + " )";
            mTradeFee.setText(
                    StrUtil.mergeTextWithColor(tradeFeeWithSign, tradeFeeRmb, Color.parseColor("#666666"))
            );
            String totalRmb = FinanceUtil.UNIT_SIGN_CNY
                    + FinanceUtil.formatWithScale((tradeQuantity.getMargin() + tradeQuantity.getFee()) * mProduct.getRatio());
            String totalForeign = "  ( " + totalWithSign + " )";
            mTotalTobePaid.setText(
                    StrUtil.mergeTextWithColor(totalRmb, totalForeign, Color.parseColor("#666666"))
            );
        }
    }

    private void updatePlaceOrderViews() {
        if (isRemoving() || !isAdded()) return;
        // 设置止损
        mTouchStopLossSelector.setOrderConfigurationList(mFuturesFinancing.getStopLossList(mProduct));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBlurEngine.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                if (mCallback != null) {
                    mCallback.onConfirmBtnClick(mSubmittedOrder);
                }
                break;
        }
    }

    public interface Callback {
        void onConfirmBtnClick(SubmittedOrder submittedOrder);

        void onPlaceOrderFragmentEmptyAreaClick();
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation animation;

        if (enter) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        } else {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        }

        return animation;
    }
}
