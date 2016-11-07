package com.lecloud.skin.videoview.pano.live;

import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.videoview.live.ActionLiveVideoView;
import com.lecloud.skin.videoview.pano.base.BasePanoSurfaceView;
import com.letv.pano.IPanoListener;

/**
 * Created by heyuekuai on 16/6/14.
 */
public class PanoActionLiveVideoView extends ActionLiveVideoView {
    ISurfaceView surfaceView;
    protected int controllMode = -1;
    protected int displayMode = -1;
    public PanoActionLiveVideoView(Context context) {
        super(context);
    }

    @Override
    protected void prepareVideoSurface() {
        surfaceView = new BasePanoSurfaceView(context);
        controllMode = ((BasePanoSurfaceView) surfaceView).switchControllMode(controllMode);
        displayMode = ((BasePanoSurfaceView) surfaceView).switchDisplayMode(displayMode);
        setVideoView(surfaceView);
        ((BasePanoSurfaceView) surfaceView).registerPanolistener(new IPanoListener() {
            @Override
            public void setSurface(Surface surface) {
                player.setDisplay(surface);
            }
            @Override
            public void onSingleTapUp(MotionEvent e) {

            }

            @Override
            public void onNotSupport(int mode) {

            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((BasePanoSurfaceView) surfaceView).onPanoTouch(v, event);
                return true;
            }
        });
    }

    protected int switchControllMode(int mode) {
        controllMode = ((BasePanoSurfaceView) surfaceView).switchControllMode(mode);
        return controllMode;
    }

    protected int switchDisplayMode(int mode) {
        displayMode = ((BasePanoSurfaceView) surfaceView).switchDisplayMode(mode);
        return displayMode;
    }
}
