package com.jnhyxx.html5.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jnhyxx.chart.FlashView;
import com.jnhyxx.chart.KlineChart;
import com.jnhyxx.chart.KlineView;
import com.jnhyxx.chart.TrendView;
import com.jnhyxx.chart.domain.FlashViewData;
import com.jnhyxx.chart.domain.KlineViewData;
import com.jnhyxx.chart.domain.PartialTrendHelper;
import com.jnhyxx.chart.domain.TrendViewData;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.RechargeActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.order.OrderActivity;
import com.jnhyxx.html5.activity.trade.SetLightningOrdersActivity;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.finance.SupportApplyWay;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.FuturesFinancing;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.domain.order.LightningOrderAsset;
import com.jnhyxx.html5.fragment.order.AgreementFragment;
import com.jnhyxx.html5.fragment.order.PlaceOrderFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.FontUtil;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.presenter.HoldingOrderPresenter;
import com.jnhyxx.html5.utils.presenter.IHoldingOrderView;
import com.jnhyxx.html5.view.BuySellVolumeLayout;
import com.jnhyxx.html5.view.ChartContainer;
import com.jnhyxx.html5.view.HoldingOrderView;
import com.jnhyxx.html5.view.MarketDataView;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.TradePageFooter;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.StrUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.lastPrice;

