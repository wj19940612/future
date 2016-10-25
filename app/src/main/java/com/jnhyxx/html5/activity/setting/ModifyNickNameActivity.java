package com.jnhyxx.html5.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.utils.ValidityDecideUtil;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.CustomToast;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyNickNameActivity extends BaseActivity {

    @BindView(R.id.nickname)
    EditText mNickname;
    @BindView(R.id.commonFailTvWarn)
    CommonFailWarn mModifyNickNameFailWarnWarn;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        ButterKnife.bind(this);

        mNickname.addTextChangedListener(mValidationWatcher);
    }

    private void submitNickName(final String nickname) {
        if (!ValidityDecideUtil.getNicknameStatus(nickname)) {
            mModifyNickNameFailWarnWarn.show(getString(R.string.modify_nick_name_error_warn));
            return;
        }
        API.User.updateNickName(nickname)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
                        if (resp.isSuccess()) {
                            UserInfo user = LocalUser.getUser().getUserInfo();
                            user.setUserName(nickname);
                            user.setNickNameModified();
                            CustomToast.getInstance().showText(getActivity(), resp.getMsg());

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            mModifyNickNameFailWarnWarn.show(resp.getMsg());
                        }
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
            String string = s.toString();
            if (string.contains(" ")) {
                String newData = string.replaceAll(" ", "");
                mNickname.setText(newData);
                mNickname.setSelection(mNickname.getText().toString().length());
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        String modifyNickName = ViewUtil.getTextTrim(mNickname);
        if (TextUtils.isEmpty(modifyNickName)) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        String nickName = ViewUtil.getTextTrim(mNickname);
        submitNickName(nickName);
    }
}
