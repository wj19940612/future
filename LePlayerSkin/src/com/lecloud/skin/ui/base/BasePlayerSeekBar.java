package com.lecloud.skin.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.lecloud.skin.ui.LetvUIListener;

/**
 * 
 *
 */
public abstract class BasePlayerSeekBar extends SeekBar{

    private static final String TAG = "BasePlayerSeekBar";
    private static final int timeout = 0;
    protected boolean isdragging = false;
    
    protected LetvUIListener mLetvUIListener;

    /**
     * 判断是否seeking状态
     */
    protected boolean isSeeking = false;

    public BasePlayerSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

	public BasePlayerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasePlayerSeekBar(Context context) {
        super(context);
        init();
    }
    
    public void setLetvUIListener(LetvUIListener mLetvVodUIListener) {
		this.mLetvUIListener = mLetvVodUIListener;
	}

    private void init() {
    	setMax(1000);
    	setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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
            }
        });
		
	}
    public void pauseSeek() {
        isdragging = true;
    }

    public void resumeSeek() {
        isdragging = false;
    }

    /**
     * 展示seekbar
     */
    public void showSeekbar(int timeout) {

    }

    /**
     * 隐藏seekbar,同时会取消获取播放时间
     */
    public void hideSeekbar() {
    	
    }

    private int progress;
    
    public void startTrackingTouch() {
    	if (isSeeking) {
			return;
		}
		isSeeking = true;
        progress = getProgress();
//        Log.d("BasePlayerSeekBar", "startTrackingTouch" + progress);
        if (mLetvUIListener != null) {
			mLetvUIListener.onStartSeek();
		}
	}

    public void stopTrackingTouch() {
		isSeeking = false;
		if (mLetvUIListener != null) {
			mLetvUIListener.onSeekTo((float)getProgress()/getMax());
		}
	}

    /**
     * 重置seekbar
     */
    public void reset() {
        setProgress(0);
        resumeSeek();
    }

    private long getDuration() {
       return 0;
    }

    private void seekTo(long sec) {
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.w(TAG, "[seekbar] removew from window");
    }
    
    @Override
    public synchronized void setProgress(int progress) {
//    	if (isTrackingTouch) {
//			return;
//		}
    	super.setProgress(progress);
    }
    
    long position;
    public void setProgressGap(int gap) {
    	int newProgress = Math.max(0, Math.min(getMax(), this.progress+gap));
	    super.setProgress(newProgress);
	}

	public void setCurrentPosition(long position) {
    	this.position = position;
    	if (duration != 0) {
    		progress = (int) Math.floor((position*getMax()/(float)duration));
    		Log.d("BasePlayerSeekBar", "setCurrentPosition" + progress);
    		setProgress(progress);
		}else {
		    setProgress(0);
        }
	}
    long duration;
	public void setDuration(long duration) {
		this.duration = duration;
	}
    
}
