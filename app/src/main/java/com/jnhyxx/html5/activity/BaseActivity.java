package com.jnhyxx.html5.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.TimerHandler;
import com.jnhyxx.html5.view.dialog.Progress;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.net.ApiIndeterminate;
import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    public static final int REQUEST_CODE = 8;

    protected String TAG;

    private TimerHandler mTimerHandler;
    private Progress mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        TAG = this.getClass().getSimpleName();
        mProgress = new Progress(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                API.cancel(TAG);
            }
        });
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
        API.cancel(TAG);

        SmartDialog.dismiss(this);
        mProgress.dismissAll();
        stopScheduleJob();
    }

    protected FragmentActivity getActivity() {
        return this;
    }

    @Override
    public void onShow(String tag) {
        if (mProgress != null) {
            mProgress.show(this);
        }
    }

    @Override
    public void onDismiss(String tag) {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    protected void startScheduleJob(int millisecond) {
        stopScheduleJob();

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

    @Override
    public void onTimeUp(int count) {

    }
}
