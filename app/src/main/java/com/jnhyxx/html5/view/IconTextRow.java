package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

public class IconTextRow extends LinearLayout {

    private static final float HEIGHT_SPLIT_LINE_DP = 0.5f;

    private Drawable mLeftIcon;
    private Drawable mRightIcon;
    private Drawable mSubTextViewBg;
    private int mRightIconVisibility;
    private CharSequence mText;
    private int mTextSize;
    private ColorStateList mTextColor;
    private CharSequence mSubText;
    private int mSubTextSize;
    private ColorStateList mSubTextColor;
    private int mVerticalPaddingTop;
    private boolean mHasBottomSplitLine;
    private ColorStateList mSplitLineColor;

    private TextView mTextView;
    private TextView mSubTextView;

    private Paint mPaint;
    private float mSplitLineHeight;

    public IconTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconTextRow);


        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getResources().getDisplayMetrics());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftIcon = typedArray.getDrawable(R.styleable.IconTextRow_leftIcon);
        mRightIcon = typedArray.getDrawable(R.styleable.IconTextRow_rightIcon);
        mSubTextViewBg = typedArray.getDrawable(R.styleable.IconTextRow_subTextBackground);
        mRightIconVisibility = typedArray.getInt(R.styleable.IconTextRow_rightIconVisibility, 0);
        mText = typedArray.getText(R.styleable.IconTextRow_rowText);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_rowTextSize, defaultFontSize);
        mTextColor = typedArray.getColorStateList(R.styleable.IconTextRow_rowTextColor);
        mSubText = typedArray.getText(R.styleable.IconTextRow_subText);
        mSubTextSize = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_subTextSize, defaultFontSize);
        mSubTextColor = typedArray.getColorStateList(R.styleable.IconTextRow_subTextColor);
        mVerticalPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_rowVerticalPadding, 0);
        mHasBottomSplitLine = typedArray.getBoolean(R.styleable.IconTextRow_hasBottomSplitLine, false);
        mSplitLineColor = typedArray.getColorStateList(R.styleable.IconTextRow_splitLineColor);
        if (mSplitLineColor == null) {
            mSplitLineColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.black));
        }
        mSplitLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_SPLIT_LINE_DP,
                getResources().getDisplayMetrics());

        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHasBottomSplitLine) {
            mPaint.setColor(mSplitLineColor.getDefaultColor());
            mPaint.setStrokeWidth(mSplitLineHeight);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(0, getHeight() - mSplitLineHeight, getWidth(), getHeight() - mSplitLineHeight, mPaint);
        }
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        setPadding(padding, padding, padding, padding);
        if (mVerticalPaddingTop != 0) {
            setPadding(padding, mVerticalPaddingTop, padding, mVerticalPaddingTop);
        }

        LayoutParams params;
        if (mLeftIcon != null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, padding, 0);
            ImageView leftIcon = new ImageView(getContext());
            leftIcon.setImageDrawable(mLeftIcon);
            addView(leftIcon, params);
        }

        if (mText == null) mText = "";
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.setMargins(0, 0, padding, 0);
        mTextView = new TextView(getContext());
        mTextView.setText(mText);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTextView.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(Color.BLACK));
        addView(mTextView, params);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, padding, 0);
        mSubTextView = new TextView(getContext());
        mSubTextView.setText(mSubText);
        mSubTextView.setGravity(Gravity.CENTER);
        mSubTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubTextSize);
        mSubTextView.setTextColor(mSubTextColor != null ? mSubTextColor : ColorStateList.valueOf(Color.GRAY));
        if (mSubTextViewBg != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mSubTextView.setBackground(mSubTextViewBg);
            } else {
                mSubTextView.setBackgroundDrawable(mSubTextViewBg);
            }
        }
        addView(mSubTextView, params);

        if (mRightIcon != null) {
            ImageView rightImage = new ImageView(getContext());
            rightImage.setImageDrawable(mRightIcon);
            rightImage.setVisibility(mRightIconVisibility);
            addView(rightImage);
        }
    }

    public void setSubTextViewBg(int resId) {
        mSubTextView.setBackgroundResource(resId);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setSubText(String subText) {
        mSubTextView.setText(subText);
    }

    public void setText(int resid) {
        mTextView.setText(resid);
    }

    public void setSubText(int resid) {
        mSubTextView.setText(resid);
    }

    public String getSubText() {
        return mSubTextView.getText().toString();
    }

    public TextView getSubTextView() {
        return mSubTextView;
    }

}
