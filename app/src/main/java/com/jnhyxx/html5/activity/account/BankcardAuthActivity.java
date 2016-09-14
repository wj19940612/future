package com.jnhyxx.html5.activity.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.NameAuth;
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
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.jnhyxx.umenglibrary.UmengLib;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankcardAuthActivity extends BaseActivity implements BankListFragment.OnBankItemClickListener {

    public static final String NAME_AUTH_RESULT = "nameAuthResult";
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

    NameAuth.Result mNameAuthResult;

    ChannelBankList mChannelBankList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_auth);
        ButterKnife.bind(this);

        mCardholderName.addTextChangedListener(mValidationWatcher);
        mBankcardNum.addTextChangedListener(mValidationWatcher);
        mPayingBank.addTextChangedListener(mValidationWatcher);
        mPhoneNum.addTextChangedListener(mValidationWatcher);
//        updateBankcardView(getIntent());

//        API.User.getUserNameAuth(com.jnhyxx.html5.domain.local.User.getUser().getToken())
//                .setTag(TAG)
//                .setCallback(new Callback2<Resp<NameAuth>, NameAuth>() {
//                    @Override
//                    public void onRespSuccess(NameAuth nameAuth) {
//                        if (nameAuth.getStatus() == NameAuth.STATUS_NOT_FILLED) {
//                            showAuthNameDialog(nameAuth);
//                        } else {
//                            mCardholderName.setText(nameAuth.getUserName());
//                        }
//                    }
//                }).fire();

        showBankBindStatus();
    }

    /**
     * 这是显示银行卡是否绑定的方法，
     * 如果没有绑定，显示绑定银行卡界面，
     * 若果绑定了，显示银行卡信息
     */

    private void showBankBindStatus() {
        LocalUser localUser = LocalUser.getUser();
        UserInfo userInfo = localUser.getUserInfo();
        if (!localUser.isLogin()) {
            ToastUtil.curt(R.string.nickname_unknown);
            finish();
            return;
        }
        if (userInfo != null) {
            //一般不会出现，在设置界面做了判断，没有登录不可进入这个界面
            if (userInfo.getIdStatus() == UserInfo.BANK_CARD_AUTH_STATUS_NOT_WRITE) {
                showAuthNameDialog();
            }
            // TODO: 2016/9/9 这是没有绑定
            //  cardState银行卡状态 0未填写，1已填写，2已认证
            if (userInfo.getCardState() == 0) {
                mBankcardInputArea.setVisibility(View.VISIBLE);
                mBankcardImageArea.setVisibility(View.GONE);
            } else {
                //这是绑定了的界面
                mBankcardInputArea.setVisibility(View.GONE);
                mBankcardImageArea.setVisibility(View.VISIBLE);
                mTitleBar.setTitle(R.string.bankcard);
                if (!TextUtils.isEmpty(userInfo.getIssuingbankName())) {
                    mBank.setText(userInfo.getIssuingbankName());
                }
                String bankIconUrl = userInfo.getIcon();
                if (!TextUtils.isEmpty(bankIconUrl)) {
                    Picasso.with(BankcardAuthActivity.this).load(bankIconUrl).into(mBankCardIcon, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "银行图标下载失败");
                        }
                    });
                }
                if (!TextUtils.isEmpty(userInfo.getCardNumber())) {
                    String bankNumber = CommonMethodUtils.bankNumber(userInfo.getCardNumber());
                    mBankCardNumber.setText(bankNumber);
                }
            }
        }
    }

    //
