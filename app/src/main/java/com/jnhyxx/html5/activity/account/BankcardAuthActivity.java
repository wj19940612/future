package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.NameAuth;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankcardAuthActivity extends BaseActivity {

    public static final int REQUEST_CODE = 0;
    public static final String NAME_AUTH = "nameAuth";
    @BindView(R.id.cardholderName)
    EditText mCardholderName;
    @BindView(R.id.bankcardNum)
    EditText mBankcardNum;
    @BindView(R.id.payingBank)
    EditText mPayingBank;
    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.submitToAuthButton)
    TextView mSubmitToAuthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_auth);
        ButterKnife.bind(this);

        mCardholderName.addTextChangedListener(mValidationWatcher);
        mBankcardNum.addTextChangedListener(mValidationWatcher);
        mPayingBank.addTextChangedListener(mValidationWatcher);
        mPhoneNum.addTextChangedListener(mValidationWatcher);

        API.Account.getUserNameAuth(User.getUser().getToken())
                .setTag(TAG)
                .setCallback(new Resp.Callback<NameAuth>() {
                    @Override
                    protected void onRespSuccess(NameAuth nameAuth) {
                        if (nameAuth.getStatus() == NameAuth.STATUS_NOT_FILLED) {
                            showAuthNameDialog();
                        } else {
                            mCardholderName.setText(nameAuth.getUserName());
                            requestBankcardAuth();
                        }
                    }
                }).post();

    }

    private void requestBankcardAuth() {
        API.Account.getBankcardInfo(User.getUser().getToken())
                .setTag(TAG)
                .setCallback(new Resp.Callback<BankcardAuth>() {
                    @Override
                    protected void onRespSuccess(BankcardAuth bankcardAuth) {
                        if (bankcardAuth.getStatus() == BankcardAuth.STATUS_BE_BOUND) {
                            mCardholderName.setEnabled(false);
                            mBankcardNum.setEnabled(false);
                            mPayingBank.setEnabled(false);
                            mPhoneNum.setEnabled(false);
                        }
                    }
                }).post();
    }

    private void showAuthNameDialog() {
        SmartDialog.with(getActivity())
                .setMessage(getString(R.string.prompt_unauthorized_name))
                .setPositive(R.string.go_and_auth, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Launcher.with(getActivity(), NameAuthActivity.class)
                                .executeForResult(REQUEST_CODE);
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            NameAuth nameAuth = (NameAuth) data.getSerializableExtra(NAME_AUTH);
            mCardholderName.setText(nameAuth.getUserName());
            SmartDialog.dismiss(null, this);
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSubmitButtonEnable();
            if (enable != mSubmitToAuthButton.isEnabled()) {
                mSubmitToAuthButton.setEnabled(enable);
            }
        }
    };

    private boolean checkSubmitButtonEnable() {
        String cardholderName = ViewUtil.getEditTextTrim(mCardholderName);
        String bankcardNum = ViewUtil.getEditTextTrim(mBankcardNum);
        String payingBank = ViewUtil.getEditTextTrim(mPayingBank);
        String phoneNum = ViewUtil.getEditTextTrim(mPhoneNum);
        if (TextUtils.isEmpty(cardholderName) || TextUtils.isEmpty(bankcardNum)
                || TextUtils.isEmpty(payingBank) || TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.submitToAuthButton)
    public void onClick() {
    }
}
