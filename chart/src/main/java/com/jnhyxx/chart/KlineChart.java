package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.jnhyxx.chart.domain.KlineViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KlineChart extends ChartView {

    private static final int CANDLES_WIDTH_DP = 6; //dp

    private static final String MA_WHITE = "#E5E5E5";
    private static final String MA_ORANGE = "#FFBB22";
    private static final String MA_PURPLE = "#AA20AF";

    public static final String DATE_FORMAT_DAY_K = "MM/dd";
    public static final String DATE_FORMAT_DAY_MIN = "HH:mm";

    private List<KlineViewData> mDataList;
    private SparseArray<KlineViewData> mVisibleList;
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;

    private Settings mSettings;
    private SimpleDateFormat mDateFormat;
    private String mDateFormatStr;
    private Date mDate;
    private int[] mMovingAverages;
    private OnTouchLinesAppearListener mOnTouchLinesAppearListener;

    // visible points index range
    private int mStart;
    private int mLength;
    private int mEnd;
    private long mMaxVolume;
    private float mCandleWidth;
    private float mMaxBaseLine;
    private float mMinBaseLine;

    public KlineChart(Context context) {
        super(context);
        init();
    }

    @Override
    protected boolean enableDragChart() {
        return true;
    }

    @Override
    protected boolean enableMovingAverages() {
        return true;
    }

    @Override
    protected boolean enableDrawTouchLines() {
        return true;
    }

    private void init() {
        mVisibleList = new SparseArray<>();
        mDateFormat = new SimpleDateFormat();
        mDateFormatStr = DATE_FORMAT_DAY_K;
        mDate = new Date();
        mCandleWidth = dp2Px(CANDLES_WIDTH_DP);
        mMovingAverages = new int[]{5, 10, 20};

        mMaxBaseLine = Float.MIN_VALUE;
        mMinBaseLine = Float.MAX_VALUE;
    }

    public void setDataList(List<KlineViewData> dataList) {
        mDataList = dataList;
        redraw();
    }

    public void setVisibleList(SparseArray<KlineViewData> visibleList) {
        mVisibleList = visibleList;
    }

    public SparseArray<KlineViewData> getVisibleList() {
        return mVisibleList;
    }

    public void setDataFormat(String formatStr) {
        mDateFormatStr = formatStr;
    }

    public void setOnTouchLinesAppearListener(OnTouchLinesAppearListener onTouchLinesAppearListener) {
        mOnTouchLinesAppearListener = onTouchLinesAppearListener;
    }

    private void setCandleLinePaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
    }

    private void setCandleBodyPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setMovingAveragesPaint(Paint paint, int movingAverage) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
        if (movingAverage == mMovingAverages[0]) {
            paint.setColor(Color.parseColor(MA_WHITE));
        } else if (movingAverage == mMovingAverages[1]) {
            paint.setColor(Color.parseColor(MA_ORANGE));
        } else if (movingAverage == mMovingAverages[2]) {
            paint.setColor(Color.parseColor(MA_PURPLE));
        }
    }

    private void setMovingAveragesTextPaint(Paint paint, int movingAverage) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
        paint.setTextSize(mMaTitleSize);
        if (movingAverage == mMovingAverages[0]) {
            paint.setColor(Color.parseColor(MA_WHITE));
        } else if (movingAverage == mMovingAverages[1]) {
            paint.setColor(Color.parseColor(MA_ORANGE));
        } else if (movingAverage == mMovingAverages[2]) {
            paint.setColor(Color.parseColor(MA_PURPLE));
        }
    }

    protected void setTouchLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLUE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    @Override
    protected void calculateMovingAverages(boolean indexesEnable) {
        if (mDataList != null && mDataList.size() > 0) {
            mStart = mDataList.size() - mSettings.getXAxis() < 0
                    ? 0 : (mDataList.size() - mSettings.getXAxis() - calculatePointOffset());
            mLength = Math.min(mDataList.size(), mSettings.getXAxis());
            mEnd = mStart + mLength;

            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (int movingAverage : mMovingAverages) {
                for (int i = mStart; i < mEnd; i++) {
                    int start = i - movingAverage + 1;
                    if (start < 0) continue;
                    float movingAverageValue = calculateMovingAverageValue(start, movingAverage);
                    mDataList.get(i).addMovingAverage(movingAverage, movingAverageValue);
                    if (max < movingAverageValue) max = movingAverageValue;
                    if (min > movingAverageValue) min = movingAverageValue;
                }
            }
            mMaxBaseLine = max;
            mMinBaseLine = min;
        }
    }

    private int calculatePointOffset() {
        return (int) (getTransactionX() / getChartX(1));
    }

    @Override
    protected float calculateMaxTransactionX() {
        if (mDataList != null) {
            return Math.max((mDataList.size() - mSettings.getXAxis()) * getChartX(1), 0);
        }
        return super.calculateMaxTransactionX();
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = mMaxBaseLine;
            float min = mMinBaseLine;
            mMaxVolume = Long.MIN_VALUE;
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

    private float calculateMovingAverageValue(int start, int movingAverage) {
        float result = 0;
        for (int i = start; i < start + movingAverage; i++) {
            result += mDataList.get(i).getClosePrice();
        }
        return result / movingAverage;
    }

    @Override
    protected void drawTitleAboveBaselines(int left, int top, int top2, int touchIndex, Canvas canvas) {
        float textX = left + mTextMargin * 4;
        if (mVisibleList != null && mVisibleList.size() > 0) {
            KlineViewData data = mVisibleList.get(mVisibleList.size() - 1);
            if (hasThisTouchIndex(touchIndex)) {
                data = mVisibleList.get(touchIndex);
            }
            for (int movingAverage : mMovingAverages) {
                setMovingAveragesTextPaint(sPaint, movingAverage);
                float movingAverageValue = data.getMovingAverage(movingAverage);
                String maText = "MA" + movingAverage + ":--";
                if (movingAverageValue != 0) {
                    maText = "MA" + movingAverage + ":" + formatNumber(movingAverageValue);
                }
                float textWidth = sPaint.measureText(maText);
                canvas.drawText(maText, textX, top + mMaTitleHeight / 2 + mOffset4CenterMaTitle, sPaint);
                textX += textWidth + mTextMargin * 2;
            }
        } else {
            for (int movingAverage : mMovingAverages) {
                setMovingAveragesTextPaint(sPaint, movingAverage);
                String maText = "MA" + movingAverage + ":--";
                float textWidth = sPaint.measureText(maText);
                canvas.drawText(maText, textX, top + mFontHeight / 2 + mOffset4CenterMaTitle, sPaint);
                textX += textWidth + mTextMargin * 2;
            }
        }
    }

    public void setPriceAreaWidth(float priceAreaWidth) {
        mPriceAreaWidth = priceAreaWidth;
    }

    public float getPriceAreaWidth() {
        return mPriceAreaWidth;
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
                String baseLineValue = formatIndexesNumber(indexesBaseLines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);

                topY += verticalInterval;
            }
        }
    }

    protected String formatIndexesNumber(long value) {
        if (mSettings.getIndexesType() == Settings.INDEXES_VOL) {
            formatNumber(value, 0);
        }
        return value + "";
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2, Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = mStart; i < mEnd; i++) {
                KlineViewData data = mDataList.get(i);
                float chartX = getChartXOfScreen(i, data);
                drawCandle(chartX, data, canvas);
                if (indexesEnable) {
                    drawIndexes(chartX, data, canvas);
                }
            }
            drawMovingAverageLines(canvas);
        }
    }

    private void drawMovingAverageLines(Canvas canvas) {
        for (int movingAverage : mMovingAverages) {
            setMovingAveragesPaint(sPaint, movingAverage);
            float startX = -1;
            float startY = -1;
            for (int i = mStart; i < mEnd; i++) {
                int start = i - movingAverage + 1;
                if (start < 0) continue;
                float chartX = getChartXOfScreen(i);
                float movingAverageValue = mDataList.get(i).getMovingAverage(movingAverage);
                float chartY = getChartY(movingAverageValue);
                if (startX == -1 && startY == -1) { // start
                    startX = chartX;
                    startY = chartY;
                } else {
                    canvas.drawLine(startX, startY, chartX, chartY, sPaint);
                    startX = chartX;
                    startY = chartY;
                }
            }
        }
    }

    private void drawCandle(float chartX, KlineViewData data, Canvas canvas) {
        // default line is positive line
        ChartColor color = ChartColor.RED;
        float topPrice = data.getClosePrice();
        float bottomPrice = data.getOpenPrice();
        if (data.getClosePrice() < data.getOpenPrice()) { // negative line
            color = ChartColor.GREEN;
            topPrice = data.getOpenPrice();
            bottomPrice = data.getClosePrice();
        }
        drawTopCandleLine(data.getMaxPrice(), topPrice, color.get(), chartX, canvas);
        drawCandleBody(topPrice, bottomPrice, color.get(), chartX, canvas);
        drawBottomCandleLine(data.getMinPrice(), bottomPrice, color.get(), chartX, canvas);
    }

    private void drawTopCandleLine(Float maxPrice, float topPrice, String color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        Path path = getPath();
        path.moveTo(chartX, getChartY(maxPrice));
        path.lineTo(chartX, getChartY(topPrice));
        canvas.drawPath(path, sPaint);
    }

    private void drawCandleBody(float topPrice, float bottomPrice, String color, float chartX, Canvas canvas) {
        if (topPrice == bottomPrice) {
            setCandleLinePaint(sPaint, color);
            Path path = getPath();
            path.moveTo(chartX - mCandleWidth / 2, getChartY(topPrice));
            path.lineTo(chartX + mCandleWidth / 2, getChartY(bottomPrice));
            canvas.drawPath(path, sPaint);
        } else {
            setCandleBodyPaint(sPaint, color);
            RectF rectf = getRectF();
            rectf.left = chartX - mCandleWidth / 2;
            rectf.top = getChartY(topPrice);
            rectf.right = chartX + mCandleWidth / 2;
            rectf.bottom = getChartY(bottomPrice);
            canvas.drawRect(rectf, sPaint);
        }
    }

    private void drawBottomCandleLine(Float minPrice, float bottomPrice, String color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        Path path = getPath();
        path.moveTo(chartX, getChartY(bottomPrice));
        path.lineTo(chartX, getChartY(minPrice));
        canvas.drawPath(path, sPaint);
    }


    private void drawIndexes(float chartX, KlineViewData data, Canvas canvas) {
        if (mSettings.getIndexesType() == Settings.INDEXES_VOL) {
            drawVolumes(chartX, data, canvas);
        }
    }

    private void drawVolumes(float chartX, KlineViewData data, Canvas canvas) {
        ChartColor color = ChartColor.RED;
        if (data.getClosePrice() < data.getOpenPrice()) {
            color = ChartColor.GREEN;
        }
        setCandleBodyPaint(sPaint, color.get());
        RectF rectf = getRectF();
        rectf.left = chartX - mCandleWidth / 2;
        rectf.top = getIndexesChartY(data.getNowVolume());
        rectf.right = chartX + mCandleWidth / 2;
        rectf.bottom = getIndexesChartY(0);
        canvas.drawRect(rectf, sPaint);
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
                    float textX = getChartXOfScreen(i) - textWidth / 2;
                    canvas.drawText(displayTime, textX, textY, sPaint);
                }
            }
        }
    }

    private float getChartXOfScreen(int index) {
        index = index - mStart; // visible index 0 ~ 39
        return getChartX(index);
    }

    private float getChartXOfScreen(int index, KlineViewData data) {
        index = index - mStart; // visible index 0 ~ 39
        updateFirstLastVisibleIndex(index);
        mVisibleList.put(index, data);
        return getChartX(index);
    }

    private void updateFirstLastVisibleIndex(int indexOfXAxis) {
        mFirstVisibleIndex = Math.min(indexOfXAxis, mFirstVisibleIndex);
        mLastVisibleIndex = Math.max(indexOfXAxis, mLastVisibleIndex);
    }

    @Override
    protected float getChartX(int index) {
        float offset = super.getChartX(1) / 2;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        float chartX = getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
        return chartX + offset;
    }

    @Override
    protected int getIndexOfXAxis(float chartX) {
        float offset = super.getChartX(1) / 2;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        chartX = chartX - offset - getPaddingLeft();
        return (int) (chartX * mSettings.getXAxis() / width);
    }

    private String formatTimestamp(long timestamp) {
        if (mSettings.getkType() == Settings.DAY_K) {
            mDateFormat.applyPattern(mDateFormatStr);
            mDate.setTime(timestamp);
            return mDateFormat.format(mDate);
        }
        return "";
    }

    public void clearData() {
        mStart = 0;
        mEnd = 0;
        mLength = 0;
        mVisibleList.clear();
        resetTouchIndex();
        setDataList(null);
    }

    @Override
    protected void onTouchLinesAppear(int touchIndex) {
        if (mOnTouchLinesAppearListener != null) {
            KlineViewData curData = mVisibleList.get(touchIndex);
            KlineViewData preData = null;
            int previousIndex = touchIndex - 1;
            if (previousIndex >= 0) {
                preData = mVisibleList.get(previousIndex);
            }
            mOnTouchLinesAppearListener.onAppear(curData, preData, touchIndex < mSettings.getXAxis() / 2);
        }
    }

    @Override
    protected void onTouchLinesDisappear() {
        if (mOnTouchLinesAppearListener != null) {
            mOnTouchLinesAppearListener.onDisappear();
        }
    }

    @Override
    protected void drawTouchLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2,
                                  Canvas canvas) {
        if (hasThisTouchIndex(touchIndex)) {
            KlineViewData data = mVisibleList.get(touchIndex);
            float touchX = getChartX(touchIndex);
            float touchY = getChartY(data.getClosePrice());

            // draw cross line: vertical line and horizontal line
            setTouchLinePaint(sPaint);
            Path path = getPath();
            path.moveTo(touchX, top);
            path.lineTo(touchX, top + height);
            canvas.drawPath(path, sPaint);
            path = getPath();
            path.moveTo(left, touchY);
            path.lineTo(left + width, touchY);
            canvas.drawPath(path, sPaint);
        }
    }

    @Override
    protected int calculateTouchIndex(MotionEvent e) {
        float touchX = e.getX();
        int touchIndex = getIndexOfXAxis(touchX);
        if (mVisibleList != null && mVisibleList.size() > 0) {
            touchIndex = Math.max(touchIndex, mFirstVisibleIndex);
            touchIndex = Math.min(touchIndex, mLastVisibleIndex);
        }
        return touchIndex;
    }

    @Override
    protected boolean hasThisTouchIndex(int touchIndex) {
        if (touchIndex != -1 && mVisibleList != null && mVisibleList.get(touchIndex) != null) {
            return true;
        }
        return super.hasThisTouchIndex(touchIndex);
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

    public interface OnTouchLinesAppearListener {
        /**
         * @param data current kline data
         * @param previousData previous data of current data, n & n - 1
         * @param isLeftArea true means left area of view
         */
        void onAppear(KlineViewData data, KlineViewData previousData, boolean isLeftArea);

        void onDisappear();
    }
}
