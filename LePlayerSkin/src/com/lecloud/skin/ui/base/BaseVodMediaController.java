package com.lecloud.skin.ui.base;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.ui.LetvUIListener;

import java.util.List;

public abstract class BaseVodMediaController extends BaseMediaController{

	protected BasePlayerSeekBar mBasePlayerSeekBar;

	protected TextTimerView mTextTimerView;

	public BaseVodMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BaseVodMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseVodMediaController(Context context) {
		super(context);
	}

	@Override
	public void setLetvUIListener(LetvUIListener mLetvUIListener) {
		this.mLetvUIListener = mLetvUIListener;
		if (mLetvUIListener != null) {
			if (mBasePlayBtn != null) {
				mBasePlayBtn.setLetvUIListener(mLetvUIListener);
			}
			if (mBaseChgScreenBtn != null) {
				mBaseChgScreenBtn.setLetvUIListener(mLetvUIListener);
			}
//			if (mBaseDownloadBtn != null) {
//				mBaseDownloadBtn.setLetvVodUIListener(mLetvVodUIListener);
//			}
			if (mBaseRateTypeBtn != null) {
				mBaseRateTypeBtn.setLetvUIListener(mLetvUIListener);
			}
			if (mBasePlayerSeekBar != null) {
				mBasePlayerSeekBar.setLetvUIListener(mLetvUIListener);
			}
			if (mBaseChangeModeBtn != null) {
				mBaseChangeModeBtn.setLetvUIListener(mLetvUIListener);
			}
			if (mBaseChangeVRModeBtn != null) {
				mBaseChangeVRModeBtn.setLetvUIListener(mLetvUIListener);
			}
		}
	}

	@Override
	public void setRateTypeItems(List<String> ratetypes,String definition) {
		if(ratetypes != null && ratetypes.size()>0){
			mBaseRateTypeBtn.setVisibility(VISIBLE);
		}
		if (mBaseRateTypeBtn != null) {
			mBaseRateTypeBtn.setRateTypeItems(ratetypes, definition);
		}
	}

	@Override
	public void setPlayState(boolean isPlayState) {
		if (mBasePlayBtn != null) {
			mBasePlayBtn.setPlayState(isPlayState);
		}
	}

	@Override
	public void setCurrentPosition(final long position) {
		mBasePlayerSeekBar.setCurrentPosition(position);
		if (mTextTimerView != null) {
			post(new Runnable() {

				@Override
				public void run() {
					if (duration != 0) {
						mTextTimerView.setTextTimer(position,duration);
					}else {
						mTextTimerView.setTextTimer(0,duration);
					}
				}
			});
		}
	}

	long duration;
	@Override
	public void setDuration(long duration) {
		if (duration != 0) {
			this.duration = duration;
		}
		mBasePlayerSeekBar.setDuration(duration);
	}

	@Override
	public void setBufferPercentage(long bufferPercentage) {
		// TODO Auto-generated method stub
	}

	public BasePlayerSeekBar getSeekbar() {
		return mBasePlayerSeekBar;
	}

}
