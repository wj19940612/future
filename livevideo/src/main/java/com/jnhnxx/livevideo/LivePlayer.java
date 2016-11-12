package com.jnhnxx.livevideo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.netease.neliveplayer.NELivePlayer;
import com.netease.neliveplayer.NEMediaPlayer;

import java.io.IOException;

public class LivePlayer extends TextureView implements TextureView.SurfaceTextureListener, ILivePlayer {
    private static final String TAG = "LiveVideoView";

    private static final int IDLE = 0;
    private static final int INITIALIZED = 1;
    private static final int PREPARING = 2;
    private static final int PREPARED = 3;
    private static final int STARTED = 4;
    private static final int PAUSED = 5;
    private static final int ERROR = -1;

    private Uri mUri;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mPixelSarNum;
    private int mPixelSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private boolean isBackground;

    private SurfaceTexture mSurfaceTexture;
    private NELivePlayer mMediaPlayer;
    private ResourceReleaseReceiver mReceiver; //接收资源释放成功的通知
    private LivePlayerController mPlayerController;
    private View mBufferView;

    // assist
    private String mLogPath = null;
    private int mLogLevel = 0;

    // state
    private int mCurrState;
    private int mNextState;

    private boolean mFullScreen;

    public LivePlayer(Context context) {
        super(context);
        initVideoView();
    }

