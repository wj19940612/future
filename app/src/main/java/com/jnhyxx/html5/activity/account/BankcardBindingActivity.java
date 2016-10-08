package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.StrFormatter;
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

public class BankcardBindingActivity extends BaseActivity {

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

    ChannelBankList mChannelBank = null;
    @BindView(R.id.bindCardHint)
    ImageView mBindCardHint;
    @BindView(R.id.llChooseBank)
    LinearLayout mLlChooseBank;


    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_binding);
        ButterKnife.bind(this);

        mCardholderName.addTextChangedListener(mValidationWatcher);
        mPhoneNum.addTextChangedListener(mPhoneValidationWatcher);
        mBankcardNum.addTextChangedListener(mBankCardValidationWatcher);
        showBankBindStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBankcardNum.removeTextChangedListener(mBankCardValidationWatcher);
        mPhoneNum.removeTextChangedListener(mPhoneValidationWatcher);
        mCardholderName.removeTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mPhoneValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);
            formatPhoneNumber();
        }
    };

    private ValidationWatcher mBankCardValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);
            formatBankCardNumber();
        }
    };

    private void formatBankCardNumber() {
        String oldBankCard = mBankcardNum.getText().toString();
        String bankCardNoSpace = oldBankCard.replaceAll(" ", "");
        String newBankCard = StrFormatter.getFormatBankCardNumber(bankCardNoSpace);
        if (!newBankCard.equalsIgnoreCase(oldBankCard)) {
            mBankcardNum.setText(newBankCard);
            mBankcardNum.setSelection(newBankCard.length());
        }
    }

    private void formatPhoneNumber() {
        String oldPhone = mPhoneNum.getText().toString();
        String phoneNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatPhoneNumber(phoneNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mPhoneNum.setText(newPhone);
            mPhoneNum.setSelection(newPhone.length());
        }
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
            if (!localUser.isRealNameFilled()) {
                showAuthNameDialog();
            }
            setOldBindBankInfo(mUserInfo);

            // TODO: 2016/9/9 这是没有认证
            if (localUser.isBankcardApproved()) {
                mBankcardInputArea.setVisibility(View.VISIBLE);
                mBankcardImageArea.setVisibility(View.GONE);
            } else {
                //这是认证了的界面
                mBankcardInputArea.setVisibility(View.GONE);
                mBankcardImageArea.setVisibility(View.VISIBLE);
                mTitleBar.setTitle(R.string.bankcard);
                if (!TextUtils.isEmpty(mUserInfo.getIssuingbankName())) {
                    mBank.setText(mUserInfo.getIssuingbankName());
                }
                String bankIconUrl = mUserInfo.getIcon();
                if (!TextUtils.isEmpty(bankIconUrl)) {
                    Picasso.with(BankcardBindingActivity.this).load(bankIconUrl).into(mBankCardIcon);
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
        mBankcardNum.setText(userInfo.getCardNumber());
        mPhoneNum.setText(userInfo.getCardPhone());
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

    @OnClick({R.id.payingBank, R.id.submitToAuthButton, R.id.unbindBankcard, R.id.bindCardHint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payingBank:
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
                final int bankId = LocalUser.getUser().getBankId();
                if (!TextUtils.isEmpty(payingBank) && TextUtils.equals(payingBank, getString(R.string.please_choose_bank)) || bankId == -1) {
                    mCommonFailTvWarn.show(R.string.bind_bank_is_empty);
                    return;
                }
                API.User.bindBankCard(bankId, payingBank, bankcardNum, phoneNum)
                        .setIndeterminate(this).setTag(TAG)
                        .setCallback(new Callback<Resp>() {
                            @Override
                            public void onReceive(Resp resp) {
                                if (resp.isSuccess()) {
                                    LocalUser localUser = LocalUser.getUser();
                                    UserInfo userInfo = localUser.getUserInfo();
                                    userInfo.setIssuingbankName(payingBank);
                                    userInfo.setCardNumber(bankcardNum);
                                    userInfo.setCardPhone(phoneNum);
                                    userInfo.setCardState(UserInfo.BANKCARD_STATUS_FILLED);
                                    localUser.setBankId(bankId);
                                    setResult(RESULT_OK);
                                    CustomToast.getInstance().showText(BankcardBindingActivity.this, resp.getMsg());
                                    finish();
                                } else {
                                    mCommonFailTvWarn.show(resp.getMsg());
                                }
                            }
                        }).fire();
                break;
            case R.id.unbindBankcard:
                unwrapBindServiceTelephone();
                break;
            case R.id.bindCardHint:
                showCardHolderDialog();
                break;
        }
    }

    private void showCardHolderDialog() {
        SmartDialog.with(getActivity(), R.string.bank_hint)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }

    private void showBankDialog() {
        getChannelBankList();
    }


    private void getChannelBankList() {

        API.User.showChannelBankList()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<ArrayList<ChannelBankList>>>() {

                    @Override
                    public void onReceive(Resp<ArrayList<ChannelBankList>> arrayListResp) {
                        if (arrayListResp.isSuccess()) {
                            if (arrayListResp.getData() != null && !arrayListResp.getData().isEmpty()) {
                                View view = setWheelView(arrayListResp.getData());
                                setSelectBankDialog(view);
                            } else {
                                mCommonFailTvWarn.show(R.string.no_bank_can_bind);
                            }
                        } else {
                            mCommonFailTvWarn.show(arrayListResp.getMsg());
                        }
                    }
                }).fire();
    }

    private void setSelectBankDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BankcardBindingActivity.this)
                .setView(view)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocalUser.getUser().setBankId(mChannelBank.getId());
                        mPayingBank.setText(mChannelBank.getName());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChannelBank = null;
                        dialog.dismiss();
                    }
                });
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
                mChannelBank = (ChannelBankList) item;
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
}