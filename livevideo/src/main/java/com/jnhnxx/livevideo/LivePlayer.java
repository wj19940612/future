package com.jnhnxx.livevideo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class LivePlayer extends RelativeLayout implements IPlayerController, IPlayer {

    private static final String TAG = "LivePlayer";

    private static final int DEFAULT_TIMEOUT = 3500; // 3000ms;
    private static final int FADE_OUT = 1;
    private static final int SHOW = 2;

    private Player mPlayer;

    private boolean mShowing;

    private View mBufferingView;
    private View mController;
    private ImageView mPauseButton;
    private ImageView mSetPlayerScaleButton;
    private ImageView mMuteButton;
    private TextView mCurrentTime;

    private OnScaleButtonClickListener mOnScaleButtonClickListener;
    private OnMuteButtonClickListener mOnMuteButtonClickListener;
    private OnPlayClickListener mOnPlayClickListener;

    private LinearLayout mCenterPauseView;

    @Override
    public void start() {
        mPlayer.start();
        mPauseButton.setImageResource(R.drawable.media_controller_stop);
        mCenterPauseView.setVisibility(GONE);
    }

    @Override
    public void stop() {
        mPlayer.stop();
        mPauseButton.setImageResource(R.drawable.media_controller_start);
        mCenterPauseView.setVisibility(VISIBLE);
    }

    @Override
    public boolean isStarted() {
        return mPlayer.isStarted();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getBufferPercentage() {
        return mPlayer.getBufferPercentage();
    }

    @Override
    public void setMute(boolean mute) {
        mPlayer.setMute(mute);
        if (mute) {
            mMuteButton.setImageResource(R.drawable.media_controller_mute_off);
        } else {
            mMuteButton.setImageResource(R.drawable.media_controller_mute_on);
        }
    }

    @Override
    public boolean isMute() {
        return mPlayer.isMute();
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        mPlayer.setFullScreen(fullScreen);
        if (fullScreen) {
            mSetPlayerScaleButton.setImageResource(R.drawable.media_controller_scale);
        } else {
            mSetPlayerScaleButton.setImageResource(R.drawable.media_controller_scale_full);
        }
    }

    @Override
    public boolean isFullScreen() {
        return mPlayer.isFullScreen();
    }

    public interface OnScaleButtonClickListener {
        void onClick(boolean fullscreen);
    }

    //静音按钮监听
    public interface OnMuteButtonClickListener {
        void onClick(boolean isMute);
    }

    //播放按钮的监听
    public interface OnPlayClickListener {
        void onClick(boolean isPlay);
    }

    public LivePlayer(Context context) {
        super(context);
        init();
    }

    public LivePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPlayer = new Player(getContext());

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        addView(mPlayer, params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mBufferingView = createBufferingView();
        mBufferingView.setVisibility(GONE);
        addView(mBufferingView, params);

        mCenterPauseView = createPauseView();
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mCenterPauseView, params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        mController = createController();
        mController.setVisibility(GONE);
        addView(mController, params);

        mPlayer.setPlayerController(this);
        mPlayer.setBufferingView(mBufferingView);
    }

    private LinearLayout createPauseView() {
        LinearLayout pauseView = new LinearLayout(getContext());
        ImageView startButton = new ImageView(getContext());
        startButton.setScaleType(ImageView.ScaleType.CENTER);
        startButton.setImageResource(R.drawable.ic_live_stop_screen_center);
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        pauseView.setGravity(Gravity.CENTER);
        pauseView.setBackgroundResource(R.drawable.bg_player_paused);
        pauseView.addView(startButton);
        return pauseView;
    }

    private View createBufferingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.player_buffering, null);
    }

    private View createController() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.player_controller, null);
        mPauseButton = (ImageView) v.findViewById(R.id.media_controller_play_pause); //播放暂停按钮
        mPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted()) {
                    stop();
                    if (mOnPlayClickListener != null) {
                        mOnPlayClickListener.onClick(false);
                    }
                } else {
                    start();
                    if (mOnPlayClickListener != null) {
                        mOnPlayClickListener.onClick(true);
                    }
                }
            }
        });

        mSetPlayerScaleButton = (ImageView) v.findViewById(R.id.video_player_scale);  //画面显示模式按钮
        mSetPlayerScaleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen()) {
                    setFullScreen(false);
                    if (mOnScaleButtonClickListener != null) {
                        mOnScaleButtonClickListener.onClick(false);
                    }
                } else {
                    setFullScreen(true);
                    if (mOnScaleButtonClickListener != null) {
                        mOnScaleButtonClickListener.onClick(true);
                    }
                }
            }
        });

        mMuteButton = (ImageView) v.findViewById(R.id.video_player_mute);  //静音按钮
        mMuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMute()) {
                    setMute(false);
                    if (mOnMuteButtonClickListener != null) {
                        mOnMuteButtonClickListener.onClick(false);
                    }
                } else {
                    setMute(true);
                    if (mOnMuteButtonClickListener != null) {
                        mOnMuteButtonClickListener.onClick(true);
                    }
                }
            }
        });

        mCurrentTime = (TextView) v.findViewById(R.id.media_controller_time_current); //当前播放位置

        return v;
    }

    public void setVideoPath(String path) { //设置视频文件路径
        mPlayer.setVideoPath(path);
    }

    public void setOnScaleButtonClickListener(OnScaleButtonClickListener onScaleButtonClickListener) {
        mOnScaleButtonClickListener = onScaleButtonClickListener;
    }

    public void setOnMuteButtonClickListener(OnMuteButtonClickListener onMuteButtonClickListener) {
        this.mOnMuteButtonClickListener = onMuteButtonClickListener;
    }

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.mOnPlayClickListener = onPlayClickListener;
    }

    @Override
    public void show() {
        show(DEFAULT_TIMEOUT);
    }

    private void show(int timeout) {
        if (!mShowing) {
            mShowing = true;
            mHandler.sendEmptyMessage(SHOW);

            if (timeout != 0) {
                mHandler.removeMessages(FADE_OUT);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
            }
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            mHandler.removeMessages(SHOW);
            mController.setVisibility(GONE);
            mShowing = false;
        }
    }

    @Override
    public void enable(boolean enable) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enable);
        }
        if (mSetPlayerScaleButton != null) {
            mSetPlayerScaleButton.setEnabled(enable);
        }
        if (mMuteButton != null) {
            mMuteButton.setEnabled(enable);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW:
                    if (mShowing) {
                        updatePlayerController();
                        sendEmptyMessageDelayed(SHOW, 1000);
                    }
                    break;
            }
        }
    };

    private void updatePlayerController() {
        if (mController.getVisibility() != VISIBLE) {
            mController.setVisibility(VISIBLE);
        }
        updateTimes();
    }

    private void updateTimes() {
        if (mPlayer != null) {
            long position = mPlayer.getCurrentPosition();
            mCurrentTime.setText(formatPosition2Time(position));
        }
    }

    private static String formatPosition2Time(long position) {
        int totalSeconds = (int) ((position / 1000.0) + 0.5);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }
}
