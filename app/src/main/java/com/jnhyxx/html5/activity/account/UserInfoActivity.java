package com.jnhyxx.html5.activity.account;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.setting.ModifyNickNameActivity;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.fragment.dialog.UploadUserImageDialogFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;


/**
 * 用户个人信息界面
 */
public class UserInfoActivity extends BaseActivity {

    // 绑定银行卡前 先进行实名认证
    public static final int REQ_CODE_BINDING_CARD_VERIFY_NAME_FIRST = 900;

    private static final String SEX_BOY = "男";
    private static final String SEX_GIRL = "女";

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
    @BindView(R.id.realNameAuth)
    IconTextRow mRealNameAuth;
    @BindView(R.id.bindBankCard)
    IconTextRow mBindBankCard;
    @BindView(R.id.bindingPhone)
    IconTextRow mBindingPhone;
    @BindView(R.id.logoutButton)
    TextView mLogoutButton;
    //    @BindView(R.id.location)
//    IconTextRow mLocation;//
    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.ll_Location)
    LinearLayout mLlLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);


        updateUserInfo();

        getUserInfo();
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
                case UploadUserImageDialogFragment.REQ_CLIP_HEAD_IMAGE_PAGE:
                    if (!TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getUserPortrait())) {
                        Picasso.with(getActivity()).load(LocalUser.getUser().getUserInfo().getUserPortrait()).transform(new CircleTransform() {
                        }).into(mUserHeadImage);
                    } else {
                        getUserInfo();
                    }
                    break;
            }
        }

    }


    @OnClick({R.id.headImageLayout, R.id.userName, R.id.userRealName, R.id.sex, R.id.birthday, R.id.ll_Location, R.id.introductionLayout, R.id.realNameAuth, R.id.bindBankCard, R.id.logoutButton})
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
                showSexPicker();
                break;
            case R.id.birthday:
                showBirthdayPicker();
                break;
            case R.id.ll_Location:
                selectAddress();
                break;
            case R.id.introductionLayout:
                Launcher.with(getActivity(), UserIntroduceActivity.class).executeForResult(REQ_CODE_BASE);
                break;
            case R.id.realNameAuth:
                break;
            case R.id.bindBankCard:
                bingBankCard();
                break;
            case R.id.logoutButton:
                logout();
                break;
        }
    }

    private void showBirthdayPicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final String birthday = LocalUser.getUser().getUserInfo().getBirthday();
        if (!TextUtils.isEmpty(birthday)) {
            String[] split = birthday.split("-");
            if (split.length == 3) {
                year = Integer.valueOf(split[0]);
                month = Integer.valueOf(split[1]) - 1;
                day = Integer.valueOf(split[2]);
            }
        }
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datePickerDialogStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            }
        }, year, month, day);

//        //设置时间picker的取消颜色
        SpannableStringBuilder cancelSpannableString = new SpannableStringBuilder();
        cancelSpannableString.append(getString(R.string.cancel));
        ForegroundColorSpan backgroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.lucky));
        cancelSpannableString.setSpan(backgroundColorSpan, 0, cancelSpannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, cancelSpannableString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //设置时间picker的确定颜色
        SpannableStringBuilder confirmSpannableString = new SpannableStringBuilder();
        confirmSpannableString.append(getString(R.string.ok));
        ForegroundColorSpan confirmColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blueAssist));
        confirmSpannableString.setSpan(confirmColorSpan, 0, confirmSpannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, confirmSpannableString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                StringBuilder birthdayDate = new StringBuilder();
                LocalUser.getUser().getUserInfo().setBirthday(FormatBirthdayDate(year, month, dayOfMonth, birthdayDate));
                updateUserInfo();
            }
        });
        datePickerDialog.show();
    }

    private void showSexPicker() {
        OptionPicker picker = new OptionPicker(this, new String[]{SEX_BOY, SEX_GIRL,});
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.lucky));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.blueAssist));
        picker.setAnimationStyle(R.style.BottomDialogStyle);
        picker.setOffset(1);
        if (!TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getChinaSex())) {
            picker.setSelectedItem(LocalUser.getUser().getUserInfo().getChinaSex());
        }
