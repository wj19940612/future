package com.lecloud.skin.ui.impl;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.ui.ILetvVodUICon;
import com.lecloud.skin.ui.LetvVodUIListener;

public class BaseLetvVodUICon extends LetvUICon implements ILetvVodUICon {
	
	protected LetvVodUIListener mLetvVodUIListener;
	protected long duration;
	protected long position;
	public BaseLetvVodUICon(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BaseLetvVodUICon(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseLetvVodUICon(Context context) {
		super(context);
	}
	
	@Override
	public void setCurrentPosition(long position) {
	    this.position = position;
		if (mLargeMediaController != null) {
			mLargeMediaController.setCurrentPosition(position);
		}
		if (mSmallMediaController != null) {
			mSmallMediaController.setCurrentPosition(position);
		}
	}
	
	@Override
	public void setDuration(long duration) {
	    this.duration = duration;
		if (mLargeMediaController != null) {
			mLargeMediaController.setDuration(duration);
		}
		if (mSmallMediaController != null) {
			mSmallMediaController.setDuration(duration);
		}
	}

	@Override
	public void setBufferPercentage(long bufferPercentage) {
		if (mLargeMediaController != null) {
			mLargeMediaController.setBufferPercentage(bufferPercentage);
		}
		if (mSmallMediaController != null) {
			mSmallMediaController.setBufferPercentage(bufferPercentage);
		}
	}
	
	@Override
	public void setLetvVodUIListener(LetvVodUIListener mLetvVodUIListener) {
		this.mLetvVodUIListener = mLetvVodUIListener;
		if (mLetvVodUIListener != null) {
			if (mLargeMediaController != null) {
				mLargeMediaController.setLetvUIListener(mLetvVodUIListener);
			}
			if (mSmallMediaController != null) {
				mSmallMediaController.setLetvUIListener(mLetvVodUIListener);
			}
			if (mV4TopTitleView != null) {
			    mV4TopTitleView.setLetvUIListener(mLetvVodUIListener);
            }
		}
		super.setLetvUIListener(mLetvVodUIListener);
	}

	

}
