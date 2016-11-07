package com.lecloud.skin.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsSeekBar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VerticalSeekBar extends AbsSeekBar {

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defstyle) {
        super(context, attrs, defstyle);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        this.mOnSeekBarChangeListener = l;
    }

    void onStartTrackingTouch() {
        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onStopTrackingTouch() {
        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    void onProgressRefresh(float scale, boolean fromUser) {
        Drawable thumb = null;
        try {
            Field mThumb_f = this.getClass().getSuperclass()
                    .getDeclaredField("mThumb");
            mThumb_f.setAccessible(true);
            thumb = (Drawable) mThumb_f.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setThumbPos(this.getWidth(), thumb, scale, Integer.MIN_VALUE);

        this.invalidate();

        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onProgressChanged(this,
                    this.getProgress(), fromUser);
        }
    }

    private void setThumbPos(int w, Drawable thumb, float scale, int gap) {
        int available = 0;
        try {

            int up = this.getPaddingTop();
            int bottom = this.getPaddingBottom();

            available = this.getHeight() - up - bottom;
            int thumbWidth = thumb.getIntrinsicWidth();
            int thumbHeight = thumb.getIntrinsicHeight();
            available -= thumbWidth;

            // The extra space for the thumb to move on the track
            available += this.getThumbOffset() * 2;

            int thumbPos = (int) (scale * available);

            int topBound, bottomBound;
            if (gap == Integer.MIN_VALUE) {
                Rect oldBounds = thumb.getBounds();
                topBound = oldBounds.top;
                bottomBound = oldBounds.bottom;
            } else {
                topBound = gap;
                bottomBound = gap + thumbHeight;
            }
            // Canvas will be translated, so 0,0 is where we start drawing
            thumb.setBounds(thumbPos, topBound, thumbPos + thumbWidth,
                    bottomBound);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
            int heightMeasureSpec) {
        // width = 30;
        // height = View.MeasureSpec.getSize(heightMeasureSpec);

        // this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        this.setMeasuredDimension(this.getMeasuredHeight(),
                this.getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-this.getHeight(), 0);
        super.onDraw(c);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean mIsUserSeekable = true;
        try {
            Field mIsUserSeekable_f = this.getClass().getSuperclass()
                    .getDeclaredField("mIsUserSeekable");
            mIsUserSeekable_f.setAccessible(true);

            mIsUserSeekable = mIsUserSeekable_f.getBoolean(this);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (!mIsUserSeekable || !this.isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            this.setPressed(true);
            this.onStartTrackingTouch();
            this.trackTouchEvent(event);
            break;

        case MotionEvent.ACTION_MOVE:
            this.trackTouchEvent(event);
            Method attemptClaimDrag;
            try {
                attemptClaimDrag = this.getClass().getSuperclass()
                        .getDeclaredMethod("attemptClaimDrag");
                attemptClaimDrag.setAccessible(true);
                attemptClaimDrag.invoke(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;

        case MotionEvent.ACTION_UP:
            this.trackTouchEvent(event);
            this.onStopTrackingTouch();
            this.setPressed(false);
            // ProgressBar doesn't know to repaint the thumb drawable
            // in its inactive state when the touch stops (because the
            // value has not apparently changed)
            this.invalidate();
            break;

        case MotionEvent.ACTION_CANCEL:
            this.onStopTrackingTouch();
            this.setPressed(false);
            this.invalidate(); // see above explanation
            break;
        }
        return true;
    }

    protected void trackTouchEvent(MotionEvent event) {

        final int height = this.getHeight();
        final int available = height - this.getPaddingLeft()
                - this.getPaddingRight();
        int y = (int) (height - event.getY());
        float scale;
        float progress = 0;
        if (y < this.getPaddingLeft()) {
            scale = 0.0f;
        } else if (y > height - this.getPaddingRight()) {
            scale = 1.0f;
        } else {
            scale = (float) (y - this.getPaddingLeft()) / (float) available;
            float mTouchProgressOffset = 0.0f;
            try {
                Field mTouchProgressOffset_f = this.getClass().getSuperclass()
                        .getDeclaredField("mTouchProgressOffset");
                mTouchProgressOffset_f.setAccessible(true);
                mTouchProgressOffset = mTouchProgressOffset_f.getFloat(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            progress = mTouchProgressOffset;
        }

        final int max = this.getMax();
        progress += scale * max;

        try {
            Method setProgress = this.getClass().getSuperclass()
                    .getSuperclass()
                    .getDeclaredMethod("setProgress", int.class, boolean.class);
            setProgress.setAccessible(true);
            setProgress.invoke(this, (int) progress, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onProgressRefresh(progress, true);
    }

    public interface OnSeekBarChangeListener {
        public void onProgressChanged(VerticalSeekBar vBar, int progress,
                                      boolean fromUser);

        public void onStartTrackingTouch(VerticalSeekBar vBar);

        public void onStopTrackingTouch(VerticalSeekBar vBar);
    }
}