package com.jnhyxx.html5.view.market;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

public class ChartContainer extends LinearLayout implements View.OnClickListener {

    private static final int PADDING_IN_DP = 12;

    public static final int POS_TREND = 0;
    public static final int POS_FLASH = 1;
    public static final int POS_PLATE = 2;

    private LinearLayout mTabsLayout;
    private FrameLayout mContainer;

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

    public void addTrendView(ChartView chartView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(chartView, POS_TREND, params);
    }

    public ChartView getTrendView() {
        return (ChartView) mContainer.getChildAt(POS_TREND);
    }

//    private void initPopupWindow() {
//        //View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_kline, null);
//        LinearLayout popupViewGroup = (LinearLayout) popupView;
//        for (int i = 0; i < popupViewGroup.getChildCount(); i++) {
//            View child = popupViewGroup.getChildAt(i);
//            if (child instanceof TextView) {
//                child.setOnClickListener(this);
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
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mTabsLayout = new LinearLayout(getContext());
        mTabsLayout.setPadding(paddingPx, 0, paddingPx, paddingPx);
        mTabsLayout.setOrientation(HORIZONTAL);
        mTabsLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bluePrimary));

        mTabsLayout.addView(createTab(R.string.trend_chart), POS_TREND);

        LinearLayout.MarginLayoutParams marginLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.setMargins(paddingPx * 2, 0, 0, 0);
        mTabsLayout.addView(createTab(R.string.flash_chart), POS_FLASH, marginLayoutParams);

        marginLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.setMargins(paddingPx * 2, 0, 0, 0);
        mTabsLayout.addView(createTab(R.string.plate), POS_PLATE, marginLayoutParams);

        addView(mTabsLayout, params);
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
//            mOnDropDownItemClickListener.onItemClick(v, index);
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
    }

}
