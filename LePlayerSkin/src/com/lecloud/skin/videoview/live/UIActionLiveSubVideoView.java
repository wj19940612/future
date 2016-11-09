package com.lecloud.skin.videoview.live;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.player.live.ActionLiveSubPlayer;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.base.BaseVideoView;

/**
 */
public class UIActionLiveSubVideoView extends BaseVideoView {

	public UIActionLiveSubVideoView(Context con) {
        super(con);
		context = con;
	}
	
	@Override
	public void setDataSource(String playUrl) {
	    super.setDataSource(playUrl);
	}

	protected void initPlayer() {
//		player = new BaseMediaPlayer();
		player = new ActionLiveSubPlayer(context);
	}

	BaseSurfaceView mBaseSurfaceView;
	@Override
	protected void prepareVideoSurface() {
	    mBaseSurfaceView = new BaseSurfaceView(context);
		mBaseSurfaceView.getHolder().addCallback(surfaceCallback);
		mBaseSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
		mBaseSurfaceView.setZOrderOnTop(true);
		setVideoView(mBaseSurfaceView);
    }
	
	
	protected SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopAndRelease();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder.getSurface());
//            onResume();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            
        }
    };

	@Override
	protected void notifyPlayerEvent(int state, Bundle bundle) {
		super.notifyPlayerEvent(state, bundle);
		switch (state) {
		case PlayerEvent.PLAY_INIT:
		    player.setVolume(0, 0);
            break;
		case PlayerEvent.PLAY_PREPARED:
	        // 播放器准备完成，此刻调用start()就可以进行播放了
		    onStart();
	        break;

        default:
            break;
        }
	}
	
	@Override
	public void onResume() {
	    mBaseSurfaceView.setVisibility(View.VISIBLE);
	    super.onResume();
	}
	
	@Override
	public void onDestroy() {
	    mBaseSurfaceView.setVisibility(View.GONE);
	    super.onDestroy();
	}

}
