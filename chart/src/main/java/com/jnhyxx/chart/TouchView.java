package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.jnhyxx.chart.domain.TrendViewData;

public class TouchView extends View {

    private GestureDetector mGestureDetector;
    private ChartView mChartView;

    private boolean mShowCross;
    private int mTouchIndex;

    public TouchView(Context context, ChartView chartView) {
        super(context);
        mChartView = chartView;
        mGestureDetector = new GestureDetector(getContext(), new DefaultGestureListener());
        mShowCross = false;
        mTouchIndex = -1;
    }

    private class DefaultGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = "TEST";

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress: ");
            mShowCross = true;
            mTouchIndex = mChartView.calculateTouchIndex(e);
            redraw();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll: ex1: " + e1.getX() + ", ex2: " + e2.getX());
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress: ");
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: ");

            mShowCross = false;
            mTouchIndex = -1;
            redraw();

            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTouchIndex == -1) return;

        drawTopTouchLines(mTouchIndex, getLeft(), getTop(),
                getWidth() - getPaddingLeft() - getPaddingRight(), mChartView.getTopPartHeight(),
                canvas);
    }

    private void setRedRectBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.RED.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setCrossLineTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.WHITE.get()));
        paint.setTextSize(mChartView.mBigFontSize);
        paint.setPathEffect(null);
    }

    private void setCrossLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.RED.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    private void drawTopTouchLines(int touchIndex, int left, int top, int width, int height, Canvas canvas) {
        if (mChartView instanceof TrendChart) {
            TrendViewData data = ((TrendChart) mChartView).getVisibleList().get(touchIndex);
            float touchX = mChartView.getChartX(touchIndex);
            float touchY = mChartView.getChartY(data.getLastPrice());

            // draw date connected to vertical line
            setCrossLineTextPaint(mChartView.sPaint);
            float dateWidth = mChartView.sPaint.measureText(data.getHHmm());
            RectF tmpRect = mChartView.getBigFontBgRectF(0, 0, dateWidth);
            float dateX = touchX - dateWidth / 2;
            float dateY = top + height + tmpRect.height() / 2 + mChartView.mOffset4CenterBigText;
            RectF redRect = mChartView.getBigFontBgRectF(dateX, dateY, dateWidth);
            setRedRectBgPaint(mChartView.sPaint);
            canvas.drawRoundRect(redRect, 2, 2, mChartView.sPaint);
            setCrossLineTextPaint(mChartView.sPaint);
            canvas.drawText(data.getHHmm(), dateX, dateY, mChartView.sPaint);
            // vertical line
            setCrossLinePaint(mChartView.sPaint);
            Path path = mChartView.getPath();
            path.moveTo(touchX, top);
            path.lineTo(touchX, top + height);
            canvas.drawPath(path, mChartView.sPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                Log.d("TEST", "onTouchEvent: move");
                if (mShowCross) {
                    int newTouchIndex = mChartView.calculateTouchIndex(event);
                    if (newTouchIndex != mTouchIndex) {
                        if (mChartView.hasThisTouchIndex(newTouchIndex)) {
                            mTouchIndex = newTouchIndex;
                            redraw();
                            return true;
                        }
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    private void redraw() {
        invalidate(0, 0, getWidth(), getHeight());
    }
}
