package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.jnhyxx.html5.R;

public class PageIndicator extends View {

    private int mCount;
    private int mInterval;
    private ColorStateList mPoint;
    private ColorStateList mSelectedPoint;
    private int mPointRadius;
    private int mCurrentIndex;

    private static Paint sPaint;
    private static RectF sRect;

    private int mDefaultRadius;
    private int mDefaultInterval;
    private boolean mInfinite;

    public PageIndicator(Context context) {
        super(context);

        init();
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();

        processAttrs(attrs);
    }

    private void init() {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sRect = new RectF();

        mCount = 1;

        mDefaultInterval = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());

        mInterval = mDefaultInterval;

        mPoint = ColorStateList.valueOf(Color.BLACK);
        mSelectedPoint = ColorStateList.valueOf(Color.WHITE);

        mDefaultRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());

        mPointRadius = mDefaultRadius;

        mCurrentIndex = 0;
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageIndicator);

        mCount = typedArray.getInt(R.styleable.PageIndicator_indicators, 1);
        mInterval = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_indicatorsInterval, mDefaultInterval);
        mPointRadius = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_pointRadius, mPointRadius);

        mPoint = typedArray.getColorStateList(R.styleable.PageIndicator_point);
        mSelectedPoint = typedArray.getColorStateList(R.styleable.PageIndicator_selectedPoint);

        if (mPoint == null) {
            mPoint = ColorStateList.valueOf(Color.BLACK);
        }
        if (mSelectedPoint == null) {
            mSelectedPoint = ColorStateList.valueOf(Color.WHITE);
        }
        mInfinite = typedArray.getBoolean(R.styleable.PageIndicator_infinite, false);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = measureDimension(widthMeasureSpec, (mPointRadius * 2 * mCount + (mCount - 1) * mInterval));
        int height = measureDimension(heightMeasureSpec, mPointRadius * 2);

        setMeasuredDimension(width, height);
    }

    private int measureDimension(int measureSpec, int defaultDimension) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = specSize;

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = Math.min(defaultDimension, specSize);
                break;
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = 0;
        int top = 0;

        for (int i = 0; i < mCount; i++) {
            int right = left + mPointRadius * 2;
            int bottom = top + mPointRadius * 2;

            if (i == mCurrentIndex) {
                sPaint.setColor(mSelectedPoint.getColorForState(getDrawableState(), 0));
            } else {
                sPaint.setColor(mPoint.getColorForState(getDrawableState(), 0));
            }

            sRect.set(left, top, right, bottom);
            canvas.drawOval(sRect, sPaint);

            left = right + mInterval;
        }
    }

    public void setCount(int count) {
        if (mCount != count) {
            mCount = count;
            invalidate();
        }
    }

    public void setInterval(int interval) {
        if (mInterval != interval) {
            mInterval = interval;
            invalidate();
        }
    }

    public void setPoint(ColorStateList point) {
        mPoint = point;
    }

    public void setPoint(int pointColor) {
        mPoint = ColorStateList.valueOf(pointColor);
        invalidate();
    }

    public void setSelectedPoint(ColorStateList selectedPoint) {
        mSelectedPoint = selectedPoint;
    }

    public void setSelectedPoint(int selectedPointColor) {
        mSelectedPoint = ColorStateList.valueOf(selectedPointColor);
        invalidate();
    }

    public void setPointRadius(int pointRadius) {
        if (mPointRadius != pointRadius) {
            mPointRadius = pointRadius;
            invalidate();
        }
    }

    public void move(int index) {
        if (mInfinite) {
            index = (index + mCount) % mCount;
        }

        if (index < 0 || index >= mCount) return;

        if (mCurrentIndex != index) {
            invalidate();
        }
    }

    public void next() {
        move(mCurrentIndex + 1);
    }

    public void back() {
        move(mCurrentIndex - 1);
    }

    public void setInfinite(boolean infinite) {
        mInfinite = infinite;
    }
}
