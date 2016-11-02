package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.FontUtil;
import com.johnz.kutils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TradePageHeader extends FrameLayout {

    @BindView(R.id.totalProfitAndUnit)
    TextView mTotalProfitAndUnit;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.totalProfitRmb)
    TextView mTotalProfitRmb;
    @BindView(R.id.oneKeyClosePositionBtn)
    TextView mOneKeyClosePositionBtn;

    @BindView(R.id.availableBalanceAndUnit)
    TextView mAvailableBalanceAndUnit;
    @BindView(R.id.availableBalance)
    TextView mAvailableBalance;
    @BindView(R.id.orderListBtn)
    TextView mOrderListBtn;

    @BindView(R.id.signInButton)
    TextView mSignInButton;

    @OnClick({R.id.totalProfitArea, R.id.oneKeyClosePositionBtn, R.id.orderListBtn, R.id.signInButton})
    public void onClick(View view) {

        if (mListener == null) return;

        switch (view.getId()) {
            case R.id.totalProfitArea:
                mListener.onProfitAreaClick();
                break;
            case R.id.oneKeyClosePositionBtn:
                mListener.onOneKeyClosePosButtonClick();
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

        void onOneKeyClosePosButtonClick();

        void onProfitAreaClick();
    }

    private OnViewClickListener mListener;

    public static final int HEADER_UNLOGIN = 0;
    public static final int HEADER_AVAILABLE_BALANCE = 1;
    public static final int HEADER_HOLDING_POSITION = 2;

    private ViewGroup[] mHeaders;

    private int mHeaderIndex;

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
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.trade_header_height));
            addView(mHeaders[i], i, params);
        }
        mHeaderIndex = -1;
        showView(HEADER_UNLOGIN);
        ButterKnife.bind(this);

        FontUtil.setTt0173MFont(mAvailableBalance);
        FontUtil.setTt0173MFont(mTotalProfit);
    }

    public void showView(int headerIndex) {
        if (mHeaderIndex == headerIndex) return;

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(GONE);
        }
        getChildAt(headerIndex).setVisibility(VISIBLE);

        mHeaderIndex = headerIndex;
    }

    public void setOnViewClickListener(OnViewClickListener listener) {
        mListener = listener;
    }

    public void setTotalProfitUnit(String unit) {
        mTotalProfitAndUnit.setText(getContext().getString(R.string.holding_position_total_profit_and_unit, unit));
    }

    public void setTotalProfit(double totalProfit, boolean isForeign, int scale, double ratio, String fundUnit) {
        int color = ContextCompat.getColor(getContext(), R.color.greenPrimary);
        if (totalProfit >= 0) {
            color = ContextCompat.getColor(getContext(), R.color.redPrimary);
        }
        mTotalProfit.setTextColor(color);
        mTotalProfitRmb.setTextColor(color);

        String totalProfitStr = totalProfit >= 0 ?
                "+" + FinanceUtil.formatWithScale(totalProfit, scale) :
                "" + FinanceUtil.formatWithScale(totalProfit, scale);
        if (isForeign) {
            double totalProfitRmb = FinanceUtil.multiply(totalProfit, ratio).doubleValue();
            String totalProfitRmbStr = totalProfit >= 0 ? "+" + FinanceUtil.formatWithScale(totalProfitRmb)
                    : FinanceUtil.formatWithScale(totalProfitRmb);
            totalProfitRmbStr = "(" + totalProfitRmbStr + fundUnit + ")";
//            mTotalProfit.setText(StrUtil.mergeTextWithTypefaceRatio(totalProfitStr, FontUtil.getTt0173MFont(), " " + totalProfitRmbStr, 0.5f),
//                    TextView.BufferType.SPANNABLE);
            mTotalProfit.setText(totalProfitStr);
            mTotalProfitRmb.setText(totalProfitRmbStr);
        } else {
            mTotalProfit.setText(totalProfitStr);
        }
    }

    public void setAvailableBalance(double availableBalance) {
        mAvailableBalance.setText(FinanceUtil.formatWithScale(availableBalance));
    }

    public void setAvailableBalanceUnit(String unit) {
        mAvailableBalanceAndUnit.setText(getContext().getString(R.string.available_balance_and_unit, unit));
    }
}
