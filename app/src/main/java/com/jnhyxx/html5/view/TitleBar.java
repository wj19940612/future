package com.jnhyxx.html5.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

public class TitleBar extends RelativeLayout {

    private CharSequence mTitle;
    private int mTitleSize;
    private ColorStateList mTitleColor;
    private CharSequence mRightText;
    private int mRightTextSize;
    private ColorStateList mRightTextColor;
    private Drawable mRightImage;
    private boolean mRightVisible;
    private boolean mBackFeature;
    private Drawable mBackIcon;

    private TextView mTitleView;
    private TextView mLeftView;
    private TextView mRightView;
    private View mCustomView;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);

        int defaultTitleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18,
                getResources().getDisplayMetrics());
        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                getResources().getDisplayMetrics());

        mTitle = typedArray.getText(R.styleable.TitleBar_titleText);
        mTitleSize = typedArray.getDimensionPixelOffset(R.styleable.TitleBar_titleTextSize, defaultTitleSize);
        mTitleColor = typedArray.getColorStateList(R.styleable.TitleBar_titleTextColor);
        mRightText = typedArray.getText(R.styleable.TitleBar_rightText);
        mRightTextSize = typedArray.getDimensionPixelOffset(R.styleable.TitleBar_rightTextSize, defaultFontSize);
        mRightTextColor = typedArray.getColorStateList(R.styleable.TitleBar_rightTextColor);
        mRightImage = typedArray.getDrawable(R.styleable.TitleBar_rightImage);
        mRightVisible = typedArray.getBoolean(R.styleable.TitleBar_rightVisible, false);
        mBackFeature = typedArray.getBoolean(R.styleable.TitleBar_backFeature, false);
        mBackIcon = typedArray.getDrawable(R.styleable.TitleBar_backIcon);
        int customViewResId = typedArray.getResourceId(R.styleable.TitleBar_customView, -1);
        if (customViewResId != -1) {
            mCustomView = LayoutInflater.from(getContext()).inflate(customViewResId, null);
        }

        typedArray.recycle();
    }

    private void init() {
        setBackgroundResource(R.color.colorPrimary);
        int fixedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                getResources().getDisplayMetrics());

        // center view
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, fixedHeight);
        if (mCustomView != null) {
            addView(mCustomView, params);
        } else {
            mTitleView = new TextView(getContext());
            mTitleView.setGravity(Gravity.CENTER);
            addView(mTitleView, params);
        }

        // left view
        int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                getResources().getDisplayMetrics());
        params =  new LayoutParams(LayoutParams.WRAP_CONTENT, fixedHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        mLeftView = new TextView(getContext());
        mLeftView.setGravity(Gravity.CENTER);
        mLeftView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        addView(mLeftView, params);

        // right view
        mRightView = new TextView(getContext());
        mRightView.setGravity(Gravity.CENTER);
        mRightView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, fixedHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        addView(mRightView, params);
        if (mBackFeature) {
            setBackButtonIcon(mBackIcon);
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackClick(view);
                }
            });
        }
        setTitle(mTitle);
        setTitleSize(mTitleSize);
        setTitleColor(mTitleColor);
        setRightText(mRightText);
        setRightTextSize(mRightTextSize);
        setRightTextColor(mRightTextColor);
        setRightImage(mRightImage);
        setRightVisible(mRightVisible);
    }

    private void onBackClick(View view) {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            activity.onBackPressed();
        }
    }

    public void setBackButtonIcon(Drawable backIcon) {
        if (backIcon != null) {
            mLeftView.setCompoundDrawables(backIcon, null, null, null);
        } else { // default icon
            mLeftView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tb_back, 0, 0, 0);
        }
    }

    public void setTitle(int resid) {
        CharSequence title = getContext().getText(resid);
        setTitle(title);
    }

    public void setTitle(CharSequence title) {
        if (mTitleView == null) return;

        mTitle = title;
        if (TextUtils.isEmpty(mTitle)) return;
        mTitleView.setText(mTitle);
    }

    public void setTitleSize(int titleSize) {
        if (mTitleView == null) return;

        mTitleSize = titleSize;
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
    }

    public void setRightText(CharSequence rightText) {
        mRightText = rightText;
        mRightView.setText(rightText);
    }

    public void setRightTextSize(int rightTextSize) {
        mRightTextSize = rightTextSize;
        mRightView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
    }

    public void setRightImage(Drawable rightImage) {
        mRightImage = rightImage;
        mRightView.setCompoundDrawablesWithIntrinsicBounds(mRightImage, null, null, null);
    }

    public void setRightVisible(boolean rightVisible) {
        mRightView.setVisibility(rightVisible ? VISIBLE: INVISIBLE);
    }

    public void setOnRightViewClickListener(View.OnClickListener listener) {
        mRightView.setOnClickListener(listener);
    }

    public void setTitleColor(ColorStateList titleColor) {
        if (mTitleView == null) return;

        mTitleColor = titleColor;
        if (mTitleColor != null) {
            mTitleView.setTextColor(mTitleColor);
        } else {
            mTitleView.setTextColor(ColorStateList.valueOf(Color.WHITE));
        }
    }

    public void setRightTextColor(ColorStateList rightTextColor) {
        mRightTextColor = rightTextColor;
        if (mRightTextColor != null) {
            mRightView.setTextColor(mRightTextColor);
        } else {
            mRightView.setTextColor(ColorStateList.valueOf(Color.WHITE));
        }
    }

    public View getCustomView() {
        return mCustomView;
    }
}
