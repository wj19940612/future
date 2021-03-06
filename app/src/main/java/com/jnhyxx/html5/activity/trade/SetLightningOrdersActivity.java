package com.jnhyxx.html5.activity.trade;

import android.app.Dialog;
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
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.FuturesFinancing;
import com.jnhyxx.html5.domain.order.LightningOrderAsset;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.OrderConfigurationSelector;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.StrUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 闪电下单配置界面
 */
public class SetLightningOrdersActivity extends BaseActivity {

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
    @BindView(R.id.openLightningOrderHint)
    TextView mOpenLightningOrderHint;


    private Product mProduct;
    private int mFundType;
    private FuturesFinancing mFuturesFinancing;
    private ExchangeStatus mExchangeStatus;


    //判断是否有期货配资
    private boolean hasFuturesFinancing;

    //用来包装提交到服务器的闪电下单的类
    private LightningOrderAsset mLightningOrderAsset;
    //本地缓存的闪电下单数据
    private LightningOrderAsset mLocalLightningOrderAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightning_orsers_set);
        ButterKnife.bind(this);

        mLightningOrderAsset = new LightningOrderAsset();
        initData(getIntent());
        //获取期货配资方案
        getFuturesFinancing();


        setTradeQuantity();
        setTouchStopLoss();
        setTouchStopPro();

        getExchangeTradeStatus();
    }

    @OnClick({R.id.openLightningOrder, R.id.closeLightningOrder, R.id.restartLightningOrder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openLightningOrder:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.OPEN_LIGHTNING_ORDERS);
                openLightningOrder();
                break;
            case R.id.closeLightningOrder:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.ClOSE_LIGHTNING_ORDERS);
                removeLightningOrder(false);
                break;
            case R.id.restartLightningOrder:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.RESTART_SET_LIGHTNING_ORDERS);
                againShowOpenBtn();
                break;
        }
    }

    //再次显示开启按钮
    private void againShowOpenBtn() {
        mTradeQuantitySelector.setEnabled(true);
        mTouchStopLossSelector.setEnabled(true);
        mTouchStopProfitSelector.setEnabled(true);

        mRestartLightningOrder.setVisibility(View.GONE);
        mOpenLightningOrder.setVisibility(View.VISIBLE);
        mOpenLightningOrderHint.setVisibility(View.VISIBLE);
    }

    private void removeLightningOrder(final boolean isFuturesFinChanged) {
        API.Market.removeOrderAssetStoreStatus(mProduct.getVarietyId(), mFundType)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        LightningOrderAsset.setLocalLightningOrder(mProduct, mFundType, null);
                        setResult(RESULT_OK);
                        if (!isFuturesFinChanged) {
                            onBackPressed();
                        }
                    }
                })
                .fire();
    }

    private void openLightningOrder() {
        if (hasFuturesFinancing) {
            API.Market.saveAndUpdateOrderAssetStore(mLightningOrderAsset)
                    .setIndeterminate(this)
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        public void onReceive(Resp<JsonObject> jsonObjectResp) {
                            if (jsonObjectResp.isSuccess()) {
                                LightningOrderAsset.setLocalLightningOrder(mProduct, mFundType, mLightningOrderAsset);
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                ToastUtil.curt(jsonObjectResp.getMsg());
                            }
                        }
                    })
                    .fire();
        } else {
            ToastUtil.curt("产品配资不可为空");
        }
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
                    mLightningOrderAsset.setStopProfitPoint(stopProfit.getStopProfitPoint());
                    mLightningOrderAsset.setStopWinPrice(stopProfit.getStopProfit());
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
                        if (mFuturesFinancing != null) {
                            mLocalLightningOrderAsset = LightningOrderAsset.getLocalLightningOrderAsset(mProduct, mFundType);
                            hasFuturesFinancing = true;
                            mLightningOrderAsset.setRatio(mFuturesFinancing.getRatio());
                            if (mLocalLightningOrderAsset != null) {
                                boolean compareDataWithWeb = mLocalLightningOrderAsset.isValid(futuresFinancing);
                                if (compareDataWithWeb) {
                                    updatePlaceOrderViews();
                                    int selectStopLossIndex = mLocalLightningOrderAsset.getSelectStopLossIndex(futuresFinancing);
                                    mTouchStopLossSelector.selectItem(selectStopLossIndex);
                                    setLayoutStatus();
                                } else {
                                    showLightningOrderInvalidDialog();
                                }
                            } else {
                                updatePlaceOrderViews();
                                setLayoutStatus();
                            }
                        }
                    }
                }).fire();
    }


    private void showLightningOrderInvalidDialog() {
        SmartDialog.with(getActivity(),
                getString(R.string.lightning_orders_status_run_out))

                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        removeLightningOrder(true);
                        showOpenBtn();
                        updatePlaceOrderViews();
                    }
                })
                .show();
    }

    private void showOpenBtn() {
        mTradeQuantitySelector.setEnabled(true);
        mTouchStopLossSelector.setEnabled(true);
        mTouchStopProfitSelector.setEnabled(true);

        mOpenLightningOrder.setVisibility(View.VISIBLE);
        mCloseLightningOrder.setVisibility(View.GONE);
        mRestartLightningOrder.setVisibility(View.GONE);
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

                    if (mLocalLightningOrderAsset != null) {
                        int selectHandNum = mLocalLightningOrderAsset.getSelectHandNum(mProduct, mFuturesFinancing);
                        mTradeQuantitySelector.selectItem(selectHandNum);
                        int selectStopProfit = mLocalLightningOrderAsset.getSelectStopProfit(mProduct, mFuturesFinancing);
                        mTouchStopProfitSelector.selectItem(selectStopProfit);
                        Log.d(TAG, " 选择手数 " + selectHandNum + " 选择止盈 " + selectStopProfit);
                    }

                    mLightningOrderAsset.setAssetsId(stopLoss.getAssetsBean().getAssetsId());
                    mLightningOrderAsset.setStopLossPrice(((FuturesFinancing.StopLoss) configuration).getAssetsBean().getStopLossBeat());
                }
            }
        });
    }

    private void updatePlaceOrderViews() {
        // 设置止损
        mFuturesFinancing.sort();
        List<FuturesFinancing.StopLoss> stopLossList = mFuturesFinancing.getStopLossList(mProduct);
        mTouchStopLossSelector.setOrderConfigurationList(stopLossList);
    }

    private void setTradeQuantity() {
        mTradeQuantitySelector.setOnItemSelectedListener(new OrderConfigurationSelector.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                if (configuration instanceof FuturesFinancing.TradeQuantity) {
                    FuturesFinancing.TradeQuantity tradeQuantity = (FuturesFinancing.TradeQuantity) configuration;
                    mLightningOrderAsset.setHandsNum(tradeQuantity.getQuantity());
                    updateMarginTradeFeeAndTotal(tradeQuantity);
                }
            }
        });
    }


    private void initData(Intent intent) {
        mProduct = intent.getParcelableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mLightningOrderAsset.setVarietyId(mProduct.getVarietyId());
        mLightningOrderAsset.setPayType(mFundType);
    }

    private void setLayoutStatus() {
        //如果是开启状态
        if (LightningOrderAsset.isLightningOrderOpened(mProduct, mFundType)) {
            mTradeQuantitySelector.setEnabled(false);
            mTouchStopLossSelector.setEnabled(false);
            mTouchStopProfitSelector.setEnabled(false);

            mOpenLightningOrder.setVisibility(View.GONE);
            mCloseLightningOrder.setVisibility(View.VISIBLE);
            mRestartLightningOrder.setVisibility(View.VISIBLE);
            mOpenLightningOrderHint.setVisibility(View.GONE);
        } else {
            showOpenBtn();
        }
    }

    private void updateMarginTradeFeeAndTotal(FuturesFinancing.TradeQuantity tradeQuantity) {
        // DOMESTIC
        String marginWithSign = mProduct.getSign() + tradeQuantity.getMargin();
        String tradeFeeWithSign = mProduct.getSign() + tradeQuantity.getFee();
        String totalWithSign = mProduct.getSign() + (tradeQuantity.getMargin() + tradeQuantity.getFee());
        mMargin.setText(marginWithSign);
        mLightningOrderAsset.setMarginMoney(tradeQuantity.getMargin());
        mLightningOrderAsset.setFees(tradeQuantity.getFee());
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}
