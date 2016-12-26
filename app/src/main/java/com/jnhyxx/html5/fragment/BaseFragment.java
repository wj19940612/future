package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.TimerHandler;
import com.johnz.kutils.net.ApiIndeterminate;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    private TimerHandler mTimerHandler;
    protected String TAG;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onShow(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onShow(tag);
        }
    }

    @Override
    public void onDismiss(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onDismiss(tag);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScheduleJob();
        API.cancel(TAG);
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

    protected void updateUsableMoneyScore(final LocalUser.Callback callback) {
        if (LocalUser.getUser().isLogin()) {
            API.User.getUserShortInfo().setTag(TAG)
                    .setCallback(new Callback<Resp<UserInfo>>(false) {
                        @Override
                        public void onSuccess(Resp<UserInfo> userInfoResp) {
                            Log.d("VolleyHttp", getUrl() + " onSuccess: " + userInfoResp.toString());
                            if (userInfoResp.isSuccess()) {
                                LocalUser.getUser().setUsableMoneyScore(userInfoResp.getData());
                                if (callback != null) {
                                    callback.onUpdateCompleted();
                                }
                            }
                        }

                        @Override
                        public void onReceive(Resp<UserInfo> userInfoResp) {

                        }
                    }).fire();
        }
    }
}
