package com.jnhyxx.html5.fragment.live;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.utils.VideoLayoutParams;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseMediaDataVideoView;
import com.lecloud.sdk.videoview.live.ActionLiveVideoView;
import com.lecloud.sdk.videoview.live.LiveVideoView;
import com.lecloud.skin.videoview.live.UIActionLiveVideoView;
import com.lecloud.skin.videoview.live.UILiveVideoView;
import com.lecloud.skin.videoview.pano.live.PanoActionLiveVideoView;
import com.lecloud.skin.videoview.pano.live.PanoLiveVideoView;
import com.lecloud.skin.videoview.pano.live.UIPanoActionLiveVideoView;
import com.lecloud.skin.videoview.pano.live.UIPanoLiveVideoView;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/11/7.
 */

public class VideoPlayFragment extends BaseFragment {


    @BindView(R.id.liveLayout)
    RelativeLayout mLiveLayout;


    private Unbinder mBind;

    private IMediaDataVideoView videoView;

    LinkedHashMap<String, String> rateMap = new LinkedHashMap<String, String>();
    VideoViewListener mVideoViewListener = new VideoViewListener() {


        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调

        }


        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            rateMap = map;
            for (Map.Entry<String, String> rates : map.entrySet()) {
                if (rates.getValue().equals("高清")) {
                    return rates.getKey();
                }
            }
            return "";
        }
    };

    private void handleLiveEvent(int event, Bundle bundle) {

    }

    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int event, Bundle bundle) {
        switch (event) {
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
                if (videoView != null) {
                    videoView.onStart();
                }
                break;
            default:
                break;
        }
    }

    private void handleVideoInfoEvent(int event, Bundle bundle) {

    }

    private Bundle mBundle;
    private String mPlayUrl;
    private boolean mHasSkin;
    private boolean mPano;
    private int mPlayMode;

    public static final String DATA = "data";
    //视频路径
    public static final String KEY_VIDEO_PATH = "path";
    //视频码数
    public static final String KEY_VIDEO_MODE = "videoMode";
    //是否全景
    public static final String KEY_IS_PANORAMA = "isPanorama";
    //是否有皮肤
    public static final String KEY_HAS_SKIN = "hasSkin";

    public interface OnConfigurationChangedListener {
        void onConfigurationChanged(Configuration newConfig);
    }

    OnConfigurationChangedListener mOnConfigurationChangedListener;


    public static VideoPlayFragment newInstance(Bundle bundle) {
        VideoPlayFragment videoPlayFragment = new VideoPlayFragment();
        Bundle data = new Bundle();
        data.putBundle(DATA, bundle);
        videoPlayFragment.setArguments(data);
        return videoPlayFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mBundle = arguments.getBundle(DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_play, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.onPause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.onDestroy();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoView != null) {
            videoView.onConfigurationChanged(newConfig);
            mOnConfigurationChangedListener.onConfigurationChanged(newConfig);
        }
    }

    private void initView() {
        switch (mPlayMode) {
            case PlayerParams.VALUE_PLAYER_LIVE: {
                videoView = mHasSkin ? (mPano ? new UIPanoLiveVideoView(getActivity()) : new UILiveVideoView(getActivity())) : (mPano ? new PanoLiveVideoView(getActivity()) : new LiveVideoView(getActivity()));
                break;
            }
//            case PlayerParams.VALUE_PLAYER_VOD: {
//                videoView = mHasSkin ? (mPano ? new UIPanoVodVideoView(this) : new UIVodVideoView(this)) : (mPano ? new PanoVodVideoView(this) : new VodVideoView(this));
//                break;
//            }
            case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
                videoView = mHasSkin ? (mPano ? new UIPanoActionLiveVideoView(getActivity()) : new UIActionLiveVideoView(getActivity())) : (mPano ? new PanoActionLiveVideoView(getActivity()) : new ActionLiveVideoView(getActivity()));
                setActionLiveParameter(mBundle.getBoolean(PlayerParams.KEY_PLAY_USEHLS));
                break;
            }
            default:
                videoView = new BaseMediaDataVideoView(getActivity());
                break;
        }

        videoView.setVideoViewListener(mVideoViewListener);

        mLiveLayout.addView((View) videoView, VideoLayoutParams.computeContainerSize(getActivity(), 16, 9));
        if (!TextUtils.isEmpty(mPlayUrl)) {
            videoView.setDataSource(mPlayUrl);
        } else {
            videoView.setDataSource(mBundle);
        }
    }

    private void setActionLiveParameter(boolean hls) {
        if (hls) {
            videoView.setCacheWatermark(1000, 100);
            videoView.setMaxDelayTime(50000);
            videoView.setCachePreSize(1000);
            videoView.setCacheMaxSize(40000);
        } else {
            //rtmp
            videoView.setCacheWatermark(500, 100);
            videoView.setMaxDelayTime(1000);
            videoView.setCachePreSize(200);
            videoView.setCacheMaxSize(10000);
        }
    }

    private void initData() {
        if (mBundle != null) {
            mPlayUrl = mBundle.getString(KEY_VIDEO_PATH);
            mPlayMode = mBundle.getInt(KEY_VIDEO_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
            mHasSkin = mBundle.getBoolean(KEY_HAS_SKIN);
            mPano = mBundle.getBoolean(KEY_IS_PANORAMA);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    public void setOnConfigurationChangedListener(OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListener = onConfigurationChangedListener;
    }
}
