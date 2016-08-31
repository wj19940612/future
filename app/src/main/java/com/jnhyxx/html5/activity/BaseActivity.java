package com.jnhyxx.html5.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.domain.local.SysTime;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.TimerHandler;
import com.jnhyxx.html5.view.dialog.Progress;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.ApiIndeterminate;
import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    public static final int REQUEST_CODE = 8;

    public static final String ACTION_TOKEN_EXPIERD = "com.jnhyxx.app.TOKEN_EXPIERD";

    protected String TAG;

    Toast mToast;
    private TimerHandler mTimerHandler;
    private Progress mProgress;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SmartDialog.with(getActivity(), "Token 过期,重新登入")
                    .setCancelableOnTouchOutside(false)
                    .setNegative(R.string.cancel)
                    .setPositive(R.string.sign_in, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            Launcher.with(getActivity(), SignInActivity.class)
                                    .execute();
                        }
                    }).show();
        }
    };

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
        SysTime.getSysTime().sync();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Preference.get().setForeground(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(ACTION_TOKEN_EXPIERD));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preference.get().setForeground(false);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
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
