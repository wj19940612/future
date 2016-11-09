package com.lecloud.skin.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.LetvUIListener;
import com.lecloud.skin.ui.utils.TimerUtils;

/**
 * 时移seekbar
 */
public abstract class BaseLiveSeekBar extends RelativeLayout implements IBaseLiveSeekBar {

    
    protected LetvUIListener mLetvUIListener;
    private static final int MIN_SEEKTIME_BUFFER = -5;
    private static final int MAX = 7200;
    private static final int MAX_2 = 7200;

    private static final String TAG = "BaseLiveSeekBar";

    private static final int HOURS_2_SECOND = 60 * 60 * 1000;// 1小时，单位毫秒

    protected SeekBar seekBar;
    protected TextView timeTextView;
    /**
     * 判断是否拖动状态
     */
    protected boolean isTrackingTouch = false;
    long serverTime;
    long currentTime;
    long begin;
    private long currentSeekTime = 0;
    private long betweenTime;
    private int mprogress = 0;
    private OnSeekChangeListener mOnSeekChangeListener;

    public BaseLiveSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public BaseLiveSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BaseLiveSeekBar(Context context) {
        super(context);
        initView(context);
    }

    
    public void setLetvUIListener(LetvUIListener mLetvUIListener) {
        this.mLetvUIListener = mLetvUIListener;
    }
    public abstract String getLayout();

    @Override
    protected void onFinishInflate() {
        onInitView();
        super.onFinishInflate();
    }
    
    protected void onInitView() {
        seekBar = (SeekBar) findViewById(R.id.live_seek_bar);
        timeTextView = (TextView) findViewById(R.id.vnew_time_text);
        initSeekbar();
        reset();
    }

    protected void initView(Context context) {
        
    }

    public void reset() {
        seekBar.setProgress(MAX);
        setSeekToTime(MAX, false);
    }

    private void initSeekbar() {
        if (seekBar != null) {
            seekBar.setMax(MAX);
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    stopTrackingTouch();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    startTrackingTouch();
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged(progress);
                    if (mOnSeekChangeListener != null) {
                        mOnSeekChangeListener.onProgressChanged(seekBar, progress, fromUser || isTrackingTouch);
                    }
                    setSeekToTime(progress, fromUser);
                }

            });
        }
    }

    public void startTrackingTouch() {
        if (isTrackingTouch) {
            return;
        }
        isTrackingTouch = true;
        if (mLetvUIListener != null) {
            mLetvUIListener.onStartSeek();
        }
        mprogress = seekBar.getProgress();
    }

    public void stopTrackingTouch() {
        isTrackingTouch = false;
        if (this.serverTime == 0) {
            // TODO
            return;
        }
        
        int progress = seekBar.getProgress();

        long gapTime = (long) ((currentTime - serverTime) * 0.001);
        long seekTime = gapTime + progress - mprogress; 
        if (seekTime >= 0) {
            progress = (int) (progress - seekTime);
            seekTime = MIN_SEEKTIME_BUFFER;
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.returned_to_live_time), Toast.LENGTH_SHORT).show();
        }
        if (seekTime > -6600) {
        	seekBar.setProgress((int)(MAX + seekTime));
		}else {
			setLiveSeekBarProgress(seekBar, progress);
		}
        if (seekTime > 0) {
        	seekBar.setProgress(seekBar.getMax());
		}

        Log.d(TAG, "[seekTime] seekTime:" + seekTime + ",gapTime:" + gapTime+ ",serverTime:" + serverTime);
        
        if (mLetvUIListener != null) {
            mLetvUIListener.onSeekTo(serverTime+seekTime*1000);
            mLetvUIListener.onProgressChanged(seekBar.getProgress());
        }
    }
    

    private void progressChanged(int progress) {
        int i = seekBar.getWidth() * progress / seekBar.getMax();
    }

    public void setProgress(int progress) {
        if (seekBar != null) {
//            seekBar.setProgress(this.mprogress + progress * seekBar.getMax() / 1000);
        	seekBar.setProgress(progress);
        }
    }

    public String getCurrentTime() {
        if (serverTime == 0 || seekBar == null) {
            return "";
        }
        String time = TimerUtils.timeToDate(currentTime) + "";
        return time;
    }

    public String getSeekToTime() {
        if (serverTime == 0 || seekBar == null) {
            return "";
        }
        long currentPosition = currentTime + (seekBar.getProgress() - mprogress)* 1000;
        String time = TimerUtils.timeToDate(currentPosition) + "";
        return time;
    }

    private void setLiveSeekBarProgress(SeekBar seekBar, int progress) {
        if (progress > 600 && progress < 6600) {
            seekBar.setProgress(progress);
        } else {
            seekBar.setProgress((int) (seekBar.getMax() * 0.5));
        }
    }

    private void showTimeshitSeekProgress(Bundle bundle) {
//        if (!isShown()) {
//            setVisibility(View.VISIBLE);
//        }
        if (mOnSeekChangeListener != null && !isTrackingTouch) {
            mOnSeekChangeListener.onProgressChanged(seekBar, seekBar.getProgress(), false);
        }
    }
    
    public void setProgressGap(int gap) {
        seekBar.setProgress(mprogress + gap * seekBar.getMax() / 1000);
    }

    public void setOnSeekChangeListener(OnSeekChangeListener l) {
        mOnSeekChangeListener = l;
    }

    public interface OnSeekChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
        void onShowBackToLive(boolean show);
    }
    
    public void setTimeShiftChange(long serverTime, long currentTime, long begin) {
    	this.serverTime = serverTime;
    	this.currentTime = currentTime;
    	this.begin = begin;
        if (currentTime > serverTime -10000) {
            showBackToLive(false);
            setProgress(seekBar.getMax());
        }else {
            showBackToLive(true);
        }	
        showTimeshitSeekProgress(null);
        betweenTime = currentTime - begin;
        
        if (!isTrackingTouch) {
            setTimeText(getContext().getResources().getString(R.string.is_playing) + getCurrentTime());
            return;
        }
    }
    
    private void showBackToLive(boolean show) {
        if (mOnSeekChangeListener != null && this.getVisibility() == View.VISIBLE){
            mOnSeekChangeListener.onShowBackToLive(show);
        }
     
    }

    public void setSeekToTime(int progress, boolean fromUser) {
        if (!seekBar.isShown()) {
            return;
        }
        if (timeTextView != null) {
            if (timeTextView.getVisibility() != VISIBLE) {
                timeTextView.setVisibility(VISIBLE);
            }
            LayoutParams params = (LayoutParams) timeTextView.getLayoutParams();
            int right = seekBar.getRight() - seekBar.getWidth() * progress / seekBar.getMax();
            if (fromUser) {
                setTimeText(getContext().getResources().getString(R.string.is_playing) + getSeekToTime());
            } else {
                setTimeText(getContext().getResources().getString(R.string.is_playing) + getCurrentTime());
            }
            int leftMargin = seekBar.getRight() - right - timeTextView.getMeasuredWidth();
            if (leftMargin > 0) {
                params.rightMargin = right;
                timeTextView.setLayoutParams(params);
            }
        }
    }

    public void setTimeText(CharSequence text) {
        if (!seekBar.isShown()) {
//            timeTextView.setVisibility(INVISIBLE);
            return;
        }
        timeTextView.setText(text);
        timeTextView.setVisibility(VISIBLE);
    }

}
