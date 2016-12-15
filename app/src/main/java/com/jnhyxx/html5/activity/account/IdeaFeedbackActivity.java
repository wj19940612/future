package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CustomToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdeaFeedbackActivity extends BaseActivity {

    @BindView(R.id.feedbackContent)
    EditText mFeedbackContent;
    @BindView(R.id.feedbackConnectWay)
    EditText mFeedbackConnectWay;
    @BindView(R.id.feedbackSubmit)
    TextView mFeedbackSubmit;
    @BindView(R.id.feedbackContentNumber)
    TextView mFeedbackContentNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_feedback);
        ButterKnife.bind(this);
        mFeedbackContentNumber.setText(getString(R.string.feedback_content_number));
        mFeedbackContent.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            String importAbilityWords = String.valueOf(200 - editable.toString().trim().length());
            mFeedbackContentNumber.setText(importAbilityWords);

            if (!TextUtils.isEmpty(editable.toString().trim())) {
                mFeedbackSubmit.setEnabled(true);
            } else {
                mFeedbackSubmit.setEnabled(false);
            }
        }
    };

    @OnClick(R.id.feedbackSubmit)
    public void onClick(View view) {
        String userId = "";
        String userName = "";
        String realName = "";
        if (LocalUser.getUser().isLogin()) {
            UserInfo userInfo = LocalUser.getUser().getUserInfo();
            if (userInfo != null) {
                userName = userInfo.getUserName();
                realName = userInfo.getRealName();
            }
        }

        String feedBackContent = mFeedbackContent.getText().toString().trim();

        if (TextUtils.isEmpty(feedBackContent)) return;
        submitFeedBack(userName, realName, feedBackContent);
    }

    private void submitFeedBack(String userName, String realName, String feedBackContent) {
        API.User.submitFeedBack(feedBackContent, null, userName, realName, mFeedbackConnectWay.getText().toString())
                .setCallback(new Callback2<Resp<JsonObject>, JsonObject>() {
                    @Override
                    public void onRespSuccess(JsonObject jsonObject) {
                        CustomToast.getInstance().showText(getActivity(), R.string.feedback_submit_success);
                        finish();
                    }
                })
                .setIndeterminate(this)
                .setTag(TAG)
                .fire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFeedbackContent.removeTextChangedListener(mValidationWatcher);
    }
}