//        picker.setTopPadding(toDp(10));
//                picker.setTextSize(11);
//                picker.setLineConfig(new WheelView.LineConfig(0));//使用最长的线
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mSex.setSubText(item);
                    LocalUser.getUser().getUserInfo().setChinaSex(item);
                    updateUserHeadImage(LocalUser.getUser().getUserInfo());
                }
            }
        });
        picker.show();
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

        Launcher.with(getActivity(), BankcardBindingActivity.class).executeForResult(REQ_CODE_BASE);
    }

    //选择所在地
    private void selectAddress() {
        AddressInitTask addressInitTask = new AddressInitTask(getActivity(), true);
        String land = LocalUser.getUser().getUserInfo().getLand();
        String province = "";
        String city = "";
        if (!TextUtils.isEmpty(land)) {
            String[] split = land.split("-");
            if (split.length == 2) {
                province = split[0];
                city = split[1];
            }
        }
        addressInitTask.execute(province, city);
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
            Launcher.with(getActivity(), SubmitRealNameActivity.class)
                    .executeForResult(REQ_CODE_BASE);
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
                .setTag(TAG).setIndeterminate(this)
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
        updateUserHeadImage(userInfo);
        mUserName.setSubText(userInfo.getUserName());
        mUserRealName.setSubText(userInfo.getRealName());
        mUserName.setSubText(userInfo.getUserName());
        mSex.setSubText(userInfo.getChinaSex());
//        mLocation.setSubText(userInfo.getLand());
        mLocation.setText(userInfo.getLand());
        mBirthday.setSubText(userInfo.getBirthday());
        mUserIntroduction.setText(userInfo.getIntroduction());
        mRealNameAuth.setSubText(getRealNameAuthStatusRes(userInfo.getIdStatus()));
        mBindBankCard.setSubText(getBindBankcardAuthStatusRes(userInfo.getCardState()));
        mBindingPhone.setSubText(userInfo.getUserPhone());

    }

    private void updateUserHeadImage(UserInfo userInfo) {
        if (!TextUtils.isEmpty(userInfo.getUserPortrait())) {
            Picasso.with(this).load(userInfo.getUserPortrait()).transform(new CircleTransform()).into(mUserHeadImage);
        } else {
            if (!TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getChinaSex())) {
                if (LocalUser.getUser().getUserInfo().isUserisBoy()) {
                    Picasso.with(this).load(R.drawable.ic_user_info_head_boy).transform(new CircleTransform()).into(mUserHeadImage);
                } else {
                    Picasso.with(this).load(R.drawable.ic_user_info_head_girl).transform(new CircleTransform()).into(mUserHeadImage);
                }
            } else {
                Picasso.with(this).load(R.drawable.ic_user_info_head_visitor).transform(new CircleTransform()).into(mUserHeadImage);
            }
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


    /**
     * 获取地址数据并显示地址选择器
     *
     * @author
     * @since 2015/12/15
     */
    public class AddressInitTask extends AsyncTask<String, Void, ArrayList<Province>> {
        private static final String TAG = "AddressInitTask";

        private Activity mActivity;
        private String mSelectedProvince = "",
                mSelectedCity = "",
                mSelectedCounty = "";
        private boolean mHideCounty;

        public AddressInitTask(Activity activity) {
            this.mActivity = activity;
        }

        /**
         * 初始化为不显示区县的模式
         */
        public AddressInitTask(Activity activity, boolean hideCounty) {
            this.mActivity = activity;
            this.mHideCounty = hideCounty;
        }

        @Override
        protected ArrayList<Province> doInBackground(String... params) {
            if (params != null) {
                switch (params.length) {
                    case 1:
                        mSelectedProvince = params[0];
                        break;
                    case 2:
                        mSelectedProvince = params[0];
                        mSelectedCity = params[1];
                        break;
                    case 3:
                        mSelectedProvince = params[0];
                        mSelectedCity = params[1];
                        mSelectedCounty = params[2];
                        break;
                    default:
                        break;
                }
            }
            ArrayList<Province> data = new ArrayList<Province>();
            try {
                String json = ConvertUtils.toString(mActivity.getAssets().open("city.json"));

                Type listType = new TypeToken<ArrayList<Province>>() {
                }.getType();

                ArrayList<Province> provinces = new Gson().fromJson(json, listType);
                data.addAll(provinces);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Province> result) {
//        dialog.dismiss();
            if (result.size() > 0) {
                AddressPicker picker = new AddressPicker(mActivity, result);
                picker.setHideCounty(mHideCounty);
                if (mHideCounty) {
                    picker.setColumnWeight(1 / 3.0, 2 / 3.0);//将屏幕分为3份，省级和地级的比例为1:2
                } else {
                    picker.setColumnWeight(2 / 8.0, 3 / 8.0, 3 / 8.0);//省级、地级和县级的比例为2:3:3
                }
                picker.setCancelTextColor(R.color.lucky);
//            picker.setSubmitTextColor(R.color.blueAssist);
                picker.setSubmitTextColor(Color.parseColor("#358CF3"));
                picker.setAnimationStyle(R.style.BottomDialogStyle);
                picker.setSelectedItem(mSelectedProvince, mSelectedCity, mSelectedCounty);
                WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
//            lineConfig.setColor(R.color.lucky);//设置分割线颜色
                picker.setLineConfig(lineConfig);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        LocalUser.getUser().getUserInfo().setLand(province.getAreaName() + "-" + city.getAreaName());
                        updateUserInfo();
                    }
                });
                picker.show();
            } else {
                Toast.makeText(mActivity, "数据初始化失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
