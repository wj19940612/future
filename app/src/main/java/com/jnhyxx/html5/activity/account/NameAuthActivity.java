package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.johnz.kutils.Launcher;
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

        mName.addTextChangedListener(mValidationWatcher);
        mIdentityNum.addTextChangedListener(mValidationWatcher);

        updateNameAuthView(getIntent());
    }

    private void updateNameAuthView(Intent intent) {
        NameAuth nameAuth = (NameAuth) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        mName.setText(nameAuth.getUserName());
        mIdentityNum.setText(nameAuth.getIdCardNum());

        if (nameAuth.getStatus() == NameAuth.STATUS_BE_BOUND) {
            mName.setEnabled(false);
            mIdentityNum.setEnabled(false);
            mSubmitToAuthButton.setVisibility(View.GONE);
        } else {
            mName.setEnabled(true);
            mIdentityNum.setEnabled(true);
            mSubmitToAuthButton.setVisibility(View.VISIBLE);
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
        String realName = ViewUtil.getTextTrim(mName);
        String identityNum = ViewUtil.getTextTrim(mIdentityNum);
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
                .setCallback(new Callback<Resp<NameAuth.Result>>() {
                    @Override
                    public void onSuccess(final Resp<NameAuth.Result> resp) {
                        if (resp.isSuccess()) {
                            SmartDialog.with(getActivity(), resp.getMsg())
                                    .setCancelable(false)
                                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                        @Override
                                        public void onClick(Dialog dialog) {
                                            dialog.dismiss();
                                            sendResultForCalling(resp.getData());
                                            finish();
                                        }
                                    }).show();
                        } else {
                            SmartDialog.with(getActivity(), resp.getMsg()).show();
                        }
                    }
                }).post();
    }

    /**
     * 由银行卡认证页面唤起,在实名认证成功后返回结果
     *
     * 由个人信息页唤起,实名认证后返回结果
     *
     * @param result
     */
    private void sendResultForCalling(NameAuth.Result result) {
        if (getCallingActivity() == null) return;
        String fromClass = getCallingActivity().getClassName();

        if (fromClass.equals(BankcardAuthActivity.class.getName())) {
            Intent intent = new Intent().putExtra(BankcardAuthActivity.NAME_AUTH_RESULT, result);
            setResult(RESULT_OK, intent);
        }

        if (fromClass.equals(ProfileActivity.class.getName())) {
            Intent intent = new Intent().putExtra(ProfileActivity.NAME_AUTH_RESULT, result);
            setResult(RESULT_OK, intent);
        }
    }
}
