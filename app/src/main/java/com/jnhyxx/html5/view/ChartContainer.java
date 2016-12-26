package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    private static final int PADDING_IN_DP = 12;

    public static final int POS_TREND = 0;
    public static final int POS_FLASH = 1;
    public static final int POS_PLATE = 2;
    public static final int POS_KLINE = 3;
    public static final int POS_LIVE_ENTER = 4;

    private RelativeLayout mTabsLayout;
    private FrameLayout mContainer;
    private OnLiveEnterClickListener mOnLiveEnterClickListener;

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
        //initPopupWindow();

        initTabs();

        mContainer = new FrameLayout(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContainer, params);

        onTabClick(POS_TREND);
    }

    public void addTrendView(TrendView trendView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(trendView, POS_TREND, params);
    }

    public void addFlashView(FlashView flashView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(flashView, POS_FLASH, params);
    }

    public void addMarketDataView(MarketDataView marketDataView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(marketDataView, POS_PLATE, params);
    }

    public void addKlineView(KlineView klineView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(klineView, POS_KLINE, params);
    }

    public TrendView getTrendView() {
        return (TrendView) mContainer.getChildAt(POS_TREND);
    }

    public FlashView getFlashView() {
        return (FlashView) mContainer.getChildAt(POS_FLASH);
    }

    public MarketDataView getMarketDataView() {
        return (MarketDataView) mContainer.getChildAt(POS_PLATE);
    }

    public KlineView getKlineView() {
        return (KlineView) mContainer.getChildAt(POS_KLINE);
    }

    public void showTrendView() {
        onTabClick(POS_TREND);
    }

    public void setOnLiveEnterClickListener(OnLiveEnterClickListener onLiveEnterClickListener) {
        mOnLiveEnterClickListener = onLiveEnterClickListener;
    }

//    private void initPopupWindow() {
//        //View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_kline, null);
//        LinearLayout popupViewGroup = (LinearLayout) popupView;
//        for (int i = 0; i < popupViewGroup.getChildCount(); i++) {
//            View child = popupViewGroup.getChildAt(i);
//            if (child instanceof TextView) {
//                child.setOnCheckDetailListener(this);
//            }
//        }
//
//        mPopupWindow = new PopupWindow(popupView,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT);
//        mPopupWindow.setOutsideTouchable(true);
//        mPopupWindow.setFocusable(true);
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
//        mPopupWindow.setClippingEnabled(true);
//    }

    private void initTabs() {
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DP, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mTabsLayout = new RelativeLayout(getContext());
        mTabsLayout.setPadding(paddingPx, 0, paddingPx, paddingPx);
        mTabsLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bluePrimary));

        mTabsLayout.addView(createTab(R.string.trend_chart), POS_TREND);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 2, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.trend_chart);
        mTabsLayout.addView(createTab(R.string.flash_chart), POS_FLASH, layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 2, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.flash_chart);
        mTabsLayout.addView(createTab(R.string.plate), POS_PLATE, layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(paddingPx * 2, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.string.plate);
        mTabsLayout.addView(createTab(R.string.day_k_line), POS_KLINE, layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mTabsLayout.addView(createLiveEnter(), POS_LIVE_ENTER, layoutParams);

        addView(mTabsLayout, params);
    }

    public void setLiveEnterVisible(boolean visible) {
        View liveEnter = mTabsLayout.getChildAt(POS_LIVE_ENTER);
        if (liveEnter != null) {
            liveEnter.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    private View createLiveEnter() {
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DP, getResources().getDisplayMetrics());
        TextView view = new TextView(getContext());
        view.setText(R.string.live);
        view.setTextColor(ContextCompat.getColor(getContext(), R.color.blueAssist));
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_live_enter, 0);
        view.setCompoundDrawablePadding(paddingPx / 3);
        view.setPadding(0, paddingPx, 0, paddingPx / 3); // increase click area
        view.setVisibility(GONE);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLiveEnterClickListener != null) {
                    mOnLiveEnterClickListener.onClick();
                }
            }
        });
        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.string.trend_chart:
                onTabClick(POS_TREND);
                break;
            case R.string.flash_chart:
                onTabClick(POS_FLASH);
                break;
            case R.string.plate:
                onTabClick(POS_PLATE);
                break;
            case R.string.day_k_line:
                onTabClick(POS_KLINE);
                break;
        }
    }

//    public void setTabEnable(int position, boolean enable) {
//        mTabsLayout = (LinearLayout) findViewById(R.id.tabs);
//        LinearLayout tab = (LinearLayout) mTabsLayout.getChildAt(position);
//        tab.setEnabled(enable);
//        tab.getChildAt(0).setEnabled(enable);
//    }
//
//    private void restoreKlineTab() {
//        LinearLayout klineTab = (LinearLayout) findViewById(R.id.kline_tab);
//        TextView tabText = (TextView) klineTab.getChildAt(0);
//        tabText.setText(R.string.kline_chart);
//    }
//
//    private void onDropDownItemClick(View v, int index) {
//        if (mPopupWindow.isShowing()) {
//            mPopupWindow.dismiss();
//        }
//
//        TextView clickedView = (TextView) v;
//        LinearLayout klineTab = (LinearLayout) findViewById(R.id.kline_tab);
//        TextView tabText = (TextView) klineTab.getChildAt(0);
//        tabText.setText(clickedView.getText());
//
//        if (mOnDropDownItemClickListener != null) {
//            mOnDropDownItemClickListener.onItemSelected(v, index);
//        }
//    }
//
//    private void showPopupWindow(View v) {
//        if (mPopupWindow != null) {
//            if (!mPopupWindow.isShowing()) {
//                View popupView = mPopupWindow.getContentView();
//                popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                int offsetX = v.getWidth() / 2 - popupView.getMeasuredWidth() / 2;
//                mPopupWindow.showAsDropDown(v, offsetX, 0);
//            } else {
//                mPopupWindow.dismiss();
//            }
//        }
//    }

    private void onTabClick(int pos) {
        for (int i = 0; i < mTabsLayout.getChildCount(); i++) {
            TextView tab = (TextView) mTabsLayout.getChildAt(i);
            tab.setSelected(false);
        }
        TextView tab = (TextView) mTabsLayout.getChildAt(pos);
        tab.setSelected(true);

        for (int i = 0; i < mContainer.getChildCount(); i++) {
            View view = mContainer.getChildAt(i);
            if (view != null) {
                view.setVisibility(GONE);
            }
        }
        if (pos < mContainer.getChildCount()) {
            mContainer.getChildAt(pos).setVisibility(VISIBLE);
        }
    }
}