//    private void updateBankcardView(Intent intent) {
//        BankcardAuth bankcardAuth = (BankcardAuth) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
//        if (bankcardAuth.getStatus() == BankcardAuth.STATUS_BE_BOUND) {
//            mBankcardInputArea.setVisibility(View.GONE);
//            mBankcardImageArea.setVisibility(View.VISIBLE);
//        } else if (bankcardAuth.getStatus() == BankcardAuth.STATUS_FILLED) {
//            mBankcardNum.setText(bankcardAuth.getBankNum());
//            mPayingBank.setText(bankcardAuth.getBankName());
//            mPhoneNum.setText(bankcardAuth.getPhone());
//        }
//    }

    private void showAuthNameDialog() {
        SmartDialog.with(getActivity(), R.string.dialog_unauthorized_name)
                .setPositive(R.string.go_and_auth, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Launcher.with(getActivity(), NameAuthActivity.class).executeForResult(REQUEST_CODE_NAME_AUTH);
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
                mBankcardInputArea.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new BankListFragment(), BankListFragment.BANK_LIST).commit();
                mTitleBar.setTitle(R.string.bankcard);
                break;
            case R.id.submitToAuthButton:
                final String bankcardNum = ViewUtil.getTextTrim(mBankcardNum);
                final String payingBank = ViewUtil.getTextTrim(mPayingBank);
                final String phoneNum = ViewUtil.getTextTrim(mPhoneNum);
                // TODO: 2016/9/13 这里的银行Id没有赋值 
                int bankId = -1;
//                API.User.bindBankCard(bankId, payingBank, bankcardNum, phoneNum)
//                        .setIndeterminate(this).setTag(TAG)
//                        .setCallback(new Callback<Resp<BankcardAuth>>() {
//                            @Override
//                            public void onReceive(final Resp<BankcardAuth> resp) {
//                                if (resp.isSuccess()) {
//                                    SmartDialog.with(getActivity(), resp.getMsg())
//                                            .setCancelableOnTouchOutside(false)
//                                            .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
//                                                @Override
//                                                public void onClick(Dialog dialog) {
//                                                    dialog.dismiss();
//                                                    BankcardAuth auth = resp.getData();
//                                                    auth.setStatus(BankcardAuth.STATUS_FILLED);
//                                                    setResultForCalling(auth);
//                                                    finish();
//                                                }
//                                            }).show();
//
//                                } else {
//                                    mCommonFailTvWarn.setCenterTxt(r);
//                                }
//                            }
//                        }).fire();
                if (mChannelBankList == null) {
                    ToastUtil.curt(R.string.bind_bank_is_empty);
                    return;
                }
                API.User.bindBankCard(bankId, payingBank, bankcardNum, phoneNum)
                        .setIndeterminate(this).setTag(TAG)
                        .setCallback(new Callback<Resp>() {
                            @Override
                            public void onReceive(Resp resp) {
                                if (resp.isSuccess()) {
                                    ToastUtil.curt("银行卡提交成功");
                                    UserInfo userInfo = LocalUser.getUser().getUserInfo();
                                    userInfo.setIssuingbankName(payingBank);
                                    userInfo.setCardNumber(bankcardNum);
                                    userInfo.setCardPhone(phoneNum);
                                    userInfo.setCardState(1);
                                    setResult(RESULT_OK);
                                } else {
                                    mCommonFailTvWarn.setCenterTxt(resp.getMsg());
                                }
                            }
                        }).fire();
                break;
            case R.id.unbindBankcard:
                unwrapBindServiceTelephone();
                break;
        }
    }

    boolean mIsCall = false;

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

    /**
     * 由提现页面唤起,回传 BankcardAuth
     * <p>
     * 由个人信息页面唤起,回传 BankcardAuth NameAuth
     * <p>
     * 由充值页面唤起,回传 BankcardAuth
     *
     * @param data
     */
    private void setResultForCalling(BankcardAuth data) {
        if (getCallingActivity() == null) return;
        String fromClass = getCallingActivity().getClassName();

        if (fromClass.equals(WithdrawActivity.class.getName())) {
            Intent intent = new Intent().putExtra(WithdrawActivity.RESULT_BANKCARD_AUTH, data);
            setResult(RESULT_OK, intent);
        }

        if (fromClass.equals(ProfileActivity.class.getName())) {
            Intent intent = new Intent()
                    .putExtra(ProfileActivity.RESULT_BANKCARD_AUTH, data)
                    .putExtra(ProfileActivity.RESULT_NAME_AUTH, mNameAuthResult);
            setResult(RESULT_OK, intent);
        }

        if (fromClass.equals(RechargeActivity.class.getName())) {
            Intent intent = new Intent().putExtra(RechargeActivity.RESULT_BANKCARD_AUTH, data);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void onBankItemClick(ChannelBankList bank) {
        mChannelBankList = bank;
        mPayingBank.setText(bank.getName());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BankListFragment.BANK_LIST);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        mBankcardInputArea.setVisibility(View.VISIBLE);
    }
}