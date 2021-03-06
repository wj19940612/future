package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserFundInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.withdrawAmount;

public class WithdrawActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.withdrawRule)
    TextView mWithdrawRule;
    @BindView(R.id.bankCardIcon)
    ImageView mBankCardIcon;
    @BindView(R.id.bankName)
    TextView mBankName;
    @BindView(R.id.withdrawRecord)
    TextView mWithdrawRecord;
    @BindView(withdrawAmount)
    EditText mWithdrawAmount;
    @BindView(R.id.allWithdraw)
    TextView mAllWithdraw;
    @BindView(R.id.bankcardInfoArea)
    LinearLayout mBankcardInfoArea;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;
    @BindView(R.id.failWarn)
    CommonFailWarn mFailWarn;
    private double mMoneyDrawUsable;
    private UserFundInfo userFundInfo;

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mConfirmButton.isEnabled()) {
                mConfirmButton.setEnabled(enable);
            }
            mWithdrawAmount.setSelection(s.toString().length());
        }
    };

    private boolean checkConfirmButtonEnable() {
        String withdrawAmount = ViewUtil.getTextTrim(mWithdrawAmount);
        if (TextUtils.isEmpty(withdrawAmount)) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);

        if (isFirstWithdraw()) {
            Preference.get().setIsFirstWithdraw(LocalUser.getUser().getPhone(), false);
            showWithdrawRuleDialog();
        }

        mWithdrawAmount.addTextChangedListener(mValidationWatcher);

        getMoneyDrawUsable();

        updateUserStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BASE && resultCode == RESULT_OK) {
            updateUserInfoBalance(Double.valueOf(ViewUtil.getTextTrim(mWithdrawAmount)));
        }
    }

    private boolean isFirstWithdraw() {
        return Preference.get().isFirstWithdraw(LocalUser.getUser().getPhone());
    }

    private void updateUserStatus() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        getUserBindBankInfo();
        String bankCardEndNumber = userInfo.getCardNumber().substring(userInfo.getCardNumber().length() - 4);
        mBankName.setText(getString(R.string.bank_name_card_number, userInfo.getIssuingbankName(), bankCardEndNumber));
    }

    public void getUserBindBankInfo() {
        API.User.getUserBankInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<UserInfo>>() {

                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        updateUserBankInfo(resp);
                        UserInfo userInfo = LocalUser.getUser().getUserInfo();
                        if (!TextUtils.isEmpty(userInfo.getAppIcon())) {
                            Picasso.with(getActivity()).load(userInfo.getAppIcon()).into(mBankCardIcon);
                        } else {
                            Picasso.with(getActivity()).load(userInfo.getIcon()).into(mBankCardIcon);
                        }
                    }
                })
                .fireSync();
    }

    private void updateUserBankInfo(Resp<UserInfo> resp) {
        UserInfo webUserBankInfo = resp.getData();
        UserInfo userInfo = LocalUser.getUser().getUserInfo();

        userInfo.setIdStatus(webUserBankInfo.getIdStatus());
        userInfo.setRealName(webUserBankInfo.getRealName());
        userInfo.setCardPhone(webUserBankInfo.getCardPhone());
        userInfo.setAppIcon(webUserBankInfo.getAppIcon());
        userInfo.setIssuingbankName(webUserBankInfo.getIssuingbankName());
        userInfo.setIdCard(webUserBankInfo.getIdCard());
        userInfo.setCardState(webUserBankInfo.getCardState());
        userInfo.setCardNumber(webUserBankInfo.getCardNumber());
        userInfo.setIcon(webUserBankInfo.getIcon());
        userInfo.setAppIcon(webUserBankInfo.getAppIcon());
        userInfo.setBankId(webUserBankInfo.getBankId());
        userInfo.setLimitSingle(webUserBankInfo.getLimitSingle());

        LocalUser.getUser().setUserInfo(userInfo);
    }

    private void getMoneyDrawUsable() {
        API.Finance.getFundInfo().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<UserFundInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserFundInfo> resp) {

                        userFundInfo = resp.getData();
                        Log.d(TAG, "用户资金信息 " + userFundInfo.toString());
                        mMoneyDrawUsable = userFundInfo.getMoneyDrawUsable();
                        mWithdrawAmount.setHint(getString(R.string.withdraw_least_money_hint, FinanceUtil.formatWithThousandsSeparator(LocalUser.getUser().getUserInfo().getMoneyUsable())));
                    }
                }).fire();
    }


    void doConfirmButtonClick() {
        String withdrawAmount = ViewUtil.getTextTrim(mWithdrawAmount).replace(",", "");
        if (!TextUtils.isEmpty(withdrawAmount)) {
            MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.WITHDRAW_OK);
            final double amount = Double.valueOf(withdrawAmount);
            if (amount < 20) {
                mFailWarn.show(R.string.withdraw_once_least_limit);
                return;
            }
            API.Finance.withdraw(amount)
                    .setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                updateUserInfoBalance(amount);
                                Launcher.with(getActivity(), WithdrawInfoActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, amount)
                                        .putExtra(Launcher.EX_PAYLOAD_1, (double) resp.getData())
                                        .execute();
                                finish();
                            } else {
                                SmartDialog.with(getActivity(), resp.getMsg()).show();
                            }
                        }
                    }).fire();
        }
    }

    private void updateUserInfoBalance(double withdrawAmount) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        userInfo.setMoneyUsable(FinanceUtil.subtraction(userFundInfo.getMoneyUsable(), withdrawAmount).doubleValue());
        mWithdrawAmount.setHint(getString(R.string.withdraw_least_money_hint, FinanceUtil.formatWithScale(FinanceUtil.subtraction(mMoneyDrawUsable, withdrawAmount).doubleValue())));
    }

    @OnClick({R.id.withdrawRule, R.id.withdrawRecord, R.id.allWithdraw, R.id.confirmButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdrawRule:
                showWithdrawRuleDialog();
                break;
            case R.id.withdrawRecord:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.WITHDRAW_RECORD);
                Launcher.with(getActivity(), WithdrawRecordActivity.class).execute();
                break;
            case R.id.allWithdraw:
                mWithdrawAmount.setText(FinanceUtil.formatWithThousandsSeparator(mMoneyDrawUsable));
                break;
            case R.id.confirmButton:
                doConfirmButtonClick();
                break;
        }
    }

    private void showWithdrawRuleDialog() {
        SmartDialog.with(getActivity(), R.string.withdraw_rule_content, R.string.withdraw_rule_title)
                .setPositive(R.string.i_get_it)
                .setSingleButtonBg(R.drawable.btn_blue)
                .setMessageMaxLines(Integer.MAX_VALUE)
                .setMessageGravity(Gravity.LEFT)
                .setTitleMargin(30)
                .setTitleTextColor(R.color.blackPrimary)
                .setMessageTextColor(R.color.blackPrimary)
                .show();
    }
}
