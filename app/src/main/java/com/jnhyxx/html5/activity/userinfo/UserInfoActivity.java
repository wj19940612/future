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
import com.jnhyxx.html5.activity.account.BankcardBindingActivity;
import com.jnhyxx.html5.activity.account.NameVerifyActivity;
import com.jnhyxx.html5.activity.setting.ModifyNickNameActivity;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.fragment.dialog.SelectUserSexDialogFragment;
import com.jnhyxx.html5.fragment.dialog.UploadUserImageDialogFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.wheel.entity.City;
import com.jnhyxx.html5.view.wheel.entity.County;
import com.jnhyxx.html5.view.wheel.entity.Province;
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
public class UserInfoActivity extends BaseActivity implements SelectUserSexDialogFragment.OnUserSexListener, AddressInitTask.OnAddressListener, UploadUserImageDialogFragment.OnUserImageListener {

    // 绑定银行卡前 先进行实名认证
    private static final int REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST = 900;
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
        Log.d(TAG, "将要上传的用户信息 " + LocalUser.getUser().getUserInfo().getUserDefiniteInfo().toString());
        API.User.submitUserInfo(LocalUser.getUser().getUserInfo().getUserDefiniteInfo()).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                    }
                }).fireSync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BASE && resultCode == RESULT_OK) {
            updateUserInfo();
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_BASE:
                    updateUserInfo();
                    break;
                case REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST:
                    bingBankCard();
                    break;
            }
        }
    }

    @OnClick({R.id.headImageLayout, R.id.userName, R.id.userRealName, R.id.sex, R.id.birthday, R.id.location, R.id.introductionLayout, R.id.realNameAuth, R.id.bindBankCard, R.id.logoutButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                UploadUserImageDialogFragment.newInstance().show(getSupportFragmentManager());
                break;
            case R.id.userName:
                modifyNickName();
                break;
            case R.id.userRealName:
                openUserRealNamePage();
                break;
            case R.id.sex:
//                new SelectUserSexDialogFragment().show(getSupportFragmentManager());

//                OptionPicker picker = new OptionPicker(this, new String[]{"男", "女",});
//                picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.lucky));
////            picker.setSubmitTextColor(R.color.blueAssist);
////                picker.setSubmitTextColor(Color.parseColor("#358CF3"));
//                picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.blueAssist));
//                picker.setAnimationStyle(R.style.BottomDialogStyle);
//                picker.setOffset(2);
//                picker.setSelectedIndex(0);
////                picker.setTextSize(11);
//                picker.setLineConfig(new WheelView.LineConfig(0));//使用最长的线
//                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
//                    @Override
//                    public void onOptionPicked(int index, String item) {
//                        Log.d(TAG, "返回的结果  " + item);
//                        LocalUser.getUser().getUserInfo().setChinaSex(item);
//                    }
//                });
//                picker.show();

                break;
            case R.id.birthday:

                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                final String birthday = LocalUser.getUser().getUserInfo().getBirthday();
                if (!TextUtils.isEmpty(birthday)) {
                    String[] split = birthday.split("-");
                    if (split.length == 3) {
                        year = Integer.valueOf(split[0]);
                        month = Integer.valueOf(split[1]) - 1;
                        day = Integer.valueOf(split[2]);
                    }
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Material_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, year + "年" + month + " 月" + dayOfMonth + " 天");
                        StringBuilder birthdayDate = new StringBuilder();
                        LocalUser.getUser().getUserInfo().setBirthday(FormatBirthdayDate(year, month, dayOfMonth, birthdayDate));
                        updateUserInfo();
                    }
                }, year, month, day);

                datePickerDialog.show();
                break;
            case R.id.location:
                selectAddress();
                break;
            case R.id.introductionLayout:
                Launcher.with(getActivity(), UserIntroduceActivity.class).executeForResult(REQ_CODE_BASE);
                break;
            case R.id.realNameAuth:
                Launcher.with(getActivity(), NameVerifyActivity.class).executeForResult(REQ_CODE_BASE);
                break;
            case R.id.bindBankCard:
                bingBankCard();
                break;
            case R.id.logoutButton:
                logout();
                break;
        }
    }

    private void logout() {
        API.User.logout().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback1<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        LocalUser.getUser().logout();
                        setResult(RESULT_OK);
                        finish();
                    }
                }).fire();
    }

    private void bingBankCard() {
        if (!LocalUser.getUser().isRealNameFilled()) {
            Launcher.with(getActivity(), NameVerifyActivity.class).executeForResult(REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST);
            return;
        }
        Launcher.with(getActivity(), BankcardBindingActivity.class).executeForResult(REQ_CODE_BASE);
    }

    //选择所在地
    private void selectAddress() {
        AddressInitTask addressInitTask = new AddressInitTask(getActivity(), true);
        String land = LocalUser.getUser().getUserInfo().getLand();
        String province = "浙江";
        String city = "杭州市";
        if (!TextUtils.isEmpty(land)) {
            String[] split = land.split("-");
            if (split.length == 2) {
                province = split[0];
                city = split[1];
            }
        }
        addressInitTask.execute(province, city);
        addressInitTask.setOnAddressListener(this);
    }

    private String FormatBirthdayDate(int year, int month, int dayOfMonth, StringBuilder birthdayDate) {
        birthdayDate.append(year);
        birthdayDate.append("-");
        birthdayDate.append(month + 1);
        birthdayDate.append("-");
        birthdayDate.append(dayOfMonth);
        return birthdayDate.toString();
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

    //选择性别的回调
    @Override
    public void onSelected(String userSex) {
        updateUserInfo();
    }

    @Override
    public void onSelectAddress(Province province, City city, County county) {
        updateUserInfo();
    }

    @Override
    public void getUserImage(String headImageUrl, String bitmapToBase64) {
        if (!TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getUserPortrait())) {
            Picasso.with(getActivity()).load(LocalUser.getUser().getUserInfo().getUserPortrait()).transform(new CircleTransform() {
            }).into(mUserHeadImage);
        } else {
            getUserInfo();
        }
    }
}
