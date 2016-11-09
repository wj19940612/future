package com.lecloud.skin.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lecloud.skin.R;

@SuppressWarnings({"deprecation", "unused"})
public class V4PageSeekBar extends View {
    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED = {android.R.attr.state_pressed,
            android.R.attr.state_window_focused,};

    private static final int ANIMATION_DIRECTION_NONE = 0;
    private static final int ANIMATION_DIRECTION_LEFT_PAGE = 1;
    private static final int ANIMATION_DIRECTION_RIGHT_PAGE = 2;

    protected int progressWidth;
    protected int secondaryProgressWidth;

    private long mMinValue;
    private long mMaxValue;
    private long mCurrentValue;

    private long mCurrentTouchValue;

    private long mPerPageValue;
    private long mLeftPageIndicatorValue;
    private long mRightPageIndicatorValue;

    private long mMinPage;
    private long mCurrentPage;
    private long mTotalPage;

    private Drawable mProgressBackgroundDrawable;
    private Drawable mCurrentThumb;
    private Drawable mProgressDrawable;
    private Drawable mSecondaryProgressDrawable;
    private Drawable mLeftPageIndicatorDrawable;
    private Drawable mRightPageIndicatorDrawable;

    private Paint mTextPaint;
    private String mText;
    private int mTextColor;
    private float mTextSize;
    private int mTextPaddingTop;

    private int mMeasuredTextHeight;
    private int mMeasuredTextBaselineHeight;
    private int mMeasuredHeight;

    private boolean mTracking;

    private int mAnimatingDirection;
    private float mOffset;
    private ValueAnimator mSlideLeftPageAnimator;
    private ValueAnimator mSlideRightPageAnimator;

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public V4PageSeekBar(Context context) {
        this(context, null);
    }

