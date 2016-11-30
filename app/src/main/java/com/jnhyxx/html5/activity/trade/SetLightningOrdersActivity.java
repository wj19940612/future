package com.jnhyxx.html5.activity.trade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.MarketServer;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.market.ProductLightningOrderStatus;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.FuturesFinancing;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.LightningOrdersArrayMap;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.OrderConfigurationSelector;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.StrUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.openLightningOrder;

/**
 * 闪电下单配置界面
 */
public class SetLightningOrdersActivity extends BaseActivity {

    public static final int RESULT_CODE_OPEN_LIGHTNING_ORDER = 4600;
    public static final int RESULT_CODE_CLOSE_LIGHTNING_ORDER = 5555;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
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


    @BindView(R.id.closeLightningOrder)
    TextView mCloseLightningOrder;
    @BindView(R.id.restartLightningOrder)
    TextView mRestartLightningOrder;
    @BindView(R.id.openLightningOrder)
    TextView mOpenLightningOrder;
    @BindView(R.id.lightningOrdersOpen)
    LinearLayout mLightningOrdersOpen;


    private List<Product> mProductList;
    private Product mProduct;
    private int mFundType;
    private FuturesFinancing mFuturesFinancing;
    private ExchangeStatus mExchangeStatus;


    private boolean mLightningOrdersStatus;

    private ProductLightningOrderStatus mProductLightningOrderStatus;
    private NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveData(FullMarketData data) {
            if (data != null) {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightning_orsers_set);
        ButterKnife.bind(this);

        mProductLightningOrderStatus = new ProductLightningOrderStatus();
        initData(getIntent());
        setTradeQuantity();
        setTouchStopLoss();
        setTouchStopPro();

        //获取期货配资方案
        getFuturesFinancing();
        getExchangeTradeStatus();
    }

    @OnClick({openLightningOrder, R.id.closeLightningOrder, R.id.restartLightningOrder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openLightningOrder:
                openLightningOrder();
                break;
            case R.id.closeLightningOrder:
                setResult(RESULT_CODE_CLOSE_LIGHTNING_ORDER);
                onBackPressed();
                break;
            case R.id.restartLightningOrder:
                mTradeQuantitySelector.setEnabled(true);
                mTouchStopLossSelector.setEnabled(true);
                mTouchStopProfitSelector.setEnabled(true);

                mRestartLightningOrder.setVisibility(View.GONE);
                mOpenLightningOrder.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void openLightningOrder() {
        Log.d(TAG, "提交的闪电下单配资 " + mProductLightningOrderStatus.toString());
        API.Market.saveAndUpdateOrderAssetStore(mProductLightningOrderStatus.getVarietyId(), mProductLightningOrderStatus.getPayType(),
                mProductLightningOrderStatus.getAssetsId(), mProductLightningOrderStatus.getHandsNum(),
                mProductLightningOrderStatus.getStopLossPrice(), mProductLightningOrderStatus.getStopWinPrice(),
                mProductLightningOrderStatus.getMarginMoney(), mProductLightningOrderStatus.getFees(), mProductLightningOrderStatus.getRatio())
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    public void onReceive(Resp<JsonObject> jsonObjectResp) {
                        if (jsonObjectResp.hasData() && jsonObjectResp.isSuccess()) {
                            //提交成功
                            ToastUtil.curt("提交成功");
                            LightningOrdersArrayMap.getInstance().setLightningOrders(mProductLightningOrderStatus);
                        } else {
                            ToastUtil.curt(jsonObjectResp.getMsg());
                        }
                    }
                })
                .fire();
    }

