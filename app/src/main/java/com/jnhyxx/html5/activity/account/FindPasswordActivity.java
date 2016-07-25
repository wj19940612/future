package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.fragment.dialog.EasyDialog;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.APIBase;
import com.jnhyxx.html5.net.Callback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPasswordActivity extends BaseActivity {

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
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);

        mPhoneNum.addTextChangedListener(new ValidationWatcher());
        mMessageAuthCode.addTextChangedListener(new ValidationWatcher());
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
            activeButtons();
        }
    }

    private void activeButtons() {
        boolean enable = checkObtainAuthCodeEnable();
        if (enable != mObtainAuthCode.isEnabled()) {
            mObtainAuthCode.setEnabled(enable);
        }

        enable = checkNextStepButtonEnable();
        if (enable != mNextStepButton.isEnabled()) {
            mNextStepButton.setEnabled(enable);
        }
    }

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
        API.Account.obtainAuthCodeWhenFindPassword(phoneNum)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<APIBase.Resp>() {
                    @Override
                    public void onSuccess(APIBase.Resp resp) {
                        EasyDialog.newInstance(resp.getMsg())
                                .setPositive(R.string.ok)
                                .show(getActivity());

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

    @Override
    protected void onTimeUp() { // TODO: 7/22/16 考虑Home了之后页面被回收的情况
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
