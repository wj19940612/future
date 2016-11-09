package com.lecloud.skin.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.LetvUIListener;
import com.lecloud.skin.ui.base.IBaseLiveSeekBar;
import com.lecloud.skin.ui.utils.TimerUtils;

@SuppressWarnings("unused")
public class V4LivePageSeekBar extends V4PageSeekBar implements IBaseLiveSeekBar {
    //        private static final long PER_PAGE_VALUE = 2 * 60 * 60 * 1000;  // 2 hours
    private static final long PER_PAGE_VALUE = 100 * 60 * 1000;  // 1.40 hours
    private static final long INDICATOR_VALUE = 10 * 60 * 1000;     // 10 minutes

    private LetvUIListener mLetvUIListener;
    private OnBackToLiveListener mOnBackToLiveListener;

    private long mServerTime;
    private long mCurrentTime;
    private long mBegin;

    public V4LivePageSeekBar(Context context) {
        super(context);
        init();
    }

    public V4LivePageSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public V4LivePageSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Update back to live button position if need
        final int oldWidth = secondaryProgressWidth;
        super.onDraw(canvas);
        if (oldWidth != secondaryProgressWidth && mOnBackToLiveListener != null) {
            mOnBackToLiveListener.onPositionChanged(secondaryProgressWidth + getPaddingLeft());
        }
    }

    @Override
    public void setProgress(long minValue, long currentValue, long maxValue) {
        super.setProgress(minValue, currentValue, maxValue);
        updateBackToLiveVisible();
    }

    @Override
    public void setProgressImmediately(long minValue, long currentValue, long maxValue) {
        super.setProgressImmediately(minValue, currentValue, maxValue);
        updateBackToLiveVisible();
    }

    @Override
    public void setLetvUIListener(LetvUIListener letvUIListener) {
        mLetvUIListener = letvUIListener;
    }

    @Override
    public void startTrackingTouch() {
        setTracking(true);
        if (mLetvUIListener != null) {
            mLetvUIListener.onStartSeek();
        }
    }

    @Override
    public void stopTrackingTouch() {
        setTracking(false);
        if (mServerTime == 0) {
            return;
        }
        if (mLetvUIListener != null) {
            mLetvUIListener.onEndSeek();
        }

        // Seek to when stop tracking
        long seekToTime = getCurrentValue() + mBegin;
        seekTo(seekToTime);
    }

    @Override
    public void setProgress(int progress) {

    }

    @Override
    public void setProgressGap(int gap) {
        long seekToTime = mCurrentTime + gap * 30 * 1000;
        if (seekToTime > mServerTime) {
            seekToTime = mServerTime;
        }

        // Ensure value in current page
        long currentPage = getCurrentPage();
        long perPageValue = getPerPageValue();
        long minValue = currentPage * perPageValue + mBegin;
        long maxValue = minValue + perPageValue + getLeftPageIndicatorValue() + getRightPageIndicatorValue();
        seekToTime = Math.max(seekToTime, minValue);
        seekToTime = Math.min(seekToTime, maxValue);

        setProgress(0, seekToTime - mBegin, mServerTime - mBegin);
    }

    @Override
    public void setSeekToTime(int progress, boolean fromUser) {

    }

    @Override
    public void setTimeShiftChange(long serverTime, long currentTime, long begin) {
        mServerTime = serverTime;
        mCurrentTime = currentTime;
        mBegin = begin;
        if (isTracking()) { // If is tracking, show track time
            setProgress(0, getCurrentValue(), mServerTime - mBegin);
        } else { // Show current play time
            if (getMaxValue() == 0) {
                setProgressImmediately(0, mCurrentTime - mBegin, mServerTime - mBegin);
            } else {
                setProgress(0, mCurrentTime - mBegin, mServerTime - mBegin);
            }
        }
    }

    @Override
    public void setTimeText(CharSequence text) {
        setText(text.toString());
    }

    @Override
    public void reset() {
        mBegin = mCurrentTime = mServerTime = 0;
        setPage(PER_PAGE_VALUE, INDICATOR_VALUE, INDICATOR_VALUE);
        setProgress(0, 0, 0);
    }

    @Override
    public String getCurrentTime() {
        if (mServerTime == 0) {
            return "";
        }
        return TimerUtils.timeToDate(mCurrentTime) + "";
    }

    @Override
    public String getSeekToTime() {
        if (mServerTime == 0) {
            return "";
        }
        return TimerUtils.timeToDate(getCurrentValue() + mBegin) + "";
    }

    public OnBackToLiveListener getOnBackToLiveListener() {
        return mOnBackToLiveListener;
    }

    public void setOnBackToLiveListener(OnBackToLiveListener onBackToLiveListener) {
        mOnBackToLiveListener = onBackToLiveListener;
    }

    private String getServerTime() {
        if (mServerTime == 0) {
            return "";
        }
        return TimerUtils.timeToDate(mServerTime) + "";
    }

    private void init() {
        setTextColor(Color.WHITE);
        setPage(PER_PAGE_VALUE, INDICATOR_VALUE, INDICATOR_VALUE);
        setProgressImmediately(0, 0, 0);

        setOnSeekBarChangeListenerListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(V4PageSeekBar seekBar, long progress, boolean fromUser) {
                if (isTracking()) {
                    setTimeText(getContext().getResources().getString(R.string.is_playing) + getSeekToTime());
                } else {
                    setTimeText(getContext().getResources().getString(R.string.is_playing) + getCurrentTime());

                    // Deal user click progress bar
                    if (fromUser) {
                        long progressTime = getCurrentValue() + mBegin;
                        if (Math.abs(mCurrentTime - progressTime) > 1000) {
                            seekTo(progressTime);
                            Log.d("huahua", "seekTo: 194===");
                        }
                    }
                }
                if (mLetvUIListener != null) {
                    mLetvUIListener.onProgressChanged((int) (getCurrentValue() + mBegin / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(V4PageSeekBar seekBar) {
                startTrackingTouch();
            }

            @Override
            public void onStopTrackingTouch(V4PageSeekBar seekBar) {
                stopTrackingTouch();
            }
        });
    }

    private void seekTo(long seekToTime) {
        boolean nearRealTime = Math.abs(mServerTime - seekToTime) < 1000;
        if (Math.abs(mCurrentTime - seekToTime) < 1000) {
            if (nearRealTime) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.returned_to_live_time), Toast.LENGTH_SHORT).show();
            }
            setProgress(0, mCurrentTime - mBegin, mServerTime - mBegin);
        } else {
            setProgress(0, seekToTime - mBegin, mServerTime - mBegin);
            if (mLetvUIListener != null) {
                if (nearRealTime) {
                    mLetvUIListener.resetPlay();
                } else {
                    mLetvUIListener.onSeekTo(seekToTime);
                }
                mLetvUIListener.onProgressChanged((int) (seekToTime / 1000));
            }
        }
    }

    private void updateBackToLiveVisible() {
        // Update back to live button when shift time changed
        long currentTime = isTracking() ? mCurrentTime : getCurrentValue() + mBegin;
        if (mOnBackToLiveListener != null) {
            if (currentTime >= mServerTime - 1000) {
                mOnBackToLiveListener.onVisibilityChanged(GONE);
            } else {
                mOnBackToLiveListener.onPositionChanged(secondaryProgressWidth + getPaddingLeft());
                mOnBackToLiveListener.onVisibilityChanged(VISIBLE);
                mOnBackToLiveListener.onTextChanged(
                        getContext().getResources().getString(R.string.back_to_live) + ":" + getServerTime());
            }
        }
    }

    public interface OnBackToLiveListener {
        void onVisibilityChanged(int visibility);

        void onTextChanged(String text);

        void onPositionChanged(int leftMargin);
    }
}
