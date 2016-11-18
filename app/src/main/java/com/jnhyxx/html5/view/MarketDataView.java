package com.jnhyxx.html5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.johnz.kutils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketDataView extends FrameLayout {

    private static final String NO_DATA = "—";

    @BindView(R.id.todayPosition)
    TextView mTodayPosition;
    @BindView(R.id.prePosition)
    TextView mPrePosition;
    @BindView(R.id.todaySettlement)
    TextView mTodaySettlement;
    @BindView(R.id.preSettlement)
    TextView mPreSettlement;
    @BindView(R.id.totalHands)
    TextView mTotalHands;
    @BindView(R.id.totalAmount)
    TextView mTotalAmount;
    @BindView(R.id.risingLimit)
    TextView mRisingLimit;
    @BindView(R.id.downLimit)
    TextView mDownLimit;

    public MarketDataView(Context context) {
        super(context);
        init();
    }

    public MarketDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_market_data, this, true);

        ButterKnife.bind(this);
    }

    public void setMarketData(FullMarketData marketData, Product product) {
        if (product.isForeign()) {
            mTodayPosition.setText(marketData.getPositionVolume() != 0
                    ? FinanceUtil.addUnitWhenBeyondTenThousand(marketData.getPositionVolume())
                    : NO_DATA);
            mPrePosition.setText(marketData.getPrePositionVolume() != 0
                    ? FinanceUtil.addUnitWhenBeyondTenThousand(marketData.getPrePositionVolume())
                    : NO_DATA);

            int priceScale = product.getPriceDecimalScale();
            mTodaySettlement.setText(getPrice(marketData.getSettlePrice(), priceScale));
            mPreSettlement.setText(getPrice(marketData.getPreSetPrice(), priceScale));

            mTotalHands.setText(marketData.getVolume() != 0
                    ? FinanceUtil.addUnitWhenBeyondTenThousand(marketData.getVolume())
                    : NO_DATA);
            mTotalAmount.setText(marketData.getTurnover() != 0
                    ? FinanceUtil.addUnitWhenBeyondHundredMillion(marketData.getTurnover())
                    : NO_DATA);

            mRisingLimit.setText(getPrice(marketData.getUpLimitPrice(), priceScale));
            mDownLimit.setText(getPrice(marketData.getDownLimitPrice(), priceScale));
        } else {
            mTodayPosition.setText(marketData.getOpenInterest() != 0
                    ? FinanceUtil.addUnitWhenBeyondTenThousand(marketData.getOpenInterest())
                    : NO_DATA);
            mPrePosition.setText(marketData.getPreOpenInterest() != 0
                    ? FinanceUtil.addUnitWhenBeyondTenThousand(marketData.getPreOpenInterest())
                    : NO_DATA);

            int priceScale = product.getPriceDecimalScale();
            mTodaySettlement.setText(getPrice(marketData.getSettlePrice(), priceScale));
            mPreSettlement.setText(getPrice(marketData.getPreSetPrice(), priceScale));

            mTotalHands.setText(marketData.getVolume() != 0
                    ? FinanceUtil.addUnitWhenBeyondTenThousand(marketData.getVolume())
                    : NO_DATA);
            mTotalAmount.setText(marketData.getTurnover() != 0
                    ? FinanceUtil.addUnitWhenBeyondHundredMillion(marketData.getTurnover())
                    : NO_DATA);

            mRisingLimit.setText(getPrice(marketData.getUpLimitPrice(), priceScale));
            mDownLimit.setText(getPrice(marketData.getDownLimitPrice(), priceScale));
        }
    }

    public void clearData() {
        mTodayPosition.setText(getPrice(0, 0));
        mPrePosition.setText(getPrice(0, 0));
        mTodaySettlement.setText(getPrice(0, 0));
        mPreSettlement.setText(getPrice(0, 0));
    }

    private String getPrice(double price, int priceScale) {
        if (price == 0) {
            return NO_DATA;
        }
        return FinanceUtil.formatWithScale(price, priceScale);
    }
}
