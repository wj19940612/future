package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.chart.FlashView;
import com.jnhyxx.chart.KlineView;
import com.jnhyxx.chart.TrendView;
import com.jnhyxx.html5.R;

public class ChartContainer extends LinearLayout implements View.OnClickListener {

    public interface OnLiveEnterClickListener {
        void onClick();
    }

    public interface OnTabClickListener {
        void onClick(int tabId);
    }

    public interface OnKlineClickListener {
        void onClick(int kline);
    }

    private static final int PADDING_IN_DP = 12;

    public static final int TAB_TREND = 0;
    public static final int TAB_ALL_DAY = 1;
    public static final int TAB_FLASH = 2;
    public static final int TAB_PLATE = 3;
    public static final int TAB_KLINE = 4;
    public static final int TAB_LIVE_ENTER = 5;

    public static final int KLINE_DAY = 0;
    public static final int KLINE_THREE = 3;
    public static final int KLINE_FIVE = 5;
    public static final int KLINE_TEN = 10;
    public static final int KLINE_THIRTY = 30;
    public static final int KLINE_SIXTY = 60;

    private RelativeLayout mTabsLayout;
    private FrameLayout mContainer;
    private OnLiveEnterClickListener mOnLiveEnterClickListener;
    private OnTabClickListener mOnTabClickListener;
    private OnKlineClickListener mOnKlineClickListener;
    private PopupWindow mPopupWindow;

    private int mKlineType;

    public ChartContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartContainer(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        initPopupWindow();

        initTabs();

        mContainer = new FrameLayout(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContainer, params);
    }

    public void addTrendView(TrendView trendView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(trendView, 0, params);
    }

    public void addFlashView(FlashView flashView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(flashView, 1, params);
    }

    public void addMarketDataView(MarketDataView marketDataView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(marketDataView, 2, params);
    }

    public void addKlineView(KlineView klineView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(klineView, 3, params);
    }

    public TrendView getTrendView() {
        return (TrendView) mContainer.getChildAt(0);
    }

    public FlashView getFlashView() {
        return (FlashView) mContainer.getChildAt(1);
    }

    public MarketDataView getMarketDataView() {
        return (MarketDataView) mContainer.getChildAt(2);
    }

    public KlineView getKlineView() {
        return (KlineView) mContainer.getChildAt(3);
    }

    public void showTrendView() {
        onTabClick(TAB_TREND);
    }

    public void setOnLiveEnterClickListener(OnLiveEnterClickListener onLiveEnterClickListener) {
        mOnLiveEnterClickListener = onLiveEnterClickListener;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        mOnTabClickListener = onTabClickListener;
    }

    public void setOnKlineClickListener(OnKlineClickListener onKlineClickListener) {
        mOnKlineClickListener = onKlineClickListener;
    }

