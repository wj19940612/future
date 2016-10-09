package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.jnhyxx.chart.domain.TrendViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TrendChart extends ChartView {

    private List<TrendViewData> mDataList;
    private TrendViewData mUnstableData;
    private SparseArray<TrendViewData> mVisibleList;

    private TrendView.Settings mSettings;

    public TrendChart(Context context) {
        super(context);
        init();
    }

    public TrendChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mVisibleList = new SparseArray<>();
    }

    private void setDashLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLUE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 3}, 1));
    }

    private void setUnstablePricePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.WHITE.get()));
        paint.setTextSize(mBigFontSize);
        paint.setPathEffect(null);
    }

    private void setUnstablePriceBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLUE.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setRealTimeLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLUE.get()));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    private void setRealTimeFillPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.FILL.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(51);
    }

//    private void setTouchLinePaint(Paint paint) {
//        paint.setColor(Color.parseColor("#7F7F7F"));
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(dp2Px(1));
//        paint.setPathEffect(null);
//    }
//    protected void setYellowBgPaint(Paint paint) {
//        paint.setColor(Color.parseColor(ChartView.ChartColor.YELLOW.get()));
//        paint.setStyle(Paint.Style.FILL);
//    }
//
//    protected void setBlackTextPaint(Paint paint) {
//        paint.setColor(Color.BLACK);
//    }

    public void setDataList(List<TrendViewData> dataList) {
        mDataList = dataList;
        redraw();
    }

    public List<TrendViewData> getDataList() {
        return mDataList;
    }

    public SparseArray<TrendViewData> getVisibleList() {
        return mVisibleList;
    }

    public void setUnstableData(TrendViewData unstableData) {
        mUnstableData = unstableData;
        redraw();
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (TrendView.Settings) settings;
        super.setSettings(settings);
        redraw();
    }

    @Override
    public TrendView.Settings getSettings() {
        return mSettings;
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (TrendViewData trendViewData : mDataList) {
                if (max < trendViewData.getLastPrice()) {
                    max = trendViewData.getLastPrice();
                }
                if (min > trendViewData.getLastPrice()) {
                    min = trendViewData.getLastPrice();
                }
            }

            if (mUnstableData != null) {
                if (max < mUnstableData.getLastPrice()) {
                    max = mUnstableData.getLastPrice();
                }
                if (min > mUnstableData.getLastPrice()) {
                    min = mUnstableData.getLastPrice();
                }
            }

            // the chart need a min height
            double delta = new BigDecimal(max).subtract(new BigDecimal(min)).doubleValue();
            float limitUp = mSettings.getLimitUp();
            if (delta < limitUp) {
                max = new BigDecimal(min).add(new BigDecimal(limitUp)).floatValue();
            }

            float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1), RoundingMode.HALF_EVEN)
                    .floatValue();

            float pricePadding = mDataList.get(0).getLastPrice() * 0.0025f;
            /** expand max ~ min to not let trend line touch top and bottom **/
            baselines[0] = max + pricePadding;
            baselines[baselines.length - 1] = min - pricePadding;
            for (int i = baselines.length - 2; i > 0; i--) {
                baselines[i] = baselines[i + 1] + priceRange;
            }
        }
    }

    @Override
    protected void calculateIndexesBaseLines(float[] indexesBaseLines) {

    }

    @Override
    protected void drawBaseLines(boolean indexesEnable,
                                 float[] baselines, int left, int top, int width, int height,
                                 float[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                 Canvas canvas) {
        if (baselines == null || baselines.length < 2) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        mPriceAreaWidth = calculatePriceWidth(baselines[0]);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            float baselineWidth = width;
            if (i > 0 && i < baselines.length - 1) {
                baselineWidth -= mPriceAreaWidth;
            }
            Path path = getPath();
            path.moveTo(left, topY);
            path.lineTo(left + baselineWidth, topY);
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

        // vertical line
        Path path = getPath();
        float chartX = left + width - mPriceAreaWidth;
        path.moveTo(chartX, top);
        path.lineTo(chartX, topY - verticalInterval);
        setBaseLinePaint(sPaint);
        canvas.drawPath(path, sPaint);
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable,
                                    int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2,
                                    Canvas canvas) {
        float firstChartX = 0;
        if (mDataList != null && mDataList.size() > 0) {
            int size = mDataList.size();
            Path path = getPath();
            float chartX = 0;
            float chartY = 0;
            for (int i = 0; i < size; i++) {
                chartX = getChartX(mDataList.get(i));
                chartY = getChartY(mDataList.get(i).getLastPrice());
                if (path.isEmpty()) {
                    firstChartX = chartX;
                    path.moveTo(chartX, chartY);
                } else {
                    path.lineTo(chartX, chartY);
                }
            }

            if (mUnstableData != null && mDataList.size() > 0) {
                chartX = getChartX(mUnstableData);
                chartY = getChartY(mUnstableData.getLastPrice());
                path.lineTo(chartX, chartY);
            }

            setRealTimeLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            // fill area
            setRealTimeFillPaint(sPaint);
            path.lineTo(chartX, top + height);
            path.lineTo(firstChartX, top + height);
            path.close();
            canvas.drawPath(path, sPaint);

            if (mUnstableData != null) {
                // dash line
                path = getPath();
                path.moveTo(chartX, chartY);
                path.lineTo(left + width - mPriceAreaWidth, chartY);
                setDashLinePaint(sPaint);
                canvas.drawPath(path, sPaint);

                // unstable price
                setUnstablePricePaint(sPaint);
                String unstablePrice = formatNumber(mUnstableData.getLastPrice());
                float priceWidth = sPaint.measureText(unstablePrice);
                float priceMargin = (mPriceAreaWidth - priceWidth) / 2;
                float priceX = left + width - priceMargin - priceWidth;
                RectF blueRect = getBigFontBgRectF(priceX, chartY + mOffset4CenterText, priceWidth);
                //// the center of rect is connected to dashLine
                //// add offset and let the bottom of rect connect to dashLine
                float rectHeight = blueRect.height();
                blueRect.top -= rectHeight / 2;
                blueRect.bottom -= rectHeight / 2;
                setUnstablePriceBgPaint(sPaint);
                canvas.drawRoundRect(blueRect, 2, 2, sPaint);
                float priceY = chartY - rectHeight / 2 + mOffset4CenterText;
                setUnstablePricePaint(sPaint);
                canvas.drawText(unstablePrice, priceX, priceY, sPaint);
            }
        }
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            String[] displayMarketTimes = mSettings.getDisplayMarketTimes();
            if (displayMarketTimes.length != 0) {
                setDefaultTextPaint(sPaint);
                float textY = top + mTextMargin + mFontHeight / 2 + mOffset4CenterText;
                for (int i = 0; i < displayMarketTimes.length; i++) {
                    if (i == 0) {
                        float textX = left + mTextMargin;
                        canvas.drawText(displayMarketTimes[i], textX, textY, sPaint);
                    } else {
                        float textWidth = sPaint.measureText(displayMarketTimes[i]);
                        float textX = getChartX(getIndexFromDate(displayMarketTimes[i])) - textWidth / 2;
                        canvas.drawText(displayMarketTimes[i], textX, textY, sPaint);
                    }
                }
            }
        }
    }

    @Override
    protected int calculateTouchIndex(MotionEvent e) {
        float touchX = e.getX();
        return getIndexOfXAxis(touchX);
    }

    private float getChartX(TrendViewData model) {
        int indexOfXAxis = getIndexFromDate(model.getHHmm());
        mVisibleList.put(indexOfXAxis, model);
        return getChartX(indexOfXAxis);
    }

    @Override
    protected float getChartX(int index) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        float chartX = getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
        return chartX;
    }

    private int getIndexFromDate(String hhmm) {
        String[] timeLines = mSettings.getOpenMarketTimes();
        int size = timeLines.length;
        size = (size % 2 == 0 ? size : size - 1);

        int index = 0;
        for (int i = 0; i < size; i += 2) {
            if (TrendView.Util.isBetweenTimesClose(timeLines[i], timeLines[i + 1], hhmm)) {
                index = TrendView.Util.getDiffMinutes(timeLines[i], hhmm, "hh:mm");
                for (int j = 0; j < i; j += 2) {
                    // the total points of this period
                    index += TrendView.Util.getDiffMinutes(timeLines[j], timeLines[j + 1], "hh:mm");
                }
            }
        }
        return index;
    }

    @Override
    protected boolean hasThisTouchIndex(int touchIndex) {
        if (mVisibleList != null && mVisibleList.get(touchIndex) != null) {
            return true;
        }
        return super.hasThisTouchIndex(touchIndex);
    }

    @Override
    protected void drawTouchLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2, Canvas canvas) {
        if (hasThisTouchIndex(touchIndex)) {

            Log.d("TAG", "drawTouchLines: " + touchIndex);

//            TrendViewData model = mVisibleList.get(touchIndex);
//            float touchX = getChartX(touchIndex);
//            float touchY = getChartY(model.getLastPrice());
//
//            // draw model.date connected to vertical line
//            String date = model.getDate();
//            date = date.substring(8, 10) + ":" + date.substring(10, 12);
//            float dateY = top + mFontHeight / 2 + mOffset4CenterText;
//            float textWidth = sPaint.measureText(date);
//            float dateX = touchX - textWidth / 2;
//
//            // when date string touches the borders, add offset
//            dateX = dateX < 0 ? 0 + mXRectPadding : dateX;
//            dateX = dateX > width - textWidth ? width - textWidth - mXRectPadding : dateX;
//
//            // draw rectangle yellow background for date
//            RectF rectF = getBigFontBgRectF(dateX, dateY, textWidth);
//            setYellowBgPaint(sPaint);
//            canvas.drawRoundRect(rectF, 5, 5, sPaint);
//            setBlackTextPaint(sPaint);
//            canvas.drawText(date, dateX, dateY, sPaint);
//
//            // draw vertical line
//            setTouchLinePaint(sPaint);
//            float topY = top + rectF.height(); // connected to rect
//            float bottomY = height - mTextMargin - mFontHeight;
//            Path verticalPath = getPath();
//            verticalPath.moveTo(touchX, topY);
//            verticalPath.lineTo(touchX, bottomY);
//            canvas.drawPath(verticalPath, sPaint);
//
//            // draw model.lastPrice connected to horizontal line
//            String price = formatNumber(model.getLastPrice());
//            textWidth = sPaint.measureText(price);
//            float priceX = left + width - textWidth - mXRectPadding;
//            float priceY = touchY + mOffset4CenterText;
//
//            // when touchX is larger than half of width, move price to left
//            if (touchX > width / 2) {
//                priceX = left + mXRectPadding;
//            }
//
//            rectF = getBigFontBgRectF(priceX, priceY, textWidth);
//            setYellowBgPaint(sPaint);
//            canvas.drawRoundRect(rectF, 5, 5, sPaint);
//            setBlackTextPaint(sPaint);
//            canvas.drawText(price, priceX, priceY, sPaint);
//
//            // draw horizontal line
//            setTouchLinePaint(sPaint);
//            float leftX = left;
//            float rightX = leftX + width - rectF.width(); // connected to rect
//
//            // when touchX is larger than half of width, add offset for line.x
//            if (touchX > width / 2) {
//                leftX = leftX + rectF.width();
//                rightX = left + width;
//            }
//
//            Path horizontalPath = getPath();
//            horizontalPath.moveTo(leftX, touchY);
//            horizontalPath.lineTo(rightX, touchY);
//            canvas.drawPath(horizontalPath, sPaint);
        }
    }
}
