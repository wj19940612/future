package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.LoginInfo;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.APIBase;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.closeButton)
    ImageView mCloseButton;
    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.signUp)
    TextView mSignUp;
    @BindView(R.id.forgetPassword)
    TextView mForgetPassword;
    @BindView(R.id.signInButton)
    TextView mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        mPhoneNum.addTextChangedListener(new ValidationWatcher());
        mPassword.addTextChangedListener(new ValidationWatcher());
    }

    private class ValidationWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSignInButtonEnable();
            if (enable != mSignInButton.isEnabled()) {
                mSignInButton.setEnabled(enable);
            }
        }
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

    @OnClick(R.id.closeButton)
    void close() {
        finish();
    }

    @OnClick(R.id.signInButton)
    void signIn() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        API.Account.signIn(phoneNum, password)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<APIBase.Resp<JsonObject>>() {
                    @Override
                    public void onSuccess(APIBase.Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            LoginInfo info = new Gson().fromJson(resp.getData(), LoginInfo.class);
                            User.getUser().setLoginInfo(info);
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).post();
    }

    @OnClick(R.id.signUp)
    void openSignUpPage() {
        Launcher.with(this, SignUpActivity.class).execute();
        finish();
    }

    @OnClick(R.id.forgetPassword)
    void openFindPasswordPage() {
        Launcher.with(this, FindPwdActivity.class).execute();
    }
}
