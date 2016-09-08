package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CustomToast;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;

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
    @BindView(R.id.findpasswordTitleBar)
    TitleBar mTitleBar;
    @BindView(R.id.moidifyPasswordWarn)
    RelativeLayout modifyPasswordWarn;
    @BindView(R.id.common_fail_tv_warn)
    TextView tvFailWarn;
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
        tvFailWarn.setText(R.string.newpassword_different_from_confimpassword);
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

//        if (!confirmPassword.equals(mNewPwd)) {
//            return false;
//        }

        return true;
    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        Log.d(TAG, "新密码与提交密码是否相同  " + TextUtils.equals(mNewPwd, confirmPassword));
        if (!TextUtils.equals(mNewPwd, confirmPassword)) {
            modifyPasswordWarn.setVisibility(View.VISIBLE);
        } else {
            modifyPasswordWarn.setVisibility(View.GONE);
            API.User.modifyPwdWhenFindPwd(mPhone,  mNewPwd)
                    .setIndeterminate(this).setTag(TAG)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                SmartDialog.with(getActivity(), resp.getMsg())
                                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                            @Override
                                            public void onClick(Dialog dialog) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).show();
                                CustomToast.getInstance().custommakeText(ModifyPwdActivity.this, R.string.modify_passWord_success);
                            } else {
                                ToastUtil.show(resp.getMsg());
                            }
                        }
                    }).fire();
        }
    }
}
