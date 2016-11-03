package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.jnhyxx.chart.domain.FlashViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

public class FlashView extends ChartView {

    private static final int INTERVAL_OF_POINTS = 2; //px

    private List<FlashViewData> mPointList;
    private Settings mSettings;
    private int mMaxPoints;

    public FlashView(Context context) {
        super(context);
        init();
    }

    public FlashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPointList = new LinkedList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = 2 * (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 3;
        mMaxPoints = maxWidth / INTERVAL_OF_POINTS + 1;
    }

    public void addData(FlashViewData point) {
        if (point != null && mPointList != null) {
            if (mMaxPoints > 0 && mPointList.size() >= mMaxPoints) {
                int indexBeforeDelete = mPointList.size() - mMaxPoints + 1;
                for (int i = 0; i < indexBeforeDelete; i++) {
                    mPointList.remove(i);
                }
            }

            mPointList.add(point);

            if (getVisibility() == VISIBLE) {
                redraw();
            }
        }
    }

    public void clearData() {
        if (mPointList != null) {
            mPointList.clear();
        }
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (Settings) settings;
        super.setSettings(settings);
        redraw();
    }

    private void setDashLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLUE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 4}, 1));
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

    @Override
    protected void calculateBaseLines(float[] baselines) {
        FlashViewData lastPoint = getLastPoint();
        if (lastPoint != null) {
            int areaCount = baselines.length - 1;
            float priceRange = new BigDecimal(mSettings.getFlashChartPriceInterval())
                    .divide(new BigDecimal(areaCount), mSettings.getNumberScale(),
                            RoundingMode.HALF_EVEN).floatValue();

            if (baselines[0] == 0 && baselines[baselines.length - 1] == 0) { // the first time
                BigDecimal bigMid = BigDecimal.valueOf(lastPoint.getLastPrice())
                        .setScale(mSettings.getNumberScale(), RoundingMode.HALF_EVEN);
                BigDecimal firstBaseLine = bigMid.add(new BigDecimal((areaCount / 2) * priceRange));

                if (baselines.length % 2 == 0) {
                    firstBaseLine = firstBaseLine.add(new BigDecimal(priceRange / 2));
                    setBaseLinesFromFirst(baselines, firstBaseLine.floatValue(), priceRange);
                } else {
                    setBaseLinesFromFirst(baselines, firstBaseLine.floatValue(), priceRange);
                }
                return;
            }

            float firstBaseLine = baselines[0];
            if (lastPoint.getLastPrice() > firstBaseLine - priceRange / 2) {
                while (lastPoint.getLastPrice() > firstBaseLine - priceRange / 2) {
                    firstBaseLine += priceRange;
                }
                setBaseLinesFromFirst(baselines, firstBaseLine, priceRange);
                return;
            }

            float lastBaseLine = baselines[baselines.length - 1];
            if (lastPoint.getLastPrice() < lastBaseLine - priceRange / 2) {
                while (lastPoint.getLastPrice() < lastBaseLine - priceRange / 2) {
                    lastBaseLine -= priceRange;
                }
                setBaselinesFromLast(baselines, lastBaseLine, priceRange);
                return;
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

            if (i != 0 && i != baselines.length - 1) {
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY + mOffset4CenterText;
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
                                    int left2, int top2, int width2, int height2, Canvas canvas) {
        int size = mPointList.size();
        Path path = getPath();
        float chartX = 0;
        float chartY = 0;
        float currentPrice = 0;
        for (int i = 0; i < size; i++) {
            currentPrice = mPointList.get(i).getLastPrice();
            chartX = getChartX(i);
            chartY = getChartY(currentPrice);
            if (path.isEmpty()) {
                path.moveTo(chartX, chartY);
            } else {
                path.lineTo(chartX, chartY);
            }
        }

        setRealTimeLinePaint(sPaint);
        canvas.drawPath(path, sPaint);

        if (size > 0) {
            // horizontal dash line to vertical line
            path = getPath();
            path.moveTo(chartX, chartY);
            path.lineTo(width - mPriceAreaWidth, chartY);
            setDashLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            // unstable price with rect bg
            setUnstablePricePaint(sPaint);
            String unstablePrice = formatNumber(currentPrice);
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

    @Override
    protected float getChartX(int index) {
        return index * INTERVAL_OF_POINTS + getPaddingLeft();
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {

    }

    public FlashViewData getLastPoint() {
        if (mPointList != null && mPointList.size() > 0) {
            return mPointList.get(mPointList.size() - 1);
        }
        return null;
    }

    protected void setBaseLinesFromFirst(float[] baselines, float firstBaseLine, float priceRange) {
        baselines[0] = firstBaseLine;
        for (int i = 1; i < baselines.length; i++) {
            baselines[i] = BigDecimal.valueOf(baselines[i - 1])
                    .subtract(new BigDecimal(priceRange))
                    .floatValue();
        }
    }

    protected void setBaselinesFromLast(float[] baselines, float lastBaseLine, float priceRange) {
        baselines[baselines.length - 1] = lastBaseLine;
        for (int i = baselines.length - 2; i >= 0; i--) {
            baselines[i] = BigDecimal.valueOf(baselines[i + 1])
                    .add(new BigDecimal(priceRange))
                    .floatValue();
        }
    }

    public static class Settings extends ChartSettings {

        private double flashChartPriceInterval;

        public Settings() {
            super();
            this.flashChartPriceInterval = 0;
        }

        public double getFlashChartPriceInterval() {
            return flashChartPriceInterval;
        }

        public void setFlashChartPriceInterval(double flashChartPriceInterval) {
            this.flashChartPriceInterval = flashChartPriceInterval;
        }
    }
}
