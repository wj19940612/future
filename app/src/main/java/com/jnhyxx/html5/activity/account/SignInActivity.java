package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.LocalCacheUserInfoManager;
import com.jnhyxx.html5.domain.account.UserInfo;
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

    @BindView(R.id.loginImagePasswordType)
    ImageView mImageViewPasswordType;
    @BindView(R.id.loginImagePhoneDelete)
    ImageView mIvPhoneDelete;

    private boolean flag = false;

    @BindView(R.id.rlFailWarn)
    RelativeLayout rlFailWarn;
    @BindView(R.id.commonFailTvWarn)
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
            mImageViewPasswordType.setVisibility(View.VISIBLE);
        } else {
            mImageViewPasswordType.setVisibility(View.GONE);
        }
    }

    private void ivPhoneDeleteStatus() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNum)) {
            mIvPhoneDelete.setVisibility(View.VISIBLE);
        } else {
            mIvPhoneDelete.setVisibility(View.GONE);
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
    @OnClick({R.id.loginImagePhoneDelete, R.id.loginImagePasswordType})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginImagePhoneDelete:
                String phoneNum = mPhoneNum.getText().toString();
                if (!TextUtils.isEmpty(phoneNum)) {
                    mPhoneNum.setText("");
                }
                break;
            case R.id.loginImagePasswordType:
                changeEdittextPasswordInputtYPE();
                break;
        }
    }

    private void changeEdittextPasswordInputtYPE() {
        if (!flag) {
            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mImageViewPasswordType.setSelected(true);
        } else {
            mImageViewPasswordType.setSelected(false);
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
        API.User.signIn(phoneNum, password)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        LoginInfo info = new Gson().fromJson(resp.getData(), LoginInfo.class);
                        User.getUser().setLoginInfo(info);
                        Log.d(TAG, "登陆信息" + info.toString());
                        UserInfo userInfo = new Gson().fromJson(resp.getData(), UserInfo.class);
                        LocalCacheUserInfoManager localCacheUserInfoManager = LocalCacheUserInfoManager.getInstance();
                        localCacheUserInfoManager.setUser(userInfo);
                        Log.d(TAG, "用户信息 " + LocalCacheUserInfoManager.getInstance().getUser().toString());
                        setResult(RESULT_OK);
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
        Launcher.with(this, FindPwdActivity.class).execute();
//        Launcher.with(this, ModifyPwdActivity.class).execute();
}
        }
