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
    RelativeLayout mRlRealNameSetting;
    @BindView(R.id.rlBindBankCardSetting)
    RelativeLayout mRlBindBankCardSetting;

    @BindView(R.id.tvUserNameSetting)
    TextView mTvUserNameSetting;
    @BindView(R.id.tvRealNameSetting)
    TextView mTvRealNameSetting;
    @BindView(R.id.tvBindBankCardSetting)
    TextView mTvBindBankCardSetting;
    @BindView(R.id.tvUserPhoneSetting)
    TextView mTvUserPhoneSetting;
    @BindView(R.id.tvLoginOutSetting)
    TextView mLoginOut;
    //实名认证的请求码
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
        if (!localCacheUserInfoManager.isLogin()) {
            mTvRealNameSetting.setText(R.string.nickname_unknown);
            mRlRealNameSetting.setEnabled(false);
            mTvBindBankCardSetting.setText(R.string.nickname_unknown);
            mRlBindBankCardSetting.setEnabled(false);
        }
        if (userInfo != null) {
            Log.d(TAG, "用户信息 " + userInfo.toString());
            if (!TextUtils.isEmpty(userInfo.getUserName())) {
                mTvUserNameSetting.setText(userInfo.getUserName());
            }
            if (!TextUtils.isEmpty(userInfo.getUserPhone())) {
                String userPhone = userInfo.getUserPhone();
                String phoneNumberMiddle = CommonMethodUtils.hidePhoneNumberMiddle(userPhone);
                mTvUserPhoneSetting.setText(phoneNumberMiddle);
                Log.d(TAG, "手机号码" + userPhone);
            }
            /**
             * idStatus实名状态 0未填写，1已填写，2已认证
             */
            if (userInfo.getIdStatus() == -1 || !localCacheUserInfoManager.isLogin()) {
                mTvRealNameSetting.setText(R.string.nickname_unknown);
                mRlRealNameSetting.setEnabled(false);
            } else if (userInfo.getIdStatus() == 0) {
                mTvRealNameSetting.setText(R.string.setting_no_write);
            } else if (userInfo.getIdStatus() == 1) {
                mTvRealNameSetting.setText(R.string.setting_write_now);
            } else if (userInfo.getIdStatus() == 2) {
                mTvRealNameSetting.setText(R.string.setting_attestation);
                mRlRealNameSetting.setEnabled(false);
            }
            /**
             * cardState银行卡状态 0未填写，1已填写，2已认证
             */
            if (userInfo.getCardState() == -1 || !localCacheUserInfoManager.isLogin()) {
                mTvBindBankCardSetting.setText(R.string.nickname_unknown);
                mRlBindBankCardSetting.setEnabled(false);
            } else if (userInfo.getCardState() == 0) {
                mTvBindBankCardSetting.setText(R.string.setting_no_write);
            } else if (userInfo.getCardState() == 1) {
                mTvBindBankCardSetting.setText(R.string.setting_write_now);
            } else if (userInfo.getCardState() == 2) {
                mTvBindBankCardSetting.setText(R.string.setting_attestation);
                mRlBindBankCardSetting.setEnabled(false);
            }
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
                        mTvUserPhoneSetting.setText("");
                        mTvUserNameSetting.setText("");
                    }
                }
            }).fire();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NAME_AUTH && resultCode == RESULT_OK) {
            mTvRealNameSetting.setText(R.string.authorized);
            mRlRealNameSetting.setEnabled(false);
        }
    }
}
