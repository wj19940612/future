package com.jnhyxx.html5.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.account.BankcardBindingActivity;
import com.jnhyxx.html5.activity.account.NameVerifyActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    // 绑定银行卡前 先进行实名认证
    private static final int REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST = 900;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.nickname)
    IconTextRow mNickname;
    @BindView(R.id.realNameVerity)
    IconTextRow mRealNameVerity;
    @BindView(R.id.bandingBankcard)
    IconTextRow mBandingBankcard;
    @BindView(R.id.bindingPhone)
    IconTextRow mBindingPhone;
    @BindView(R.id.logoutButton)
    TextView mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        updateViews();

    }

    private void updateViews() {
        if (LocalUser.getUser().isLogin()) {
            UserInfo userInfo = LocalUser.getUser().getUserInfo();
            mNickname.setSubText(userInfo.getUserName());
            mBindingPhone.setSubText(userInfo.getUserPhone());
            mRealNameVerity.setSubText(getRealNameAuthStatusRes(userInfo.getIdStatus()));
            mBandingBankcard.setSubText(getBindBankcardAuthStatusRes(userInfo.getCardState()));
        }
    }

    private int getBindBankcardAuthStatusRes(int authStatus) {
        /**
         * cardState银行卡状态 0未填写，1已填写，2已绑定
         */
        if (authStatus == UserInfo.BANKCARD_STATUS_FILLED) {
            return  R.string.filled;
        } else if (authStatus == UserInfo.BANKCARD_STATUS_BOUND) {
            return R.string.bound;
        }
        return R.string.unbound;
    }

    private int getRealNameAuthStatusRes(int authStatus) {
        /**
         * idStatus实名状态 0未填写，1已填写，2已认证
         */
        if (authStatus == UserInfo.REAL_NAME_STATUS_FILLED) {
            return R.string.filled;
        } else if (authStatus == UserInfo.REAL_NAME_STATUS_VERIFIED) {
            return R.string.authorized;
        }
        return R.string.un_authorized;
    }

    private void logout() {
        if (LocalUser.getUser().isLogin()) {
            API.User.logout().setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback1<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            LocalUser.getUser().logout();
                            mNickname.setSubText("");
                            mRealNameVerity.setSubText("");
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
        if (requestCode == REQ_CODE_BASE && resultCode == RESULT_OK) {
            updateViews();
        }

        if (requestCode == REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST && resultCode == RESULT_OK) {
            bindingBankcard();
        }
    }

    @OnClick({R.id.nickname, R.id.realNameVerity, R.id.bandingBankcard, R.id.logoutButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nickname:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.NICK_NAME);
                openModifyNicknamePage();
                break;
            case R.id.realNameVerity:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.REAL_NAME);
                Launcher.with(getActivity(), NameVerifyActivity.class).executeForResult(REQ_CODE_BASE);
                break;
            case R.id.bandingBankcard:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.BIND_BANK);
                bindingBankcard();
                break;
            case R.id.logoutButton:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.LOGOUT);
                logout();
                break;
        }
    }

    private void bindingBankcard() {
        if (!LocalUser.getUser().isRealNameFilled()) {
            Launcher.with(getActivity(), NameVerifyActivity.class).executeForResult(REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST);
            return;
        }

        Launcher.with(getActivity(), BankcardBindingActivity.class).executeForResult(REQ_CODE_BASE);
    }

    private void openModifyNicknamePage() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo.isNickNameModifiedBefore()) {
            ToastUtil.show(R.string.nick_name_can_be_modified_once_only);
        } else {
            Launcher.with(getActivity(), ModifyNickNameActivity.class)
                    .executeForResult(REQ_CODE_BASE);
        }
    }
}
