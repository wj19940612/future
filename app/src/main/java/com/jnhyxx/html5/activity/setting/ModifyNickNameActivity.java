package com.jnhyxx.html5.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyNickNameActivity extends BaseActivity {

    @BindView(R.id.etModifyNickName)
    EditText mEtModifyNickName;
    @BindView(R.id.modifyNickNameFailWarnWarn)
    RelativeLayout mModifyNickNameFailWarnWarn;
    @BindView(R.id.commonFailTvWarn)
    TextView mCommonFailTvWarn;

    @BindView(R.id.confirmButton)
    TextView mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        ButterKnife.bind(this);

        mCommonFailTvWarn.setText(R.string.modify_nick_name_submit_warn);
        mEtModifyNickName.addTextChangedListener(mValidationWatcher);
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
                        setResult(RESULT_OK);
                        ToastUtil.curt(R.string.modify_nick_name_success);
                    }
                }).fire();
    }

    ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean etModifyNickName = getEtModifyNickName();
            if (etModifyNickName != mConfirmButton.isEnabled()) {
                mConfirmButton.setEnabled(etModifyNickName);
            }
            if (!etModifyNickName && mCommonFailTvWarn.isShown()) {
                mModifyNickNameFailWarnWarn.setVisibility(View.GONE);
            }
        }
    };

    private boolean getEtModifyNickName() {
        String modifyNickNmae = ViewUtil.getTextTrim(mEtModifyNickName);
        if (TextUtils.isEmpty(modifyNickNmae)) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        String nickName = mEtModifyNickName.getText().toString().trim();
        if (nickName.length() < 2) {
            ToastUtil.curt(R.string.common_txt_length_fail);
            return;
        }
        if (!CommonMethodUtils.getNicknameStatus(nickName)) {
            mModifyNickNameFailWarnWarn.setVisibility(View.VISIBLE);
            return;
        }
        submitNickName(nickName);
    }
}
