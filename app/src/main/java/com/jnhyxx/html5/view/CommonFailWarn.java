package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

/**
 * Created by wangJie on 2016/9/11.
 */

public class CommonFailWarn extends RelativeLayout {

    private static final String TAG = "CommonFailWarn";


    private static final int HIDE_VIEW_TAG = 33;


    //显示的时间
    private static final int SHOW_TIME = 2000;

    private static final int ANIMATION_TIME = 500;

    private boolean isFirst = false;


    private CharSequence mCenterTxt;
    private int mCenterSize;
    private int mDrawLeftPadding;
    private ColorStateList mCenterTxtColor;
    private Drawable mCenterDrawable;

    private TextView mCenterView;
    private boolean mViewVisible;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_VIEW_TAG:
                    handleHide();
                    break;
            }
        }
    };


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
        mViewVisible = typedArray.getBoolean(R.styleable.CommonFailWarn_visible, false);

        setBackgroundResource(R.color.common_rise_activity_sum);
        setGravity(Gravity.CENTER);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());

        setPadding(padding, padding, padding, padding);
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
        setVisible(mViewVisible, isFirst);
    }
    public void setVisible(boolean viewVisible) {
        this.setVisible(viewVisible,true);
    }
    public void setVisible(boolean viewVisible, boolean isFirst) {
        this.mViewVisible = viewVisible;
        this.isFirst = isFirst;
//        float translationY = getTranslationY();
//        TranslateAnimation translateDownAnimation = new TranslateAnimation(0, 0, 0, translationY);
//        translateDownAnimation.setFillAfter(true);
//        translateDownAnimation.setDuration(ANIMATION_TIME);
//        translateDownAnimation.start();
        this.setVisibility(mViewVisible ? VISIBLE : INVISIBLE);
        if (isFirst) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_top);
            startAnimation(animation);

            if (isShown()) {
                mHandler.sendEmptyMessageDelayed(HIDE_VIEW_TAG, SHOW_TIME);
            }
        }
    }


    private void handleHide() {
        setVisible(false, true);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_top);
        startAnimation(animation);
        mHandler.removeMessages(HIDE_VIEW_TAG);
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
