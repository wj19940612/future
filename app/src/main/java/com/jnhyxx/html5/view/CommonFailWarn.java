package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

/**
 * Created by wangJie on 2016/9/11.
 */

public class CommonFailWarn extends RelativeLayout {

    private static final String TAG = "CommonFailWarn";

    private CharSequence mCenterTxt;
    private int mCenterSize;
    private ColorStateList mCenterTxtColor;
    private Drawable mCenterDrawable;
    private int mDrawLeftPadding;

    private TextView mCenterView;

    public CommonFailWarn(Context context) {
        super(context);
    }

    public CommonFailWarn(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(context, attrs);

    }

    public CommonFailWarn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void processAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonFailWarn);

        int mDefaultTxtSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        int mDefaultTxtDrawLeftPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

        mCenterTxt = typedArray.getText(R.styleable.CommonFailWarn_centerText);
        mCenterTxtColor = typedArray.getColorStateList(R.styleable.CommonFailWarn_centerTextColor);
        mCenterSize = typedArray.getDimensionPixelOffset(R.styleable.CommonFailWarn_centerTextSize, mDefaultTxtSize);
        mCenterDrawable = typedArray.getDrawable(R.styleable.CommonFailWarn_centerDrawable);
        mDrawLeftPadding = typedArray.getDimensionPixelOffset(R.styleable.CommonFailWarn_centerDrawablePadding, mDefaultTxtDrawLeftPadding);

//        View mView = View.inflate(context, R.layout.common_fail_warn, this);
//        mCenterView = (TextView) mView.findViewById(R.id.commonFailTvWarn);
//        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
//        /// 这一步必须要做,否则不会显示.
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        textView.setCompoundDrawables(mCenterDrawable, null, null, null);

        init();
        typedArray.recycle();
    }

    private void init() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mCenterView != null) {
            addView(mCenterView, layoutParams);
        } else {
            mCenterView = new TextView(getContext());
            mCenterView.setGravity(Gravity.CENTER);
            addView(mCenterView, layoutParams);
        }
        setCenterTxt(mCenterTxt);
        setCenterTxtSize(mCenterSize);
        setCenterColor(mCenterTxtColor);
        setDrawLeft(mCenterDrawable);
    }

    private void setDrawLeft(Drawable centerDrawable) {
        if (mCenterView == null) return;
        mCenterDrawable = centerDrawable;
        if (mCenterDrawable != null) {
            mCenterDrawable.setBounds(0, 0, mCenterDrawable.getMinimumWidth(), mCenterDrawable.getMinimumHeight());
            mCenterView.setCompoundDrawables(mCenterDrawable, null, null, null);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_failed_warn);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mCenterView.setCompoundDrawables(drawable, null, null, null);
        }
        setDrawLeftPadding(mDrawLeftPadding);
    }

    private void setDrawLeftPadding(int mDrawLeftPadding) {
        if (mDrawLeftPadding == 0) {
            mCenterView.setCompoundDrawablePadding(5);
        } else {
            mCenterView.setCompoundDrawablePadding(mDrawLeftPadding);
        }
    }

    public void setCenterColor(ColorStateList centerTxtColor) {
        if (mCenterView == null) return;
        mCenterTxtColor = centerTxtColor;
        if (mCenterTxtColor != null) {
            mCenterView.setTextColor(mCenterTxtColor);
        } else {
            mCenterView.setTextColor(ColorStateList.valueOf(Color.WHITE));
        }
    }

    public void setCenterTxt(int centerTxtId) {
        CharSequence text = getContext().getText(centerTxtId);
        setCenterTxt(text);
    }

    public void setCenterTxt(CharSequence centerTxt) {
        if (mCenterView == null) return;
        mCenterTxt = centerTxt;
        if (TextUtils.isEmpty(mCenterTxt)) return;
        mCenterView.setText(mCenterTxt);
    }

    public void setCenterTxtSize(int centerTxtSize) {
        if (mCenterView == null) return;
        mCenterSize = centerTxtSize;
        mCenterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerTxtSize);
    }

}
