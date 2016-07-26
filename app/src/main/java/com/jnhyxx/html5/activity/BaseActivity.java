package com.jnhyxx.html5.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.activity.dialog.ProgressActivity;
import com.jnhyxx.html5.net.APIBase;
import com.johnz.kutils.net.ApiIndeterminate;
import com.umeng.message.PushAgent;

import java.lang.ref.WeakReference;

public class BaseActivity extends AppCompatActivity implements ApiIndeterminate {

    protected static String TAG;

    private TimerHandler mTimerHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        TAG = this.getClass().getSimpleName();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Preference.get().setForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preference.get().setForeground(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        APIBase.cancel(TAG);
        stopScheduleJob();
    }

    protected FragmentActivity getActivity() {
        return this;
    }

    @Override
    public void onShow(String tag) {
        ProgressActivity.show(this, tag);
    }

    @Override
    public void onDismiss(String tag) {
        ProgressActivity.dismiss(this, tag);
    }

    private static class TimerHandler extends Handler {

        WeakReference<BaseActivity> mReference;

        public TimerHandler(BaseActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = mReference.get();
            if (activity != null) {
                activity.onTimeUp();
            }
            int delayMillis = msg.what;
            this.sendEmptyMessageDelayed(delayMillis, delayMillis);
        }
    }

    protected void startScheduleJob(int millisecond) {
        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }
        mTimerHandler.sendEmptyMessageDelayed(millisecond, 0);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
        }
    }

    protected void onTimeUp() {}
}
