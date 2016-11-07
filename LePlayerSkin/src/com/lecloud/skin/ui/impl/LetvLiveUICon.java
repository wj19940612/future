package com.lecloud.skin.ui.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.base.IBaseLiveSeekBar;
import com.lecloud.skin.ui.view.V4LargeLiveMediaControllerNew;
import com.lecloud.skin.ui.view.V4SmallLiveMediaControllerNew;
import com.lecloud.skin.ui.view.V4TopTitleView;

public class LetvLiveUICon extends BaseLetvLiveUICon {

    public LetvLiveUICon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LetvLiveUICon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetvLiveUICon(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
//		UI 分层
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rlSkin = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.letv_skin_v4_skin_live, null);
        addView(rlSkin, params);

        mLargeMediaController = (V4LargeLiveMediaControllerNew) findViewById(R.id.v4_large_media_controller);
        mV4TopTitleView = (V4TopTitleView) findViewById(R.id.v4_letv_skin_v4_top_layout);
        mSmallMediaController = (V4SmallLiveMediaControllerNew) findViewById(R.id.v4_small_media_controller);
    }

    @Override
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mV4TopTitleView.setTitle(title);
        }
    }

    @Override
    public boolean performClick() {
        if (rlSkin != null) {
            if (rlSkin.getVisibility() == VISIBLE) {
                hide();
            } else {
                show();
            }
            return false;
        }
        return super.performClick();
    }

    @Override
    protected void seekTo(int seekGap) {
        IBaseLiveSeekBar seekBar = ((V4LargeLiveMediaControllerNew) mLargeMediaController).getSeekbar();
        if (seekBar != null) {
            seekBar.startTrackingTouch();
            Log.i("zsn", "--------seekGap:" + seekGap);
            seekBar.setProgressGap(seekGap);
            seekBar.setSeekToTime(seekGap, true);
            if (mGestureControl.mSeekToPopWindow != null) {
                mGestureControl.mSeekToPopWindow.setProgress(seekBar.getSeekToTime());
            }
        }
        super.seekTo(seekGap);
    }

    @Override
    protected void touchUp() {
        IBaseLiveSeekBar seekBar = ((V4LargeLiveMediaControllerNew) mLargeMediaController).getSeekbar();
        if (seekBar != null) {
            seekBar.stopTrackingTouch();
        }
        super.touchUp();
    }

    /**
     * 同步进度条
     *
     * @param position
     */
    @Override
    public void syncSeekProgress(int progress) {
        if (mLargeMediaController != null) {
            ((V4LargeLiveMediaControllerNew) mLargeMediaController).getSeekbar().setProgress(progress);
        }
        if (mSmallMediaController != null) {
            ((V4SmallLiveMediaControllerNew) mSmallMediaController).getSeekbar().setProgress(progress);
        }
    }

}
