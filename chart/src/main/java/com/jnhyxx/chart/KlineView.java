package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.SparseArray;

import com.jnhyxx.chart.domain.KlineViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;

public class KlineView extends ChartView {

    private List<KlineViewData> mDataList;
    private SparseArray<KlineViewData> mVisibleList;
    private Settings mSettings;
    private SimpleDateFormat mDateFormat;

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
            for (int i = mStart; i < mEnd; i++) {
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
    protected void calculateIndexesBaseLines(long[] indexesBaseLines) {
        if (mSettings.getIndexesType() == Settings.INDEXES_VOL) {
            long volumeRange = mMaxVolume / (indexesBaseLines.length - 1);
            indexesBaseLines[0] = mMaxVolume;
            indexesBaseLines[indexesBaseLines.length - 1] = 0;
            for (int i = indexesBaseLines.length - 2; i > 0; i--) {
                indexesBaseLines[i] = indexesBaseLines[i + 1] + volumeRange;
            }
        }
    }

    @Override
    protected void drawBaseLines(boolean indexesEnable, float[] baselines, int left, int top, int width, int height,
                                 long[] indexesBaseLines, int left2, int top2, int width2, int height2, Canvas canvas) {
        if (baselines == null || baselines.length < 2) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        mPriceAreaWidth = calculatePriceWidth(baselines[0]);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            Path path = getPath();
            path.moveTo(left, topY);
            path.lineTo(left + width, topY);
            setBaseLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            if (i % 2 == 0 && i != baselines.length - 1) {
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY + mTextMargin + mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);

            } else if (i == baselines.length - 1) { // last baseline
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);
            }
            topY += verticalInterval;
        }

        if (indexesEnable && indexesBaseLines.length >= 2) {
            topY = top2;
            verticalInterval = height2 * 1.0f / (indexesBaseLines.length - 1);
            for (int i = 0; i < indexesBaseLines.length; i++) {
                Path path = getPath();
                path.moveTo(left2, topY);
                path.lineTo(left2 + width2, topY);
                setBaseLinePaint(sPaint);
                canvas.drawPath(path, sPaint);

                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(indexesBaseLines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);

                topY += verticalInterval;
            }
        }
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2, Canvas canvas) {

    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            float textY = top + mTextMargin + mFontHeight / 2 + mOffset4CenterText;
            for (int i = mStart; i < mEnd; i += 10) {
                KlineViewData data = mDataList.get(i);
                setDefaultTextPaint(sPaint);
                if (i == mStart) {
                    float textX = left + mTextMargin;
                    canvas.drawText(formatTimestamp(data.getTimeStamp()), textX, textY, sPaint);
                } else {
                    String displayTime = formatTimestamp(data.getTimeStamp());
                    float textWidth = sPaint.measureText(displayTime);
                    float textX = getChartX(i) - textWidth / 2;
                    canvas.drawText(displayTime, textX, textY, sPaint);
                }
            }
        }
    }

    private String formatTimestamp(long timestamp) {
        if (mSettings.getkType() == Settings.DAY_K) {

        }
        return "";
    }

    public void clearData() {

    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (Settings) settings;
        super.setSettings(settings);
        redraw();
    }

    public static class Settings extends ChartSettings {
        public static final int INDEXES_VOL = 1;

        public static final int DAY_K = 10;

        private int indexesType;
        private int kType;

        public Settings() {
            super();
            indexesType = INDEXES_VOL;
            kType = DAY_K;
        }

        public int getIndexesType() {
            return indexesType;
        }

        public void setIndexesType(int indexesType) {
            this.indexesType = indexesType;
        }

        public int getkType() {
            return kType;
        }

        public void setkType(int kType) {
            this.kType = kType;
        }
    }
}
