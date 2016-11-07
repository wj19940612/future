package com.lecloud.skin.ui.utils;
//package com.le.skin.ui.utils;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.letv.controller.interfacev1.ISplayerController;
//import com.letv.universal.notice.ObservablePlus;
//
//public abstract class PlayerTimer extends Handler {
//
//    public static final int TIMER_HANDLER_SHOW_PROGRESS = 1;
//    public static final int TIMER_HANDLER_HIDE_PROGRESS = 2;
//    public static final int TIMER_HANDLER_PER_TIME = 4901;
//    public static final String key_position = "key_position";
//    public static final String key_duration = "key_duration";
//    public static final String key_bufferpercentage = "key_bufferpercentage";
//    private static final String TAG = "PlayerTimer";
//
//    protected ISplayerController player;
//    private boolean isPause = false;
//    private ObservablePlus obs = new ObservablePlus();
////    private static StringBuilder formatBuilder;
////    private static Formatter formatter;
//
//    public PlayerTimer(ISplayerController player) {
//        this.player = player;
//    }
//
//    public ObservablePlus getObserver() {
//        Log.d(TAG, "[obs] [playerTimer] obs count:"+obs.countObservers());
//        return obs;
//    }
//
//    public void pause() {
//        isPause = true;
//    }
//
//    public void resume() {
//        isPause = false;
//        try {
//            removeMessages(TIMER_HANDLER_SHOW_PROGRESS);
//            sendEmptyMessage(TIMER_HANDLER_SHOW_PROGRESS);
//        } catch (Exception e) {
//
//        }
//    }
//
//    public boolean isPause() {
//        return isPause;
//    }
//
//    @Override
//    public void handleMessage(Message msg) {
//
//        switch (msg.what) {
//        case TIMER_HANDLER_HIDE_PROGRESS:
//            hideProgress();
//            reset();
//            break;
//        case TIMER_HANDLER_SHOW_PROGRESS:
//            setProgress();
//            if (player != null && player.isPlaying()) {
//                msg = obtainMessage(TIMER_HANDLER_SHOW_PROGRESS);
//                sendMessageDelayed(msg, 1000);
//            }
//            break;
//
//        default:
//            break;
//        }
//    }
//
//    public abstract void hideProgress();
//
//    public void reset() {
//        try {
//            removeMessages(TIMER_HANDLER_SHOW_PROGRESS);
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * 获取当前视频播放时间
//     * 
//     * @return
//     */
//    private long setProgress() {
//        long position = getPosition();
//        long duration = getDuration();
//        if (duration > 0) {
//            /**
//             * 缓冲进度
//             */
//            long percentage = player.getBufferPercentage();
//            if (!isPause()) {
//                Bundle data = new Bundle();
//                data.putInt("state", TIMER_HANDLER_PER_TIME);
//                data.putInt(key_position, (int) (position));
//                data.putInt(key_duration, (int) (duration));
//                data.putInt(key_bufferpercentage, (int) percentage);
//                obs.notifyObserverPlus(data);
//            }
//        }
//
//        return position;
//    }
//
//    private long getDuration() {
//        if (player == null) {
//            return 0;
//        }
//        return player.getDuration();
//    }
//
//    private long getPosition() {
//        if (player == null) {
//            return 0;
//        }
//        return player.getPosition();
//    }
//
////    /**
////     * 初始化formatter
////     */
////    public static void initTextFormatter() {
////        formatBuilder = new StringBuilder();
////        formatter = new Formatter(formatBuilder, Locale.getDefault());
////    }
////
////    /**
////     * 
////     * @param times
////     * @return
////     */
////    public static String stringForTime(int times) {
////        int totalSeconds = times;
////        int seconds = totalSeconds % 60;
////        int minutes = (totalSeconds / 60) % 60;
////        int hours = totalSeconds / 3600;
////
////        formatBuilder.setLength(0);
////        if (hours > 0) {
////            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
////        } else {
////            return formatter.format("%02d:%02d", minutes, seconds).toString();
////        }
////    }
//}
