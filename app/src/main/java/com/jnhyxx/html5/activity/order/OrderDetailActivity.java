package com.jnhyxx.html5.activity.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.SettlementOrder;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.StrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.lossProfitUnit)
    TextView mLossProfitUnit;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.lossProfit)
    TextView mLossProfit;
    @BindView(R.id.tradeVariety)
    TextView mTradeVariety;
    @BindView(R.id.tradeQuantity)
    TextView mTradeQuantity;
    @BindView(R.id.tradeFee)
    TextView mTradeFee;
    @BindView(R.id.margin)
    TextView mMargin;
    @BindView(R.id.contractDeadline)
    TextView mContractDeadline;
    @BindView(R.id.buyPrice)
    TextView mBuyPrice;
    @BindView(R.id.buyType)
    TextView mBuyType;
    @BindView(R.id.buyTime)
    TextView mBuyTime;
    @BindView(R.id.sellPrice)
    TextView mSellPrice;
    @BindView(R.id.sellType)
    TextView mSellType;
    @BindView(R.id.sellTime)
    TextView mSellTime;
    @BindView(R.id.orderId)
    TextView mOrderId;

    private Product mProduct;
    private SettlementOrder mSettlementOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        initData(getIntent());

        initView();
    }

    private void initView() {
        mLossProfitUnit.setText(getString(R.string.settlement_loss_profit_unit, mProduct.getCurrencyUnit()));

        int tradeType = mSettlementOrder.getTradeType();
        if (tradeType == SettlementOrder.TRADE_TYPE_LONG) {
            mTradeType.setText(R.string.bullish);
            mTradeType.setBackgroundResource(R.drawable.bg_red_primary);
        } else {
            mTradeType.setText(R.string.bearish);
            mTradeType.setBackgroundResource(R.drawable.bg_green_primary);
        }

        if (mSettlementOrder.isForeign()) {
            double lossProfit = mSettlementOrder.getLossProfit();
            double rate = mSettlementOrder.getRate();
            int grayColor = Color.parseColor("#CCCCCC"); //gray
            int color;
            String lossProfitForeign;
            if (lossProfit < 0) {
                color = getResources().getColor(R.color.greenPrimary);
                lossProfitForeign = FinanceUtil.formatWithScale(lossProfit, mProduct.getLossProfitPrecision());
            } else {
                color = getResources().getColor(R.color.redPrimary);
                lossProfitForeign = "+" + FinanceUtil.formatWithScale(lossProfit, mProduct.getLossProfitPrecision());
            }
            String lossProfitInner = "   (" + FinanceUtil.formatWithScale(lossProfit * rate) + FinanceUtil.UNIT_YUAN + ")";
            mLossProfit.setTextColor(color);
            mLossProfit.setText(StrUtil.mergeTextWithRatioColor(lossProfitForeign, lossProfitInner, 0.35f, grayColor));

        } else {
            double lossProfit = mSettlementOrder.getLossProfit();
            int color;
            String lossProfitInner;
            if (lossProfit < 0) {
                color = getResources().getColor(R.color.greenPrimary);
                lossProfitInner = FinanceUtil.formatWithScale(lossProfit, mProduct.getLossProfitPrecision());

            } else {
                color = getResources().getColor(R.color.redPrimary);
                lossProfitInner = "+" + FinanceUtil.formatWithScale(lossProfit, mProduct.getLossProfitPrecision());
            }
            mLossProfit.setTextColor(color);
            mLossProfit.setText(lossProfitInner);
        }

        // above is header, next is contract and order info // TODO: 8/11/16 add latter with new API and Model

    }

    private void initData(Intent intent) {
        mSettlementOrder = (SettlementOrder) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
    }
}
