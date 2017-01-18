package com.jnhyxx.html5.activity.userinfo;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserIntroduceActivity extends BaseActivity {

    @BindView(R.id.introduceInput)
    EditText mIntroduceInput;
    @BindView(R.id.submit)
    TextView mSubmit;

    UserDefiniteInfo mUserDefiniteInfo;

    //最多输入30字符
    private static final int MAX_INPUT_LENGTH = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_introduce);
        ButterKnife.bind(this);

        mUserDefiniteInfo = new UserDefiniteInfo();
        String introduction = LocalUser.getUser().getUserInfo().getIntroduction();
        if (!TextUtils.isEmpty(introduction)) {
            mIntroduceInput.setText(introduction);
        }
        mIntroduceInput.addTextChangedListener(mValidationWatcher);
        mIntroduceInput.setFilters(new InputFilter[]{emojiFilter,Lengthfilter});

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIntroduceInput.removeTextChangedListener(mValidationWatcher);
    }


    //中文和英文都算一个字符
    InputFilter Lengthfilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
            int dindex = 0;
            int count = 0;

            while (count <= MAX_INPUT_LENGTH && dindex < dest.length()) {
                char c = dest.charAt(dindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 1;
                }
            }

            if (count > MAX_INPUT_LENGTH) {
                return dest.subSequence(0, dindex - 1);
            }

            int sindex = 0;
            while (count <= MAX_INPUT_LENGTH && sindex < src.length()) {
                char c = src.charAt(sindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 1;
                }
            }

            if (count > MAX_INPUT_LENGTH) {
                sindex--;
            }

            return src.subSequence(0, sindex);
        }
    };


    InputFilter emojiFilter = new InputFilter() {

        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",

                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher emojiMatcher = emoji.matcher(source);

            if (emojiMatcher.find()) {

                return "";
            }
            return null;

        }
    };

    ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            super.onTextChanged(charSequence, i, i1, i2);

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mSubmit.isEnabled()) {
                mSubmit.setEnabled(enable);
            }
//            if (s.toString().contains(EMOIL)) {
//                String noEmoilData = s.toString().replaceAll(EMOIL, "");
//                mIntroduceInput.setText(noEmoilData);
//            }


//            Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
//                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
//            Matcher m = p.matcher(s.toString());
//            String noEmoilData = m.replaceAll("");
//            mIntroduceInput.setText(noEmoilData);
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
