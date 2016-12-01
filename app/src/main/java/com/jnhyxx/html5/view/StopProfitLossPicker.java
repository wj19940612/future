package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.order.AbsOrder;
import com.johnz.kutils.FinanceUtil;

public class StopProfitLossPicker extends LinearLayout {

    private CharSequence mSelectorTitle;

    private TextView mTitle;
    private TextView mRange;

    private ImageView mMinusButton;
    private ImageView mPlusButton;
    private TextView mPrice;
    private TextView mAmountChange;

    private Config mConfig;
    private double mLastPrice;

    private double mStopLossDown;
    private double mStopLossUp;
    private double mStopProfitDown;
    private double mStopProfitUp;
    private double mNewStopLossPrice;
    private double mNewStopProfitPrice;

    public StopProfitLossPicker(Context context) {
        super(context);
        init();
    }

    public StopProfitLossPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StopProfitLossPicker);

        mSelectorTitle = typedArray.getText(R.styleable.StopProfitLossPicker_pickerTitle);

        typedArray.recycle();

        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mTitle = new TextView(getContext());
        mTitle.setText(mSelectorTitle);
        mTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.lucky));

        mRange = new TextView(getContext());
        mRange.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mRange.setTextColor(ContextCompat.getColor(getContext(), R.color.blueAssist));

        LinearLayout firstLine = new LinearLayout(getContext());
        firstLine.setOrientation(HORIZONTAL);
        firstLine.addView(mTitle);
        LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int leftMargin = getResources().getDimensionPixelOffset(R.dimen.common_margin);
        params.setMargins(leftMargin, 0, 0, 0);
        firstLine.addView(mRange, params);
        addView(firstLine);

        // price picker
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_price_picker, null);
        mMinusButton = (ImageView) view.findViewById(R.id.minusButton);
        mMinusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfig == null) return;

                if ((mConfig.isStopLoss && mNewStopLossPrice != 0)
                        || (!mConfig.isStopLoss && mNewStopProfitPrice != 0)) {
                    onMinusButtonClick();
                }
            }
        });
        mPlusButton = (ImageView) view.findViewById(R.id.plusButton);
        mPlusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfig == null) return;

                if ((mConfig.isStopLoss && mNewStopLossPrice != 0)
                        || (!mConfig.isStopLoss && mNewStopProfitPrice != 0)) {
                    onPlusButtonClick();
                }
            }
        });
        mPrice = (TextView) view.findViewById(R.id.textView);
        mAmountChange = (TextView) view.findViewById(R.id.amountChange);
        params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());
        params.setMargins(0, marginTop, 0, 0);
        addView(view, params);
    }

    public double getPrice() {
        String price = mPrice.getText().toString();
        if (!TextUtils.isEmpty(price)) {
            return Double.valueOf(price).doubleValue();
        }
        return 0;
    }

    private void onPlusButtonClick() {
        if (mConfig.isStopLoss) {
            double stopLossPrice = mNewStopLossPrice + mConfig.pointPerBeat;
            if (stopLossPrice >= mStopLossDown && stopLossPrice <= mStopLossUp) {
                setStopLossPrice(stopLossPrice);
            }
        } else {
            double stopProfitPrice = mNewStopProfitPrice + mConfig.pointPerBeat;
            if (stopProfitPrice >= mStopProfitDown && stopProfitPrice <= mStopProfitUp) {
                setStopProfitPrice(stopProfitPrice);
            }
        }
    }

    private void onMinusButtonClick() {
        if (mConfig.isStopLoss) {
            double stopLossPrice = mNewStopLossPrice - mConfig.pointPerBeat;
            if (stopLossPrice >= mStopLossDown && stopLossPrice <= mStopLossUp) {
                setStopLossPrice(stopLossPrice);
            }
        } else {
            double stopProfitPrice = mNewStopProfitPrice - mConfig.pointPerBeat;
            if (stopProfitPrice >= mStopProfitDown && stopProfitPrice <= mStopProfitUp) {
                setStopProfitPrice(stopProfitPrice);
            }
        }
    }

    public void setConfig(Config config) {
        mConfig = config;
        updateRange();
    }

    public void setLastPrice(double lastPrice) {
        mLastPrice = lastPrice;
        updateRange();
    }

    private void updateRange() {
        if (mConfig != null && mLastPrice > 0) {
            if (mConfig.isStopLoss) {
                updateStopLossRange();
            } else {
                updateStopProfitRange();
            }
        }
    }

    private void setStopLossPrice(double stopLossPrice) {
        mNewStopLossPrice = stopLossPrice;
        mPrice.setText(FinanceUtil.formatWithScale(mNewStopLossPrice, mConfig.priceScale));
        double diff = 0;
        if (mConfig.buyOrSell == AbsOrder.DIRECTION_LONG) {
            diff = mNewStopLossPrice - mConfig.buyPrice;
        } else {
            diff = mConfig.buyPrice - mNewStopLossPrice;
        }
        if (diff < 0) {
            mAmountChange.setTextColor(ContextCompat.getColor(getContext(), R.color.greenPrimary));
            mAmountChange.setText(FinanceUtil.formatWithScale(diff * mConfig.eachPointMoney, mConfig.lossProfitScale));
        } else {
            mAmountChange.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            mAmountChange.setText("+" + FinanceUtil.formatWithScale(diff * mConfig.eachPointMoney, mConfig.lossProfitScale));
        }
    }

    private void setStopProfitPrice(double stopProfitPrice) {
        mNewStopProfitPrice = stopProfitPrice;
        mPrice.setText(FinanceUtil.formatWithScale(mNewStopProfitPrice, mConfig.priceScale));
        double diff = 0;
        if (mConfig.buyOrSell == AbsOrder.DIRECTION_LONG) {
            diff = mNewStopProfitPrice - mConfig.buyPrice;
        } else {
            diff = mConfig.buyPrice - mNewStopProfitPrice;
        }
        if (diff < 0) {
            mAmountChange.setTextColor(ContextCompat.getColor(getContext(), R.color.greenPrimary));
            mAmountChange.setText(FinanceUtil.formatWithScale(diff * mConfig.eachPointMoney, mConfig.lossProfitScale));
        } else {
            mAmountChange.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            mAmountChange.setText("+" + FinanceUtil.formatWithScale(diff * mConfig.eachPointMoney, mConfig.lossProfitScale));
        }
    }

    private void updateStopLossRange() {
        if (mConfig.buyOrSell == AbsOrder.DIRECTION_LONG) {
            mStopLossDown = mConfig.firstStopLossPrice;
            mStopLossUp = mLastPrice - mConfig.stopLossPriceOffset;
            if (mStopLossUp < mStopLossDown) {
                mStopLossUp = mStopLossDown;
            }
            mRange.setText(getResources().getString(R.string.stop_range,
                    FinanceUtil.formatWithScale(mStopLossDown, mConfig.priceScale),
                    FinanceUtil.formatWithScale(mStopLossUp, mConfig.priceScale)));
            if (mNewStopLossPrice == 0) { // 初始值
                setStopLossPrice(mConfig.stopLossPrice);
            }
            if (mNewStopLossPrice > mStopLossUp) {
                setStopLossPrice(mStopLossUp);
            }
        } else {
            mStopLossDown = mLastPrice + mConfig.stopLossPriceOffset;
            mStopLossUp = mConfig.firstStopLossPrice;
            if (mStopLossDown > mStopLossUp) {
                mStopLossDown = mStopLossUp;
            }
            mRange.setText(getResources().getString(R.string.stop_range,
                    FinanceUtil.formatWithScale(mStopLossDown, mConfig.priceScale),
                    FinanceUtil.formatWithScale(mStopLossUp, mConfig.priceScale)));
            if (mNewStopLossPrice == 0) { // 初始值
                setStopLossPrice(mConfig.stopProfitPrice);
            }
            if (mNewStopLossPrice < mStopLossDown) {
                setStopLossPrice(mStopLossDown);
            }
        }
    }

    private void updateStopProfitRange() {
        if (mConfig.buyOrSell == AbsOrder.DIRECTION_LONG) {
            mStopProfitDown = mLastPrice + mConfig.stopProfitPriceOffset;
            mStopProfitUp = mConfig.highestStopProfitPrice;
            if (mStopProfitDown > mStopProfitUp) {
                mStopProfitDown = mStopProfitUp;
            }
            mRange.setText(getResources().getString(R.string.stop_range,
                    FinanceUtil.formatWithScale(mStopProfitDown, mConfig.priceScale),
                    FinanceUtil.formatWithScale(mStopProfitUp, mConfig.priceScale)));
            if (mNewStopProfitPrice == 0) { //初始值
                setStopProfitPrice(mConfig.stopProfitPrice);
            }
            if (mNewStopProfitPrice < mStopProfitDown) {
                setStopProfitPrice(mStopProfitDown);
            }
        } else {
            mStopProfitDown = mConfig.highestStopProfitPrice;
            mStopProfitUp = mLastPrice - mConfig.stopProfitPriceOffset;
            if (mStopProfitUp < mStopProfitDown) {
                mStopProfitUp = mStopProfitDown;
            }
            mRange.setText(getResources().getString(R.string.stop_range,
                    FinanceUtil.formatWithScale(mStopProfitDown, mConfig.priceScale),
                    FinanceUtil.formatWithScale(mStopProfitUp, mConfig.priceScale)));
            if (mNewStopProfitPrice == 0) {
                setStopProfitPrice(mConfig.stopProfitPrice);
            }
            if (mNewStopProfitPrice > mStopProfitUp) {
                setStopProfitPrice(mStopProfitUp);
            }
        }
    }

    public static class Config {

        private boolean isStopLoss;
        private int buyOrSell;
        private double buyPrice;
        private double pointPerBeat; // 每次跳动多少个点
        private double stopLossPrice;
        private double stopProfitPrice;
        private double highestStopProfitPrice; // 最高止盈价
        private double firstStopLossPrice; // 最初的止损价格
        private double stopLossPriceOffset;
        private double stopProfitPriceOffset;
        private double eachPointMoney; // 每个点多少钱
        private int priceScale;
        private int lossProfitScale;

        public Config(boolean isStopLoss,
                      int buyOrSell,
                      double buyPrice,
                      double pointPerBeat,
                      double stopLossPrice, double stopProfitPrice,
                      double firstStopLossPrice, double highestStopProfitPrice,
                      double stopLossPriceOffset, double stopProfitPriceOffset,
                      double eachPointMoney,
                      int priceScale,
                      int lossProfitScale) {

            this.isStopLoss = isStopLoss;
            this.buyOrSell = buyOrSell;
            this.buyPrice = buyPrice;
            this.pointPerBeat = pointPerBeat;
            this.stopLossPrice = stopLossPrice;
            this.stopProfitPrice = stopProfitPrice;
            this.firstStopLossPrice = firstStopLossPrice;
            this.highestStopProfitPrice = highestStopProfitPrice;
            this.stopLossPriceOffset = stopLossPriceOffset;
            this.stopProfitPriceOffset = stopProfitPriceOffset;
            this.eachPointMoney = eachPointMoney;
            this.priceScale = priceScale;
            this.lossProfitScale = lossProfitScale;
        }
    }
}
