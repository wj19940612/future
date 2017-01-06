package com.jnhyxx.html5.activity.userinfo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.setting.ModifyNickNameActivity;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.fragment.dialog.UploadUserImageDialogFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.realNameAuth;

/**
 * 用户个人信息界面
 */
public class UserInfoActivity extends BaseActivity {

    //修改昵称的请求码
    private static final int REQ_CODE_MODIFY_NICK_NAME = 659;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.headImageLayout)
    RelativeLayout mHeadImageLayout;
    @BindView(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @BindView(R.id.userName)
    IconTextRow mUserName;
    @BindView(R.id.userRealName)
    IconTextRow mUserRealName;
    @BindView(R.id.sex)
    IconTextRow mSex;
    @BindView(R.id.birthday)
    IconTextRow mBirthday;
    @BindView(R.id.introductionLayout)
    LinearLayout mIntroductionLayout;
    @BindView(R.id.userIntroduction)
    TextView mUserIntroduction;
    @BindView(realNameAuth)
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

    private Calendar mCalendar;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BASE && resultCode == RESULT_OK) {
            updateUserInfo();
        }
    }

    @OnClick({R.id.userName, R.id.userRealName, R.id.birthday, R.id.location, R.id.headImageLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userName:
                modifyNickName();
                break;
            case R.id.userRealName:
                openUserRealNamePage();
                break;
            case R.id.birthday:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, year + "年" + month + " 月" + dayOfMonth + " 天");
                    }
                },
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
                break;
            case R.id.headImageLayout:
                UploadUserImageDialogFragment.newInstance().show(getSupportFragmentManager());
                break;
        }
    }

    private void openUserRealNamePage() {
        if (LocalUser.getUser().getUserInfo().isUserRealNameAuth()) {
            ToastUtil.curt(R.string.is_already_real_name);
        } else {
            Launcher.with(getActivity(), SubmitRealNameActivity.class).executeForResult(REQ_CODE_BASE);
        }
    }

    private void modifyNickName() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo.isNickNameModifiedBefore()) {
            ToastUtil.show(R.string.nick_name_can_be_modified_once_only);
        } else {
            Launcher.with(getActivity(), ModifyNickNameActivity.class)
                    .executeForResult(REQ_CODE_BASE);
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
                            LocalUser.getUser().getUserInfo().setUserDefiniteInfo(userDefiniteInfo);
                            updateUserInfo();
                        }
                    }
                })
                .fireSync();
    }

    private void updateUserInfo() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (!TextUtils.isEmpty(userInfo.getUserPortrait())) {
            Picasso.with(UserInfoActivity.this).load(userInfo.getUserPortrait()).transform(new CircleTransform()).into(mUserHeadImage);
        }
        if (!TextUtils.isEmpty(userInfo.getUserName())) {
            mUserName.setSubText(userInfo.getUserName());
        } else {
            mUserName.setSubText("");
        }

        if (!TextUtils.isEmpty(userInfo.getRealName())) {
            mUserRealName.setSubText(userInfo.getRealName());
        } else {
            mUserRealName.setSubText("");
        }

        if (!TextUtils.isEmpty(userInfo.getUserName())) {
            mUserName.setSubText(userInfo.getUserName());
        } else {
            mUserName.setSubText("");
        }

        if (!TextUtils.isEmpty(userInfo.getChinaSex())) {
            mSex.setSubText(userInfo.getChinaSex());
        } else {
            mSex.setSubText("");
        }

        if (!TextUtils.isEmpty(userInfo.getLand())) {
            mLocation.setSubText(userInfo.getLand());
        } else {
            mLocation.setSubText("");
        }

        if (!TextUtils.isEmpty(userInfo.getBirthday())) {
            mBirthday.setSubText(userInfo.getBirthday());
        } else {
            mBirthday.setSubText("");
        }

        if (!TextUtils.isEmpty(userInfo.getIntroduction())) {
            mUserIntroduction.setText(userInfo.getIntroduction());
        }

        mRealNameAuth.setSubText(getRealNameAuthStatusRes(userInfo.getIdStatus()));
        mBindBankCard.setSubText(getBindBankcardAuthStatusRes(userInfo.getCardState()));

        if (!TextUtils.isEmpty(userInfo.getUserPhone())) {
            mBindingPhone.setSubText(userInfo.getUserPhone());
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

    /**
     * @param authStatus
     * @return
     */
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
