package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.utils.ValidityDecideUtil;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameVerifyActivity extends BaseActivity {

    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.identityNum)
    EditText mIdentityNum;
    @BindView(R.id.submitToAuthButton)
    TextView mSubmitToAuthButton;
    @BindView(R.id.identityCardWarn)
    CommonFailWarn mRlIdentityCardWarn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_verify);
        ButterKnife.bind(this);

        mName.addTextChangedListener(mValidationWatcher);
        mIdentityNum.addTextChangedListener(mValidationWatcher);

        updateNameAuthView();
    }

    private void updateNameAuthView() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            mName.setText(userInfo.getRealName());
            mIdentityNum.setText(userInfo.getIdCard());

            if (userInfo.getIdStatus() == UserInfo.REAL_NAME_STATUS_VERIFIED) {
                mName.setEnabled(false);
                mIdentityNum.setEnabled(false);
                mSubmitToAuthButton.setVisibility(View.GONE);
            } else {
                mName.setEnabled(true);
                mIdentityNum.setEnabled(true);
                mSubmitToAuthButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSubmitButtonEnable();
            if (enable != mSubmitToAuthButton.isEnabled()) {
                mSubmitToAuthButton.setEnabled(enable);
            }
        }
    };

    private boolean checkSubmitButtonEnable() {
        String realName = ViewUtil.getTextTrim(mName);
        String identityNum = ViewUtil.getTextTrim(mIdentityNum);
        if (TextUtils.isEmpty(realName) || TextUtils.isEmpty(identityNum)) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.submitToAuthButton)
    public void onClick() {
        final String realName = mName.getText().toString().trim();
        final String identityNum = mIdentityNum.getText().toString().trim();
        if (!ValidityDecideUtil.IDCardValidate(identityNum)) {
            mRlIdentityCardWarn.show(R.string.settings_identity_card_fail);
            return;
        }
        if (LocalUser.getUser().isLogin()) {
            API.User.authUserName(realName, identityNum)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                UserInfo user = LocalUser.getUser().getUserInfo();
                                user.setRealName(realName);
                                user.setIdCard(identityNum);
                                user.setIdStatus(UserInfo.REAL_NAME_STATUS_FILLED);

                                setResult(RESULT_OK);
                                finish();
                            } else {
                                mRlIdentityCardWarn.show(resp.getMsg());
                            }
                        }
                    }).fire();
        }
    }
}
