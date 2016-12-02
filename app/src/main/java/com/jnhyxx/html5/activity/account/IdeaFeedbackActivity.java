package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.KeyBoardHelper;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;

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

    @BindView(R.id.topLayout)
    LinearLayout mTopLayout;
    @BindView(R.id.emptyLayout)
    View mHideLayout;

    private KeyBoardHelper mKeyBoardHelper;
    private int bottomHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_feedback);
        ButterKnife.bind(this);
        mFeedbackContentNumber.setText(getString(R.string.feedback_content_number, 0));
        mFeedbackContent.addTextChangedListener(mValidationWatcher);
        setKeyboardHelper();
    }

    /**
     * 设置对键盘高度的监听
     */
    private void setKeyboardHelper() {
        mKeyBoardHelper = new KeyBoardHelper(this);
        mKeyBoardHelper.onCreate();
//        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
//        mHideLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                bottomHeight = mHideLayout.getHeight();
//            }
//        });
    }

    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyboardHeight) {
            final int height = keyboardHeight;
            if (bottomHeight < height) {
                int offset = bottomHeight - height;
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mTopLayout
                        .getLayoutParams();
                lp.topMargin = offset;
                mTopLayout.setLayoutParams(lp);
            }

        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mTopLayout
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                mTopLayout.setLayoutParams(lp);
            }

        }
    };
    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {

            mFeedbackContentNumber.setText(getString(R.string.feedback_content_number, editable.toString().length()));

            if (!TextUtils.isEmpty(editable.toString())) {
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
        API.User.submitFeedBack(mFeedbackContent.getText().toString(), null, userName, realName, mFeedbackConnectWay.getText().toString())
                .setCallback(new Callback2<Resp<JsonObject>, JsonObject>() {
                    @Override
                    public void onRespSuccess(JsonObject jsonObject) {
                        ToastUtil.curt(R.string.feedback_submit_success);
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
        mKeyBoardHelper.onDestroy();
    }

}
