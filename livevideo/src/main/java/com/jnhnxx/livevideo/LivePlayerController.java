package com.jnhnxx.livevideo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import static com.netease.neliveplayer.util.sys.NetworkUtil.TAG;

public class LivePlayerController extends FrameLayout {

    private ILivePlayer mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private boolean mShowing;
    private static final int sDefaultTimeout = 3000; // 3000ms;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private ImageView mPauseButton;
    private ImageView mSetPlayerScaleButton;
    private ImageView mMuteButton;

    private boolean mMuteFlag = false;
    private boolean mPaused = false;
    private boolean mIsFullScreen = false;

    public LivePlayerController(Context context) {
        super(context);
        mContext = context;
        initFloatingWindow();
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    public void setAnchorView(View view) {
        mAnchor = view;
        removeAllViews();
        mRoot = LayoutInflater.from(mContext).inflate(R.layout.media_controller, this);
        mWindow.setContentView(mRoot);
        mWindow.setWidth(LayoutParams.MATCH_PARENT);
        mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        initControllerView(mRoot);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageView) v.findViewById(R.id.media_controller_play_pause); //播放暂停按钮
        mPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) return;

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPauseButton.setImageResource(R.drawable.media_controller_pause);
                } else {
                    mPlayer.start();
                    mPauseButton.setImageResource(R.drawable.media_controller_play);
                }
            }
        });

        mSetPlayerScaleButton = (ImageView) v.findViewById(R.id.video_player_scale);  //画面显示模式按钮
        mSetPlayerScaleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMuteButton = (ImageView) v.findViewById(R.id.video_player_mute);  //静音按钮
        mMuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) return;

                if (mPlayer.isMute()) {
                    mPlayer.setMute(false);
                    mMuteButton.setImageResource(R.drawable.media_controller_mute01);
                } else {
                    mPlayer.setMute(true);
                    mMuteButton.setImageResource(R.drawable.media_controller_mute02);
                }
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

        setEnabled(false);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    updateProgress();
                    if (mShowing) {
                        sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
                    }
                    break;
            }
        }
    };

    private void updateProgress() {
        if (mPlayer != null) {
            long position = mPlayer.getCurrentPosition();
            long duration = mPlayer.getDuration();
            Log.d(TAG, "updateProgress: pos: " + position + ", dur: " + duration);

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

    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            int[] location = new int[2];
            mAnchor.getLocationOnScreen(location);
            Rect anchorRect = new Rect(location[0], location[1],
                    location[0] + mAnchor.getWidth(), location[1]
                    + mAnchor.getHeight());

            View popupView = mWindow.getContentView();
            popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mWindow.setAnimationStyle(mAnimStyle);
            mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY,
                    anchorRect.left, anchorRect.bottom - popupView.getMeasuredHeight());

            mShowing = true;

            mHandler.sendEmptyMessage(SHOW_PROGRESS);
            if (timeout != 0) {
                mHandler.removeMessages(FADE_OUT);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
            }
        }
    }

    public void hide() {
        if (mShowing && mAnchor != null) {
            mHandler.removeMessages(SHOW_PROGRESS);
            mWindow.dismiss();
            mShowing = false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mSetPlayerScaleButton != null) {
            mSetPlayerScaleButton.setEnabled(enabled);
        }
        if (mMuteButton != null) {
            mMuteButton.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    public void setPlayer(LivePlayer livePlayer) {
        mPlayer = livePlayer;
    }

    public boolean isShowing() {
        return mShowing;
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
//            show(3600000);
//            mDragging = true;
//
//            mHandler.removeMessages(SHOW_PROGRESS);
        }

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
//            mHandler.removeMessages(SHOW_PROGRESS);
//            mDragging = false;
//            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };
}
