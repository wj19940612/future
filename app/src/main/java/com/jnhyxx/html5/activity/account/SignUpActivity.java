package com.jnhyxx.html5.activity.account;

import android.content.Intent;
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
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.StrFormatter;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.CustomToast;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends BaseActivity {

    private static final int REQ_CODE_LOGIN = 2;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.failWarn)
    CommonFailWarn mFailWarn;

    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.registerAuthCode)
    EditText mRegisterAuthCode;
    @BindView(R.id.obtainAuthCode)
    TextView mObtainAuthCode;
    @BindView(R.id.authCodeImage)
    ImageView mAuthCodeImage;
    @BindView(R.id.imageAuthCode)
    EditText mImageAuthCode;
    @BindView(R.id.imageCodeArea)
    RelativeLayout mImageCodeArea;
    @BindView(R.id.showPasswordButton)
    ImageView mShowPasswordButton;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.agreeProtocol)
    CheckBox mAgreeProtocol;
    @BindView(R.id.serviceProtocol)
    TextView mServiceProtocol;
    @BindView(R.id.signUpButton)
    TextView mSignUpButton;

    private boolean mFreezeObtainAuthCode;
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        initTitleBar();

        mPhoneNum.addTextChangedListener(mPhoneValidationWatcher);
        mRegisterAuthCode.addTextChangedListener(mValidationWatcher);
        mPassword.addTextChangedListener(mValidationWatcher);

        mAgreeProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mAgreeProtocol.setChecked(isChecked);
                mAgreeProtocol.setButtonDrawable(isChecked ? R.drawable.checkbox_register_selected : R.drawable.checkbox_register_nor);
                // TODO: 10/5/16 checkbox 无法重写样式?

                activeButtons();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNum.removeTextChangedListener(mPhoneValidationWatcher);
        mRegisterAuthCode.removeTextChangedListener(mValidationWatcher);
        mPassword.removeTextChangedListener(mValidationWatcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            // User login done, then finish
            finish();
        }
    }

    private void initTitleBar() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignInPage();
            }
        });
    }

    private void switchToSignInPage() {
        if (getCallingActivity() != null
                && getCallingActivity().getClassName().equals(SignInActivity.class.getName())) {
            finish();
        } else {
            Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQ_CODE_LOGIN);
        }
    }

    private ValidationWatcher mPhoneValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);

            formatPhoneNumber();
        }
    };

    private void formatPhoneNumber() {
        String oldPhone = mPhoneNum.getText().toString();
        String phoneNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatPhoneNumber(phoneNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mPhoneNum.setText(newPhone);
            mPhoneNum.setSelection(newPhone.length());
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            activeButtons();

            boolean visible = checkShowPasswordButtonVisible();
            mShowPasswordButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    };

    private boolean checkShowPasswordButtonVisible() {
        String password = ViewUtil.getTextTrim(mPassword);
        if (!TextUtils.isEmpty(password)) {
            return true;
        }
        return false;
    }

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

        String authCode = mRegisterAuthCode.getText().toString().trim();
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

        String imageAuthCode = ViewUtil.getTextTrim(mImageAuthCode);
        if (mImageCodeArea.isShown() && TextUtils.isEmpty(imageAuthCode)) {
            return false;
        }

        return true && !mFreezeObtainAuthCode;
    }

    @OnClick({R.id.obtainAuthCode, R.id.signUpButton, R.id.authCodeImage, R.id.serviceProtocol, R.id.showPasswordButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.obtainAuthCode:
                obtainAuthCode();
                break;
            case R.id.signUpButton:
                signUp();
                break;
            case R.id.authCodeImage:
                getImageAuthCode();
                break;
            case R.id.serviceProtocol:
                Launcher.with(SignUpActivity.this, WebViewActivity.class)
                        .putExtra(WebViewActivity.EX_URL, API.getRegisterServiceProtocol())
                        .putExtra(WebViewActivity.EX_TITLE, getString(R.string.service_protocol_title)).execute();
                break;
            case R.id.showPasswordButton:
                changePasswordInputType();
                break;

        }
    }

    private void obtainAuthCode() {
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        String imageAuthCode = null;
        if (mImageCodeArea.isShown()) {
            imageAuthCode = ViewUtil.getTextTrim(mImageAuthCode);
        }

        Log.d("TAG", "注册获取图片验证码的手机号码 " + mPhoneNum + "所输入的图片验证码" + imageAuthCode);

        API.User.obtainAuthCode(phoneNum, imageAuthCode)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
                        if (resp.isSuccess()) {
                            mCounter = 60;
                            mFreezeObtainAuthCode = true;
                            mObtainAuthCode.setEnabled(false);
                            mObtainAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
                            startScheduleJob(1 * 1000);
                        } else if (resp.getCode() == Resp.CODE_REQUEST_AUTH_CODE_OVER_LIMIT) {
                            mImageCodeArea.setVisibility(View.VISIBLE);
                            mFailWarn.show(resp.getMsg());
                            getImageAuthCode();
                        } else {
                            mFailWarn.show(resp.getMsg());
                        }
                    }
                }).fire();
    }

    public void signUp() {
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        String password = ViewUtil.getTextTrim(mPassword);
        String authCode = ViewUtil.getTextTrim(mRegisterAuthCode);
        API.User.register(phoneNum, password, authCode, null)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    public void onReceive(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            UserInfo info = new Gson().fromJson(resp.getData(), UserInfo.class);
                            LocalUser.getUser().setUserInfo(info);
                            CustomToast.getInstance().showText(getActivity(), resp.getMsg());

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            mFailWarn.show(resp.getMsg());
                        }
                    }
                }).fire();
    }

    private void getImageAuthCode() {
        mObtainAuthCode.setEnabled(false);
        mImageAuthCode.setText("");

        final String userPhone = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        String imageUrl = API.User.getRegisterAuthCodeImage(userPhone);

        Picasso.with(getActivity())
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(mAuthCodeImage);
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
