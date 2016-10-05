package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.StrFormatter;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPwdActivity extends BaseActivity {

    @BindView(R.id.findPasswordPhoneNum)
    EditText mPhoneNum;
    @BindView(R.id.findPasswordMessageAuthCode)
    EditText mMessageAuthCode;
    @BindView(R.id.obtainAuthCode)
    TextView mObtainAuthCode;
    @BindView(R.id.nextStepButton)
    TextView mNextStepButton;
    @BindView(R.id.inputImageCode)
    EditText mInputImageCode;
    @BindView(R.id.retrieveImageCode)
    ImageView mRetrieveImageCode;
    @BindView(R.id.imageCode)
    LinearLayout mImageCode;
    @BindView(R.id.failWarn)
    CommonFailWarn mCommonFailWarn;

    private boolean mFreezeObtainAuthCode;
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ButterKnife.bind(this);

        mPhoneNum.addTextChangedListener(mPhoneValidationWatcher);
        mMessageAuthCode.addTextChangedListener(mValidationWatcher);
        mInputImageCode.addTextChangedListener(mValidationWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNum.removeTextChangedListener(mPhoneValidationWatcher);
        mMessageAuthCode.removeTextChangedListener(mValidationWatcher);
        mInputImageCode.removeTextChangedListener(mValidationWatcher);
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

            boolean enable = checkObtainAuthCodeEnable();
            if (enable != mObtainAuthCode.isEnabled()) {
                mObtainAuthCode.setEnabled(enable);
            }

            enable = checkNextStepButtonEnable();
            if (enable != mNextStepButton.isEnabled()) {
                mNextStepButton.setEnabled(enable);
            }
        }
    };

    private boolean checkNextStepButtonEnable() {
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }

        String authCode = mMessageAuthCode.getText().toString().trim();
        if (TextUtils.isEmpty(authCode) || authCode.length() < 4) {
            return false;
        }

        return true;
    }

    private boolean checkObtainAuthCodeEnable() {
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum);
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }

        String regImageCode = ViewUtil.getTextTrim(mInputImageCode);
        if (mImageCode.isShown() && TextUtils.isEmpty(regImageCode)) {
            return false;
        }

        return true && !mFreezeObtainAuthCode;
    }

    @OnClick({R.id.obtainAuthCode, R.id.nextStepButton, R.id.retrieveImageCode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.obtainAuthCode:
                obtainAuthCode();
                break;
            case R.id.nextStepButton:
                doNextStepButtonClick();
                break;
            case R.id.retrieveImageCode:
                getRetrieveImageCode();
                break;
        }
    }

    private void obtainAuthCode() {
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        String regImageCode = null;
        if (mImageCode.isShown()) {
            regImageCode = ViewUtil.getTextTrim(mInputImageCode);
        }
        Log.d("TAG", "手机号码：" + phoneNum + "\n 图片验证码" + regImageCode);
        API.User.obtainAuthCodeWhenFindPwd(phoneNum, regImageCode)
                .setIndeterminate(this)
                .setTag(TAG)
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
                            mCommonFailWarn.show(resp.getMsg());
                            mImageCode.setVisibility(View.VISIBLE);
                            getRetrieveImageCode();
                        } else {
                            mCommonFailWarn.show(resp.getMsg());
                        }
                    }
                }).fire();
    }

    private void getRetrieveImageCode() {
        mObtainAuthCode.setEnabled(false);
        mInputImageCode.setText("");

        final String userPhone = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        if (TextUtils.isEmpty(userPhone)) return;

        final String imageCodeUrl = API.User.getRetrieveImage(userPhone);
        Log.d(TAG, "找回密码页面图片验证码地址  " + imageCodeUrl);

        Picasso.with(getActivity()).load(imageCodeUrl).into(mRetrieveImageCode);
    }

    private void doNextStepButtonClick() {
        final String phoneNum = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
        String authCode = ViewUtil.getTextTrim(mMessageAuthCode);
        API.User.authCodeWhenFindPassword(phoneNum, authCode)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
                        if (resp.isSuccess()) {
                            Launcher.with(getActivity(), ModifyPwdActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, phoneNum)
                                    .execute();
                            finish();
                        } else {
                            mCommonFailWarn.show(resp.getMsg());
                        }
                    }
                }).fire();
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



