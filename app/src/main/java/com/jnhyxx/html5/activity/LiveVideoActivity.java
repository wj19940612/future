package com.jnhyxx.html5.activity;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.WindowManager;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseMediaDataVideoView;
import com.lecloud.skin.videoview.live.UIActionLiveVideoView;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class LiveVideoActivity extends BaseActivity {

    private IMediaDataVideoView mVideoView;

    LinkedHashMap<String, String> mRateMap = new LinkedHashMap<>();
    VideoViewListener mVideoViewListener = new VideoViewListener() {

        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
        }

        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            mRateMap = map;
            for (Map.Entry<String, String> rates : map.entrySet()) {
                if (rates.getValue().equals("高清")) {
                    return rates.getKey();
                }
            }
            return "";
        }
    };

    private int mPlayMode;
    private boolean mHls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {
        mPlayMode = PlayerParams.VALUE_PLAYER_ACTION_LIVE;
        mHls = false;
    }

    protected void initVideoView() {
        switch (mPlayMode) {
            case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
                mVideoView = new UIActionLiveVideoView(this);
                setActionLiveParameter(mHls);
                break;
            }
            default:
                mVideoView = new BaseMediaDataVideoView(this);
                break;
        }

        mVideoView.setVideoViewListener(mVideoViewListener);

        onAddViewView(mVideoView);

//        final RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
//        videoContainer.addView((View) mVideoView, VideoLayoutParams.computeContainerSize(this, 16, 9));

        mVideoView.setDataSource(initBundle());
    }

    //private static final String LIVE_ID = "A2016080200000n1";
    private static final String LIVE_ID = "A2016053100000je";

    private Bundle initBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
        bundle.putString(PlayerParams.KEY_PLAY_ACTIONID, LIVE_ID); // important
        bundle.putString(PlayerParams.KEY_PLAY_PU, "");
        bundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, false);
//        mBundle.putString(PlayerParams.KEY_ACTION_CUID, "1");
//        mBundle.putString(PlayerParams.KEY_ACTION_UTOKEN, "22");
        bundle.putString(PlayerParams.KEY_ACTION_CUID, "");
        bundle.putString(PlayerParams.KEY_ACTION_UTOKEN, "");
        bundle.putBoolean("pano", false);
        bundle.putBoolean("hasSkin", true);


        return bundle;
    }


    protected abstract void onAddViewView(IMediaDataVideoView videoView);


    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.onPause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoView != null) {
            mVideoView.onConfigurationChanged(newConfig);
        }
    }

    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        switch (state) {
            case PlayerEvent.ACTION_LIVE_PLAY_PROTOCOL:
                setActionLiveParameter(bundle.getBoolean(PlayerParams.KEY_PLAY_USEHLS));
                break;
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                /**
                 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
                 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
                 * 意味着你的surfaceView显示的内容有可能是拉伸的
                 */
                break;
            case PlayerEvent.PLAY_PREPARED:
                // 播放器准备完成，此刻调用start()就可以进行播放了
                if (mVideoView != null) {
                    mVideoView.onStart();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
    }

    private void setActionLiveParameter(boolean hls) {
        if (hls) {
            mVideoView.setCacheWatermark(1000, 100);
            mVideoView.setMaxDelayTime(50000);
            mVideoView.setCachePreSize(1000);
            mVideoView.setCacheMaxSize(40000);
        } else { // RTMP
            mVideoView.setCacheWatermark(500, 100);
            mVideoView.setMaxDelayTime(1000);
            mVideoView.setCachePreSize(200);
            mVideoView.setCacheMaxSize(10000);
        }
    }
}
