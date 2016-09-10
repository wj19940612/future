package com.jnhyxx.html5.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.account.BankcardAuthActivity;
import com.jnhyxx.html5.activity.account.NameAuthActivity;
import com.jnhyxx.html5.domain.account.LocalCacheUserInfoManager;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.account.UserIsModifyNickName;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.CustomToast;
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
    //绑定银行卡的请求码
    private static final int REQUEST_CODE_BIND_BANK = 24400;
    //修改昵称的请求码
    private static final int REQUEST_CODE_MODIFY_NICK_NAME = 45900;

    LocalCacheUserInfoManager mLocalCacheUserInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mLocalCacheUserInfoManager = LocalCacheUserInfoManager.getInstance();
        UserInfo userInfo = mLocalCacheUserInfoManager.getUser();
        if (!mLocalCacheUserInfoManager.isLogin()) {
            mTvRealNameSetting.setText(R.string.nickname_unknown);
            mRlRealNameSetting.setEnabled(false);
            mTvBindBankCardSetting.setText(R.string.nickname_unknown);
            mRlBindBankCardSetting.setEnabled(false);
            mTvUserNameSetting.setText(R.string.nickname_unknown);
            mRlUserNameSetting.setEnabled(false);
        }
        if (userInfo != null) {
            Log.d(TAG, "用户信息 " + userInfo.toString());
            getUserNickName(userInfo);
            if (!TextUtils.isEmpty(userInfo.getUserPhone())) {
                String userPhone = userInfo.getUserPhone();
                String phoneNumberMiddle = CommonMethodUtils.hidePhoneNumberMiddle(userPhone);
                mTvUserPhoneSetting.setText(phoneNumberMiddle);
                Log.d(TAG, "手机号码" + userPhone);
            }

            getRealNameStatus(mLocalCacheUserInfoManager, userInfo);
            getBindBankStatus(mLocalCacheUserInfoManager, userInfo);
        }

    }

    private void getUserNickName(UserInfo userInfo) {
        if (!TextUtils.isEmpty(userInfo.getUserName())) {
            mTvUserNameSetting.setText(userInfo.getUserName());
        }
    }

    private void getBindBankStatus(LocalCacheUserInfoManager localCacheUserInfoManager, UserInfo userInfo) {
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

    private void getRealNameStatus(LocalCacheUserInfoManager localCacheUserInfoManager, UserInfo userInfo) {
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
    }


    @OnClick({R.id.rlUserNameSetting, R.id.rlRealNameSetting, R.id.rlBindBankCardSetting, R.id.tvLoginOutSetting})
    void onClick(View view) {
        switch (view.getId()) {
            //用户名
            case R.id.rlUserNameSetting:
                updateNickName();
                break;
            case R.id.rlRealNameSetting:
                Launcher.with(SettingActivity.this, NameAuthActivity.class).executeForResult(REQUEST_CODE_NAME_AUTH);
                break;
            case R.id.rlBindBankCardSetting:
                Launcher.with(SettingActivity.this, BankcardAuthActivity.class).executeForResult(REQUEST_CODE_BIND_BANK);
                break;
            case R.id.tvLoginOutSetting:
                loginOut();
                break;
        }
    }

    private void updateNickName() {
        API.User.findIsUpdateNickName().setTag(TAG).setIndeterminate(this).setCallback(new Callback1<Resp<UserIsModifyNickName>>() {

            @Override
            protected void onRespSuccess(Resp<UserIsModifyNickName> resp) {
                if (!resp.getData().isBIsSetNickName()) {
                    Launcher.with(SettingActivity.this, ModifyNickNameActivity.class).executeForResult(REQUEST_CODE_MODIFY_NICK_NAME);
                }else{
                    ToastUtil.curt(R.string.modify_nick_name_twice);
                }
            }
        }).fire();
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
                        mTvUserNameSetting.setText(R.string.nickname_unknown);
                        mTvBindBankCardSetting.setText(R.string.nickname_unknown);
                        mTvRealNameSetting.setText(R.string.nickname_unknown);
                    }
                }
            }).fire();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocalCacheUserInfoManager instance = LocalCacheUserInfoManager.getInstance();
        UserInfo user = instance.getUser();
        if (requestCode == REQUEST_CODE_NAME_AUTH && resultCode == RESULT_OK) {
            if (user != null) {
                getRealNameStatus(instance, user);
            }
        }
        if (requestCode == REQUEST_CODE_MODIFY_NICK_NAME && resultCode == RESULT_OK) {
            if (user != null) {
                getUserNickName(user);
            }
        }
    }
}
