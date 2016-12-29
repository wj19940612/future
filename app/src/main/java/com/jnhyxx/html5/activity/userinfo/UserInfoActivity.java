package com.jnhyxx.html5.activity.userinfo;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户个人信息界面
 */
public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @BindView(R.id.helpArrow)
    ImageView mHelpArrow;
    @BindView(R.id.userName)
    IconTextRow mUserName;
    @BindView(R.id.userRealName)
    IconTextRow mUserRealName;
    @BindView(R.id.sex)
    IconTextRow mSex;
    @BindView(R.id.birthday)
    IconTextRow mBirthday;
    @BindView(R.id.userIntroduction)
    EditText mUserIntroduction;
    @BindView(R.id.realNameAuth)
    IconTextRow mRealNameAuth;
    @BindView(R.id.bindBankCard)
    IconTextRow mBindBankCard;
    @BindView(R.id.bindingPhone)
    IconTextRow mBindingPhone;
    @BindView(R.id.logoutButton)
    TextView mLogoutButton;
    @BindView(R.id.activity_user_info)
    LinearLayout mActivityUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
    }
}
