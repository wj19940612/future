package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.clearPhoneNumButton)
    ImageView mClearPhoneNumButton;
    @BindView(R.id.showPasswordButton)
    ImageView mShowPasswordButton;

    @BindView(R.id.signUpButton)
    TextView mSignUpButton;
    @BindView(R.id.forgetPassword)
    TextView mForgetPassword;
    @BindView(R.id.signInButton)
    TextView mSignInButton;

    @BindView(R.id.FailWarn)
    CommonFailWarn rlFailWarn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        mSignUpButton.setEnabled(true);
        mPhoneNum.addTextChangedListener(mValidationWatcher);
        mPassword.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSignInButtonEnable();
            if (enable != mSignInButton.isEnabled()) {
                mSignInButton.setEnabled(enable);
            }

            boolean visible = checkClearPhoneNumButtonVisible();
            mClearPhoneNumButton.setVisibility(visible ? View.VISIBLE : View.GONE);

            visible = checkShowPasswordButtonVisible();
            mShowPasswordButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    };

    private boolean checkClearPhoneNumButtonVisible() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNum)) {
            return true;
        }
        return false;
    }

    private boolean checkShowPasswordButtonVisible() {
        String password = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(password)) {
            return true;
        }
        return false;
    }

    private boolean checkSignInButtonEnable() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }
        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }
        return true;
    }

    @OnClick({R.id.clearPhoneNumButton, R.id.showPasswordButton, R.id.signInButton, R.id.signUpButton, R.id.forgetPassword})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.clearPhoneNumButton:
                mPhoneNum.setText("");
                break;
            case R.id.showPasswordButton:
                changePasswordInputType();
                break;
            case R.id.signInButton:
                String phoneNum = ViewUtil.getTextTrim(mPhoneNum);
                String password = ViewUtil.getTextTrim(mPassword);
                API.User.signIn(phoneNum, password).setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<JsonObject>>() {
                            @Override
                            public void onReceive(Resp<JsonObject> jsonObjectResp) {
                                if (jsonObjectResp.isSuccess()) {
                                    UserInfo userInfo = new Gson().fromJson(jsonObjectResp.getData(), UserInfo.class);
                                    LocalUser.getUser().setUserInfo(userInfo);
                                    setResult(RESULT_OK);
                                    ToastUtil.curt("登陆成功");
                                    finish();
                                } else {
                                    // TODO: 9/10/16 登入错误处理
                                    mPassword.setText("");
                                    ToastUtil.curt(jsonObjectResp.getMsg());
                                    rlFailWarn.setVisibility(View.VISIBLE);
                                    rlFailWarn.setCenterTxt(jsonObjectResp.getMsg());
                                }
                            }
                        }).fire();
                break;
            case R.id.signUpButton:
                Launcher.with(this, SignUpActivity.class).execute();
                finish();
                break;
            case R.id.forgetPassword:
                Launcher.with(this, FindPwdActivity.class).execute();
                break;
        }
    }

    private void changePasswordInputType() {
        if (mShowPasswordButton.isSelected()) {
            mShowPasswordButton.setSelected(false);
            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mShowPasswordButton.setSelected(true);
        }
        mPassword.postInvalidate();
        CharSequence text = mPassword.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }
}