package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.confirmPassword;

public class ModifyPwdActivity extends BaseActivity {

    @BindView(R.id.newPassword)
    EditText mNewPassword;
    @BindView(confirmPassword)
    EditText mConfirmPassword;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.modifyPasswordWarn)
    CommonFailWarn mModifyPasswordWarn;
    @BindView(R.id.showFirstPassword)
    ImageView mShowFirstPassword;
    @BindView(R.id.showSecondPass)
    ImageView mShowSecondPass;

    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);

        mNewPassword.addTextChangedListener(mValidationWatcher);
        mConfirmPassword.addTextChangedListener(mValidationWatcher);

        initData(getIntent());
    }

    private void initData(Intent intent) {
        mPhone = intent.getStringExtra(Launcher.EX_PAYLOAD);
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

    private boolean getNewPasswordStatus() {
        String newPass = ViewUtil.getTextTrim(mNewPassword);
        if (!TextUtils.isEmpty(newPass)) {
            return true;
        }
        return false;
    }

    private boolean getOldPasswordStatus() {
        String newPass = ViewUtil.getTextTrim(mConfirmPassword);
        if (!TextUtils.isEmpty(newPass)) {
            return true;
        }
        return false;
    }

    private boolean checkConfirmPasswordButtonEnable() {
        String newPwd = ViewUtil.getTextTrim(mNewPassword);
        if (TextUtils.isEmpty(newPwd) || newPwd.length() < 6) {
            return false;
        }

        String confirmPwd = ViewUtil.getTextTrim(mConfirmPassword);
        if (TextUtils.isEmpty(confirmPwd) || confirmPwd.length() < 6) {
            return false;
        }

        return true;
    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        String newPwd = ViewUtil.getTextTrim(mNewPassword);
        String confirmPwd = ViewUtil.getTextTrim(mConfirmPassword);

        if (!newPwd.equals(confirmPwd)) {
            mModifyPasswordWarn.show(R.string.the_passwords_are_not_the_same);
        } else {
            API.User.modifyPwdWhenFindPwd(mPhone, newPwd)
                    .setIndeterminate(this).setTag(TAG)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                CustomToast.getInstance().showText(getActivity(), resp.getMsg());
                                finish();
                            } else {
                                mModifyPasswordWarn.show(resp.getMsg());
                            }
                        }
                    }).fire();
        }
    }
}
