package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.CustomToast;
import com.jnhyxx.html5.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPwdActivity extends BaseActivity {

    public static final String EX_PHONE = API.TELE;
    public static final String EX_AUTH_CODE = API.CODE;

    @BindView(R.id.newPassword)
    EditText mNewPassword;
    @BindView(R.id.confirmPassword)
    EditText mConfirmPassword;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;
    @BindView(R.id.findPasswordTitleBar)
    TitleBar mTitleBar;
    @BindView(R.id.modifyPasswordWarn)
    CommonFailWarn mModifyPasswordWarn;
    private String mPhone;
    private String mAuthCode;
    private String mNewPwd;

    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);

        mNewPassword.addTextChangedListener(mValidationWatcher);
        mConfirmPassword.addTextChangedListener(mValidationWatcher);

        processIntent(getIntent());

        initTitleBar();
    }

    private void initTitleBar() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void processIntent(Intent intent) {
        mPhone = intent.getStringExtra(EX_PHONE);
        mAuthCode = intent.getStringExtra(EX_AUTH_CODE);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkConfirmPasswordButtonEnable();
            if (enable != mConfirmButton.isEnabled()) {
                mConfirmButton.setEnabled(enable);
            }
        }
    };

    private boolean checkConfirmPasswordButtonEnable() {
        mNewPwd = mNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mNewPwd) || mNewPwd.length() < 6) {
            return false;
        }

        confirmPassword = mConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPassword) || confirmPassword.length() < 6) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        if (!TextUtils.equals(mNewPwd, confirmPassword)) {
            mModifyPasswordWarn.setVisible(true);
            mModifyPasswordWarn.setCenterTxt(R.string.newPassword_different_from_confimPassword);
        } else {
            API.User.modifyPwdWhenFindPwd(mPhone, mNewPwd)
                    .setIndeterminate(this).setTag(TAG)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                CustomToast.getInstance().custommakeText(ModifyPwdActivity.this, R.string.modify_passWord_success);
                            } else {
                                mModifyPasswordWarn.setVisible(true);
                                mModifyPasswordWarn.setCenterTxt(resp.getMsg());
                            }
                        }
                    }).fire();
        }
    }
}
