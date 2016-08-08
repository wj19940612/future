package com.jnhyxx.html5.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class TimerHandler extends Handler {

    public interface TimerCallback {
        void onTimeUp(int count);
    }

    WeakReference<TimerCallback> mWeakReference;
    private int mCount;

    public TimerHandler(TimerCallback callback) {
        mWeakReference = new WeakReference<>(callback);
        mCount = 0;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        TimerCallback callback = mWeakReference.get();
        if (callback != null) {
            callback.onTimeUp(++mCount);
            int delayMillis = msg.what;
            this.sendEmptyMessageDelayed(delayMillis, delayMillis);
        }
    }
}
