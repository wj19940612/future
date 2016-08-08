package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPwdActivity extends BaseActivity {

    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.messageAuthCode)
    EditText mMessageAuthCode;
    @BindView(R.id.obtainAuthCode)
    TextView mObtainAuthCode;
    @BindView(R.id.nextStepButton)
    TextView mNextStepButton;

    private boolean mFreezeObtainAuthCode;
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ButterKnife.bind(this);

        mPhoneNum.addTextChangedListener(mValidationWatcher);
        mMessageAuthCode.addTextChangedListener(mValidationWatcher);
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
        String phoneNum = mPhoneNum.getText().toString().trim();
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
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11) {
            return false;
        }
        return true && !mFreezeObtainAuthCode;
    }

    @OnClick(R.id.obtainAuthCode)
    void obtainAuthCode() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        API.Account.obtainAuthCodeWhenFindPwd(phoneNum)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onSuccess(Resp resp) {
                        ToastUtil.show(resp.getMsg());
                        if (resp.isSuccess()) {
                            mCounter = 60;
                            mFreezeObtainAuthCode = true;
                            mObtainAuthCode.setEnabled(false);
                            mObtainAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
                            startScheduleJob(1 * 1000);
                        }
                    }
                }).post();
    }

    @OnClick(R.id.nextStepButton)
    void doNextStepButtonClick() {
        final String phoneNum = mPhoneNum.getText().toString().trim();
        final String authCode = mMessageAuthCode.getText().toString().trim();
        API.Account.authCodeWhenFindPassword(phoneNum, authCode)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onSuccess(Resp resp) {
                        if (resp.isSuccess()) {
                            Launcher.with(getActivity(), ModifyPwdActivity.class)
                                    .putExtra(ModifyPwdActivity.EX_PHONE, phoneNum)
                                    .putExtra(ModifyPwdActivity.EX_AUTH_CODE, authCode)
                                    .execute();
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).post();
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