    public LivePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView();
    }

    private void initVideoView() {
        Log.d(TAG, "initVideoView: ");
        mVideoWidth = 0;
        mVideoHeight = 0;
        mPixelSarNum = 0;
        mPixelSarDen = 0;
        setSurfaceTextureListener(this);
        registerBroadcast();
        mCurrState = IDLE;
        mNextState = IDLE;
    }

    private void registerBroadcast() {
        unregisterBroadcast();
        mReceiver = new ResourceReleaseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NEMediaPlayer.NELP_RELEASE_SUCCESS);
        getContext().registerReceiver(mReceiver, filter);
    }

    private void unregisterBroadcast() {
        if (mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public void setVideoPath(String path) { //设置视频文件路径
        isBackground = false; //指示是否在后台
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setPlayerController(LivePlayerController playerController) {
        mPlayerController = playerController;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable: w: " + width + " h: " + height);
        mSurfaceTexture = surface;
        openVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: w: " + width + " h: " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed: ");
        releaseResource();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated: ");
    }

    private void openVideo() {
        if (mUri == null || mSurfaceTexture == null) {
            return;
        }

        // Tell the music playback service to pause
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        getContext().sendBroadcast(i);

        releaseResource();

        try {
            NEMediaPlayer neMediaPlayer = null;
            if (mUri != null) {
                neMediaPlayer = new NEMediaPlayer();
            }
            mMediaPlayer = neMediaPlayer;
            getLogPath();
            mMediaPlayer.setLogPath(mLogLevel, mLogPath);
            mMediaPlayer.setBufferStrategy(0); // 设置缓冲策略，0为直播低延时，1为点播抗抖动
            mMediaPlayer.setHardwareDecoder(true); //设置是否开启硬件解码，0为软解，1为硬解, TextureView 只支持硬件解码
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            // mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener); 只用于点播
            mMediaPlayer.setOnVideoParseErrorListener(mVideoParseErrorListener);
            if (mUri != null) {
                //设置播放地址，返回0正常，返回-1则说明地址非法，需要使用网易视频云官方生成的地址
                int ret = mMediaPlayer.setDataSource(mUri.toString());
                if (ret < 0) { // 地址非法，请输入网易视频云官方地址！
                    releaseResource();
                    return;
                }
                mCurrState = INITIALIZED;
                mNextState = PREPARING;
            }
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync(getContext()); //初始化视频文件
            mCurrState = PREPARING;
            mNextState = PREPARED;

            attachPlayerController();

        } catch (IOException ex) {
            Log.e(TAG, "Unable to open content: " + mUri, ex);
            mErrorListener.onError(mMediaPlayer, -1, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Unable to open content: " + mUri, ex);
            mErrorListener.onError(mMediaPlayer, -1, 0);
            return;
        }
    }

    private void attachPlayerController() {
        if (mMediaPlayer != null && mPlayerController != null) {
            mPlayerController.setPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View) this.getParent() : this;
            mPlayerController.setAnchorView(anchorView);
            mPlayerController.setEnabled(mCurrState >= PREPARED);
        }
    }

    public void releaseResource() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrState = IDLE;
        }
    }

    private NELivePlayer.OnVideoParseErrorListener mVideoParseErrorListener = new NELivePlayer.OnVideoParseErrorListener() {
        public void onVideoParseError(NELivePlayer mp) {
            Toast.makeText(getContext(), "错误: 视频解析异常", Toast.LENGTH_SHORT).show();
        }
    };

    private NELivePlayer.OnInfoListener mInfoListener = new NELivePlayer.OnInfoListener() {
        /**
         * 在缓冲开始、缓冲结束时调用，可以在该函数内添加处理逻辑
         * @param mp 播放器实例
         * @param what info类型
         * @param extra 附加信息
         */
        @Override
        public boolean onInfo(NELivePlayer mp, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            if (mMediaPlayer != null) {
                if (what == NELivePlayer.NELP_BUFFERING_START) {
                    Log.d(TAG, "onInfo: NELP_BUFFERING_START");
                    if (mBufferView != null) {
                        mBufferView.setVisibility(View.VISIBLE);
                    }
                } else if (what == NELivePlayer.NELP_BUFFERING_END) {
                    Log.d(TAG, "onInfo: NELP_BUFFERING_END");
                    if (mBufferView != null) {
                        mBufferView.setVisibility(View.GONE);
                    }
                } else if (what == NELivePlayer.NELP_FIRST_VIDEO_RENDERED) {
                    Log.d(TAG, "onInfo: NELP_FIRST_VIDEO_RENDERED");
                } else if (what == NELivePlayer.NELP_FIRST_AUDIO_RENDERED) {
                    Log.d(TAG, "onInfo: NELP_FIRST_AUDIO_RENDERED");
                }
            }
            return true;
        }
    };

    private NELivePlayer.OnBufferingUpdateListener mBufferingUpdateListener = new NELivePlayer.OnBufferingUpdateListener() {
        /**
         * 网络视频流缓冲发生变化时调用，可以在该函数内添加处理逻辑
         * @param  mp 播放器实例
         * @param  percent 缓冲百分比
         */
        @Override
        public void onBufferingUpdate(NELivePlayer mp, int percent) {
            Log.d(TAG, "onBufferingUpdate: " + percent);
        }
    };

    private NELivePlayer.OnCompletionListener mCompletionListener = new NELivePlayer.OnCompletionListener() {
        /**
         * 播放完成后调用，可以在该函数内添加处理逻辑
         * @param  mp 播放器实例
         */
        @Override
        public void onCompletion(NELivePlayer mp) {
            Log.d(TAG, "onCompletion");
            /**
             * 对于点播文件或本地文件，播放结束后会触发该回调。
             * 对于直播来说，播放器无法判断直播是否结束，只能通过业务服务器来进行通知。
             * 若主播推流结束，播放器可能会读取不到数据超时退出，进入 onError 回调。
             */
        }
    };

    private NELivePlayer.OnErrorListener mErrorListener = new NELivePlayer.OnErrorListener() {
        /**
         * 播放发生错误时调用，可以在该函数内添加处理逻辑
         * @param mp 播放器实例
         * @param what 错误类型
         * @param extra 附加信息
         */
        @Override
        public boolean onError(NELivePlayer mp, int what, int extra) {
            Log.d(TAG, "Error: " + what + " ," + extra);
            mCurrState = ERROR;
            if (mPlayerController != null) {
                mPlayerController.hide();
            }

            Toast.makeText(getContext(), "错误: 直播已经结束,请稍后再试", Toast.LENGTH_SHORT).show();

            return true;
        }
    };

    NELivePlayer.OnVideoSizeChangedListener mSizeChangedListener = new NELivePlayer.OnVideoSizeChangedListener() {

        /**
         * 视频大小发生变化时调用，可以在该函数内添加处理逻辑
         * @param  mp 播放器实例
         * @param  width 视频宽度
         * @param  height 视频高度
         * @param  sarNum 像素宽高比的分子
         * @param  sarDen 像素宽高比的分母
         */
        @Override
        public void onVideoSizeChanged(NELivePlayer mp, int width, int height,
                                       int sarNum, int sarDen) {
            Log.d(TAG, "onVideoSizeChanged: " + width + " x " + height);
            mVideoWidth = width;
            mVideoHeight = height;
            mPixelSarNum = sarNum;
            mPixelSarDen = sarDen;
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                scalePlayerBasedOnVideoSize();
            }
        }
    };

    NELivePlayer.OnPreparedListener mPreparedListener = new NELivePlayer.OnPreparedListener() {

        /**
         * 预处理完成后调用，可以在该函数内添加处理逻辑
         * @param  mp 播放器实例
         */
        @Override
        public void onPrepared(NELivePlayer mp) {
            Log.d(TAG, "onPrepared");
            mCurrState = PREPARED;
            mNextState = STARTED;

            // briefly show the mediacontroller
            if (mPlayerController != null) {
                mPlayerController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            Log.d(TAG, "onPrepared: videoWidth: " + mVideoWidth + ", videoHeight: " + mVideoHeight);

            if (mVideoWidth != 0 && mVideoHeight != 0) {
                scalePlayerBasedOnVideoSize();
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    if (mNextState == STARTED) {
                        if (!isPaused()) {
                            start();
                        }
                        if (mPlayerController != null) {
                            mPlayerController.show();
                        }
                    }
                }
            }
        } // onPrepared
    };

    //获取日志文件路径
    public void getLogPath() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mLogPath = Environment.getExternalStorageDirectory() + "/log/";
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null && mCurrState == PREPARED) {
            mMediaPlayer.start();
            mCurrState = STARTED;
        }
        mNextState = STARTED;
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mCurrState >= PREPARED) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrState = PAUSED;
            }
        }
        mNextState = PAUSED;
    }

    @Override
    public boolean isPaused() {
        return mCurrState == PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrState == STARTED;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public void setMute(boolean mute) {

    }

    @Override
    public boolean isMute() {
        return false;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        mFullScreen = fullScreen;
    }

    @Override
    public boolean isFullScreen() {
        return mFullScreen;
    }


    /**
     * 资源释放成功通知的消息接收器类
     */
    private class ResourceReleaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NEMediaPlayer.NELP_RELEASE_SUCCESS)) {
                Log.i(TAG, NEMediaPlayer.NELP_RELEASE_SUCCESS);
                unregisterBroadcast(); // 接收到消息后反注册监听器
            }
        }
    }

    public void scalePlayerBasedOnVideoSize() {
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            float aspectRatio = mVideoWidth * 1.0f / mVideoHeight;
            if (mPixelSarNum > 0 && mPixelSarDen > 0) {
                aspectRatio = aspectRatio * mPixelSarNum / mPixelSarDen;
            }

            mSurfaceHeight = mVideoHeight;
            mSurfaceWidth = mVideoWidth;

            ViewGroup.LayoutParams params = getLayoutParams();
            if (mFullScreen) {

            } else {
                params.width = getWidth();
                params.height = (int) (params.width / aspectRatio);
            }
            setLayoutParams(params);
        }
    }

}
