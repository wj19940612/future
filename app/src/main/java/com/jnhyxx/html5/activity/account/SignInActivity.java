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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.LoginInfo;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity {

    /* @BindView(R.id.closeButton)
     ImageView mCloseButton;*/
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

    @BindView(R.id.login_image_password_type)
    ImageView imageViewPasswordType;
    @BindView(R.id.login_image_phone_delete)
    ImageView ivPhoneDelete;

    private boolean flag = false;

    @BindView(R.id.rlFailWarn)
    RelativeLayout rlFailWarn;
    @BindView(R.id.common_fail_tv_warn)
    TextView mFailWarnTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        mSignUp.setEnabled(true);
        mPhoneNum.addTextChangedListener(mValidationWatcher);
        mPassword.addTextChangedListener(mValidationWatcher);
        mFailWarnTv.setText("");
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSignInButtonEnable();
            if (enable != mSignInButton.isEnabled()) {
                mSignInButton.setEnabled(enable);
            }
            ivPhoneDeleteStatus();
            imageViewPasswordTypeStatus();
        }
    };

    private void imageViewPasswordTypeStatus() {
        String password = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(password)) {
            imageViewPasswordType.setVisibility(View.VISIBLE);
        } else {
            imageViewPasswordType.setVisibility(View.GONE);
        }
    }

    private void ivPhoneDeleteStatus() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNum)) {
            ivPhoneDelete.setVisibility(View.VISIBLE);
        } else {
            ivPhoneDelete.setVisibility(View.GONE);
        }
    }

    private boolean checkSignInButtonEnable() {
        String phoneNum = mPhoneNum.getText().toString().trim();
//        if (!TextUtils.isEmpty(phoneNum) && phoneNum.length() > 1) {
//            ivPhoneDelete.setVisibility(View.VISIBLE);
//        } else {
//            ivPhoneDelete.setVisibility(View.GONE);
//        }
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }
        String password = mPassword.getText().toString().trim();
//        if (!TextUtils.isEmpty(password) && password.length() > 1) {
//            imageViewPasswordType.setVisibility(View.VISIBLE);
//        } else {
//            imageViewPasswordType.setVisibility(View.GONE);
//        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }

        return true;
    }

    /*  @OnClick(R.id.closeButton)
      void close() {
          finish();
      }
  */
    @OnClick({R.id.login_image_phone_delete, R.id.login_image_password_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_image_phone_delete:
                String phoneNum = mPhoneNum.getText().toString();
                if (!TextUtils.isEmpty(phoneNum)) {
                    mPhoneNum.setText("");
                }
                break;
            case R.id.login_image_password_type:
                changeEdittextPasswordInputtYPE();
                break;
        }
    }

    private void changeEdittextPasswordInputtYPE() {
        if (!flag) {
            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageViewPasswordType.setSelected(true);
        } else {
            imageViewPasswordType.setSelected(false);
            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        flag = !flag;
        mPassword.postInvalidate();
        CharSequence text = mPassword.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    @OnClick(R.id.signInButton)
    void signIn() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        API.Account.signIn(phoneNum, password)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        LoginInfo info = new Gson().fromJson(resp.getData(), LoginInfo.class);
                        User.getUser().setLoginInfo(info);
                        finish();
                    }
                }).fire();
    }


    @OnClick(R.id.signUp)
    void openSignUpPage() {
        Launcher.with(this, SignUpActivity.class).execute();
        finish();
    }

    @OnClick(R.id.forgetPassword)
    void openFindPasswordPage() {
        // TODO: 2016/8/29 跳过验证码界面，直接修改密码，仅作测试
        Launcher.with(this, FindPwdActivity.class).execute();
//        Launcher.with(this, ModifyPwdActivity.class).execute();
    }
}
