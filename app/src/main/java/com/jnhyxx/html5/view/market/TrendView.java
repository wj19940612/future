package com.jnhyxx.html5.view.market;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jnhyxx.html5.domain.market.TrendViewData;
import com.johnz.kutils.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendView extends ChartView {

    private List<TrendViewData> mModelList;
    private TrendViewData mFloatingModel;
    private Map<Integer, TrendViewData> mVisibleModelList;

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

    protected void setUpPointPaint(Paint paint) {
        paint.setColor(Color.parseColor("#F44306"));
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
        paint.setColor(Color.parseColor(ChartColor.YELLOW.get()));
        paint.setStrokeWidth(dp2Px(1));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    protected void setYellowBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.YELLOW.get()));
        paint.setStyle(Paint.Style.FILL);
    }

    protected void setBlackTextPaint(Paint paint) {
        paint.setColor(Color.BLACK);
    }

    private void setTimeLineTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.YELLOW.get()));
    }

    public void setChartModels(List<TrendViewData> modelList) {
        mModelList = modelList;

        redraw();
    }

//    public void addFloatingPrice(Double lastPrice) {
//        if (mModelList != null && mModelList.size() > 0) {
//            TrendViewData lastModel = mModelList.get(mModelList.size() - 1);
//            mFloatingModel = new TrendViewData(lastModel, lastPrice);
//            if (mProduct != null && mProduct.isValidDate(mFloatingModel.getDate())) {
//                redraw();
//            } else {
//                mFloatingModel = null;
//            }
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mModelList != null && mModelList.size() > 0) {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (TrendViewData data: mModelList) {
                if (max < data.getLastPrice()) {
                    max = data.getLastPrice();
                }
                if (min > data.getLastPrice()) {
                    min = data.getLastPrice();
                }
            }

            if (mFloatingModel != null) {
                if (max < mFloatingModel.getLastPrice()) {
                    max = mFloatingModel.getLastPrice();
                }
                if (min > mFloatingModel.getLastPrice()) {
                    min = mFloatingModel.getLastPrice();
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

            baselines[0] = max;
            baselines[baselines.length - 1] = min;
            for (int i = baselines.length - 2; i > 0; i--) {
                baselines[i] = baselines[i + 1] + priceRange;
            }
        }
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int width, Canvas canvas) {
        if (mModelList != null && mModelList.size() > 0) {
            int size = mModelList.size();
            Path path = getPath();
            float chartX = 0;
            float chartY = 0;
            for (int i = 0; i < size; i++) {
                chartX = getChartX(mModelList.get(i));
                chartY = getChartY(mModelList.get(i).getLastPrice());
                if (path.isEmpty()) {
                    path.moveTo(chartX, chartY);
                } else {
                    path.lineTo(chartX, chartY);
                }
            }

            if (mFloatingModel != null) {
                chartX = getChartX(mFloatingModel);
                chartY = getChartY(mFloatingModel.getLastPrice());
                path.lineTo(chartX, chartY);
            }

            setRealTimeLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            setUpPointPaint(sPaint);
            canvas.drawCircle(chartX, chartY, 4, sPaint);
        }
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
//        if (mProduct != null && mModelList != null && mModelList.size() > 0) {
//            setTimeLineTextPaint(sPaint);
//            float startY = top + mFontHeight / 2 + mOffset4CenterText;

//            String[] times = mProduct.getTimeLine(mIsAtNight);
//            if (times != null && times.length > 0) {
//                float textWidth = sPaint.measureText(times[0]);
//                for (int i = 0; i < times.length; i++) {
//                    if (i == 0) {
//                        canvas.drawText(times[i], getChartX(0), startY, sPaint);
//                    } else if (i == times.length - 1) {
//                        canvas.drawText(times[i], getChartX(0) + width - textWidth, startY, sPaint);
//                    } else {
//                        int indexOfTime = mProduct.getIndexFromDate(times[i], mIsAtNight);
//                        canvas.drawText(times[i], getChartX(indexOfTime) - textWidth/2, startY, sPaint);
//                    }
//                }
//            }
//        }
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

    private int getIndexFromDate(String hhmm) {
        String[] timeLines = mSettings.getOpenMarketTimes();
        int size = timeLines.length;
        size = (size % 2 == 0 ? size : size - 1);

        int index = 0;
        for (int i = 0; i < size; i += 2) {
            if (DateUtil.isBetweenTimes(timeLines[i], timeLines[i + 1], hhmm)) {
                index = DateUtil.getDiffMinutes(timeLines[i], hhmm, "hh:mm");
                for (int j = 0; j < i; j += 2) {
                    // the total points of this period
                    index += DateUtil.getDiffMinutes(timeLines[j], timeLines[j + 1], "hh:mm") + 1;
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
            dateX = dateX < 0 ? 0 + mRectPadding : dateX;
            dateX = dateX > width - textWidth ? width - textWidth - mRectPadding : dateX;

            // draw rectangle yellow background for date
            RectF rectF = getTextBgRectF(dateX, dateY, textWidth);
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
            float priceX = left + width - textWidth - mRectPadding;
            float priceY = touchY + mOffset4CenterText;

            // when touchX is larger than half of width, move price to left
            if (touchX > width / 2) {
                priceX = left + mRectPadding;
            }

            rectF = getTextBgRectF(priceX, priceY, textWidth);
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
