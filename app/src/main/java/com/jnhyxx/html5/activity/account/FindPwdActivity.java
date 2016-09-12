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
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.Launcher;
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
    @BindView(R.id.RetrieveImageCode)
    ImageView mRetrieveImageCode;
    @BindView(R.id.imageCode)
    LinearLayout mImageCode;
    @BindView(R.id.FailWarn)
    CommonFailWarn mCommonFailWarn;
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

    //获取验证码
    @OnClick(R.id.obtainAuthCode)
    void obtainAuthCode() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        String regImageCode = null;
        if (mImageCode.isShown()) {
            regImageCode = mInputImageCode.getText().toString().trim();
        }
        API.User.obtainAuthCodeWhenFindPwd(phoneNum, regImageCode)
                .setIndeterminate(this)
                .setTag(TAG)
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
                        } else if (resp.getCode() == 601) {
                            showFailWarnView(resp);
                            getRetrieveImageCode();
                        } else {
                            showFailWarnView(resp);
                        }
                    }
                }).fire();
    }

    private void getRetrieveImageCode() {
        ToastUtil.curt("获取注册验证码");
        String userPhone = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(userPhone)) return;
        String url = CommonMethodUtils.imageCodeUri(userPhone);
        Log.d(TAG, "注册页面图片验证码地址  " + url);
        Picasso.with(FindPwdActivity.this).load(url).into(mRetrieveImageCode, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mImageCode.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                // TODO: 2016/9/8  目前先做这样处理
                ToastUtil.curt("图片验证码下载失败，请点击短信验证码再次下载");
            }
        });
    }

    private void showFailWarnView(Resp resp) {
        mCommonFailWarn.setCenterTxt(resp.getMsg());
        mCommonFailWarn.setVisibility(View.VISIBLE);
    }

    //下一步跳转
    @OnClick(R.id.nextStepButton)
    void doNextStepButtonClick() {
        final String phoneNum = mPhoneNum.getText().toString().trim();
        final String authCode = mMessageAuthCode.getText().toString().trim();

        // TODO: 2016/8/31 目前没有确认短信验证码接口， 
//        API.Account.authCodeWhenFindPassword(phoneNum, authCode)
//                .setIndeterminate(this).setTag(TAG)
//                .setCallback(new Callback<Resp>() {
//                    @Override
//                    public void onReceive(Resp resp) {
//                        if (resp.isSuccess()) {
        API.User.authCodeWhenFindPassword(phoneNum, authCode)
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
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



