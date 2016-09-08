package com.jnhyxx.html5.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.account.NameAuthActivity;
import com.jnhyxx.html5.domain.model.LocalCacheUserInfoManager;
import com.jnhyxx.html5.domain.model.UserInfo;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.rlUserNameSetting)
    RelativeLayout mRlUserNameSetting;
    @BindView(R.id.rlRealNameSetting)
    RelativeLayout rlRealNameSetting;
    @BindView(R.id.rlBindBankCardSetting)
    RelativeLayout rlBindBankCardSetting;

    @BindView(R.id.tvUserNameSetting)
    TextView tvUserNameSetting;
    @BindView(R.id.tvRealNameSetting)
    TextView tvRealNameSetting;
    @BindView(R.id.tvBindBankCardSetting)
    TextView tvBindBankCardSetting;
    @BindView(R.id.tvUserphoneSetting)
    TextView tvUserphoneSetting;
    @BindView(R.id.tvLoginOutSetting)
    TextView loginOut;
    private static final int REQUEST_CODE_NAME_AUTH = 900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        LocalCacheUserInfoManager localCacheUserInfoManager = LocalCacheUserInfoManager.getInstance();
        UserInfo userInfo = localCacheUserInfoManager.getUser();
        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.getUserName())) {
                tvUserNameSetting.setText(userInfo.getUserName());
            }
            if (!TextUtils.isEmpty(userInfo.getUserPhone())) {
                String userPhone = userInfo.getUserPhone();
                String phoneNumberMiddle = CommonMethodUtils.hidePhoneNumberMiddle(userPhone);
                tvUserphoneSetting.setText(phoneNumberMiddle);
                Log.d(TAG, "手机号码" + userPhone);
            }
        }
        //如果已经实名验证成功，则实名验证不可点击
        if (localCacheUserInfoManager.isAuthName()) {
            rlRealNameSetting.setEnabled(false);
        }
    }


    @OnClick({R.id.rlUserNameSetting, R.id.rlRealNameSetting, R.id.rlBindBankCardSetting, R.id.tvLoginOutSetting})
    void onClick(View view) {
        switch (view.getId()) {
            //用户名
            case R.id.rlUserNameSetting:
                break;
            case R.id.rlRealNameSetting:
                Launcher.with(SettingActivity.this, NameAuthActivity.class).executeForResult(REQUEST_CODE_NAME_AUTH);
                break;
            case R.id.rlBindBankCardSetting:
                break;
            case R.id.tvLoginOutSetting:
                loginOut();
                break;
        }
    }

    //退出登陆
    private void loginOut() {
        final LocalCacheUserInfoManager localCacheUserInfoManager = LocalCacheUserInfoManager.getInstance();
        Log.d(TAG, "用户是否登陆  " + localCacheUserInfoManager.isLogin());
        if (localCacheUserInfoManager.isLogin()) {
            API.User.loginOut().setTag(TAG).setIndeterminate(this).setCallback(new Callback<Resp>() {
                @Override
                public void onReceive(Resp resp) {
                    Log.d(TAG, "退出登陆 " + resp.getCode() + "\n" + resp.getMsg());
                    ToastUtil.curt(resp.getMsg());
                    if (resp.isSuccess()) {
                        localCacheUserInfoManager.setUser(null);
                        tvUserphoneSetting.setText("");
                        tvUserNameSetting.setText("");
                    }
                }
            }).fire();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NAME_AUTH && resultCode == RESULT_OK) {
            tvRealNameSetting.setText(R.string.authorized);
            rlRealNameSetting.setEnabled(false);
        }
    }
}
