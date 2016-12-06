package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.SparseArray;

import com.jnhyxx.chart.domain.KlineViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class KlineView extends ChartView {

    private List<KlineViewData> mDataList;
    private SparseArray<KlineViewData> mVisibleList;

    // visible points index range
    private int mStart;
    private int mLength;
    private int mEnd;
    private long mMaxVolume;

    public KlineView(Context context) {
        super(context);
        init();
    }

    public void init() {
        mVisibleList = new SparseArray<>();
    }

    public void setDataList(List<KlineViewData> dataList) {
        mDataList = dataList;
        redraw();
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            mStart = mDataList.size() - mSettings.getXAxis() < 0
                    ? 0 : (mDataList.size() - mSettings.getXAxis());
            mLength = Math.min(mDataList.size(), mSettings.getXAxis());
            mEnd = mStart + mLength;
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            mMaxVolume = Long.MAX_VALUE;
            for (int i = mStart; i < mLength; i++) {
                KlineViewData data = mDataList.get(i);
                if (max < data.getMaxPrice()) max = data.getMaxPrice();
                if (min > data.getMinPrice()) min = data.getMinPrice();
                if (mMaxVolume < data.getNowVolume()) mMaxVolume = data.getNowVolume();
            }

            float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1), mSettings.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

            baselines[0] = max;
            baselines[baselines.length - 1] = min;
            for (int i = baselines.length - 2; i > 0; i--) {
                baselines[i] = baselines[i + 1] + priceRange;
            }
        }
    }

    @Override
    protected void calculateIndexesBaseLines(float[] indexesBaseLines) {

    }

    @Override
    protected void drawBaseLines(boolean indexesEnable, float[] baselines, int left, int top, int width, int height, float[] indexesBaseLines, int left2, int top2, int width2, int height2, Canvas canvas) {

    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int left, int top, int width, int height, int left2, int top2, int width2, int height2, Canvas canvas) {

    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {

    }

    public void clearData() {

    }
}
