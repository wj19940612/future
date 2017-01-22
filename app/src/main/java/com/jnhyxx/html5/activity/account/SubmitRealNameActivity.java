package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubmitRealNameActivity extends BaseActivity {

    @BindView(R.id.realNameInput)
    EditText mRealNameInput;
    @BindView(R.id.submit)
    TextView mSubmit;
    @BindView(R.id.errorBar)
    CommonFailWarn mErrorBar;

    private UserDefiniteInfo mUserDefiniteInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_real_name);
        ButterKnife.bind(this);

        mUserDefiniteInfo = new UserDefiniteInfo();

        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getRealName())) {
            mRealNameInput.setText(userInfo.getRealName());
        }
        mRealNameInput.addTextChangedListener(mValidationWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealNameInput.removeTextChangedListener(mValidationWatcher);
    }

    ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mSubmit.isEnabled()) {
                mSubmit.setEnabled(enable);
            }
            String string = s.toString();
            if (string.contains(" ")) {
                String newData = string.replaceAll(" ", "");
                mRealNameInput.setText(newData);
                mRealNameInput.setSelection(mRealNameInput.getText().toString().length());
            }
        }
    };

    @OnClick(R.id.submit)
    public void onClick() {
        final String realName = ViewUtil.getTextTrim(mRealNameInput);
        mUserDefiniteInfo.setRealName(realName);
        API.User.submitUserInfo(mUserDefiniteInfo)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    public void onReceive(Resp<Object> objectResp) {
                        if (objectResp.isSuccess()) {
                            LocalUser.getUser().getUserInfo().setRealName(realName);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            mErrorBar.show(objectResp.getMsg());
                        }
                    }
                })
                .fireSync();
    }

    private boolean checkConfirmButtonEnable() {
        String modifyNickName = ViewUtil.getTextTrim(mRealNameInput);
        if (TextUtils.isEmpty(modifyNickName)) {
            return false;
        }
        return true;
    }
}
