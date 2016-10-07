package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.ChannelBankList;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.fragment.BankListFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.utils.ValidityDecideUtil;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.CustomToast;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.WheelView;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankcardBindingActivity extends BaseActivity implements BankListFragment.OnBankItemClickListener {

    /**
     * 解除绑定客服电话
     */
    public static final String UNWRAP_SERVICE_TELEPHONE = "0517-87675063";
    /**
     * 实名认证的请求码
     */
    private static final int REQUEST_CODE_NAME_AUTH = 5500;

    @BindView(R.id.cardholderName)
    EditText mCardholderName;
    @BindView(R.id.bankcardNum)
    EditText mBankcardNum;
    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.submitToAuthButton)
    TextView mSubmitToAuthButton;
    @BindView(R.id.payingBank)
    TextView mPayingBank;
    //银行名称
    @BindView(R.id.bankName)
    TextView mBank;
    @BindView(R.id.bankCardNumber)
    TextView mBankCardNumber;
    @BindView(R.id.bankcardInputArea)
    LinearLayout mBankcardInputArea;
    //解除绑定
    @BindView(R.id.unbindBankcard)
    TextView mUnbindBankcard;
    //所绑定银行卡的父容器,没有绑定之前不显示
    @BindView(R.id.bankcardImageArea)
    LinearLayout mBankcardImageArea;
    @BindView(R.id.fragmentContainer)
    FrameLayout mFragmentContainer;
    //银行的图标
    @BindView(R.id.bankCardIcon)
    ImageView mBankCardIcon;
    /**
     * 头部信息
     */
    @BindView(R.id.bandCardTitle)
    TitleBar mTitleBar;
    @BindView(R.id.commonFailTvWarn)
    CommonFailWarn mCommonFailTvWarn;

    ChannelBankList mChannelBankList = null;


    private ArrayList<ChannelBankList> mChannelBankLists;
    //wheelView中的数据
    private ArrayList<String> dataList;
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_binding);
        ButterKnife.bind(this);

        mCardholderName.addTextChangedListener(mValidationWatcher);
        formatBankCardNumber();
        mPayingBank.addTextChangedListener(mValidationWatcher);
        formatPhoneNumber();
        showBankBindStatus();
    }

    private void formatBankCardNumber() {
        mBankcardNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    int length = s.toString().length();
                    if (length % 4 == 0) {
                        mBankcardNum.setText(s + " ");
                        mBankcardNum.setSelection(mBankcardNum.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean enable = checkSubmitButtonEnable();
                if (enable != mSubmitToAuthButton.isEnabled()) {
                    mSubmitToAuthButton.setEnabled(enable);
                }
            }
        });
    }

    private void formatPhoneNumber() {
        mPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    int length = s.toString().length();
                    if (length == 3 || length == 8) {
                        mPhoneNum.setText(s + " ");
                        mPhoneNum.setSelection(mPhoneNum.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean enable = checkSubmitButtonEnable();
                if (enable != mSubmitToAuthButton.isEnabled()) {
                    mSubmitToAuthButton.setEnabled(enable);
                }
            }
        });
    }

    /**
     * 这是显示银行卡是否绑定的方法，
     * 如果没有绑定，显示绑定银行卡界面，
     * 若果绑定了，显示银行卡信息
     */

    private void showBankBindStatus() {
        LocalUser localUser = LocalUser.getUser();
        mUserInfo = localUser.getUserInfo();
        if (mUserInfo != null) {
            //一般不会出现，在设置界面做了判断，没有登录不可进入这个界面
            if (mUserInfo.getIdStatus() == UserInfo.BANKCARD_STATUS_UNFILLED) {
                showAuthNameDialog();
            }
            setOldBindBankInfo(mUserInfo);

            // TODO: 2016/9/9 这是没有绑定
            //  cardState银行卡状态 0未填写，1已填写，2已认证
            if (mUserInfo.getCardState() == UserInfo.BANKCARD_STATUS_UNFILLED || mUserInfo.getCardState() == UserInfo.BANKCARD_STATUS_FILLED) {
                mBankcardInputArea.setVisibility(View.VISIBLE);
                mBankcardImageArea.setVisibility(View.GONE);
            } else {
                //这是绑定了的界面
                mBankcardInputArea.setVisibility(View.GONE);
                mBankcardImageArea.setVisibility(View.VISIBLE);
                mTitleBar.setTitle(R.string.bankcard);
                if (!TextUtils.isEmpty(mUserInfo.getIssuingbankName())) {
                    mBank.setText(mUserInfo.getIssuingbankName());
                }
                String bankIconUrl = mUserInfo.getIcon();
                if (!TextUtils.isEmpty(bankIconUrl)) {
                    Picasso.with(BankcardBindingActivity.this).load(bankIconUrl).into(mBankCardIcon, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "银行图标下载失败");
                        }
                    });
                }
                if (!TextUtils.isEmpty(mUserInfo.getCardNumber())) {
                    String bankNumber = CommonMethodUtils.bankNumber(mUserInfo.getCardNumber());
                    mBankCardNumber.setText(bankNumber);
                }
            }
        }
    }

    private void setOldBindBankInfo(UserInfo userInfo) {
        // TODO: 2016/9/18 目前没有所填写的银行卡的所属用户的信息
        mCardholderName.setText("");
        String cardNumber = userInfo.getCardNumber();
        if (!TextUtils.isEmpty(cardNumber)) {
            cardNumber = cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16) + " " + cardNumber.substring(16, cardNumber.length());
            mBankcardNum.setText(cardNumber);
        }
        String cardPhone = userInfo.getCardPhone();
        if (!TextUtils.isEmpty(cardPhone)) {
            cardPhone = cardPhone.substring(0, 3) + " " + cardPhone.substring(3, 7) + " " + cardPhone.substring(7, cardPhone.length());
            mPhoneNum.setText(cardPhone);
        }
        if (!TextUtils.isEmpty(userInfo.getIssuingbankName())) {
            mPayingBank.setText(userInfo.getIssuingbankName());
        }
    }

    private void showAuthNameDialog() {
        SmartDialog.with(getActivity(), R.string.dialog_unauthorized_name)
                .setPositive(R.string.go_and_auth, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Launcher.with(getActivity(), NameVerifyActivity.class).executeForResult(REQUEST_CODE_NAME_AUTH);
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        finish();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NAME_AUTH && resultCode == RESULT_OK) {
            UserInfo userInfo = LocalUser.getUser().getUserInfo();
            if (userInfo == null) return;
            mCardholderName.setText(userInfo.getRealName());
            SmartDialog.dismiss(this);
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSubmitButtonEnable();
            if (enable != mSubmitToAuthButton.isEnabled()) {
                mSubmitToAuthButton.setEnabled(enable);
            }
        }
    };

    private boolean checkSubmitButtonEnable() {
        String cardholderName = ViewUtil.getTextTrim(mCardholderName);
        String bankcardNum = ViewUtil.getTextTrim(mBankcardNum);
        String payingBank = ViewUtil.getTextTrim(mPayingBank);
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum);
        if (TextUtils.isEmpty(cardholderName) || TextUtils.isEmpty(bankcardNum)
                || TextUtils.isEmpty(payingBank) || TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        return true;
    }

    @OnClick({R.id.payingBank, R.id.submitToAuthButton, R.id.unbindBankcard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payingBank:
                // TODO: 2016/9/21 目前仿照h5界面的设计
//                mBankcardInputArea.setVisibility(View.GONE);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragmentContainer, new BankListFragment(), BankListFragment.BANK_LIST).commit();
//                mTitleBar.setTitle(R.string.bankcard);
                showBankDialog();
                break;
            case R.id.submitToAuthButton:
                final String bankcardNum = ViewUtil.getTextTrim(mBankcardNum).replaceAll(" ", "");
                final String payingBank = ViewUtil.getTextTrim(mPayingBank);
                final String phoneNum = ViewUtil.getTextTrim(mPhoneNum).replaceAll(" ", "");
                if (!ValidityDecideUtil.checkBankCard(bankcardNum)) {
                    mCommonFailTvWarn.show(R.string.bank_card_is_error);
                    return;
                }
                if (!ValidityDecideUtil.isMobileNum(phoneNum)) {
                    mCommonFailTvWarn.show(R.string.common_phone_num_fail);
                    return;
                }
                int bankId = -1;
                if (mChannelBankList == null) {
                    ToastUtil.curt(R.string.bind_bank_is_empty);
                    return;
                }
                bankId = mChannelBankList.getId();
                Log.d(TAG, "提交的银行数据" + "\n银行ID" + bankId + "\n银行名" + payingBank + "\n银行卡号" + bankcardNum + "\n手机号" + phoneNum);
                API.User.bindBankCard(bankId, payingBank, bankcardNum, phoneNum)
                        .setIndeterminate(this).setTag(TAG)
                        .setCallback(new Callback<Resp>() {
                            @Override
                            public void onReceive(Resp resp) {
                                if (resp.isSuccess()) {
                                    UserInfo userInfo = LocalUser.getUser().getUserInfo();
                                    userInfo.setIssuingbankName(payingBank);
                                    userInfo.setCardNumber(bankcardNum);
                                    userInfo.setCardPhone(phoneNum);
                                    userInfo.setCardState(UserInfo.BANKCARD_STATUS_FILLED);
                                    setResult(RESULT_OK);
                                    CustomToast.getInstance().showText(BankcardBindingActivity.this, resp.getMsg());
                                } else {
                                    mCommonFailTvWarn.show(resp.getMsg());
                                }
                            }
                        }).fire();
                break;
            case R.id.unbindBankcard:
                unwrapBindServiceTelephone();
                break;
        }
    }

    private void showBankDialog() {
        getChannelBnkList();

    }


    private void getChannelBnkList() {

        API.User.showChannelBankList()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<ArrayList<ChannelBankList>>>() {

                    @Override
                    public void onReceive(Resp<ArrayList<ChannelBankList>> arrayListResp) {
                        if (arrayListResp.isSuccess()) {
                            mChannelBankLists = arrayListResp.getData();
                            if (arrayListResp.getData() != null && !arrayListResp.getData().isEmpty()) {
                                View view = setWheelView(arrayListResp.getData());

                                setDialog(view);

                            } else {
                                mCommonFailTvWarn.show(R.string.no_bank_can_bind);
                            }
                        } else {
                            mCommonFailTvWarn.show(arrayListResp.getMsg());
                        }

                    }
                }).fire();
    }

    private void setDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BankcardBindingActivity.this)
                .setView(view)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChannelBankList = null;
                        mPayingBank.setText(mUserInfo.getIssuingbankName());
                    }
                });
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        builder.show();
    }

    @NonNull
    private View setWheelView(ArrayList<ChannelBankList> channelBankLists) {
        View view = LayoutInflater.from(BankcardBindingActivity.this).inflate(R.layout.dialog_wheel_view, null);
        final WheelView mWheelView = (WheelView) view
                .findViewById(R.id.wheelView);
        mWheelView.setOffset(1);
        mWheelView.setSeletion(0);// 设置默认被选中的项目

        mWheelView.setItemObjects((channelBankLists));// 实际内容
        mWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, Object item) {
                if (item instanceof ChannelBankList) {
                    mChannelBankList = (ChannelBankList) item;
                    mPayingBank.setText(((ChannelBankList) item).getName());
                } else {
                    Log.d(TAG, "返回的数据不是银行列表model类型");
                }
            }
        });
        return view;
    }


    private void unwrapBindServiceTelephone() {
        String dialogContent = getString(R.string.unBind_dialog_content, UNWRAP_SERVICE_TELEPHONE);
        SmartDialog.with(getActivity(), dialogContent)
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        boolean showing = dialog.isShowing();
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + UNWRAP_SERVICE_TELEPHONE));
                        startActivity(intent);
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onBankItemClick(ChannelBankList bank) {
        Log.d(TAG, "选择的银行信息" + bank.toString());
        mChannelBankList = bank;
        mPayingBank.setText(bank.getName());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BankListFragment.BANK_LIST);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        mBankcardInputArea.setVisibility(View.VISIBLE);
    }


}