    private void getExchangeTradeStatus() {
        API.Order.getExchangeTradeStatus(mProduct.getExchangeId(), mProduct.getVarietyType()).setTag(TAG)
                .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                    @Override
                    public void onRespSuccess(ExchangeStatus exchangeStatus) {
                        mExchangeStatus = exchangeStatus;
                        updateRateAndMarketTimeView();
                    }
                }).fire();
    }

    private void setTouchStopPro() {
        mTouchStopProfitSelector.setOnItemSelectedListener(new OrderConfigurationSelector.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                if (configuration instanceof FuturesFinancing.StopProfit) {
                    FuturesFinancing.StopProfit stopProfit = (FuturesFinancing.StopProfit) configuration;
                    mProductLightningOrderStatus.setStopWinPrice(stopProfit.getStopProfit());
                }
            }
        });
    }

    private void getFuturesFinancing() {
        API.Order.getFuturesFinancing(mProduct.getVarietyId(), mFundType).setTag(TAG)
                .setCallback(new Callback2<Resp<FuturesFinancing>, FuturesFinancing>() {
                    @Override
                    public void onRespSuccess(FuturesFinancing futuresFinancing) {
                        mFuturesFinancing = futuresFinancing;
                        if (mFuturesFinancing != null)
                            mProductLightningOrderStatus.setRatio(mFuturesFinancing.getRatio());
                        updatePlaceOrderViews();
                    }
                }).fire();
    }

    private void setTouchStopLoss() {
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

                    mProductLightningOrderStatus.setAssetsId(stopLoss.getAssetsBean().getAssetsId());
                    mProductLightningOrderStatus.setStopLossPrice(((FuturesFinancing.StopLoss) configuration).getAssetsBean().getStopLossBeat());
                }
            }
        });
    }

    private void setTradeQuantity() {
        mTradeQuantitySelector.setOnItemSelectedListener(new OrderConfigurationSelector.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                if (configuration instanceof FuturesFinancing.TradeQuantity) {
                    FuturesFinancing.TradeQuantity tradeQuantity = (FuturesFinancing.TradeQuantity) configuration;
                    mProductLightningOrderStatus.setHandsNum(tradeQuantity.getQuantity());
                    updateMarginTradeFeeAndTotal(tradeQuantity);
                }
            }
        });
    }

    private void initData(Intent intent) {
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mProductList = intent.getParcelableArrayListExtra(Product.EX_PRODUCT_LIST);
        mLightningOrdersStatus = intent.getBooleanExtra(ProductLightningOrderStatus.KEY_LIGHTNING_ORDER_IS_OPEN, false);
        MarketServer mMarketServer = (MarketServer) intent.getSerializableExtra(MarketServer.EX_MARKET_SERVER);
        NettyClient.getInstance().setIpAndPort(mMarketServer.getIp(), mMarketServer.getPort());

        if (mProduct != null) {
            mProductLightningOrderStatus.setVarietyId(mProduct.getVarietyId());
            mProductLightningOrderStatus.setPayType(mFundType);
        }

        setLayoutStatus();
    }

    private void setLayoutStatus() {
        //如果是开启状态
        if (mLightningOrdersStatus) {
            mTradeQuantitySelector.setEnabled(false);
            mTouchStopLossSelector.setEnabled(false);
            mTouchStopProfitSelector.setEnabled(false);

            mOpenLightningOrder.setVisibility(View.GONE);
            mCloseLightningOrder.setVisibility(View.VISIBLE);
            mRestartLightningOrder.setVisibility(View.VISIBLE);
        } else {
            mOpenLightningOrder.setVisibility(View.VISIBLE);
            mCloseLightningOrder.setVisibility(View.GONE);
            mRestartLightningOrder.setVisibility(View.GONE);
        }
    }

    private void updatePlaceOrderViews() {
        // 设置止损
        mFuturesFinancing.sort();
        mTouchStopLossSelector.setOrderConfigurationList(mFuturesFinancing.getStopLossList(mProduct));
    }

    private void updateMarginTradeFeeAndTotal(FuturesFinancing.TradeQuantity tradeQuantity) {
        // DOMESTIC
        String marginWithSign = mProduct.getSign() + tradeQuantity.getMargin();
        String tradeFeeWithSign = mProduct.getSign() + tradeQuantity.getFee();
        String totalWithSign = mProduct.getSign() + (tradeQuantity.getMargin() + tradeQuantity.getFee());
        mMargin.setText(marginWithSign);
        mProductLightningOrderStatus.setMarginMoney(tradeQuantity.getMargin());
        mProductLightningOrderStatus.setFees(tradeQuantity.getFee());
        mTradeFee.setText(tradeFeeWithSign);
        mTotalTobePaid.setText(totalWithSign);

        if (mProduct.isForeign() && mFuturesFinancing != null) {
            double ratio = mFuturesFinancing.getRatio();
            String marginRmb = "  ( " + Unit.SIGN_CNY +
                    FinanceUtil.formatWithScale(tradeQuantity.getMargin() * ratio) + " )";
            mMargin.setText(
                    StrUtil.mergeTextWithColor(marginWithSign, marginRmb, Color.parseColor("#666666"))
            );
            String tradeFeeRmb = "  ( " + Unit.SIGN_CNY +
                    FinanceUtil.formatWithScale(tradeQuantity.getFee() * ratio) + " )";
            mTradeFee.setText(
                    StrUtil.mergeTextWithColor(tradeFeeWithSign, tradeFeeRmb, Color.parseColor("#666666"))
            );
            String totalRmb = Unit.SIGN_CNY
                    + FinanceUtil.formatWithScale((tradeQuantity.getMargin() + tradeQuantity.getFee()) * mProduct.getRatio());
            String totalForeign = "  ( " + totalWithSign + " )";
            mTotalTobePaid.setText(
                    StrUtil.mergeTextWithColor(totalRmb, totalForeign, Color.parseColor("#666666"))
            );
        }
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        NettyClient.getInstance().addNettyHandler(mNettyHandler);
        NettyClient.getInstance().start(mProduct.getContractsCode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
        NettyClient.getInstance().stop();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNettyHandler = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
