package com.lecloud.skin.ui.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.view.V4LargeMediaController;
import com.lecloud.skin.ui.view.V4SmallMediaController;
import com.lecloud.skin.ui.base.BasePlayerSeekBar;
import com.lecloud.skin.ui.utils.TimerUtils;
import com.lecloud.skin.ui.view.V4TopTitleView;

public class LetvVodUICon extends BaseLetvVodUICon  {
	
	public LetvVodUICon(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public LetvVodUICon(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LetvVodUICon(Context context) {
		super(context);
	}

	protected void init(Context context) {
//		UI 分层
		super.init(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rlSkin = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.letv_skin_v4_skin, null);
		addView(rlSkin, params);
		
		mLargeMediaController = (V4LargeMediaController) findViewById(R.id.v4_large_media_controller);
		mV4TopTitleView = (V4TopTitleView) findViewById(R.id.v4_letv_skin_v4_top_layout);
		mSmallMediaController = (V4SmallMediaController) findViewById(R.id.v4_small_media_controller);
	}

	@Override
	public boolean performClick() {
		if (rlSkin != null) {
			if (rlSkin.getVisibility() == VISIBLE) {
				hide();
			}else {
				show();
			}
			return false;
		}
		return super.performClick();
	}
	@Override
	public void setTitle(String title) {
		if(!TextUtils.isEmpty(title)){
			mV4TopTitleView.setTitle(title);
		}
	}
	
	@Override
	protected void seekTo(int seekGap) {
	    BasePlayerSeekBar seekBar = ((V4LargeMediaController)mLargeMediaController).getSeekbar();
	    if (seekBar != null) {
	        seekBar.startTrackingTouch();
	        seekBar.setProgressGap(seekGap);
	        if (mGestureControl.mSeekToPopWindow != null) {
	            String progress = TimerUtils.stringForTime((int)(seekBar.getProgress() * duration/seekBar.getMax()/1000));
	            int times = (int) (duration/1000);
	            String duration = TimerUtils.stringForTime(times);
	            mGestureControl.mSeekToPopWindow.setProgress(progress, duration);
	        }
        }
	    super.seekTo(seekGap);
	}
	
	@Override
	protected void touchUp() {
	    BasePlayerSeekBar seekBar = ((V4LargeMediaController)mLargeMediaController).getSeekbar();
        if (seekBar != null) {
            seekBar.stopTrackingTouch();
        }
	    super.touchUp();
	}
}
