package com.jnhyxx.html5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.johnz.kutils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TradePageHeader extends FrameLayout {

    @BindView(R.id.totalProfitAndUnit)
    TextView mTotalProfitAndUnit;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.oneKeyClosePositionBtn)
    TextView mOneKeyClosePositionBtn;

    @BindView(R.id.availableBalance)
    TextView mAvailableBalance;
    @BindView(R.id.orderListBtn)
    TextView mOrderListBtn;

    @BindView(R.id.signInButton)
    TextView mSignInButton;

    @OnClick({R.id.totalProfit, R.id.oneKeyClosePositionBtn, R.id.orderListBtn, R.id.signInButton})
    public void onClick(View view) {

        if (mListener == null) return;

        switch (view.getId()) {
            case R.id.totalProfit:
                mListener.onProfitAreaClick();
                break;
            case R.id.oneKeyClosePositionBtn:
                mListener.onOneKeyButtonClick();
                break;
            case R.id.orderListBtn:
                mListener.onOrderListButtonClick();
                break;
            case R.id.signInButton:
                mListener.onSignInButtonClick();
                break;

        }
    }

    public interface OnViewClickListener {
        void onSignInButtonClick();

        void onOrderListButtonClick();

        void onOneKeyButtonClick();

        void onProfitAreaClick();
    }

    private OnViewClickListener mListener;

    public static final int HEADER_UNLOGIN = 0;
    public static final int HEADER_AVAILABLE_BALANCE = 1;
    public static final int HEADER_HOLDING_POSITION = 2;

    private ViewGroup[] mHeaders;

    public TradePageHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHeaders = new ViewGroup[3];
        mHeaders[HEADER_UNLOGIN] = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.header_unlogin, null);
        mHeaders[HEADER_AVAILABLE_BALANCE] = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.header_available_balance, null);
        mHeaders[HEADER_HOLDING_POSITION] = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.header_holding_position, null);
        for (int i = 0; i < mHeaders.length; i++) {
            FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.trade_header_height));
            addView(mHeaders[i], i, params);
        }
        showView(HEADER_UNLOGIN);
        ButterKnife.bind(this);
    }

    public void showView(int headerIndex) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(GONE);
        }
        getChildAt(headerIndex).setVisibility(VISIBLE);
    }

    public void setOnViewClickListener(OnViewClickListener listener) {
        mListener = listener;
    }

    public void setTotalProfitUnit(String unit) {
        mTotalProfitAndUnit.setText(getContext().getString(R.string.holding_position_total_profit_and_unit, unit));
    }

    public void setAvailableBalance(double availableBalance) {
        mAvailableBalance.setText(FinanceUtil.formatWithScale(availableBalance));
    }
}
