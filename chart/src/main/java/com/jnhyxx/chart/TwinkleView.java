package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;

public class TwinkleView extends TrendChart {

    private TrendChart mTrendChart;

    public TwinkleView(Context context, TrendChart chart) {
        super(context);
        mTrendChart = chart;
        setVisibleList(mTrendChart.getVisibleList());
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
    }

    @Override
    protected void calculateIndexesBaseLines(long[] indexesBaseLines) {
    }

    @Override
    protected void drawBaseLines(boolean indexesEnable,
                                 float[] baselines, int left, int top, int width, int height,
                                 long[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                 Canvas canvas) {
        setPriceAreaWidth(mTrendChart.getPriceAreaWidth());
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable,
                                    int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2,
                                    Canvas canvas) {
    }

    @Override
    protected boolean enableDrawUnstableData() {
        return true;
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
    }

}
