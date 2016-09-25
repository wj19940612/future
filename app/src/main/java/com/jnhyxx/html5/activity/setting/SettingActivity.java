package com.jnhyxx.html5.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.account.BankcardAuthActivity;
import com.jnhyxx.html5.activity.account.NameAuthActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.account.NickNameIsModified;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";

    //实名认证的请求码
    private static final int REQUEST_CODE_NAME_AUTH = 900;
    //绑定银行卡的请求码
    private static final int REQUEST_CODE_BINDING_BANKCARD = 24400;
    //修改昵称的请求码
    private static final int REQUEST_CODE_MODIFY_NICK_NAME = 45900;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.username)
    IconTextRow mUsername;
    @BindView(R.id.realNameAuth)
    IconTextRow mRealNameAuth;
    @BindView(R.id.bandingBankcard)
    IconTextRow mBandingBankcard;
    @BindView(R.id.bindingPhone)
    IconTextRow mBindingPhone;
    @BindView(R.id.logoutButton)
    TextView mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        if (LocalUser.getUser().isLogin()) {
            UserInfo userInfo = LocalUser.getUser().getUserInfo();
            mUsername.setSubText(userInfo.getUserName());
            mBindingPhone.setSubText(userInfo.getUserPhone());
            mRealNameAuth.setSubText(getRealNameAuthStatusRes(userInfo.getIdStatus()));
            mBandingBankcard.setSubText(getBindBankcardAuthStatusRes(userInfo.getCardState()));
        }
    }

    private int getBindBankcardAuthStatusRes(int authStatus) {
        /**
         * cardState银行卡状态 0未填写，1已填写，2已认证
         */
        if (authStatus == UserInfo.BANK_CARD_AUTH_STATUS_WRITE) {
            return R.string.settings_write_now;
        } else if (authStatus == UserInfo.BANK_CARD_AUTH_STATUS_ATTESTATION) {
            return R.string.settings_attestation;
        }
        return R.string.settings_no_write;
    }

    private int getRealNameAuthStatusRes(int authStatus) {
        /**
         * idStatus实名状态 0未填写，1已填写，2已认证
         */
        if (authStatus == UserInfo.REAL_NAME_AUTH_STATUS_WRITE) {
            return R.string.settings_write_now;
        } else if (authStatus == UserInfo.REAL_NAME_AUTH_STATUS_ATTESTATION) {
            return R.string.settings_attestation;
        }
        return R.string.settings_no_write;
    }

    private void updateNickName() {
        API.User.findIsUpdateNickName()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<NickNameIsModified>>() {
            @Override
            protected void onRespSuccess(Resp<NickNameIsModified> resp) {
                if (!resp.getData().isModified()) {
                    Launcher.with(getActivity(), ModifyNickNameActivity.class)
                            .executeForResult(REQUEST_CODE_MODIFY_NICK_NAME);
                } else {
                    ToastUtil.curt(R.string.modify_nick_name_twice); // TODO: 9/25/16 文字需要产品提供, 服务端返回
                }
            }
        }).fire();
    }

    private void logout() {
        if (LocalUser.getUser().isLogin()) {
            API.User.loginOut().setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback1<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            LocalUser.getUser().logout();
                            mUsername.setSubText("");
                            mRealNameAuth.setSubText("");
                            mBandingBankcard.setSubText("");
                            mBindingPhone.setSubText("");
                            setResult(RESULT_OK);
                            finish();
                        }
                    }).fire();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (requestCode == REQUEST_CODE_NAME_AUTH && resultCode == RESULT_OK) {
            mRealNameAuth.setSubText(getRealNameAuthStatusRes(userInfo.getIdStatus()));
        }
        if (requestCode == REQUEST_CODE_MODIFY_NICK_NAME && resultCode == RESULT_OK) {
            mUsername.setSubText(userInfo.getUserName());
        }
        if (requestCode == REQUEST_CODE_BINDING_BANKCARD && resultCode == RESULT_OK) {
            mBandingBankcard.setSubText(getBindBankcardAuthStatusRes(userInfo.getCardState()));
        }
    }

    @OnClick({R.id.username, R.id.realNameAuth, R.id.bandingBankcard, R.id.logoutButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.username:
                updateNickName();
                break;
            case R.id.realNameAuth:
                Launcher.with(getActivity(), NameAuthActivity.class).executeForResult(REQUEST_CODE_NAME_AUTH);
                break;
            case R.id.bandingBankcard:
                Launcher.with(getActivity(), BankcardAuthActivity.class).executeForResult(REQUEST_CODE_BINDING_BANKCARD);
                break;
            case R.id.logoutButton:
                logout();
                break;
        }
    }
}
