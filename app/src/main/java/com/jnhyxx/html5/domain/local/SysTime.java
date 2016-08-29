package com.jnhyxx.html5.domain.local;

import com.android.volley.DefaultRetryPolicy;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.TimeRecorder;

import java.util.Date;

public class SysTime {

    private static final String RECORD_KEY = "SysTime";

    private static SysTime sSysTime;

    public static SysTime getSysTime() {
        if (sSysTime == null) {
            sSysTime = new SysTime();
        }
        return sSysTime;
    }

    private long mSystemTime;

    public void sync() {
        if (TimeRecorder.getElapsedTimeInMinute(RECORD_KEY) < 10) return;

        API.User.getSystemTime()
                .setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 3, 1))
                .setCallback(new Callback<Resp<Long>>() {
                    @Override
                    public void onReceive(Resp<Long> resp) {
                        if (resp.isSuccess() && resp.getData() != null) {
                            mSystemTime = resp.getData().longValue();
                            Preference.get().setServerTime(mSystemTime);
                            TimeRecorder.record(RECORD_KEY);
                        }
                    }
                }).fire();
    }

    public long getSystemTimestamp() {
        if (mSystemTime == 0) {
            mSystemTime = Preference.get().getServerTime();
            if (mSystemTime == 0) {
                return new Date().getTime();
            }
        }
        return mSystemTime + TimeRecorder.getElapsedTimeInMillis(RECORD_KEY);
    }
}
