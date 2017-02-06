package com.jnhyxx.chart;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.chart.domain.KlineViewData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KlineView extends RelativeLayout implements KlineChart.OnTouchLinesAppearListener {

    public static final String SIDE_BAR_DATE_FORMAT_DAY_K = "yyyy/MM/dd";
    public static final String SIDE_BAR_DATE_FORMAT_MIN = "yyyy/MM/dd\nHH:mm";

    private KlineChart mKlineChart;
    private KTouchView mKTouchView;
    private View mLeftSideBar;
    private View mRightSideBar;

    private SimpleDateFormat mDateFormat;

    public KlineView(Context context) {
        super(context);
        init();
    }

    public KlineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mKlineChart = new KlineChart(getContext());
        addView(mKlineChart, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mKTouchView = new KTouchView(getContext(), mKlineChart);
        mKTouchView.setOnTouchLinesAppearListener(this);
        addView(mKTouchView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mLeftSideBar = LayoutInflater.from(getContext()).inflate(R.layout.kline_side_bar, null);
        mRightSideBar = LayoutInflater.from(getContext()).inflate(R.layout.kline_side_bar, null);

        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_LEFT);
        params.setMargins(0, marginTop, 0, 0);
        mLeftSideBar.setVisibility(GONE);
        addView(mLeftSideBar, params);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.setMargins(0, marginTop, 0, 0);
        mRightSideBar.setVisibility(GONE);
        addView(mRightSideBar, params);

        mDateFormat = new SimpleDateFormat(SIDE_BAR_DATE_FORMAT_MIN);
    }

    private void setSideBar(View sideBar, KlineViewData data, KlineViewData previousData) {
        TextView time = (TextView) sideBar.findViewById(R.id.time);
        TextView openPrice = (TextView) sideBar.findViewById(R.id.openPrice);
        TextView highestPrice = (TextView) sideBar.findViewById(R.id.highestPrice);
        TextView lowestPrice = (TextView) sideBar.findViewById(R.id.lowestPrice);
        TextView closePrice = (TextView) sideBar.findViewById(R.id.closePrice);

        time.setText(mDateFormat.format(new Date(data.getTimeStamp())));
        openPrice.setText(mKlineChart.formatNumber(data.getOpenPrice()));
        highestPrice.setText(mKlineChart.formatNumber(data.getMaxPrice()));
        lowestPrice.setText(mKlineChart.formatNumber(data.getMinPrice()));
        closePrice.setText(mKlineChart.formatNumber(data.getClosePrice()));

        int redColor = ContextCompat.getColor(getContext(), R.color.redPrimary);
        int greenColor = ContextCompat.getColor(getContext(), R.color.greenPrimary);

        openPrice.setTextColor(redColor);
        if (previousData != null && data.getOpenPrice() < previousData.getClosePrice()) {
            openPrice.setTextColor(greenColor);
        }

        highestPrice.setTextColor(redColor);
        if (data.getMaxPrice() < data.getOpenPrice()) {
            highestPrice.setTextColor(greenColor);
        }

        lowestPrice.setTextColor(redColor);
        if (data.getMinPrice() < data.getOpenPrice()) {
            lowestPrice.setTextColor(greenColor);
        }

        closePrice.setTextColor(redColor);
        if (data.getClosePrice() < data.getOpenPrice()) {
            closePrice.setTextColor(greenColor);
        }
    }

    public void setDataList(List<KlineViewData> dataList) {
        mKlineChart.setDataList(dataList);
        mKTouchView.redraw();
    }

    public void setDataFormat(String formatStr) {
        mKlineChart.setDataFormat(formatStr);
        if (formatStr.equalsIgnoreCase(KlineChart.DATE_FORMAT_DAY_K)) {
            mDateFormat.applyPattern(SIDE_BAR_DATE_FORMAT_DAY_K);
        } else {
            mDateFormat.applyPattern(SIDE_BAR_DATE_FORMAT_MIN);
        }
    }

    public void clearData() {
        mKlineChart.clearData();
        mKTouchView.clearData();
    }

    public void setSettings(ChartSettings settings) {
        mKlineChart.setSettings(settings);
        mKTouchView.setSettings(settings);
    }

    @Override
    public void onAppear(KlineViewData data, KlineViewData previousData, boolean isLeftArea) {
        setSideBar(mLeftSideBar, data, previousData);
        setSideBar(mRightSideBar, data, previousData);

        if (isLeftArea) {
            showRightSideBar();
            hideLeftSideBar();
        } else {
            showLeftSideBar();
            hideRightSideBar();
        }
    }

    private void showRightSideBar() {
        if (mRightSideBar.getVisibility() == GONE) {
            mRightSideBar.setVisibility(VISIBLE);
            mRightSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_right));
        }
    }

    private void hideLeftSideBar() {
        if (mLeftSideBar.getVisibility() == VISIBLE) {
            mLeftSideBar.setVisibility(GONE);
            mLeftSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_left));
        }
    }

    private void showLeftSideBar() {
        if (mLeftSideBar.getVisibility() == GONE) {
            mLeftSideBar.setVisibility(VISIBLE);
            mLeftSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_left));
        }
    }

    private void hideRightSideBar() {
        if (mRightSideBar.getVisibility() == VISIBLE) {
            mRightSideBar.setVisibility(GONE);
            mRightSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_right));
        }
    }

    @Override
    public void onDisappear() {
        hideLeftSideBar();
        hideRightSideBar();
    }
}
