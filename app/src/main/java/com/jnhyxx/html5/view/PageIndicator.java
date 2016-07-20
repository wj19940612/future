package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jnhyxx.html5.R;

public class PageIndicator extends LinearLayout {

    private int mCount;
    private int mMargin;
    private Drawable mPoint;
    private Drawable mSelectedPoint;
    private int mPointRadius;
    private int mCurrentIndex;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, mMargin, 0);

        for (int i = 0; i < mCount - 1; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(mPoint);
            addView(imageView, params);
        }

        if (mCount != 0) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(mPoint);
            addView(imageView);
        }

        move(0);
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageIndicator);

        mCount = typedArray.getInt(R.styleable.PageIndicator_indicators, 0);
        mMargin = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_indicatorsInterval, 0);
        mPointRadius = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_pointRadius, 5);

        Drawable pointDrawable = typedArray.getDrawable(R.styleable.PageIndicator_point);
        if (pointDrawable instanceof ColorDrawable) {
            int pointColor = ((ColorDrawable) pointDrawable).getColor();
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.setIntrinsicHeight(mPointRadius * 2);
            shapeDrawable.setIntrinsicWidth(mPointRadius * 2);
            shapeDrawable.getPaint().setColor(pointColor);
            mPoint = shapeDrawable;
        } else {
            mPoint = pointDrawable;
        }

        Drawable selectedDrawable = typedArray.getDrawable(R.styleable.PageIndicator_selectedPoint);
        if (selectedDrawable instanceof ColorDrawable) {
            int selectedColor = ((ColorDrawable) selectedDrawable).getColor();
            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(new OvalShape());
            shapeDrawable.setIntrinsicHeight(mPointRadius * 2);
            shapeDrawable.setIntrinsicWidth(mPointRadius * 2);
            shapeDrawable.getPaint().setColor(selectedColor);
            mSelectedPoint = shapeDrawable;
        } else {
            mSelectedPoint = selectedDrawable;
        }

        typedArray.recycle();
    }

    public void move(int index) {
        if (index < 0 || index >= mCount) return;

        ImageView imageView = (ImageView) getChildAt(mCurrentIndex);
        imageView.setImageDrawable(mPoint);
        imageView = (ImageView) getChildAt(index);
        imageView.setImageDrawable(mSelectedPoint);
        mCurrentIndex = index;
    }

    public void next() {
        move(mCurrentIndex + 1);
    }

    public void back() {
        move(mCurrentIndex - 1);
    }
}
