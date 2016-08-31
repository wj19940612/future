package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CustomToast;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.login_image_password_type;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.activity_register_phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.activity_register_messageAuthCode)
    EditText mMessageAuthCode;
    @BindView(R.id.obtainAuthCode)
    TextView mObtainAuthCode;
    @BindView(R.id.password)
    EditText mPassword;
    //    @BindView(R.id.promoteCode)
//    EditText mPromoteCode;
    @BindView(R.id.agree_protocol)
    CheckBox mAgreeProtocol;
    @BindView(R.id.service_protocol)
    TextView mServiceProtocol;
    @BindView(R.id.signUpButton)
    TextView mSignUpButton;
    @BindView(R.id.registerTitleBar)
    TitleBar mtitleBar;
    private boolean mFreezeObtainAuthCode;
    private int mCounter;

    @BindView(R.id.rlFailWarn)
    RelativeLayout mFailWarn;
    @BindView(R.id.common_fail_tv_warn)
    TextView mFailWarnText;
    //获取图片验证码
    @BindView(R.id.tvRegisterRetrieveImage)
    TextView tvRegisterRetrieveImage;
    @BindView(login_image_password_type)
    ImageView imagePasswordType;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mPhoneNum.addTextChangedListener(mValidationWatcher);
        mMessageAuthCode.addTextChangedListener(mValidationWatcher);
        mPassword.addTextChangedListener(mValidationWatcher);
        mAgreeProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeButtons();
            }
        });
        jumpLoginActivity();
        mFailWarnText.setText(R.string.register_auth_code_fail);


    }

    private void jumpLoginActivity() {
        mtitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(SignUpActivity.this, SignInActivity.class).execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            activeButtons();
            String phoneNum = mPassword.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNum)) {
                imagePasswordType.setVisibility(View.VISIBLE);
            } else {
                imagePasswordType.setVisibility(View.GONE);
                getRetrieveImage();
            }
        }
    };

    private void activeButtons() {
        boolean enable = checkObtainAuthCodeEnable();
        if (enable != mObtainAuthCode.isEnabled()) {
            mObtainAuthCode.setEnabled(enable);
        }

        enable = checkSignUpButtonEnable();
        if (enable != mSignUpButton.isEnabled()) {
            mSignUpButton.setEnabled(enable);
        }
    }

    private boolean checkSignUpButtonEnable() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }

        String authCode = mMessageAuthCode.getText().toString().trim();
        if (TextUtils.isEmpty(authCode) || authCode.length() < 4) {
            return false;
        }

        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }

        return true && mAgreeProtocol.isChecked();
    }

    private boolean checkObtainAuthCodeEnable() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }
        return true && !mFreezeObtainAuthCode;
    }

    @OnClick(R.id.obtainAuthCode)
    void obtainAuthCode() {
        String userPhone = mPhoneNum.getText().toString();
        if (!CommonMethodUtils.isMobileNum(userPhone)) {
            ToastUtil.curt(R.string.common_phone_num_fail);
            return;
        }
        API.Account.obtainAuthCode(userPhone)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
                        ToastUtil.show(resp.getMsg());
                        if (resp.isSuccess()) {
                            mCounter = 60;
                            mFreezeObtainAuthCode = true;
                            mObtainAuthCode.setEnabled(false);
                            mObtainAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
                            startScheduleJob(1 * 1000);
                        }
                    }
                }).fire();
    }

    //注册
    @OnClick(R.id.signUpButton)
    void signUp() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String authCode = mMessageAuthCode.getText().toString().trim();
        API.Account.signUp(phoneNum, password, authCode, null)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    public void onReceive(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            // TODO: 2016/8/29 注册成功后弹出 注册成功的Toast 
                            CustomToast.getInstance().makeText(SignUpActivity.this, R.string.register_successed);
                            LoginInfo info = new Gson().fromJson(resp.getData(), LoginInfo.class);
                            User.getUser().setLoginInfo(info);
                            SmartDialog.with(getActivity(), resp.getMsg())
                                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                        @Override
                                        public void onClick(Dialog dialog) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).show();
                        } else if (resp.getCode() == 600) {
                            mFailWarn.setVisibility(View.VISIBLE);
                        } else {
                            if (mFailWarn.isShown()) {
                                mFailWarn.setVisibility(View.GONE);
                            }
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fire();

    }

    //获取动态验证码
    @OnClick(R.id.tvRegisterRetrieveImage)
    void getRegisterRetrieveImage() {
        getRetrieveImage();
    }

    private void getRetrieveImage() {
        String userPhone = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(userPhone)) {
            tvRegisterRetrieveImage.setEnabled(false);
        } else {
            tvRegisterRetrieveImage.setEnabled(true);
            API.Account.getRetrieveImage(userPhone)
                    .setIndeterminate(this).setTag(TAG)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {

                        }
                    });
        }
    }

    //点击后改变文本输入框的输入类型，使密码可见或隐藏
    @OnClick(login_image_password_type)
    void changeEdittextPasswordInputtYPE() {
        if (!flag) {
            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imagePasswordType.setSelected(true);
        } else {
            imagePasswordType.setSelected(false);
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

    @Override
    public void onTimeUp(int count) { // TODO: 7/22/16 考虑Home了之后页面被回收的情况
        mCounter--;
        if (mCounter <= 0) {
            mFreezeObtainAuthCode = false;
            mObtainAuthCode.setEnabled(true);
            mObtainAuthCode.setText(R.string.obtain_auth_code);
            stopScheduleJob();
        } else {
            mObtainAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }
}
