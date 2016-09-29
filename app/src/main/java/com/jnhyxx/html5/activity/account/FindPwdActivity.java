package com.jnhyxx.html5.activity.account;

import android.graphics.Bitmap;
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
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.Launcher;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;

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
        String phoneNum = mPhoneNum.getText().toString().trim();
        String regImageCode = null;
        if (mImageCode.isShown()) {
            regImageCode = mInputImageCode.getText().toString().trim();
        }
        Log.d(TAG, "手机号码：" + phoneNum + "\n 图片验证码" + regImageCode);
        API.User.obtainAuthCodeWhenFindPwd(phoneNum, regImageCode)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
                        Log.d(TAG, resp.getMsg() + "\n找回密码返回的 code " + resp.getCode());
                        if (resp.isSuccess()) {
                            mCounter = 60;
                            mFreezeObtainAuthCode = true;
                            mObtainAuthCode.setEnabled(false);
                            mObtainAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
                            startScheduleJob(1 * 1000);
                        } else if (resp.getCode() == Resp.CODE_REQUEST_AUTH_CODE_OVER_LIMIT) {
                            showFailWarnView(resp);
                            mImageCode.setVisibility(View.VISIBLE);
                            getRetrieveImageCode();
                        } else {
                            showFailWarnView(resp);
                        }
                    }
                }).fire();
    }

    private void getRetrieveImageCode() {
        final String userPhone = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(userPhone)) return;
//        final String url = CommonMethodUtils.imageCodeUri(userPhone, "/user/user/getRetrieveImage.do");
        final String url = API.getFindPassImageCode(userPhone);
        Log.d(TAG, "找回密码页面图片验证码地址  " + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(userPhone)) return;
                String url = CommonMethodUtils.imageCodeUri(userPhone, "/user/user/getRetrieveImage.do");
                Picasso picasso = Picasso.with(FindPwdActivity.this);
                RequestCreator requestCreator = picasso.load(url);
                try {
                    final Bitmap bitmap = requestCreator.get();
                    Log.d(TAG, "下载的图片验证码 " + bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bitmap != null) {
                                mRetrieveImageCode.setImageBitmap(bitmap);
                                mRetrieveImageCode.setVisibility(View.VISIBLE);
                            } else {

                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showFailWarnView(Resp resp) {
        mCommonFailWarn.show(resp.getMsg());

    }

    private void doNextStepButtonClick() {
        final String phoneNum = mPhoneNum.getText().toString().trim();
        final String authCode = mMessageAuthCode.getText().toString().trim();
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
                            showFailWarnView(resp);
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



