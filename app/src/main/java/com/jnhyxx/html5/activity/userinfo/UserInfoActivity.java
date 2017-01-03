package com.jnhyxx.html5.activity.userinfo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.location)
    IconTextRow mLocation;

    private UserDefiniteInfo mUserDefiniteInfo;
    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        getUserInfo();

        mCalendar = Calendar.getInstance();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.birthday, R.id.location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.birthday:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, year + "年" + month + " 月" + dayOfMonth + " 天");

                    }
                },
                        mCalendar
                        .get(Calendar.YEAR), mCalendar
                        .get(Calendar.MONTH), mCalendar
                        .get(Calendar.DAY_OF_MONTH)
//                        1993, 11, 2
                );
                datePickerDialog.show();
                break;
        }
    }

    private void getUserInfo() {
        API.User.getUserInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2<Resp<UserDefiniteInfo>, UserDefiniteInfo>() {


                    @Override
                    public void onRespSuccess(UserDefiniteInfo userDefiniteInfo) {
                        if (userDefiniteInfo != null) {
                            mUserDefiniteInfo = userDefiniteInfo;
                            updateUserInfo(userDefiniteInfo);
                        }
                    }
                })
                .fireSync();
    }

    private void updateUserInfo(UserDefiniteInfo userDefiniteInfo) {
        if (!TextUtils.isEmpty(userDefiniteInfo.getUserPortrait())) {
            Picasso.with(UserInfoActivity.this).load(userDefiniteInfo.getUserPortrait()).transform(new CircleTransform()).into(mUserHeadImage);
        }
        if (!TextUtils.isEmpty(userDefiniteInfo.getUserName())) {
            mUserName.setSubText(userDefiniteInfo.getUserName());
        }

    }


    private int getBindBankcardAuthStatusRes(int authStatus) {
        /**
         * cardState银行卡状态 0未填写，1已填写，2已绑定
         */
        if (authStatus == UserInfo.BANKCARD_STATUS_FILLED) {
            return R.string.filled;
        } else if (authStatus == UserInfo.BANKCARD_STATUS_BOUND) {
            return R.string.bound;
        }
        return R.string.unbound;
    }

    private int getRealNameAuthStatusRes(int authStatus) {
        /**
         * idStatus实名状态 0未填写，1已填写，2已认证
         */
        if (authStatus == UserInfo.REAL_NAME_STATUS_FILLED) {
            return R.string.filled;
        } else if (authStatus == UserInfo.REAL_NAME_STATUS_VERIFIED) {
            return R.string.authorized;
        }
        return R.string.un_authorized;
    }


}