public class TradeActivity extends BaseActivity implements
        PlaceOrderFragment.Callback, AgreementFragment.Callback, IHoldingOrderView<HoldingOrder> {

    private static final int REQ_CODE_SET_LIGHTNING_ORDER_PAGE = 10000;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.priceChangeArea)
    RelativeLayout mPriceChangeArea;

    @BindView(lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.buySellVolumeLayout)
    BuySellVolumeLayout mBuySellVolumeLayout;
    @BindView(R.id.exchangeCloseText)
    TextView mExchangeCloseText;

    @BindView(R.id.openPrice)
    TextView mOpenPrice;
    @BindView(R.id.preClosePrice)
    TextView mPreClosePrice;
    @BindView(R.id.highestPrice)
    TextView mHighestPrice;
    @BindView(R.id.lowestPrice)
    TextView mLowestPrice;
    @BindView(R.id.chartContainer)
    ChartContainer mChartContainer;

    @BindView(R.id.marketStatusTime)
    TextView mMarketStatusTime;
    @BindView(R.id.tradePageFooter)
    TradePageFooter mTradePageFooter;

    @BindView(R.id.buyLongBtn)
    TextView mBuyLongBtn;
    @BindView(R.id.sellShortBtn)
    TextView mSellShortBtn;
    @BindView(R.id.lightningOrderBtn)
    ImageView mLightningOrderBtn;
    @BindView(R.id.holdingOrderView)
    HoldingOrderView mHoldingOrderView;

    private SlidingMenu mMenu;

    private Product mProduct;
    private int mFundType;
    private String mFundUnit;
    private List<Product> mProductList;
    private AnimationDrawable mQuestionMark;

    private boolean mProductChanged;
    private boolean mIsFragmentShowed;
    private boolean mIsSlidingMenuOpened;

    private HoldingOrderPresenter mHoldingOrderPresenter;

    private FullMarketData mFullMarketData;
    private ExchangeStatus mExchangeStatus;

    private NettyHandler mNettyHandler = new NettyHandler<FullMarketData>() {
        @Override
        public void onReceiveData(FullMarketData data) {
            mFullMarketData = data;
            updateFullMarketDataViews(data);
        }
    };

    private void updateFullMarketDataViews(FullMarketData data) {
        if (mIsSlidingMenuOpened) return;

        updateLastPriceView(data);
        mBuySellVolumeLayout.setVolumes(data.getAskVolume(), data.getBidVolume());

        if (mIsFragmentShowed) return;

        updateFourMainPrices(data);
        updateChartView(data);

        if (!mExchangeStatus.isTradeable()) {
            updateExchangeStatusView();
        }

        mHoldingOrderPresenter.setFullMarketData(data, mProduct.getVarietyId());
        updateBuyButtonsText(data);
    }

    private KlineView.OnAchieveTheLastListener mKlineViewOnAchieveTheLastListener
            = new KlineView.OnAchieveTheLastListener() {
        @Override
        public void onAchieveTheLast(KlineViewData data, List<KlineViewData> dataList) {
            requestKlineDataAndAppend(data);
        }
    };

    private void requestKlineDataAndAppend(KlineViewData data) {
        int type = mChartContainer.getKlineType();
        String typeStr = null;
        if (type != ChartContainer.KLINE_DAY) {
            typeStr = String.valueOf(type);
        }
        String endTime = Uri.encode(data.getTime());
        final KlineView klineView = mChartContainer.getKlineView();
        API.getKlineData(mProduct.getContractsCode(), typeStr, endTime)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<KlineViewData>>, List<KlineViewData>>() {
                    @Override
                    public void onRespSuccess(List<KlineViewData> klineDataList) {
                        if (klineView != null) {
                            if (klineDataList != null && !klineDataList.isEmpty()) {
                                Collections.reverse(klineDataList);
                                klineView.appendDataList(klineDataList);
                            } else {
                                ToastUtil.curt(R.string.there_is_no_more_data);
                            }
                        }
                    }
                }).fireSync();
    }

    //根据普通下单或者闪电下单改变买涨买跌按钮文字
    private void updateBuyButtonsText(FullMarketData data) {
        if (mLightningOrderBtn.isSelected()) {
            String lightningOrderBuyLong = getString(R.string.lightning_buy_long) + getFormattedPrice(data, true);
            mBuyLongBtn.setText(lightningOrderBuyLong);
            String lightningOrderBuyShort = getString(R.string.lightning_buy_short) + getFormattedPrice(data, false);
            mSellShortBtn.setText(lightningOrderBuyShort);
        } else {
            String buyLong = getString(R.string.buy_long) + getFormattedPrice(data, true);
            mBuyLongBtn.setText(buyLong);
            String sellShort = getString(R.string.sell_short) + getFormattedPrice(data, false);
            mSellShortBtn.setText(sellShort);
        }
    }

    private String getFormattedPrice(FullMarketData marketData, boolean askPrice) {
        if (marketData == null) return "";
        if (askPrice) {
            return FinanceUtil.formatWithScale(marketData.getAskPrice(), mProduct.getPriceDecimalScale());
        } else {
            return FinanceUtil.formatWithScale(marketData.getBidPrice(), mProduct.getPriceDecimalScale());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        updateStatusBarColor(ContextCompat.getColor(getActivity(), R.color.bluePrimary));
        ButterKnife.bind(this);

        mHoldingOrderPresenter = new HoldingOrderPresenter(this);

        initData(getIntent());
        initSlidingMenu();
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.showMenu();
            }
        });
        FontUtil.setTt0173MFont(mLastPrice);
        mTradePageFooter.setOnOneKeyClosePosButtonListener(new TradePageFooter.OnOneKeyClosePosButtonListener() {
            @Override
            public void onOneKeyClosePosButtonClick() {
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TRADE_ONE_KEY_CLOSE_OUT);
                mHoldingOrderPresenter.closeAllHoldingPositions();
            }
        });
        mTradePageFooter.setTotalProfitUnit(mProduct.getCurrencyUnit()); // based on product

        // init chart container
        mChartContainer.setOnTabClickListener(new ChartContainer.OnTabClickListener() {
            @Override
            public void onClick(int tabId) {
                switch (tabId) {
                    case ChartContainer.TAB_TREND:
                        break;
                    case ChartContainer.TAB_FULL_DAY:
                        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TIME_SHARDED);
                        break;
                    case ChartContainer.TAB_FLASH:
                        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.LIGHTNING);
                        break;
                    case ChartContainer.TAB_PLATE:
                        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HANDICAP);
                        break;
                }
            }
        });
        mChartContainer.setOnKlineClickListener(new ChartContainer.OnKlineClickListener() {
            @Override
            public void onClick(int kline) {
                switch (kline) {
                    case ChartContainer.KLINE_DAY:
                        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.DAY_K);
                        requestKlineDataAndSet(null);
                        break;
                    case ChartContainer.KLINE_THREE:
                        requestKlineDataAndSet(String.valueOf(ChartContainer.KLINE_THREE));
                        break;
                    case ChartContainer.KLINE_FIVE:
                        requestKlineDataAndSet(String.valueOf(ChartContainer.KLINE_FIVE));
                        break;
                    case ChartContainer.KLINE_TEN:
                        requestKlineDataAndSet(String.valueOf(ChartContainer.KLINE_TEN));
                        break;
                    case ChartContainer.KLINE_THIRTY:
                        requestKlineDataAndSet(String.valueOf(ChartContainer.KLINE_THIRTY));
                        break;
                    case ChartContainer.KLINE_SIXTY:
                        requestKlineDataAndSet(String.valueOf(ChartContainer.KLINE_SIXTY));
                        break;
                }
            }
        });

        updateTitleBar(); // based on product
        updateSignTradePagerHeader();
        updateChartView(); // based on product
        updateExchangeStatusView(); // based on product
        updateLightningOrderView(); // based on product
    }

    private void updateLightningOrderView() {
        if (LocalUser.getUser().isLogin()) {
            if (LightningOrderAsset.isLightningOrderOpened(mProduct, mFundType)) {
                enableLightningOrderView(true);
                compareWithWebCache();
            } else {
                enableLightningOrderView(false);
                getLightningOrderWebCache();
            }
        }
    }

    private void getLightningOrderWebCache() {
        API.Market.getOrderAssetStoreStatus(mProduct.getVarietyId(), mFundType).setTag(TAG)
                .setCallback(new Callback2<Resp<LightningOrderAsset>, LightningOrderAsset>(false) {
                    @Override
                    public void onRespSuccess(LightningOrderAsset lightningOrderAsset) {
                        if (lightningOrderAsset != null) {
                            LightningOrderAsset.setLocalLightningOrder(mProduct, mFundType, lightningOrderAsset);
                            updateLightningOrderView();
                        }
                    }
                }).fire();
    }

    private void compareWithWebCache() {
        API.Order.getFuturesFinancing(mProduct.getVarietyId(), mFundType).setTag(TAG)
                .setCallback(new Callback2<Resp<FuturesFinancing>, FuturesFinancing>() {
                    @Override
                    public void onRespSuccess(FuturesFinancing futuresFinancing) {
                        if (futuresFinancing != null) {
                            LightningOrderAsset orderAsset = LightningOrderAsset.getLocalLightningOrderAsset(mProduct, mFundType);
                            boolean isValid = orderAsset.isValid(futuresFinancing);
                            if (isValid) {
                                enableLightningOrderView(true);
                            } else {
                                enableLightningOrderView(false);
                                showLightningOrderInvalidDialog();
                                removeLightningOrder();
                            }
                        }
                    }
                }).fireSync();
    }

    private void enableLightningOrderView(boolean enable) {
        mLightningOrderBtn.setSelected(enable);
        updateBuyButtonsText(mFullMarketData);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateQuestionMarker();
        updateExchangeStatusView(); // based on product

        startScheduleJob(30 * 1000, 30 * 1000);

        NettyClient.getInstance().addNettyHandler(mNettyHandler);
        NettyClient.getInstance().start(mProduct.getContractsCode());

        mHoldingOrderPresenter.onResume();
        mHoldingOrderPresenter.loadHoldingOrderList(mProduct.getVarietyId(), mFundType);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();

        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
        NettyClient.getInstance().stop();

        mHoldingOrderPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHoldingOrderPresenter.onDestroy();
        mNettyHandler = null;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeOrderContainer);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    private void showLightningOrderInvalidDialog() {
        SmartDialog.with(getActivity(),
                getString(R.string.lightning_orders_status_run_out))
                .setPositive(R.string.ok)
                .show();
    }

    private void removeLightningOrder() {
        LightningOrderAsset.setLocalLightningOrder(mProduct, mFundType, null);
        API.Market.removeOrderAssetStoreStatus(mProduct.getVarietyId(), mFundType)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        Log.d(TAG, "removeLightningOrder: " + "remove web cache success");
                    }
                })
                .fire();
    }

    private void openOrdersPage() {
        Launcher.with(getActivity(), OrderActivity.class)
                .putExtra(Product.EX_PRODUCT, mProduct)
                .putExtra(Product.EX_FUND_TYPE, mFundType)
                .putExtra(FullMarketData.EX_MARKET_DATA, mFullMarketData)
                .execute();
    }

    private void updateSignTradePagerHeader() {
        if (LocalUser.getUser().isLogin()) {
            mTradePageFooter.showView(TradePageFooter.HEADER_AVAILABLE_BALANCE);
            mTradePageFooter.setAvailableBalance(
                    mFundType == Product.FUND_TYPE_CASH ?
                            LocalUser.getUser().getAvailableBalance() : LocalUser.getUser().getAvailableScore());
        } else {
            mTradePageFooter.showView(TradePageFooter.HEADER_UNLOGIN);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            updateSignTradePagerHeader();
            updateLightningOrderView();
        }
        //token失效重新登录回调
        if (requestCode == REQ_CODE_TOKEN_EXPIRED_LOGIN && resultCode == RESULT_OK) {
            updateLightningOrderView();
        }
        //闪电下单回调
        if (requestCode == REQ_CODE_SET_LIGHTNING_ORDER_PAGE && resultCode == RESULT_OK) {
            boolean isLightningOrderOpened = LightningOrderAsset.isLightningOrderOpened(mProduct, mFundType);
            enableLightningOrderView(isLightningOrderOpened);
            ToastUtil.curt(isLightningOrderOpened ? R.string.lightning_orders_open : R.string.lightning_orders_close);
        }
    }

    private void updateTitleBar() {
        View view = mTitleBar.getCustomView();
        TextView productName = (TextView) view.findViewById(R.id.productName);
        View productRule = view.findViewById(R.id.productRule);
        String productTitle = mFundType == Product.FUND_TYPE_CASH ? mProduct.getVarietyName() + " "
                : getString(R.string.gold) + " - " + mProduct.getVarietyName() + " ";
        productName.setText(StrUtil.mergeTextWithRatio(productTitle, mProduct.getContractsCode(), 0.75f));
        productRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.GAME_RULES);
                Launcher.with(getActivity(), WebViewActivity.class)
                        .putExtra(WebViewActivity.EX_TITLE, mProduct.getVarietyName() + getString(R.string.play_rule))
                        .putExtra(WebViewActivity.EX_URL, API.getTradeRule(mProduct.getVarietyId()))
                        .execute();
                Preference.get().setTradeRuleClicked(LocalUser.getUser().getPhone(), mProduct.getVarietyType());
            }
        });
        ImageView ruleIcon = (ImageView) view.findViewById(R.id.programmeArrow);
        mQuestionMark = (AnimationDrawable) ruleIcon.getBackground();
        updateQuestionMarker();
    }

    private void updateExchangeStatusView() {
        API.Order.getExchangeTradeStatus(mProduct.getExchangeId(), mProduct.getVarietyType())
                .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                    @Override
                    public void onRespSuccess(ExchangeStatus exchangeStatus) {
                        mProduct.setExchangeStatus(exchangeStatus.isTradeable()
                                ? Product.MARKET_STATUS_OPEN : Product.MARKET_STATUS_CLOSE);
                        mExchangeStatus = exchangeStatus;
                        if (mExchangeStatus.isTradeable()) {
                            mMarketStatusTime.setText(getString(R.string.prompt_holding_position_time_to,
                                    exchangeStatus.getNextTime()));
                            mLastPrice.setVisibility(View.VISIBLE);
                            mPriceChange.setVisibility(View.VISIBLE);
                            mBuySellVolumeLayout.setVisibility(View.VISIBLE);
                            mExchangeCloseText.setVisibility(View.INVISIBLE);
                        } else {
                            mMarketStatusTime.setText(getString(R.string.prompt_next_trade_time_is,
                                    exchangeStatus.getNextTime()));
                            mLastPrice.setVisibility(View.INVISIBLE);
                            mPriceChange.setVisibility(View.INVISIBLE);
                            mBuySellVolumeLayout.setVisibility(View.INVISIBLE);
                            mExchangeCloseText.setVisibility(View.VISIBLE);
                            updateTopPartColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                        }
                    }
                }).setTag(TAG).fireSync();
    }

    private void initData(Intent intent) {
        mProduct = intent.getParcelableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mFundUnit = (mFundType == Product.FUND_TYPE_CASH ? Unit.YUAN : Unit.GOLD);
        mProductList = intent.getParcelableArrayListExtra(Product.EX_PRODUCT_LIST);
    }

    @OnClick({R.id.buyLongBtn, R.id.sellShortBtn, R.id.lightningOrderBtn, R.id.holdingOrderView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buyLongBtn:
                if (mLightningOrderBtn.isSelected()) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.LIGHTNING_BUY_RISE_OR_BUY_DROP);
                    placeLightningOrder(LightningOrderAsset.TYPE_BUY_LONG);
                } else {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.BUY_RISE_OR_BUY_DROP);
                    placeOrder(PlaceOrderFragment.TYPE_BUY_LONG);
                }

                break;
            case R.id.sellShortBtn:
                if (mLightningOrderBtn.isSelected()) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.LIGHTNING_BUY_RISE_OR_BUY_DROP);
                    placeLightningOrder(LightningOrderAsset.TYPE_SELL_SHORT);
                } else {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.BUY_RISE_OR_BUY_DROP);
                    placeOrder(PlaceOrderFragment.TYPE_SELL_SHORT);
                }
                break;
            case R.id.lightningOrderBtn:
                MobclickAgent.onEvent(getActivity(), mLightningOrderBtn.isSelected() ? UmengCountEventIdUtils.LIGHTNING_OPEN_DOOR : UmengCountEventIdUtils.LIGHTNING_CLOSE_DOOR);
                openLightningOrdersPage();
                break;
            case R.id.holdingOrderView:
                openOrdersPage();
                break;
        }
    }

    private void openLightningOrdersPage() {
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQ_CODE_LOGIN);
            return;
        }

        if (mFundType == Product.FUND_TYPE_CASH) {
            String userPhone = LocalUser.getUser().getPhone();
            if (Preference.get().hadShowTradeAgreement(userPhone, mProduct.getVarietyType())) {
                openSetLightningOrderPage();
            } else {
                showAgreementFragment(LightningOrderAsset.TAG_OPEN_ARRGE_FRAGMENT_PAGE);
            }
        } else {
            openSetLightningOrderPage();
        }
    }

    //设置闪电下单的提交单
    private void placeLightningOrder(int buyType) {
        LightningOrderAsset orderAsset = LightningOrderAsset.getLocalLightningOrderAsset(mProduct, mFundType);
        SubmittedOrder submittedOrder = orderAsset.getSubmittedOrder(mProduct, mFundType, buyType, mFullMarketData);
        submitOrder(submittedOrder);
    }

    private void openSetLightningOrderPage() {
        Launcher.with(getActivity(), SetLightningOrdersActivity.class)
                .putExtra(Product.EX_PRODUCT, mProduct)
                .putExtra(Product.EX_FUND_TYPE, mFundType)
                .executeForResult(REQ_CODE_SET_LIGHTNING_ORDER_PAGE);

    }

    private void updateChartView(FullMarketData data) {
        TrendView trendView = mChartContainer.getTrendView();
        TrendView fullDayTrendView = mChartContainer.getFullDayTrendView();
        if (trendView != null &&  fullDayTrendView != null) {
            TrendViewData lastData = new TrendViewData(
                    data.getInstrumentId(),
                    (float) data.getLastPrice(),
                    DateUtil.format(data.getUpTime(), TrendViewData.DATE_FORMAT));

            TrendView.Settings settings = trendView.getSettings();
            settings.updateLastTrendData(lastData);
            if (TrendView.Util.isValidDate(lastData.getDate(), settings.getOpenMarketTimes())) {
                trendView.setUnstableData(lastData);
            }

            settings = fullDayTrendView.getSettings();
            if (TrendView.Util.isValidDate(lastData.getDate(), settings.getOpenMarketTimes())) {
                fullDayTrendView.setUnstableData(lastData);
            }
        }

        FlashView flashView = mChartContainer.getFlashView();
        if (flashView != null) {
            flashView.addData(new FlashViewData((float) data.getLastPrice()));
        }

        MarketDataView marketDataView = mChartContainer.getMarketDataView();
        if (marketDataView != null) {
            marketDataView.setMarketData(data, mProduct);
        }
    }

    private void updateFourMainPrices(FullMarketData data) {
        int scale = mProduct.getPriceDecimalScale();
        mOpenPrice.setText(getString(R.string.today_open, FinanceUtil.formatWithScale(data.getOpenPrice(), scale)));
        mPreClosePrice.setText(getString(R.string.pre_close, FinanceUtil.formatWithScale(data.getPreClsPrice(), scale)));
        mHighestPrice.setText(getString(R.string.highest, FinanceUtil.formatWithScale(data.getHighestPrice(), scale)));
        mLowestPrice.setText(getString(R.string.lowest, FinanceUtil.formatWithScale(data.getLowestPrice(), scale)));
    }

    private void updateLastPriceView(FullMarketData data) {
        if (mExchangeStatus != null && mExchangeStatus.isTradeable()) {
            mLastPrice.setText(FinanceUtil.formatWithScale(data.getLastPrice(), mProduct.getPriceDecimalScale()));
            double priceChangeValue = data.getLastPrice() - data.getPreSetPrice();
            double priceChangePercent = priceChangeValue / data.getPreSetPrice() * 100;
            int bgColor;
            if (priceChangeValue >= 0) {
                bgColor = ContextCompat.getColor(getActivity(), R.color.redPrimary);
                String priceChangeStr = "+" + FinanceUtil.formatWithScale(priceChangeValue, mProduct.getPriceDecimalScale())
                        + "\n+" + FinanceUtil.formatWithScale(priceChangePercent) + "%";
                mPriceChange.setText(priceChangeStr);
            } else {
                bgColor = ContextCompat.getColor(getActivity(), R.color.greenPrimary);
                String priceChangeStr = FinanceUtil.formatWithScale(priceChangeValue, mProduct.getPriceDecimalScale())
                        + "\n" + FinanceUtil.formatWithScale(priceChangePercent) + "%";
                mPriceChange.setText(priceChangeStr);
            }
            ColorDrawable colorDrawable = (ColorDrawable) mPriceChangeArea.getBackground();
            int oldBgColor = colorDrawable.getColor();
            if (bgColor != oldBgColor) {
                updateTopPartColor(bgColor);
            }
        }
    }

    private void updateTopPartColor(int bgColor) {
        mPriceChangeArea.setBackgroundColor(bgColor);
        mTitleBar.setBackgroundColor(bgColor);
        updateStatusBarColor(bgColor);
    }

    private void updateStatusBarColor(int textColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(textColor);
        }
    }

    private void updateQuestionMarker() {
        String userPhone = LocalUser.getUser().getPhone();
        if (Preference.get().isTradeRuleClicked(userPhone, mProduct.getVarietyType())) {
            mQuestionMark.stop();
        } else {
            mQuestionMark.start();
        }
    }

    @Override
    public void onTimeUp(int count) {
        requestTrendDataAndSet();
    }

    private void updateChartView() {
        TrendView trendView = mChartContainer.getTrendView();
        if (trendView == null) {
            trendView = new TrendView(this);
            mChartContainer.addTrendView(trendView);
        }
        trendView.clearData();
        TrendView.Settings settings = new TrendView.Settings();
        settings.setBaseLines(mProduct.getBaseline());
        settings.setNumberScale(mProduct.getPriceDecimalScale());
        settings.setOpenMarketTimes(mProduct.getOpenMarketTime());
        settings.setPartialTrendHelper(new PartialTrendHelper());
        settings.setLimitUpPercent((float) mProduct.getLimitUpPercent());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        trendView.setSettings(settings);

        trendView = mChartContainer.getFullDayTrendView();
        if (trendView == null) {
            trendView = new TrendView(this);
            mChartContainer.addFullDayTrendView(trendView);
        }
        trendView.clearData();
        settings = new TrendView.Settings();
        settings.setBaseLines(mProduct.getBaseline());
        settings.setNumberScale(mProduct.getPriceDecimalScale());
        settings.setOpenMarketTimes(mProduct.getOpenMarketTime());
        settings.setDisplayMarketTimes(mProduct.getDisplayMarketTimes());
        settings.setLimitUpPercent((float) mProduct.getLimitUpPercent());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        trendView.setSettings(settings);

        FlashView flashView = mChartContainer.getFlashView();
        if (flashView == null) {
            flashView = new FlashView(this);
            mChartContainer.addFlashView(flashView);
        }
        flashView.clearData();
        FlashView.Settings settings1 = new FlashView.Settings();
        settings1.setFlashChartPriceInterval(mProduct.getFlashChartPriceInterval());
        settings1.setNumberScale(mProduct.getPriceDecimalScale());
        settings1.setBaseLines(mProduct.getBaseline());
        flashView.setSettings(settings1);

        MarketDataView marketDataView = mChartContainer.getMarketDataView();
        if (marketDataView == null) {
            marketDataView = new MarketDataView(this);
            mChartContainer.addMarketDataView(marketDataView);
        }
        marketDataView.clearData();

        KlineView klineView = mChartContainer.getKlineView();
        if (klineView == null) {
            klineView = new KlineView(this);
            mChartContainer.addKlineView(klineView);
            klineView.setOnAchieveTheLastListener(mKlineViewOnAchieveTheLastListener);
        }
        klineView.clearData();
        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(mProduct.getBaseline());
        settings2.setNumberScale(mProduct.getPriceDecimalScale());
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        klineView.setSettings(settings2);

        mChartContainer.showTrendView();

        // request Trend Data
        requestTrendDataAndSet();
    }

    private void requestTrendDataAndSet() {
        API.getTrendData(mProduct.getVarietyType())
                .setCallback(new Callback<String>() {
                    @Override
                    public void onReceive(String s) {
                        TrendView trendView = mChartContainer.getTrendView();
                        TrendView.Settings settings = trendView.getSettings();
                        List<TrendViewData> data = TrendView.Util.createDataList(s);
                        if (data != null && !data.isEmpty()) {
                            settings.updateLastTrendData(data.get(data.size() - 1));
                            settings.setPreClosePrice(data.get(0).getLastPrice());
                        }
                        trendView.setDataList(data);

                        trendView = mChartContainer.getFullDayTrendView();
                        settings = trendView.getSettings();
                        data = TrendView.Util.createDataList(s);
                        if (data != null && !data.isEmpty()) {
                            settings.setPreClosePrice(data.get(0).getLastPrice());
                        }
                        trendView.setDataList(data);
                    }
                }).fireSync();
    }

    private void requestKlineDataAndSet(final String type) {
        final KlineView klineView = mChartContainer.getKlineView();
        klineView.clearData();
        API.getKlineData(mProduct.getContractsCode(), type, null)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<KlineViewData>>, List<KlineViewData>>() {
                    @Override
                    public void onRespSuccess(List<KlineViewData> klineDataList) {
                        if (klineDataList != null && klineView != null) {
                            if (TextUtils.isEmpty(type)) { // dayK
                                klineView.setDayLine(true);
                            } else {
                                klineView.setDayLine(false);
                            }
                            Collections.reverse(klineDataList);
                            klineView.setDataList(klineDataList);
                        }
                    }
                }).fireSync();
    }

    private void initSlidingMenu() {
        mMenu = new SlidingMenu(this);
        mMenu.setMode(SlidingMenu.RIGHT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mMenu.setMenu(R.layout.sm_behind_menu);
        mMenu.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                mIsSlidingMenuOpened = true;
            }
        });
        mMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                mIsSlidingMenuOpened = false;

                if (mProductChanged) {
                    hideFragmentOfContainer();

                    updateTopPartColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    updateExchangeStatusView(); // based on product
                    updateChartView(); // based on product

                    mHoldingOrderPresenter.clearData();
                    mHoldingOrderPresenter.loadHoldingOrderList(mProduct.getVarietyId(), mFundType);

                    NettyClient.getInstance().start(mProduct.getContractsCode());
                    mProductChanged = false;
                }
            }
        });
        ListView listView = (ListView) mMenu.getMenu();
        MenuAdapter menuAdapter = new MenuAdapter(this);
        menuAdapter.addAll(mProductList);
        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Product product = (Product) adapterView.getItemAtPosition(position);
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.MENU_SELECT_PRODUCT);
                if (product != null) {
                    if (product.getVarietyId() == mProduct.getVarietyId()) {
                        mProductChanged = false;
                        mMenu.toggle();
                    } else {
                        mProduct = product;

                        mProductChanged = true;
                        mMenu.toggle();
                        mFullMarketData = null;

                        updateTitleBar(); // based on product
                        mTradePageFooter.setTotalProfitUnit(mProduct.getCurrencyUnit()); // based on product
                        updateLightningOrderView(); // based on product

                        NettyClient.getInstance().stop();
                    }
                }
            }
        });
    }

    private void placeOrder(int longOrShort) {
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQ_CODE_LOGIN);
            return;
        }

        if (mFundType == Product.FUND_TYPE_CASH) {
            String userPhone = LocalUser.getUser().getPhone();
            if (Preference.get().hadShowTradeAgreement(userPhone, mProduct.getVarietyType())) {
                showPlaceOrderFragment(longOrShort);
            } else {
                showAgreementFragment(longOrShort);
            }
        } else {
            showPlaceOrderFragment(longOrShort);
        }
    }

    private void showAgreementFragment(int longOrShort) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeOrderContainer);
        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.placeOrderContainer, AgreementFragment.newInstance(longOrShort))
                    .commit();
        }
    }

    private void showPlaceOrderFragment(int longOrShort) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeOrderContainer);
        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.placeOrderContainer,
                            PlaceOrderFragment.newInstance(longOrShort, mProduct, mFundType, mFullMarketData))
                    .commit();
        }
    }

    private void hideFragmentOfContainer() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeOrderContainer);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void submitOrder(final SubmittedOrder submittedOrder) {
        API.Order.submitOrder(submittedOrder).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    public void onReceive(Resp<JsonObject> jsonObjectResp) {
                        if (jsonObjectResp.isSuccess()) {
                            hideFragmentOfContainer();
                            SmartDialog.with(getActivity(), jsonObjectResp.getMsg())
                                    .setCancelListener(new SmartDialog.OnCancelListener() {
                                        @Override
                                        public void onCancel(Dialog dialog) {
                                            dialog.dismiss();
                                            openOrdersPage();
                                        }
                                    })
                                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                        @Override
                                        public void onClick(Dialog dialog) {
                                            dialog.dismiss();
                                            openOrdersPage();
                                        }
                                    })
                                    .show();
                            mHoldingOrderPresenter.loadHoldingOrderList(mProduct.getVarietyId(), mFundType);
                        } else if (jsonObjectResp.getCode() == Resp.CODE_FUND_NOT_ENOUGH) {
                            showFundNotEnoughDialog(jsonObjectResp);
                        } else if (jsonObjectResp.getCode() == Resp.CODE_FUND_NOT_ENOUGH_AND_PART_DEAL) {
                            showFundNotEnoughDialog(jsonObjectResp);
                            mHoldingOrderPresenter.loadHoldingOrderList(mProduct.getVarietyId(), mFundType);
                        } else if (jsonObjectResp.getCode() == Resp.CODE_LIGHTNING_ORDER_INVALID) {
                            enableLightningOrderView(false);
                            showLightningOrderInvalidDialog();
                            removeLightningOrder();
                        } else {
                            SmartDialog.with(getActivity(), jsonObjectResp.getMsg())
                                    .setPositive(R.string.ok)
                                    .show();
                        }
                    }
                }).fire();
    }

    private void showFundNotEnoughDialog(Resp<JsonObject> jsonObjectResp) {
        if (mFundType == Product.FUND_TYPE_CASH) {
            SmartDialog.with(getActivity(), jsonObjectResp.getMsg())
                    .setPositive(R.string.go_to_recharge,
                            new SmartDialog.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    dialog.dismiss();
                                    openRechargePage();
                                }
                            }).setNegative(R.string.cancel)
                    .show();
        } else {
            SmartDialog.with(getActivity(), jsonObjectResp.getMsg())
                    .setPositive(R.string.ok)
                    .show();
        }
    }

    private void openRechargePage() {
        API.Finance.getSupportApplyWay()
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<SupportApplyWay>, SupportApplyWay>() {
                    @Override
                    public void onRespSuccess(SupportApplyWay supportApplyWay) {
                        Log.d(TAG, "-----" + supportApplyWay.toString());
                        if (supportApplyWay.isBank() || supportApplyWay.isAlipay() || supportApplyWay.isWechat()) {
                            MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.RECHARGE);
                            Launcher.with(getActivity(), RechargeActivity.class).putExtra(Launcher.EX_PAYLOAD, supportApplyWay).execute();
                        } else {
                            SmartDialog.with(getActivity(), R.string.now_is_not_support_recharge).show();
                        }
                    }
                }).fire();
    }

    @Override
    public void onPlaceOrderFragmentConfirmBtnClick(SubmittedOrder submittedOrder) {
        submittedOrder.setPayType(mFundType);
        submitOrder(submittedOrder);
    }

    @Override
    public void onPlaceOrderFragmentEmptyAreaClick() {
        hideFragmentOfContainer();
    }

    @Override
    public void onPlaceOrderFragmentShow() {
        mIsFragmentShowed = true;
    }

    @Override
    public void onPlaceOrderFragmentExited() {
        mIsFragmentShowed = false;
    }

    @Override
    public void onAgreementFragmentAgreeBtnClick(int longOrShort) {
        String userPhone = LocalUser.getUser().getPhone();
        Preference.get().setTradeAgreementShowed(userPhone, mProduct.getVarietyType());
        if (longOrShort == LightningOrderAsset.TAG_OPEN_ARRGE_FRAGMENT_PAGE) {
            hideFragmentOfContainer();
            openLightningOrdersPage();
            return;
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeOrderContainer);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.placeOrderContainer, PlaceOrderFragment.newInstance(longOrShort, mProduct, mFundType, mFullMarketData))
                    .commit();
        }
    }

    @Override
    public void onAgreementFragmentEmptyAreaClick() {
        hideFragmentOfContainer();
    }

    @Override
    public void onAgreementFragmentShow() {
        mIsFragmentShowed = true;
    }

    @Override
    public void onAgreementFragmentExited() {
        mIsFragmentShowed = false;
    }

    @Override
    public void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
        if (holdingOrderList != null) {
            mHoldingOrderView.setOrderNumber(holdingOrderList.size());
        }

        updateUsableMoneyScore(new LocalUser.Callback() {
            @Override
            public void onUpdateCompleted() {
            }
        });
    }

    @Override
    public void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (hasHoldingOrders) {
            mTradePageFooter.showView(TradePageFooter.HEADER_HOLDING_POSITION);
            mTradePageFooter.setTotalProfit(totalProfit, mProduct.isForeign(),
                    mProduct.getLossProfitScale(), ratio, mFundUnit);
        } else {
            updateSignTradePagerHeader();
        }
    }

    @Override
    public void onSubmitAllHoldingOrdersCompleted(String message) {
        SmartDialog.with(getActivity(),
                getString(R.string.sell_order_submit_successfully) + "\n" + message)
                .setPositive(R.string.ok)
                .show();
    }

    @Override
    public void onSubmitHoldingOrderCompleted(HoldingOrder holdingOrder) {
    }

    @Override
    public void onRiskControlTriggered(String closingOrders, String orderSplit, String stopLossSplit) {
    }


    static class MenuAdapter extends ArrayAdapter<Product> {

        public MenuAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_sliding_menu, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.productName)
            TextView mProductName;
            @BindView(R.id.productCode)
            TextView mProductCode;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Product item) {
                mProductName.setText(item.getVarietyName());
                mProductCode.setText(item.getVarietyType());
            }
        }
    }
}
