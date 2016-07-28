package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.NameAuth;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameAuthActivity extends BaseActivity {

    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.identityNum)
    EditText mIdentityNum;
    @BindView(R.id.submitToAuthButton)
    TextView mSubmitToAuthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_auth);
        ButterKnife.bind(this);

        API.Account.getUserNameAuth(User.getUser().getToken())
                .setTag(TAG)
                .setCallback(new Resp.Callback<NameAuth>() {
                    @Override
                    protected void onRespSuccess(NameAuth nameAuth) {
                        if (nameAuth.getStatus() == NameAuth.STATUS_BE_BOUND) {
                            mName.setEnabled(false);
                            mIdentityNum.setEnabled(false);
                            mSubmitToAuthButton.setVisibility(View.GONE);
                        }
                    }
                }).post();

        mName.addTextChangedListener(mValidationWatcher);
        mIdentityNum.addTextChangedListener(mValidationWatcher);
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
        String realName = ViewUtil.getEditTextTrim(mName);
        String identityNum = ViewUtil.getEditTextTrim(mIdentityNum);
        if (TextUtils.isEmpty(realName) || TextUtils.isEmpty(identityNum)) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.submitToAuthButton)
    public void onClick() {
        String token = User.getUser().getToken();
        String realName = mName.getText().toString().trim();
        String identityNum = mIdentityNum.getText().toString().trim();
        API.Account.authUserName(token, realName, identityNum)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<NameAuth>>() {
                    @Override
                    public void onSuccess(final Resp<NameAuth> resp) {
                        if (resp.isSuccess()) {
                            SmartDialog.with(getActivity())
                                    .setCancelable(false)
                                    .setMessage(resp.getMsg())
                                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                        @Override
                                        public void onClick(Dialog dialog) {
                                            sendResultForCalling(resp.getData());
                                            finish();
                                        }
                                    }).show();
                        } else {
                            SmartDialog.with(getActivity())
                                    .setMessage(resp.getMsg())
                                    .show();
                        }
                    }
                }).post();
    }

    private void sendResultForCalling(NameAuth nameAuth) {
        if (getCallingActivity() == null) return;
        String fromClass = getCallingActivity().getClassName();
        Log.d(TAG, "sendResultForCalling: " + fromClass);
        if (fromClass.equals(BankcardAuthActivity.class.getSimpleName())) {
            Intent intent = new Intent().putExtra(BankcardAuthActivity.NAME_AUTH, nameAuth);
            setResult(BankcardAuthActivity.REQUEST_CODE, intent);
        }
    }
}
