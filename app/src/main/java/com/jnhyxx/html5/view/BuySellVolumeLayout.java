package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

public class BuySellVolumeLayout extends LinearLayout {

    private static final float PADDING_DP = 7;
    private static final float MARGIN_TOP_LINE_DP = 4;
    private static final float MARGIN_TOP_RECT_DP = 10;
    private static final float RECT_HEIGHT_DP = 6;
    private static final int MAX_VOLUME = 30;

    private TextView mBuyVolumeNum;
    private TextView mSellVolumeNum;
    private View mBuyVolumeView;
    private View mSellVolumeView;
    private int mMaxWidth;

    public BuySellVolumeLayout(Context context) {
        super(context);
        init();
    }

    public BuySellVolumeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMaxWidth = (int) (getMeasuredWidth() * 0.4);
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        int paddingPx = dp2px(PADDING_DP);
        int heightPx = dp2px(RECT_HEIGHT_DP);
        int topMarginLinePx = dp2px(MARGIN_TOP_LINE_DP);
        int topMarginRectPx = dp2px(MARGIN_TOP_RECT_DP);

        setPadding(paddingPx, 0, 0, 0);

        // 2 part: 1 is rectangles, 2 is volume and text
        // part 1
        LinearLayout part = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        addView(part, params);
        part.setOrientation(VERTICAL);
        part.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mBuyVolumeView = new View(getContext());
        mBuyVolumeView.setBackgroundResource(R.drawable.bg_buy_volume);
        params = new LayoutParams(0, heightPx);
        part.addView(mBuyVolumeView, params);
        mSellVolumeView = new View(getContext());
        mSellVolumeView.setBackgroundResource(R.drawable.bg_sell_volume);
        params = new LayoutParams(0, heightPx);
        params.setMargins(0, topMarginRectPx, 0, 0);
        part.addView(mSellVolumeView, params);

        // part 2
        part = new LinearLayout(getContext());
        addView(part);
        part.setOrientation(VERTICAL);
        part.setGravity(Gravity.RIGHT);
        part.setPadding(paddingPx, 0, 0, 0);
        // line 1
        LinearLayout line = createLine();
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        params.weight = 1;
        part.addView(line, params);
        mBuyVolumeNum = new TextView(getContext());
        mBuyVolumeNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        mBuyVolumeNum.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        mBuyVolumeNum.setGravity(Gravity.RIGHT);
        mBuyVolumeNum.setText("0");
        mBuyVolumeNum.setMaxLines(1);
        line.addView(mBuyVolumeNum);
        line.addView(createTextView(R.string.buy_volume));
        //line 2 add some marginTop
        line = createLine();
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        params.weight = 1;
        params.setMargins(0, topMarginLinePx, 0, 0);
        part.addView(line, params);
        mSellVolumeNum = new TextView(getContext());
        mSellVolumeNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        mSellVolumeNum.setTextColor(ContextCompat.getColor(getContext(), R.color.greenPrimary));
        mSellVolumeNum.setText("0");
        mSellVolumeNum.setGravity(Gravity.RIGHT);
        mSellVolumeNum.setMaxLines(1);
        line.addView(mSellVolumeNum);
        line.addView(createTextView(R.string.sell_volume));
    }

    private LinearLayout createLine() {
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(HORIZONTAL);
        line.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        return line;
    }

    private View createTextView(int resId) {
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_DP, getResources().getDisplayMetrics());
        TextView textView = new TextView(getContext());
        textView.setText(resId);
        textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        textView.setPadding(paddingPx, 0, 0, 0);
        textView.setMaxLines(1);
        return textView;
    }

    public void setVolumes(int sellVolume, int buyVolume) {
        mBuyVolumeNum.setText(String.valueOf(buyVolume));
        mSellVolumeNum.setText(String.valueOf(sellVolume));
        float sellVolumeRectWidth = sellVolume * mMaxWidth / MAX_VOLUME;
        float buyVolumeRectWidth = buyVolume * mMaxWidth / MAX_VOLUME;
        if (buyVolumeRectWidth > mMaxWidth) {
            buyVolumeRectWidth = mMaxWidth;
            sellVolumeRectWidth = mMaxWidth * sellVolume / buyVolume;
        }
        if (sellVolumeRectWidth > mMaxWidth) {
            sellVolumeRectWidth = mMaxWidth;
            buyVolumeRectWidth = mMaxWidth * buyVolume / sellVolume;
        }
        LayoutParams params = (LayoutParams) mBuyVolumeView.getLayoutParams();
        params.width = (int) buyVolumeRectWidth;
        mBuyVolumeView.setLayoutParams(params);
        params = (LayoutParams) mSellVolumeView.getLayoutParams();
        params.width = (int) sellVolumeRectWidth;
        mSellVolumeView.setLayoutParams(params);
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