    public V4PageSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public V4PageSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Style define
        Resources res = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.V4PageSeekBar, defStyleAttr, 0);
        mMinValue = a.getInt(R.styleable.V4PageSeekBar_v4PageSeekBar_minValue, 0);
        mMaxValue = a.getInt(R.styleable.V4PageSeekBar_v4PageSeekBar_maxValue, 100);
        mCurrentValue = a.getInt(R.styleable.V4PageSeekBar_v4PageSeekBar_currentValue, 0);
        int perPageValue = a.getInt(R.styleable.V4PageSeekBar_v4PageSeekBar_perPageValue, 100);
        int leftPageIndicatorValue = a.getInt(R.styleable.V4PageSeekBar_v4PageSeekBar_leftPageIndicatorValue, 10);
        int rightPageIndicatorValue = a.getInt(R.styleable.V4PageSeekBar_v4PageSeekBar_rightPageIndicatorValue, 10);
        mProgressBackgroundDrawable = a.getDrawable(R.styleable.V4PageSeekBar_v4PageSeekBar_progressBackgroundDrawable);
        mCurrentThumb = a.getDrawable(R.styleable.V4PageSeekBar_v4PageSeekBar_currentThumb);
        mProgressDrawable = a.getDrawable(R.styleable.V4PageSeekBar_v4PageSeekBar_progressDrawable);
        mSecondaryProgressDrawable = a.getDrawable(R.styleable.V4PageSeekBar_v4PageSeekBar_secondaryProgressDrawable);
        mLeftPageIndicatorDrawable = a.getDrawable(R.styleable.V4PageSeekBar_v4PageSeekBar_leftPageIndicatorDrawable);
        mRightPageIndicatorDrawable = a.getDrawable(R.styleable.V4PageSeekBar_v4PageSeekBar_rightPageIndicatorDrawable);
        mText = a.getString(R.styleable.V4PageSeekBar_v4PageSeekBar_text);
        mTextColor = a.getColor(R.styleable.V4PageSeekBar_v4PageSeekBar_textColor, res.getColor(R.color.v4_page_seek_bar_text_color));
        mTextSize = a.getDimension(R.styleable.V4PageSeekBar_v4PageSeekBar_textSize, res.getDimension(R.dimen.v4_page_seek_bar_text_size));
        mTextPaddingTop = a.getDimensionPixelSize(R.styleable.V4PageSeekBar_v4PageSeekBar_textPaddingTop, res.getDimensionPixelSize(R.dimen.v4_page_seek_bar_text_padding_top));
        a.recycle();

        setPage(perPageValue, leftPageIndicatorValue, rightPageIndicatorValue);

        if (mProgressBackgroundDrawable == null) {
            mProgressBackgroundDrawable = res.getDrawable(R.drawable.letv_skin_v4_page_seek_bar_background);
        }
        if (mCurrentThumb == null) {
            mCurrentThumb = res.getDrawable(R.drawable.letv_skin_v4_new_seek_bar_thumb);
        }
        if (mProgressDrawable == null) {
            mProgressDrawable = res.getDrawable(R.drawable.letv_skin_v4_page_seek_bar_progress);
        }
        if (mSecondaryProgressDrawable == null) {
            mSecondaryProgressDrawable = res.getDrawable(R.drawable.letv_skin_v4_page_seek_bar_secondary_progress);
        }
        if (mLeftPageIndicatorDrawable == null) {
            mLeftPageIndicatorDrawable = res.getDrawable(R.drawable.letv_skin_v4_page_seek_bar_indicator);
        }
        if (mRightPageIndicatorDrawable == null) {
            mRightPageIndicatorDrawable = res.getDrawable(R.drawable.letv_skin_v4_page_seek_bar_indicator);
        }

        // Text paint
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        // Animation reset
        mAnimatingDirection = ANIMATION_DIRECTION_NONE;

        // Current touch value default -1
        mCurrentTouchValue = -1;

        // Calculate page
        calculatePage();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int dw = measureWidth(widthMeasureSpec);
        final int dh = measureHeight(heightMeasureSpec);
        Log.d("hua", "dw dh " + dw + " " + dh);
        setMeasuredDimension(dw, dh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int saveCount = canvas.save();

        final int leftPadding = getPaddingLeft();
        final int rightPadding = getPaddingRight();
        final int topPadding = getPaddingTop();
        final int bottomPadding = getPaddingBottom();
        final int width = getWidth() - leftPadding - rightPadding;
        final int height = getHeight() - topPadding - bottomPadding;
        if (width <= 0 || height <= 0) {
            return;
        }

        canvas.translate(leftPadding, topPadding);

        // Center vertical
        if (height > mMeasuredHeight) {
            canvas.translate(0, (height - mMeasuredHeight) / 2);
        }

        // Progress bar height
        final int topAreaHeight = mMeasuredHeight - mMeasuredTextHeight - mTextPaddingTop;
        final int progressBarHeight = topAreaHeight / 3;

        // Progress width
        long currentValue = mCurrentTouchValue >= 0 ? mCurrentTouchValue : mCurrentValue;
        int progressDestWidth = calculateProgressWidth(currentValue, mCurrentPage, width);
        int secondaryProgressDestWidth = calculateProgressWidth(mMaxValue, mCurrentPage, width);
        progressWidth = progressDestWidth + calculateAnimatorOffset(currentValue, progressDestWidth, width);
        secondaryProgressWidth = secondaryProgressDestWidth + calculateAnimatorOffset(mMaxValue, secondaryProgressDestWidth, width);

        // Background
        if (mProgressBackgroundDrawable != null) {
            mProgressBackgroundDrawable.setBounds(0, progressBarHeight, width, 2 * progressBarHeight);
            mProgressBackgroundDrawable.draw(canvas);
        }

        // Secondary progress
        if (mSecondaryProgressDrawable != null) {
            mSecondaryProgressDrawable.setBounds(0,
                    progressBarHeight,
                    secondaryProgressWidth,
                    2 * progressBarHeight);
            mSecondaryProgressDrawable.draw(canvas);
        }

        // Progress
        if (mProgressDrawable != null) {
            mProgressDrawable.setBounds(0,
                    progressBarHeight,
                    progressWidth,
                    2 * progressBarHeight);
            mProgressDrawable.draw(canvas);
        }

        // Page indicator
        if (mLeftPageIndicatorDrawable != null && mCurrentPage > mMinPage) {
            int left = calculateLeftPageIndicatorWidth(width);
            left -= progressBarHeight / 2;
            left = left < 0 ? 0 : left;

            mLeftPageIndicatorDrawable.setBounds(left,
                    progressBarHeight,
                    progressBarHeight + left,
                    progressBarHeight + progressBarHeight);
            mLeftPageIndicatorDrawable.draw(canvas);
        }

        if (mRightPageIndicatorDrawable != null) {
            int left = calculateRightPageIndicatorWidth(width);
            left -= progressBarHeight / 2;
            left = left < 0 ? 0 : left;

            mRightPageIndicatorDrawable.setBounds(left,
                    progressBarHeight,
                    progressBarHeight + left,
                    progressBarHeight + progressBarHeight);
            mRightPageIndicatorDrawable.draw(canvas);
        }

        // Current thumb
        if (mCurrentThumb != null) {
            int left = progressWidth;
            int thumbWidth = mCurrentThumb.getIntrinsicWidth();
            left -= thumbWidth / 2;
            left = Math.max(left, 0);
            left = Math.min(left, width - thumbWidth);

            mCurrentThumb.setBounds(left,
                    0,
                    3 * progressBarHeight + left,
                    3 * progressBarHeight);
            mCurrentThumb.draw(canvas);
        }

        // Text
        if (mText != null) {
            int textWidth = (int) mTextPaint.measureText(mText);

            int left = progressWidth;
            left = left < 0 ? 0 : left;
            left = Math.min(left, width - textWidth);

            canvas.drawText(mText,
                    left,
                    topAreaHeight + mTextPaddingTop - mMeasuredTextBaselineHeight,
                    mTextPaint);
        }

        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAnimatingDirection != ANIMATION_DIRECTION_NONE) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mTracking = isTracking(event);
                if (mTracking) {
                    if (mCurrentThumb != null) {
                        mCurrentThumb.setState(STATE_PRESSED);
                    }
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                } else {
                    mCurrentTouchValue = getTouchEventValue(event);
                    postInvalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mTracking) {
                    updateCurrentValue(event);
                } else {
                    mCurrentTouchValue = getTouchEventValue(event);
                    postInvalidate();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mCurrentTouchValue = -1;

                if (mTracking || event.getAction() == MotionEvent.ACTION_UP) {
                    updateCurrentValue(event);
                }

                if (mTracking && mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                }

                if (mCurrentThumb != null) {
                    mCurrentThumb.setState(STATE_NORMAL);
                }
                mTracking = false;

                postInvalidate();
                break;
            }
        }

        return true;
    }

    public void setProgress(long minValue, long currentValue, long maxValue) {
        final long oldPage = mCurrentPage;

        mMinValue = minValue;
        mCurrentValue = currentValue;
        mMaxValue = maxValue;

        calculatePage();
        postInvalidate();

        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, mCurrentValue, false);
        }

        if (mCurrentPage > oldPage) {
            // Slide right
            startSlideRightPageAnimation();
        } else if (mCurrentPage < oldPage) {
            // Slide left
            startSlideLeftPageAnimation();
        }
    }

    public void setProgressImmediately(long minValue, long currentValue, long maxValue) {
        stopAnimation();

        mMinValue = minValue;
        mCurrentValue = currentValue;
        mMaxValue = maxValue;

        calculatePage();
        postInvalidate();

        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, mCurrentValue, false);
        }
    }

    public void setPage(long perPageValue, long leftPageIndicatorValue, long rightPageIndicatorValue) {
        if (perPageValue <= 0) {
            throw new IllegalArgumentException("perPageValue must bigger than 0!");
        }
        if (leftPageIndicatorValue < 0 || rightPageIndicatorValue < 0) {
            throw new IllegalArgumentException(
                    leftPageIndicatorValue == 0 ? "leftPageIndicatorValue" : "rightPageIndicatorValue"
                            + " can not smaller than 0!");
        }
        mPerPageValue = perPageValue;
        mLeftPageIndicatorValue = leftPageIndicatorValue;
        mRightPageIndicatorValue = rightPageIndicatorValue;

        calculatePage();
        postInvalidate();
    }

    public long getMinValue() {
        return mMinValue;
    }

    public long getMaxValue() {
        return mMaxValue;
    }

    public long getCurrentValue() {
        return mCurrentValue;
    }

    public long getPerPageValue() {
        return mPerPageValue;
    }

    public long getRightPageIndicatorValue() {
        return mRightPageIndicatorValue;
    }

    public long getLeftPageIndicatorValue() {
        return mLeftPageIndicatorValue;
    }

    public long getCurrentPage() {
        return mCurrentPage;
    }

    public long getTotalPage() {
        return mTotalPage;
    }

    public long getMinPage() {
        return mMinPage;
    }

    public boolean isTracking() {
        return mTracking;
    }

    public void setTracking(boolean tracking) {
        mTracking = tracking;
    }

    public Drawable getProgressBackgroundDrawable() {
        return mProgressBackgroundDrawable;
    }

    public void setProgressBackgroundDrawable(Drawable progressBackgroundDrawable) {
        mProgressBackgroundDrawable = progressBackgroundDrawable;
        postInvalidate();
    }

    public Drawable getCurrentThumb() {
        return mCurrentThumb;
    }

    public void setCurrentThumb(Drawable currentThumb) {
        mCurrentThumb = currentThumb;
        postInvalidate();
    }

    public Drawable getProgressDrawable() {
        return mProgressDrawable;
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        mProgressDrawable = progressDrawable;
        postInvalidate();
    }

    public Drawable getSecondaryProgressDrawable() {
        return mSecondaryProgressDrawable;
    }

    public void setSecondaryProgressDrawable(Drawable secondaryProgressDrawable) {
        mSecondaryProgressDrawable = secondaryProgressDrawable;
        postInvalidate();
    }

    public Drawable getLeftPageIndicatorDrawable() {
        return mLeftPageIndicatorDrawable;
    }

    public void setLeftPageIndicatorDrawable(Drawable leftPageIndicatorDrawable) {
        mLeftPageIndicatorDrawable = leftPageIndicatorDrawable;
        postInvalidate();
    }

    public Drawable getRightPageIndicatorDrawable() {
        return mRightPageIndicatorDrawable;
    }

    public void setRightPageIndicatorDrawable(Drawable rightPageIndicatorDrawable) {
        mRightPageIndicatorDrawable = rightPageIndicatorDrawable;
        postInvalidate();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        postInvalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        postInvalidate();
    }

    public int getTextPaddingTop() {
        return mTextPaddingTop;
    }

    public void setTextPaddingTop(int textPaddingTop) {
        mTextPaddingTop = textPaddingTop;
        postInvalidate();
    }

    public void setOnSeekBarChangeListenerListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    private void calculatePage() {
        // If is tracking, don't flip page
        if (mTracking) {
            return;
        }

        ensureValueCorrect();

        // First page is bigger than other pages with mLeftPageIndicatorValue
        // So we reduce this value when calc page
        long minValue = mMinValue - mLeftPageIndicatorValue;
        long maxValue = mMaxValue - mLeftPageIndicatorValue;
        long currentValue = mCurrentValue - mLeftPageIndicatorValue;
        minValue = Math.max(0, minValue);
        maxValue = Math.max(0, maxValue);
        currentValue = Math.max(0, currentValue);

        mMinPage = minValue / mPerPageValue;
        mTotalPage = maxValue / mPerPageValue - 1;
        if (maxValue % mPerPageValue > 0) {
            mTotalPage += 1;
        }
        if (mTotalPage < 0) {
            mTotalPage = 0;
        }
        mCurrentPage = currentValue / mPerPageValue - 1;
        if (currentValue % mPerPageValue >= 0) {
            mCurrentPage += 1;
        }
        if (mCurrentPage < 0) {
            mCurrentPage = 0;
        }
        if (mCurrentPage > mTotalPage) {
            mCurrentPage = mTotalPage;
        }
    }

    private void ensureValueCorrect() {
        mMinValue = Math.max(mMinValue, 0);
        mCurrentValue = Math.max(mCurrentValue, 0);
        mMaxValue = Math.max(mMaxValue, 0);

        mMinValue = Math.min(mMinValue, mMaxValue);
        mCurrentValue = Math.max(mMinValue, mCurrentValue);
        mCurrentValue = Math.min(mCurrentValue, mMaxValue);
    }

    private int measureWidth(int widthMeasureSpec) {
        int backgroundWidth = mProgressBackgroundDrawable == null ? 0 : mProgressBackgroundDrawable.getIntrinsicWidth();
        int progressWidth = mProgressDrawable == null ? 0 : mProgressDrawable.getIntrinsicWidth();
        int secondaryProgressWidth = mSecondaryProgressDrawable == null ? 0 : mSecondaryProgressDrawable.getIntrinsicWidth();
        int leftIndicatorWidth = mLeftPageIndicatorDrawable == null ? 0 : mLeftPageIndicatorDrawable.getIntrinsicWidth();
        int rightIndicatorWidth = mRightPageIndicatorDrawable == null ? 0 : mRightPageIndicatorDrawable.getIntrinsicWidth();
        int thumbWidth = mCurrentThumb == null ? 0 : mCurrentThumb.getIntrinsicWidth();

        int width = Math.max(leftIndicatorWidth + rightIndicatorWidth /* Tow indicator */,
                Math.max(backgroundWidth /* background */,
                        Math.max(progressWidth /* progress */,
                                secondaryProgressWidth /* secondary progress */)));

        width = Math.max(thumbWidth, width);

        width += getPaddingLeft() + getPaddingRight();
        return resolveSizeAndState(width, widthMeasureSpec, 0);
    }

    private int measureHeight(int heightMeasureSpec) {
        int backgroundHeight = mProgressBackgroundDrawable == null ? 0 : mProgressBackgroundDrawable.getIntrinsicHeight();
        int progressHeight = mProgressDrawable == null ? 0 : mProgressDrawable.getIntrinsicHeight();
        int secondaryProgressHeight = mSecondaryProgressDrawable == null ? 0 : mSecondaryProgressDrawable.getIntrinsicHeight();
        int leftIndicatorHeight = mLeftPageIndicatorDrawable == null ? 0 : mLeftPageIndicatorDrawable.getIntrinsicHeight();
        int rightIndicatorHeight = mRightPageIndicatorDrawable == null ? 0 : mRightPageIndicatorDrawable.getIntrinsicHeight();
        int thumbHeight = mCurrentThumb == null ? 0 : mCurrentThumb.getIntrinsicHeight();

        int height = Math.max(
                Math.max(leftIndicatorHeight /* left indicator height */,
                        rightIndicatorHeight /* right indicator height */),
                Math.max(backgroundHeight /* background */,
                        Math.max(progressHeight /* progress */,
                                secondaryProgressHeight /* secondary progress */)));

        height = Math.max(thumbHeight, height * 3);

        // Add bottom text height
        height += mTextPaddingTop;
        Paint.FontMetricsInt metricsInt = mTextPaint.getFontMetricsInt();
        mMeasuredTextHeight = metricsInt.descent - metricsInt.ascent;
        mMeasuredTextBaselineHeight = metricsInt.ascent;
        height += mMeasuredTextHeight;
        height += getPaddingTop() + getPaddingBottom();

        mMeasuredHeight = height;

        return resolveSizeAndState(height, heightMeasureSpec, 0);
    }

    private boolean isTracking(MotionEvent e) {
        int left = progressWidth;
        left -= mCurrentThumb.getIntrinsicWidth() / 2 + 20;
        left = left < 0 ? 0 : left;

        int right = left + mCurrentThumb.getIntrinsicWidth() + 20;

        return e.getX() >= left && e.getX() <= right;
    }

    private void updateCurrentValue(MotionEvent e) {
        long currentValue = getTouchEventValue(e);
        final long oldPage = mCurrentPage;

        if (currentValue != mCurrentValue) {
            mCurrentValue = currentValue;
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener.onProgressChanged(this, mCurrentValue, true);
            }
        }

        calculatePage();
        postInvalidate();

        if (mCurrentPage > oldPage) {
            // Slide right
            startSlideRightPageAnimation();
        } else if (mCurrentPage < oldPage) {
            // Slide left
            startSlideLeftPageAnimation();
        }

    }

    private long getTouchEventValue(MotionEvent e) {
        final int leftPadding = getPaddingLeft();
        final int rightPadding = getPaddingRight();
        final int topPadding = getPaddingTop();
        final int bottomPadding = getPaddingBottom();
        final int width = getWidth() - leftPadding - rightPadding;
        final int height = getHeight() - topPadding - bottomPadding;
        if (width <= 0 || height <= 0) {
            return 0;
        }

        long allValue = mPerPageValue + mLeftPageIndicatorValue + mRightPageIndicatorValue;
        long startValue = mPerPageValue * mCurrentPage;

        float currentRatio = e.getX() / width;
        currentRatio = Math.max(0, currentRatio);
        currentRatio = Math.min(1, currentRatio);

        long currentValue = Math.round(currentRatio * allValue) + startValue;
        currentValue = Math.max(mMinValue, currentValue);
        currentValue = Math.min(mMaxValue, currentValue);

        return currentValue;
    }

    private int calculateAnimatorOffset(long progressValue, int destWidth, int totalWidth) {
        if (mAnimatingDirection == ANIMATION_DIRECTION_NONE) {
            return 0;
        }

        final long oldPage = mCurrentPage + (mAnimatingDirection == ANIMATION_DIRECTION_LEFT_PAGE ? 1 : -1);
        if (oldPage < 0) {
            return 0;
        }

        int oldWidth = calculateProgressWidth(progressValue, oldPage, totalWidth);
        return (int) ((oldWidth - destWidth) * mOffset);
    }

    private int calculateProgressWidth(long progressValue, long curPage, int totalWidth) {
        progressValue = Math.max(0, mCurrentPage > 0 ? progressValue - mLeftPageIndicatorValue : progressValue);
        long page = progressValue / mPerPageValue;
        long value = progressValue % mPerPageValue;
        value += page != curPage ? (page - curPage) * mPerPageValue : 0;
        value += mCurrentPage > 0 ? mLeftPageIndicatorValue : 0;

        long allValue = mPerPageValue + mLeftPageIndicatorValue + mRightPageIndicatorValue;
        if (value >= allValue) {
            return totalWidth;
        } else {
            return (int) (1.0f * totalWidth * value / allValue);
        }
    }

    private int calculateLeftPageIndicatorWidth(int totalWidth) {
        long allValue = mPerPageValue + mLeftPageIndicatorValue + mRightPageIndicatorValue;
        return (int) (1.0f * totalWidth * mLeftPageIndicatorValue / allValue);
    }

    private int calculateRightPageIndicatorWidth(int totalWidth) {
        long allValue = mPerPageValue + mLeftPageIndicatorValue + mRightPageIndicatorValue;
        long value = mPerPageValue + mLeftPageIndicatorValue;
        return (int) (1.0f * totalWidth * value / allValue);
    }

    private void startSlideLeftPageAnimation() {
        if (mAnimatingDirection != ANIMATION_DIRECTION_NONE) {
            stopAnimation();
        }

        if (mSlideLeftPageAnimator == null) {
            mSlideLeftPageAnimator = ValueAnimator.ofFloat(1f, 0f);
            mSlideLeftPageAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mSlideLeftPageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mOffset = (Float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mSlideLeftPageAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_LEFT_PAGE;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_NONE;
                    mOffset = 0f;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_NONE;
                    mOffset = 0f;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_LEFT_PAGE;
                }
            });
        }
        mSlideLeftPageAnimator.start();
    }

    private void startSlideRightPageAnimation() {
        if (mAnimatingDirection != ANIMATION_DIRECTION_NONE) {
            stopAnimation();
        }

        if (mSlideRightPageAnimator == null) {
            mSlideRightPageAnimator = ValueAnimator.ofFloat(1f, 0f);
            mSlideRightPageAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mSlideRightPageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mOffset = (Float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mSlideRightPageAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_RIGHT_PAGE;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_NONE;
                    mOffset = 0f;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_NONE;
                    mOffset = 0f;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mAnimatingDirection = ANIMATION_DIRECTION_RIGHT_PAGE;
                }
            });
        }
        mSlideRightPageAnimator.start();
    }

    private void stopAnimation() {
        if (mSlideLeftPageAnimator != null && mSlideLeftPageAnimator.isStarted()) {
            mSlideLeftPageAnimator.cancel();
        }
        if (mSlideRightPageAnimator != null && mSlideRightPageAnimator.isStarted()) {
            mSlideRightPageAnimator.cancel();
        }
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(V4PageSeekBar seekBar, long progress, boolean fromUser);

        void onStartTrackingTouch(V4PageSeekBar seekBar);

        void onStopTrackingTouch(V4PageSeekBar seekBar);
    }
}
