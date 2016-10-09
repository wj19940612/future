package com.jnhyxx.html5.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jnhyxx.chart.FlashView;
import com.jnhyxx.chart.TrendView;
import com.jnhyxx.chart.domain.FlashViewData;
import com.jnhyxx.chart.domain.TrendViewData;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.order.OrderActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.fragment.order.AgreementFragment;
import com.jnhyxx.html5.fragment.order.PlaceOrderFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.presenter.OrderPresenter;
import com.jnhyxx.html5.view.BuySellVolumeLayout;
import com.jnhyxx.html5.view.ChartContainer;
import com.jnhyxx.html5.view.MarketDataView;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.TradePageHeader;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TradeActivity extends BaseActivity implements
        PlaceOrderFragment.Callback,
        AgreementFragment.Callback,
        OrderPresenter.IHoldingOrderView {

    private static final int REQ_CODE_SIGN_IN = 1;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tradePageHeader)
    TradePageHeader mTradePageHeader;

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

    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.buySellVolumeLayout)
    BuySellVolumeLayout mBuySellVolumeLayout;

    @BindView(R.id.buyLongBtn)
    TextView mBuyLongBtn;
    @BindView(R.id.sellShortBtn)
    TextView mSellShortBtn;

    @BindView(R.id.holdingPositionTimeTo)
    TextView mHoldingPositionTimeTo;
    @BindView(R.id.nextTradeTime)
    TextView mNextTradeTime;
    @BindView(R.id.marketCloseArea)
    LinearLayout mMarketCloseArea;
    @BindView(R.id.marketOpenArea)
    LinearLayout mMarketOpenArea;

    @BindView(R.id.placeOrderContainer)
    FrameLayout mPlaceOrderContainer;

    private SlidingMenu mMenu;

    private Product mProduct;
    private int mFundType;
    private String mFundUnit;
    private List<Product> mProductList;
    private ExchangeStatus mExchangeStatus;
    private AnimationDrawable mQuestionMark;

    private NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveData(FullMarketData data) {
            Log.d("TEST", "onReceiveData: " + data); // TODO: 9/20/16 delete
            updateFourMainPrices(data);
            updateLastPriceView(data);
            mBuySellVolumeLayout.setVolumes(data.getAskVolume(), data.getBidVolume());
            updateChartView(data);
            mBuyLongBtn.setText(getString(R.string.buy_long)
                    + FinanceUtil.formatWithScale(data.getAskPrice(), mProduct.getPriceDecimalScale()));
            mSellShortBtn.setText(getString(R.string.sell_short)
                    + FinanceUtil.formatWithScale(data.getBidPrice(), mProduct.getPriceDecimalScale()));
            OrderPresenter.getInstance().setFullMarketData(data);
            updatePlaceOrderFragment(data);
        }
    };

    private void updatePlaceOrderFragment(FullMarketData data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeOrderContainer);
        if (fragment != null && fragment instanceof PlaceOrderFragment) {
            ((PlaceOrderFragment) fragment).setMarketData(data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        ButterKnife.bind(this);

        initData(getIntent());

        initSlidingMenu();
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.showMenu();
            }
        });
        mTradePageHeader.setOnViewClickListener(new TradePageHeader.OnViewClickListener() {
            @Override
            public void onSignInButtonClick() {
                Launcher.with(getActivity(), SignInActivity.class)
                        .executeForResult(REQ_CODE_SIGN_IN);
            }

            @Override
            public void onOrderListButtonClick() {
                Launcher.with(getActivity(), OrderActivity.class)
                        .putExtra(Product.EX_PRODUCT, mProduct)
                        .putExtra(Product.EX_FUND_TYPE, mFundType)
                        .execute();
            }

            @Override
            public void onOneKeyClosePosButtonClick() {
                OrderPresenter.getInstance().closeAllHoldingPositions(mFundType);
            }

            @Override
            public void onProfitAreaClick() {
                Launcher.with(getActivity(), OrderActivity.class)
                        .putExtra(Product.EX_PRODUCT, mProduct)
                        .putExtra(Product.EX_FUND_TYPE, mFundType)
                        .execute();
            }
        });
        mTradePageHeader.setAvailableBalanceUnit(mFundUnit);
        updateSignTradePagerHeader();
        updateProductRelatedViews();
        OrderPresenter.getInstance().loadHoldingOrderList(mProduct.getVarietyId(), mFundType);

        NettyClient.getInstance().addNettyHandler(mNettyHandler);
    }

    private void updateProductRelatedViews() {
        updateTitleBar();
        updateChartView();
        updateExchangeStatusView();
        mTradePageHeader.setTotalProfitUnit(mProduct.getCurrencyUnit());
    }

    private void updateSignTradePagerHeader() {
        if (LocalUser.getUser().isLogin()) {
            mTradePageHeader.showView(TradePageHeader.HEADER_AVAILABLE_BALANCE);
            mTradePageHeader.setAvailableBalance(
                    mFundType == Product.FUND_TYPE_CASH ?
                    LocalUser.getUser().getAvailableBalance() : LocalUser.getUser().getAvailableScore());
        } else {
            mTradePageHeader.showView(TradePageHeader.HEADER_UNLOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SIGN_IN && resultCode == RESULT_OK) {
            updateSignTradePagerHeader();
            OrderPresenter.getInstance().loadHoldingOrderList(mProduct.getVarietyId(), mFundType);
        }
    }

    private void updateTitleBar() {
        View view = mTitleBar.getCustomView();
        TextView productName = (TextView) view.findViewById(R.id.productName);
        View productRule = view.findViewById(R.id.productRule);
        productName.setText(mProduct.getVarietyName() + " " + mProduct.getContractsCode());
        productRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), WebViewActivity.class)
                        .putExtra(WebViewActivity.EX_URL, API.getTradeRule(mProduct.getVarietyType()))
                        .execute();
                Preference.get().setTradeRuleClicked(LocalUser.getUser().getPhone(), mProduct.getVarietyType());
            }
        });
        ImageView ruleIcon = (ImageView) view.findViewById(R.id.ruleIcon);
        mQuestionMark = (AnimationDrawable) ruleIcon.getBackground();
        updateQuestionMarker();
    }

    private void updateExchangeStatusView() {
        if (mExchangeStatus.getExchangeId() != mProduct.getExchangeId()) {
            API.Order.getExchangeTradeStatus(mProduct.getExchangeId(), mProduct.getVarietyType()).setTag(TAG)
                    .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                        @Override
                        public void onRespSuccess(ExchangeStatus exchangeStatus) {
                            mExchangeStatus = exchangeStatus;
                            mProduct.setExchangeStatus(exchangeStatus.isTradeable()
                                    ? Product.MARKET_STATUS_OPEN : Product.MARKET_STATUS_CLOSE);
                            updateExchangeStatusView();
                        }
                    }).fire();
        } else {
            if (mExchangeStatus.isTradeable()) {
                mMarketCloseArea.setVisibility(View.GONE);
                mMarketOpenArea.setVisibility(View.VISIBLE);
                mHoldingPositionTimeTo.setText(getString(R.string.prompt_holding_position_time_to,
                        mExchangeStatus.getNextTime()));
            } else {
                mMarketCloseArea.setVisibility(View.VISIBLE);
                mMarketOpenArea.setVisibility(View.GONE);
                mNextTradeTime.setText(getString(R.string.prompt_next_trade_time_is,
                        mExchangeStatus.getNextTime()));
            }
        }
    }

    private void initData(Intent intent) {
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mProductList = intent.getParcelableArrayListExtra(Product.EX_PRODUCT_LIST);
        mExchangeStatus = (ExchangeStatus) intent.getSerializableExtra(ExchangeStatus.EX_EXCHANGE_STATUS);
        mFundUnit = mFundType == Product.FUND_TYPE_CASH ? FinanceUtil.UNIT_YUAN : FinanceUtil.UNIT_SCORE;
    }

    private void updateChartView(FullMarketData data) {
        TrendView trendView = mChartContainer.getTrendView();
        if (trendView != null) {
            List<TrendViewData> dataList = trendView.getDataList();
            if (dataList != null && dataList.size() > 0) {
                TrendViewData lastData = dataList.get(dataList.size() - 1);
                String date = DateUtil.addOneMinute(lastData.getDate(), TrendViewData.DATE_FORMAT);
                TrendView.Settings settings = trendView.getSettings();
                if (TrendView.Util.isValidDate(date, settings.getOpenMarketTimes())) {
                    float lastPrice = (float) data.getLastPrice();
                    TrendViewData unstableData = new TrendViewData(lastData.getContractId(), lastPrice, date);
                    trendView.setUnstableData(unstableData);
                }
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateQuestionMarker();
        startScheduleJob(60 * 1000, 60 * 1000);
        OrderPresenter.getInstance().register(this);
        NettyClient.getInstance().start(mProduct.getContractsCode());
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
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
        OrderPresenter.getInstance().unregister(this);
        NettyClient.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
        mNettyHandler = null;
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
        TrendView.Settings settings = new TrendView.Settings();
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
        FlashView.Settings settings1 = new FlashView.Settings();
        settings1.setFlashChartPriceInterval(mProduct.getFlashChartPriceInterval());
        settings1.setNumberScale(mProduct.getPriceDecimalScale());
        settings1.setBaseLines(mProduct.getBaseline());
        flashView.setSettings(settings1);
        flashView.clearData();

        MarketDataView marketDataView = mChartContainer.getMarketDataView();
        if (marketDataView == null) {
            marketDataView = new MarketDataView(this);
            mChartContainer.addMarketDataView(marketDataView);
        }

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
                        if (trendView == null) return;
                        TrendView.Settings settings = trendView.getSettings();
                        trendView.setDataList(TrendView.Util.createDataList(s, settings.getOpenMarketTimes()));
                    }
                }).fire();
    }

    private void initSlidingMenu() {
        mMenu = new SlidingMenu(this);
        mMenu.setMode(SlidingMenu.RIGHT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mMenu.setMenu(R.layout.sm_behind_menu);
        ListView listView = (ListView) mMenu.getMenu();
        MenuAdapter menuAdapter = new MenuAdapter(this);
        menuAdapter.addAll(mProductList);
        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Product product = (Product) adapterView.getItemAtPosition(position);
                if (product != null) {
                    mProduct = product;
                    updateProductRelatedViews();
                    NettyClient.getInstance().stop();
                    NettyClient.getInstance().start(mProduct.getContractsCode());
                    mMenu.toggle();
                }
            }
        });
    }

    @OnClick({R.id.buyLongBtn, R.id.sellShortBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buyLongBtn:
                placeOrder(PlaceOrderFragment.TYPE_BUY_LONG);
                break;
            case R.id.sellShortBtn:
                placeOrder(PlaceOrderFragment.TYPE_SELL_SHORT);
                break;
        }
    }

    private void placeOrder(int longOrShort) {
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQ_CODE_SIGN_IN);
            return;
        }

        String userPhone = LocalUser.getUser().getPhone();
        if (Preference.get().hadShowTradeAgreement(userPhone, mProduct.getVarietyType())) {
            showPlaceOrderFragment(longOrShort);
        } else {
            showAgreementFragment(longOrShort);
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
                    .add(R.id.placeOrderContainer, PlaceOrderFragment.newInstance(longOrShort, mProduct, mFundType))
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

    private void submitOrder(final SubmittedOrder submittedOrder) {
        Log.d("TEST", "submitOrder: " + submittedOrder); // TODO: 9/20/16 delete
        API.Order.submitOrder(submittedOrder).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    public void onReceive(Resp<JsonObject> jsonObjectResp) {
                        if (jsonObjectResp.isSuccess()) {
                            hideFragmentOfContainer();
                            OrderPresenter.getInstance().loadHoldingOrderList(mProduct.getVarietyId(), mFundType);

                            SmartDialog.with(getActivity(), jsonObjectResp.getMsg())
                                    .setPositive(R.string.ok)
                                    .show();
                        } else {
                            SmartDialog.with(getActivity(), jsonObjectResp.getMsg())
                                    .setPositive(R.string.place_an_order_again,
                                            new SmartDialog.OnClickListener() {
                                                @Override
                                                public void onClick(Dialog dialog) {
                                                    submitOrder(submittedOrder);
                                                }
                                            }).setNegative(R.string.cancel)
                                    .show();
                        }
                    }
                }).fire();
    }

    @Override
    public void onConfirmBtnClick(SubmittedOrder submittedOrder) {
        submittedOrder.setPayType(mFundType);
        submitOrder(submittedOrder);
    }

    @Override
    public void onPlaceOrderFragmentEmptyAreaClick() {
        hideFragmentOfContainer();
    }

    @Override
    public void onAgreeProtocolBtnClick(int longOrShort) {
        String userPhone = LocalUser.getUser().getPhone();
        Preference.get().setTradeAgreementShowed(userPhone, mProduct.getVarietyType());
        hideFragmentOfContainer();
        placeOrder(longOrShort);
    }

    @Override
    public void onAgreementFragmentEmptyAreaClick() {
        hideFragmentOfContainer();
    }

    @Override
    public void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
    }

    @Override
    public void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (hasHoldingOrders) {
            mTradePageHeader.showView(TradePageHeader.HEADER_HOLDING_POSITION);
            mTradePageHeader.setTotalProfit(totalProfit, mProduct.isForeign(),
                    mProduct.getLossProfitScale(), ratio, mFundUnit);
        } else {
            updateSignTradePagerHeader();
        }
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
