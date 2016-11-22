package com.jnhnxx.livevideo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class LiveVideo extends RelativeLayout implements IPlayerController {

    private static final String TAG = "LiveVideo";

    private static final int sDefaultTimeout = 3000; // 3000ms;
    private static final int FADE_OUT = 1;
    private static final int SHOW = 2;

    private LivePlayer mPlayer;
    private ProgressBar mProgress;
    private TextView mEndTime;
    private TextView mCurrentTime;
    private boolean mShowing;

    private View mBufferingView;
    private View mController;
    private ImageView mPauseButton;
    private ImageView mSetPlayerScaleButton;
    private ImageView mMuteButton;

    public LiveVideo(Context context) {
        super(context);
        init();
    }

    public LiveVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPlayer = new LivePlayer(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        addView(mPlayer, params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mBufferingView = createBufferingView();
        mBufferingView.setVisibility(GONE);
        addView(mBufferingView, params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                dp2px(40));
        params.addRule(ALIGN_PARENT_BOTTOM);
        mController = createController();
        mController.setVisibility(GONE);
        addView(mController, params);

        mPlayer.setPlayerController(this);
        mPlayer.setBufferingView(mBufferingView);
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
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
                onStartButtonClick();
            }
        });

        mSetPlayerScaleButton = (ImageView) v.findViewById(R.id.video_player_scale);  //画面显示模式按钮
        mSetPlayerScaleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onScaleButtonClick();
            }
        });

        mMuteButton = (ImageView) v.findViewById(R.id.video_player_mute);  //静音按钮
        mMuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onMuteButtonClick();
            }
        });

        mProgress = (SeekBar) v.findViewById(R.id.media_controller_seekbar);  //进度条
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
                seeker.setThumbOffset(1);
            }
            mProgress.setMax(1000);
        }
        mProgress.setEnabled(false);

        mEndTime = (TextView) v.findViewById(R.id.media_controller_time_total); //总时长
        mCurrentTime = (TextView) v.findViewById(R.id.media_controller_time_current); //当前播放位置

        return v;
    }

    public void setVideoPath(String path) { //设置视频文件路径
        mPlayer.setVideoPath(path);
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
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

    @Override
    public void mute(boolean mute) {

    }

    @Override
    public void start(boolean start) {

    }

    @Override
    public void fullScreen(boolean full) {
        if (full) {
            mPlayer.setFullScreen(true);
            mSetPlayerScaleButton.setImageResource(R.drawable.media_controller_scale);
        } else {
            mPlayer.setFullScreen(false);
            mSetPlayerScaleButton.setImageResource(R.drawable.media_controller_scale_full);
        }
    }


    public void onMuteButtonClick() {
        if (mPlayer == null || !mShowing) return;

        if (mPlayer.isMute()) {
            mPlayer.setMute(false);
            mMuteButton.setImageResource(R.drawable.media_controller_mute_on);
        } else {
            mPlayer.setMute(true);
            mMuteButton.setImageResource(R.drawable.media_controller_mute_off);
        }
    }

    public void onScaleButtonClick() {
        if (mPlayer == null || !mShowing) return;

        if (mPlayer.isFullScreen()) {
            fullScreen(false);
        } else {
            fullScreen(true);
        }
    }

    public void onStartButtonClick() {
        if (mPlayer == null || !mShowing) return;

        if (mPlayer.isStarted()) {
            mPlayer.stop();
            mPauseButton.setImageResource(R.drawable.media_controller_start);
        } else {
            mPlayer.start();
            mPauseButton.setImageResource(R.drawable.media_controller_stop);
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

        if (mPlayer.isStopped()) {
            mPauseButton.setImageResource(R.drawable.media_controller_start);
        } else {
            mPauseButton.setImageResource(R.drawable.media_controller_stop);
        }

        if (mPlayer.isMute()) {
            mMuteButton.setImageResource(R.drawable.media_controller_mute_off);
        } else {
            mMuteButton.setImageResource(R.drawable.media_controller_mute_on);
        }

        if (mPlayer.isFullScreen()) {
            mSetPlayerScaleButton.setImageResource(R.drawable.media_controller_scale);
        } else {
            mSetPlayerScaleButton.setImageResource(R.drawable.media_controller_scale_full);
        }

        updateTimes();
    }

    private void updateTimes() {
        if (mPlayer != null) {
            long position = mPlayer.getCurrentPosition();
            long duration = mPlayer.getDuration();
            Log.d(TAG, "updatePlayerController: pos: " + position + ", dur: " + duration);

            if (duration > 0) {
                mEndTime.setText(stringForTime(duration));
            } else {
                mEndTime.setText("--:--:--");
            }

            mCurrentTime.setText(stringForTime(position));
        }
    }

    private static String stringForTime(long position) {
        int totalSeconds = (int) ((position / 1000.0) + 0.5);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
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
    public boolean isShowing() {
        return mShowing;
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar bar) {
//            show(3600000);
//            mDragging = true;
//
//            mHandler.removeMessages(SHOW);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
//            if (mPlayer.getMediaType().equals("livestream")) {
//                return;
//            }
//
//            if (!fromuser)
//                return;
//
//            final long newposition = (mDuration * progress) / 1000;
//            String time = stringForTime(newposition);
//            if (mInstantSeeking) {
//                mHandler.removeCallbacks(lastRunnable);
//                lastRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        mPlayer.seekTo(newposition);
//                    }
//                };
//                mHandler.postDelayed(lastRunnable, 200);
//            }
//
//            if (mCurrentTime != null)
//                mCurrentTime.setText(time);
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
//            if (mPlayer.getMediaType().equals("livestream")) {
//                AlertDialog alertDialog;
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
//                alertDialogBuilder.setTitle("注意");
//                alertDialogBuilder.setMessage("直播不支持seek操作");
//                alertDialogBuilder.setCancelable(false)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ;
//                            }
//                        });
//                alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//                mProgress.setProgress(0);
//                //return;
//            }
//            if (!mPlayer.getMediaType().equals("livestream")) {
//                if (!mInstantSeeking)
//                    mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
//            }
//
//            show(sDefaultTimeout);
//            mHandler.removeMessages(SHOW);
//            mDragging = false;
//            mHandler.sendEmptyMessageDelayed(SHOW, 1000);
        }
    };
}
