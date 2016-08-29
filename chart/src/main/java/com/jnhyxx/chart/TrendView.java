package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.jnhyxx.chart.domain.TrendViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TrendView extends ChartView {

    public static class Util {
        /**
         * get diff minutes bewteen endDate and startDate. endDate - startDate
         *
         * @param startDate
         * @param endDate
         * @return
         */
        public static int getDiffMinutes(String startDate, String endDate, String format) {
            long diff = 0;
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format);
                long start = parser.parse(startDate).getTime();
                long end = parser.parse(endDate).getTime();

                if (startDate.compareTo(endDate) <= 0) { // eg. 09:00 <= 09:10
                    diff = end - start;
                } else { // eg. 21:00 ~ 01:00, we should change 01:00 to 25:00
                    diff = end + 24 * 60 * 60 * 1000 - start;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                return (int) (diff / (60 * 1000));
            }
        }

        public static List<TrendViewData> createDataList(String data, String[] openMarketTime) {
            List<TrendViewData> result = new ArrayList<>();
            HashSet hashSet = new HashSet();
            int length = data.length();
            int start = 0;
            while (start < length) {
                int end = data.indexOf("|", start);
                if (end > start) {
                    String singleData = data.substring(start, end);
                    String[] splitData = singleData.split(",");
                    String date = splitData[2];
                    start = end + 1;
                    // filter invalid data and repeated data based on data.date
                    if (!isRepeatedDate(date, hashSet) && isValidDate(date, openMarketTime)) {
                        TrendViewData validData = new TrendViewData(splitData[0], Float.valueOf(splitData[1]), date);
                        result.add(validData);
                    }
                }
            }
            Log.d("TEST", "hashSet.size: " + hashSet.size());
            return result;
        }

        private static boolean isRepeatedDate(String date, HashSet hashSet) {
            String dateWithHourMinute = date.substring(8, 12); // yyyyMMddHHmmss -> hhmm
            return !hashSet.add(dateWithHourMinute);
        }

        /**
         * check if trendView data.date is valid
         *
         * @param date
         * @param openMarketTime
         * @return
         */
        public static boolean isValidDate(String date, String[] openMarketTime) {
            if (date.length() != 14) {
                return false;
            }

            String hhmm = date.substring(8, 10) + ":" + date.substring(10, 12); // yyyyMMddHHmmss -> hh:mm
            return Util.isBetweenTimes(openMarketTime, hhmm);
        }

        /**
         * check if time is between times[i] and times[i + 1] (open interval)
         * <br/> ps. 数据时间的合法性判断使用开区间
         *
         * @param times
         * @param time
         * @return
         */
        private static boolean isBetweenTimes(String[] times, String time) {
            int size = times.length;

            if (size % 2 != 0) {
                size = size - 1; // make size even
            }

            for (int i = 0; i < size; i += 2) {
                if (isBetweenTimes(times[i], times[i + 1], time)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * check if time is between time1 and time2 (open interval), time1 <= time < time2
         *
         *
         * @param time1
         * @param time2
         * @param time
         * @return
         */
        private static boolean isBetweenTimes(String time1, String time2, String time) {
            if (time1.compareTo(time2) <= 0) {
                return time.compareTo(time1) >= 0 && time.compareTo(time2) < 0;
            } else {
                return time.compareTo(time1) >= 0 || time.compareTo(time2) < 0;
            }
        }

        /**
         * check if time is between time1 and time2 (close interval), time1 <= time <= time2
         * <br/> ps. 数据时间的坐标的计算使用闭区间
         *
         * @param time1
         * @param time2
         * @param time
         * @return
         */
        private static boolean isBetweenTimesClose(String time1, String time2, String time) {
            if (time1.compareTo(time2) <= 0) {
                return time.compareTo(time1) >= 0 && time.compareTo(time2) <= 0;
            } else {
                return time.compareTo(time1) >= 0 || time.compareTo(time2) <= 0;
            }
        }
    }

    public static class Settings extends ChartSettings {

        private float mLimitUpPercent;
        private String mOpenMarketTimes;
        private String mDisplayMarketTimes;
        private boolean mCalculateXAxisFromOpenMarketTime;
        private boolean mXAxisRefresh;

        public Settings() {
            super();
            mLimitUpPercent = 0;
        }

        public void setLimitUpPercent(float limitUpPercent) {
            mLimitUpPercent = limitUpPercent;
        }

        public void setOpenMarketTimes(String openMarketTimes) {
            mOpenMarketTimes = openMarketTimes;
        }

        public String[] getOpenMarketTimes() {
            String[] result = new String[0];
            if (!TextUtils.isEmpty(mOpenMarketTimes)) {
                return mOpenMarketTimes.split(";");
            }
            return result;
        }

        public void setDisplayMarketTimes(String displayMarketTimes) {
            mDisplayMarketTimes = displayMarketTimes;
        }

        public String[] getDisplayMarketTimes() {
            String[] result = new String[0];
            if (!TextUtils.isEmpty(mDisplayMarketTimes)) {
                return mDisplayMarketTimes.split(";");
            }
            return result;
        }

        public float getLimitUp() {
            return getPreClosePrice() * mLimitUpPercent;
        }

        public void setCalculateXAxisFromOpenMarketTime(boolean value) {
            mCalculateXAxisFromOpenMarketTime = value;
            mXAxisRefresh = true;
        }

        @Override
        public int getXAxis() {
            if (mCalculateXAxisFromOpenMarketTime) {
                if (mXAxisRefresh) {
                    String[] openMarketTime = getOpenMarketTimes();
                    int size = openMarketTime.length % 2 == 0 ?
                            openMarketTime.length : openMarketTime.length - 1;
                    int xAxis = 0;
                    for (int i = 0; i < size; i += 2) {
                        xAxis += Util.getDiffMinutes(openMarketTime[i], openMarketTime[i + 1], "hh:mm");
                    }
                    setXAxis(xAxis - 1);
                    mXAxisRefresh = false;

                }
                return super.getXAxis();
            }
            return super.getXAxis();
        }
    }

    private List<TrendViewData> mDataList;
    private TrendViewData mUnstableData;
    private Map<Integer, TrendViewData> mVisibleModelList;

    private float mPriceAreaWidth;
    private Settings mSettings;

    public TrendView(Context context) {
        super(context);
        init();
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mVisibleModelList = new HashMap<>();
    }

    private void setDashLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLUE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 3}, 1));
    }

    private void setUnstablePricePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.WHITE.get()));
        paint.setTextSize(mBigFontSize);
        paint.setPathEffect(null);
    }

    private void setUnstablePriceBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLUE.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setTouchLinePaint(Paint paint) {
        paint.setColor(Color.parseColor("#7F7F7F"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2Px(1));
        paint.setPathEffect(null);
    }

    private void setRealTimeLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLUE.get()));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    private void setRealTimeFillPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.FILL.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(51);
    }

    protected void setYellowBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.YELLOW.get()));
        paint.setStyle(Paint.Style.FILL);
    }

    protected void setBlackTextPaint(Paint paint) {
        paint.setColor(Color.BLACK);
    }

    public void setDataList(List<TrendViewData> dataList) {
        mDataList = dataList;
        redraw();
    }

    public List<TrendViewData> getDataList() {
        return mDataList;
    }

    public void setUnstableData(TrendViewData unstableData) {
        mUnstableData = unstableData;
        redraw();
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (Settings) settings;
        super.setSettings(settings);
        redraw();
    }

    @Override
    public Settings getSettings() {
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
            /** expand max ~ min to not make trend line touch top and bottom **/
            baselines[0] = max + pricePadding;
            baselines[baselines.length - 1] = min - pricePadding;
            for (int i = baselines.length - 2; i > 0; i--) {
                baselines[i] = baselines[i + 1] + priceRange;
            }
        }
    }

    @Override
    protected void drawBaseLines(float[] baselines, int left, float top, int width, int height, Canvas canvas) {
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

    private float calculatePriceWidth(float baseline) {
        String preClosePrice = formatNumber(baseline);
        sPaint.setTextSize(mBigFontSize);
        float priceWidth = sPaint.measureText(preClosePrice);
        return getBigFontBgRectF(0, 0, priceWidth).width();
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

            if (mUnstableData != null) {
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
                RectF blueRect = getBigFontBgRectF(priceX, chartY + mOffset4CenterText,
                        sPaint.measureText(unstablePrice));
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
            if (Util.isBetweenTimesClose(timeLines[i], timeLines[i + 1], hhmm)) {
                index = Util.getDiffMinutes(timeLines[i], hhmm, "hh:mm");
                for (int j = 0; j < i; j += 2) {
                    // the total points of this period
                    index += Util.getDiffMinutes(timeLines[j], timeLines[j + 1], "hh:mm");
                }
            }
        }
        return index;
    }

    @Override
    protected boolean hasThisTouchIndex(int touchIndex) {
        if (mVisibleModelList != null && mVisibleModelList.containsKey(touchIndex)) {
            return true;
        }
        return super.hasThisTouchIndex(touchIndex);
    }

    @Override
    protected void drawTopTouchLines(int touchIndex, int left, int top, int width, int height, Canvas canvas) {
        if (hasThisTouchIndex(touchIndex)) {
            TrendViewData model = mVisibleModelList.get(touchIndex);
            float touchX = getChartX(touchIndex);
            float touchY = getChartY(model.getLastPrice());

            // draw model.date connected to vertical line
            String date = model.getDate();
            date = date.substring(8, 10) + ":" + date.substring(10, 12);
            float dateY = top + mFontHeight / 2 + mOffset4CenterText;
            float textWidth = sPaint.measureText(date);
            float dateX = touchX - textWidth / 2;

            // when date string touches the borders, add offset
            dateX = dateX < 0 ? 0 + mXRectPadding : dateX;
            dateX = dateX > width - textWidth ? width - textWidth - mXRectPadding : dateX;

            // draw rectangle yellow background for date
            RectF rectF = getBigFontBgRectF(dateX, dateY, textWidth);
            setYellowBgPaint(sPaint);
            canvas.drawRoundRect(rectF, 5, 5, sPaint);
            setBlackTextPaint(sPaint);
            canvas.drawText(date, dateX, dateY, sPaint);

            // draw vertical line
            setTouchLinePaint(sPaint);
            float topY = top + rectF.height(); // connected to rect
            float bottomY = height - mTextMargin - mFontHeight;
            Path verticalPath = getPath();
            verticalPath.moveTo(touchX, topY);
            verticalPath.lineTo(touchX, bottomY);
            canvas.drawPath(verticalPath, sPaint);

            // draw model.lastPrice connected to horizontal line
            String price = formatNumber(model.getLastPrice());
            textWidth = sPaint.measureText(price);
            float priceX = left + width - textWidth - mXRectPadding;
            float priceY = touchY + mOffset4CenterText;

            // when touchX is larger than half of width, move price to left
            if (touchX > width / 2) {
                priceX = left + mXRectPadding;
            }

            rectF = getBigFontBgRectF(priceX, priceY, textWidth);
            setYellowBgPaint(sPaint);
            canvas.drawRoundRect(rectF, 5, 5, sPaint);
            setBlackTextPaint(sPaint);
            canvas.drawText(price, priceX, priceY, sPaint);

            // draw horizontal line
            setTouchLinePaint(sPaint);
            float leftX = left;
            float rightX = leftX + width - rectF.width(); // connected to rect

            // when touchX is larger than half of width, add offset for line.x
            if (touchX > width / 2) {
                leftX = leftX + rectF.width();
                rightX = left + width;
            }

            Path horizontalPath = getPath();
            horizontalPath.moveTo(leftX, touchY);
            horizontalPath.lineTo(rightX, touchY);
            canvas.drawPath(horizontalPath, sPaint);
        }
    }
}
