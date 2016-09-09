package com.jnhyxx.html5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketDataView extends FrameLayout {

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

    public void setTodayPosition(TextView todayPosition) {
        mTodayPosition = todayPosition;
    }

    public void setPrePosition(TextView prePosition) {
        mPrePosition = prePosition;
    }

    public void setTodaySettlement(TextView todaySettlement) {
        mTodaySettlement = todaySettlement;
    }

    public void setPreSettlement(TextView preSettlement) {
        mPreSettlement = preSettlement;
    }

    public void setTotalHands(TextView totalHands) {
        mTotalHands = totalHands;
    }

    public void setTotalAmount(TextView totalAmount) {
        mTotalAmount = totalAmount;
    }

    public void setRisingLimit(TextView risingLimit) {
        mRisingLimit = risingLimit;
    }

    public void setDownLimit(TextView downLimit) {
        mDownLimit = downLimit;
    }
}
