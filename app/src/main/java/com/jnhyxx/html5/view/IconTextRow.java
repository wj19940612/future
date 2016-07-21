package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

public class IconTextRow extends LinearLayout {

    private Drawable mLeftIcon;
    private Drawable mRightIcon;
    private CharSequence mText;
    private int mTextSize;
    private ColorStateList mTextColor;

    public IconTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconTextRow);

        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14,
                getResources().getDisplayMetrics());

        mLeftIcon = typedArray.getDrawable(R.styleable.IconTextRow_leftIcon);
        mRightIcon = typedArray.getDrawable(R.styleable.IconTextRow_rightIcon);
        mText = typedArray.getText(R.styleable.IconTextRow_rowText);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_rowTextSize, defaultFontSize);
        mTextColor = typedArray.getColorStateList(R.styleable.IconTextRow_rowTextColor);

        typedArray.recycle();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        setPadding(padding, padding, padding, padding);

        if (mLeftIcon != null) {
            ImageView leftIcon = new ImageView(getContext());
            leftIcon.setImageDrawable(mLeftIcon);
            addView(leftIcon);
        }

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.setMargins(padding, 0, padding, 0);
        TextView text = new TextView(getContext());
        text.setText(mText);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        text.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(0));
        addView(text, params);

        if (mRightIcon != null) {
            ImageView rightImage = new ImageView(getContext());
            rightImage.setImageDrawable(mRightIcon);
            addView(rightImage);
        }
    }

}