    private void initPopupWindow() {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_kline, null);
        LinearLayout popupViewGroup = (LinearLayout) popupView;
        for (int i = 0; i < popupViewGroup.getChildCount(); i++) {
            View child = popupViewGroup.getChildAt(i);
            if (child instanceof TextView) {
                child.setOnClickListener(this);
            }
        }
        mPopupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setClippingEnabled(true);
    }

    private void initTabs() {
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DP, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mTabsLayout = new RelativeLayout(getContext());
        mTabsLayout.setPadding(paddingPx, 0, paddingPx, paddingPx);

        mTabsLayout.addView(createTab(R.string.trend_chart), TAB_TREND);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 3, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.trend_chart);
        mTabsLayout.addView(createTab(R.string.all_day), TAB_ALL_DAY, layoutParams);

         layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 3, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.all_day);
        mTabsLayout.addView(createTab(R.string.flash_chart), TAB_FLASH, layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 3, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.flash_chart);
        mTabsLayout.addView(createTab(R.string.plate), TAB_PLATE, layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 3, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.plate);
        mTabsLayout.addView(createTab(R.string.kline_chart, R.drawable.ic_kline_down_arrow), TAB_KLINE, layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        addView(mTabsLayout, params);
    }

    public void setLiveEnterVisible(boolean visible) {
        View liveEnter = mTabsLayout.getChildAt(TAB_LIVE_ENTER);
        if (liveEnter != null) {
            liveEnter.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    private View createTab(int resId) {
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DP, getResources().getDisplayMetrics());
        TextView tab = new TextView(getContext());
        tab.setText(resId);
        tab.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.chart_tab_text));
        tab.setBackgroundResource(R.drawable.bg_chart_tab);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tab.setPadding(0, paddingPx, 0, paddingPx / 3); // increase click area
        tab.setId(resId);
        tab.setOnClickListener(this);
        return tab;
    }

    private View createTab(int resId, int rightDrawableRes) {
        TextView tab = (TextView) createTab(resId);
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        tab.setCompoundDrawablePadding(paddingPx);
        tab.setCompoundDrawablesWithIntrinsicBounds(0, 0, rightDrawableRes, 0);
        return tab;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.string.trend_chart:
                onTabClick(TAB_TREND);
                resetKlineTab();
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onClick(TAB_TREND);
                }
                break;
            case R.string.all_day:
                onTabClick(TAB_ALL_DAY);
                resetKlineTab();
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onClick(TAB_ALL_DAY);
                }
                break;
            case R.string.flash_chart:
                onTabClick(TAB_FLASH);
                resetKlineTab();
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onClick(TAB_FLASH);
                }
                break;
            case R.string.plate:
                onTabClick(TAB_PLATE);
                resetKlineTab();
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onClick(TAB_PLATE);
                }
                break;
            case R.string.kline_chart: // tab[K线图]
                showPopupWindow(v);
                break;
            case R.id.dayK:
                onKlineClick(v, KLINE_DAY);
                break;
            case R.id.threeMin:
                onKlineClick(v, KLINE_THREE);
                break;
            case R.id.fiveMin:
                onKlineClick(v, KLINE_FIVE);
                break;
            case R.id.tenMin:
                onKlineClick(v, KLINE_TEN);
                break;
            case R.id.thirtyMin:
                onKlineClick(v, KLINE_THIRTY);
                break;
            case R.id.sixtyMin:
                onKlineClick(v, KLINE_SIXTY);
                break;
        }
    }

    private void resetKlineTab() {
        TextView klineTab = (TextView) mTabsLayout.getChildAt(TAB_KLINE);
        klineTab.setText(R.string.kline_chart);
    }
    
    private void onKlineClick(View v, int kline) {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

        TextView clickedView = (TextView) v;
        TextView klineTab = (TextView) mTabsLayout.getChildAt(TAB_KLINE);
        klineTab.setText(clickedView.getText());
        onTabClick(TAB_KLINE);

        mKlineType = kline;
        if (mOnKlineClickListener != null) {
            mOnKlineClickListener.onClick(kline);
        }
    }

    public int getKlineType() {
        return mKlineType;
    }

    //    public void setTabEnable(int position, boolean enable) {
//        mTabsLayout = (LinearLayout) findViewById(R.id.tabs);
//        LinearLayout tab = (LinearLayout) mTabsLayout.getChildAt(position);
//        tab.setEnabled(enable);
//        tab.getChildAt(0).setEnabled(enable);
//    }
//
//    private void onDropDownItemClick(View v, int index) {

//        if (mOnDropDownItemClickListener != null) {
//            mOnDropDownItemClickListener.onItemSelected(v, index);
//        }
//    }
//

    private void showPopupWindow(View v) {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                View popupView = mPopupWindow.getContentView();
                popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int offsetX = v.getWidth() / 2 - popupView.getMeasuredWidth() / 2;
                mPopupWindow.showAsDropDown(v, offsetX, 0);
            } else {
                mPopupWindow.dismiss();
            }
        }
    }

    private void onTabClick(int pos) {
        for (int i = 0; i < mTabsLayout.getChildCount(); i++) {
            TextView tab = (TextView) mTabsLayout.getChildAt(i);
            tab.setSelected(false);
        }
        TextView tab = (TextView) mTabsLayout.getChildAt(pos);
        tab.setSelected(true);

        // Change the content
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            View view = mContainer.getChildAt(i);
            if (view != null) {
                view.setVisibility(GONE);
            }
        }
        if (pos < mTabsLayout.getChildCount() && mContainer.getChildCount() > 0) {
            if (pos <= TAB_ALL_DAY) {
                mContainer.getChildAt(0).setVisibility(VISIBLE);
            } else {
                mContainer.getChildAt(pos - 1).setVisibility(VISIBLE);
            }
        }
    }
}
