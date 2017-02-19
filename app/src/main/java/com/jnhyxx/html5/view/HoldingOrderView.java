package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.jnhyxx.html5.R;

public class HoldingOrderView extends ImageView {

    private static final float RADIUS_RED_POINT = 7.5f;
    private static final int TEXT_SIZE = 10;

    private Paint mPaint;
    private float mPointRadius;
    private int mNumber;
    private float mTextSize;
    private float mOffset4CenterText;

    public HoldingOrderView(Context context) {
        super(context);
        init();
    }

    public HoldingOrderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HoldingOrderView);
        mNumber = typedArray.getInt(R.styleable.HoldingOrderView_orderNumber, 0);
    }

    protected float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    protected float sp2Px(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        mPointRadius = dp2Px(RADIUS_RED_POINT);
        mTextSize = sp2Px(TEXT_SIZE);
        mPaint.setTextSize(mTextSize);
        mOffset4CenterText = calOffsetY4TextCenter();
    }

    protected float calOffsetY4TextCenter() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        return fontHeight / 2 - fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        if (mNumber > 0) {
            float centerX = width - mPointRadius;
            float centerY = mPointRadius;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            canvas.drawCircle(centerX, centerY, mPointRadius, mPaint);

            mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
            if (mNumber <= 99) {
                String numberStr = String.valueOf(mNumber);
                float textWidth = mPaint.measureText(numberStr);
                canvas.drawText(numberStr, centerX - textWidth / 2, centerY + mOffset4CenterText, mPaint);
            } else {
                String numberStr = String.valueOf("...");
                float textWidth = mPaint.measureText(numberStr);
                canvas.drawText(numberStr, centerX - textWidth / 2, centerY + mOffset4CenterText, mPaint);
            }
        }
    }

    public void setOrderNumber(int number) {
        mNumber = number;
        invalidate();
    }
}
