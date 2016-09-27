package com.jnhyxx.html5.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.CustomToast;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyNickNameActivity extends BaseActivity {

    @BindView(R.id.etModifyNickName)
    EditText mEtModifyNickName;
    @BindView(R.id.commonFailTvWarn)
    CommonFailWarn mModifyNickNameFailWarnWarn;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        ButterKnife.bind(this);

        mEtModifyNickName.addTextChangedListener(mValidationWatcher);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = mEtModifyNickName.getText().toString().trim();
                if (!CommonMethodUtils.getNicknameStatus(nickName)) {
                    mModifyNickNameFailWarnWarn.setVisible(true);
                    return;
                }
                submitNickName(nickName);
            }
        });
    }


    private void submitNickName(final String nickName) {
        API.User.updateNickName(nickName)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        UserInfo user = LocalUser.getUser().getUserInfo();
                        user.setUserName(nickName);
                        user.setNickNameModified();
                        CustomToast.getInstance().custommakeText(ModifyNickNameActivity.this, R.string.modify_nick_name_success);
                        setResult(RESULT_OK);
                    }
                }).fire();
    }

    ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mConfirmButton.isEnabled()) {
                mConfirmButton.setEnabled(enable);
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        String modifyNickName = ViewUtil.getTextTrim(mEtModifyNickName);
        if (TextUtils.isEmpty(modifyNickName)) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        String nickName = mEtModifyNickName.getText().toString().trim();
        if (!CommonMethodUtils.getNicknameStatus(nickName)) {
            mModifyNickNameFailWarnWarn.setVisibility(View.VISIBLE);
            return;
        }
        submitNickName(nickName);
    }
}
