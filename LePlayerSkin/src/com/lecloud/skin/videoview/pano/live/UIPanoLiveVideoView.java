package com.lecloud.skin.videoview.pano.live;

import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.skin.videoview.live.UILiveVideoView;
import com.lecloud.skin.videoview.pano.base.BasePanoSurfaceView;
import com.lecloud.skin.ui.impl.LetvLiveUICon;
import com.letv.pano.IPanoListener;

/**
 * Created by heyuekuai on 16/5/31.
 */
public class UIPanoLiveVideoView extends UILiveVideoView {
    ISurfaceView surfaceView;
    protected int controllMode = -1;
    protected int displayMode = -1;
    public UIPanoLiveVideoView(Context context) {
        super(context);
//        letvVodUICon.canGesture(false);
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
                letvLiveUICon.performClick();
            }

            @Override
            public void onNotSupport(int mode) {
                Toast.makeText(context, "not support current mode " + mode, Toast.LENGTH_LONG).show();
            }
        });

        ((LetvLiveUICon) letvLiveUICon).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(player !=null && player.isPlaying()){
                    ((BasePanoSurfaceView) surfaceView).onPanoTouch(v, event);
                }
                return true;
            }
        });
        letvLiveUICon.isPano(true);
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
