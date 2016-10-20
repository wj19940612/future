package com.jnhyxx.html5.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.OrderDetail;
import com.jnhyxx.html5.domain.order.SettledOrder;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.lossProfitUnit)
    TextView mLossProfitUnit;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.lossProfit)
    TextView mLossProfit;
    @BindView(R.id.lossProfitRmb)
    TextView mLossProfitRmb;

    @BindView(R.id.tradeVariety)
    TextView mTradeVariety;
    @BindView(R.id.tradeQuantity)
    TextView mTradeQuantity;
    @BindView(R.id.tradeFee)
    TextView mTradeFee;
    @BindView(R.id.margin)
    TextView mMargin;
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

    private SettledOrder mSettledOrder;
    private OrderDetail mOrderDetail;
    private Product mProduct;
    private int mFundType;
    private String mFundUnit;

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

        int tradeType = mSettledOrder.getDirection();
        if (tradeType == SettledOrder.DIRECTION_LONG) {
            mTradeType.setText(R.string.buy_long);
            mTradeType.setBackgroundResource(R.drawable.bg_red_primary);
        } else {
            mTradeType.setText(R.string.sell_short);
            mTradeType.setBackgroundResource(R.drawable.bg_green_primary);
        }

        double lossProfit = mSettledOrder.getWinOrLoss();
        double rate = mSettledOrder.getRatio();
        int color;
        String lossProfitStr;
        if (lossProfit < 0) {
            color = ContextCompat.getColor(this, R.color.greenPrimary);
            lossProfitStr = FinanceUtil.formatWithScale(lossProfit, mProduct.getLossProfitScale());
        } else {
            color = ContextCompat.getColor(this, R.color.redPrimary);
            lossProfitStr = "+" + FinanceUtil.formatWithScale(lossProfit, mProduct.getLossProfitScale());
        }
        mLossProfit.setTextColor(color);
        mLossProfit.setText(lossProfitStr);
        if (mProduct.isForeign()) {
            String lossProfitRmb = "(" + FinanceUtil.formatWithScale(Math.abs(lossProfit * rate)) + mFundUnit + ")";
            mLossProfitRmb.setText(lossProfitRmb);
        }

        // above is header, next is contract and order info
        mTradeVariety.setText(mOrderDetail.getContractsCode());
        mTradeQuantity.setText(mOrderDetail.getHandsNum() + "手");
        mTradeFee.setText(mOrderDetail.getUserFees() + mProduct.getCurrencyUnit());
        mMargin.setText(mOrderDetail.getMarginMoney() + mProduct.getCurrencyUnit());
        mBuyPrice.setText(FinanceUtil.formatWithScale(mOrderDetail.getRealAvgPrice(), mProduct.getPriceDecimalScale()));
        mBuyType.setText(getString(R.string.market_price_buy));
        mBuyTime.setText(DateUtil.format(mOrderDetail.getBuyTime(), "yyyy/MM/dd HH:mm:ss"));
        mSellPrice.setText(FinanceUtil.formatWithScale(mOrderDetail.getUnwindAvgPrice(), mProduct.getPriceDecimalScale()));
        mSellType.setText(getSellTypeText(mOrderDetail.getUnwindType()));
        mSellTime.setText(DateUtil.format(mOrderDetail.getSellTime(), "yyyy/MM/dd HH:mm:ss"));
        mOrderId.setText(mOrderDetail.getShowId());
    }

    private int getSellTypeText(int sellType) {
        if (sellType == SettledOrder.SELL_OUT_SYSTEM_CLEAR) {
            return R.string.time_up_sale;
        } else if (sellType == SettledOrder.SELL_OUT_STOP_LOSS) {
            return R.string.stop_loss_sale;
        } else if (sellType == SettledOrder.SELL_OUT_STOP_PROFIT) {
            return R.string.stop_profit_sale;
        } else {
            return R.string.market_price_sale;
        }
    }

    private void initData(Intent intent) {
        mSettledOrder = (SettledOrder) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        mOrderDetail = (OrderDetail) intent.getSerializableExtra(Launcher.EX_PAYLOAD_1);
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mFundUnit = (mFundType == Product.FUND_TYPE_CASH ? Unit.YUAN : Unit.GOLD);
    }
}
