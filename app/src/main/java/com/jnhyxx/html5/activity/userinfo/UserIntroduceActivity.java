package com.jnhyxx.html5.activity.userinfo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserIntroduceActivity extends BaseActivity {

    @BindView(R.id.introduceInput)
    EditText mIntroduceInput;
    @BindView(R.id.submit)
    TextView mSubmit;

    UserDefiniteInfo mUserDefiniteInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_introduce);
        ButterKnife.bind(this);

        mUserDefiniteInfo = new UserDefiniteInfo();
        mIntroduceInput.addTextChangedListener(mValidationWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIntroduceInput.removeTextChangedListener(mValidationWatcher);
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
                mIntroduceInput.setText(newData);
                mIntroduceInput.setSelection(mIntroduceInput.getText().toString().length());
            }
        }
    };

    @OnClick(R.id.submit)
    public void onClick() {
        final String userIntroduction = ViewUtil.getTextTrim(mIntroduceInput);
        mUserDefiniteInfo.setIntroduction(userIntroduction);
        API.User.submitUserInfo(mUserDefiniteInfo)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        LocalUser.getUser().getUserInfo().setIntroduction(userIntroduction);
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .fireSync();
    }

    private boolean checkConfirmButtonEnable() {
        String modifyNickName = ViewUtil.getTextTrim(mIntroduceInput);
        if (TextUtils.isEmpty(modifyNickName)) {
            return false;
        }
        return true;
    }
}
