package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawActivity extends BaseActivity {

    private static final int REQ_CODE_ADD_BANKCARD = 1;

    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.withdrawBankcard)
    TextView mWithdrawBankcard;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;
    @BindView(R.id.addBankcardButton)
    TextView mAddBankcardButton;
    @BindView(R.id.bankcardNotFilledArea)
    LinearLayout mBankcardNotFilledArea;
    @BindView(R.id.bankcardInfoArea)
    LinearLayout mBankcardInfoArea;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.withdrawAmount)
    EditText mWithdrawAmount;

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mConfirmButton.isEnabled()) {
                mConfirmButton.setEnabled(enable);
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        String withdrawAmount = ViewUtil.getTextTrim(mWithdrawAmount);
        if (TextUtils.isEmpty(withdrawAmount)) {
            return false;
        }

        double amount = Double.valueOf(withdrawAmount);
        if (amount < 20) {
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Launcher.with(WithdrawActivity.this, WithDrawRecordActivity.class).execute();
            }
        });

        updateBankInfoView();
        mWithdrawAmount.addTextChangedListener(mValidationWatcher);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //账户余额
        double moneyUsable = LocalUser.getUser().getUserInfo().getMoneyUsable();
        String mMoneyUsable = String.valueOf(moneyUsable);
        mBalance.setText(mMoneyUsable);
    }

    private void updateBankInfoView() {
        if (LocalUser.getUser().isBankcardFilled()) {
            mBankcardNotFilledArea.setVisibility(View.GONE);
            mBankcardInfoArea.setVisibility(View.VISIBLE);

            UserInfo userInfo = LocalUser.getUser().getUserInfo();
            String cardNumber = userInfo.getCardNumber();
            String bankName = userInfo.getIssuingbankName();
            StringBuffer mStringBuffer = new StringBuffer();
            mStringBuffer.append(bankName);
            mStringBuffer.append("  ");
            if (!TextUtils.isEmpty(bankName)) {
                mStringBuffer.append("*");
                mStringBuffer.append(cardNumber.substring(cardNumber.length() - 4));
            }
            mWithdrawBankcard.setText(mStringBuffer.toString());
        } else {
            mBankcardNotFilledArea.setVisibility(View.VISIBLE);
            mBankcardInfoArea.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.confirmButton)
    void doConfirmButtonClick() {
        String withdrawAmount = ViewUtil.getTextTrim(mWithdrawAmount);
        if (!TextUtils.isEmpty(withdrawAmount)) {
            final double amount = Double.valueOf(withdrawAmount);
            API.Finance.withdraw(amount)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                updateBalance(amount);
                                SmartDialog.with(getActivity(), resp.getMsg())
                                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                            @Override
                                            public void onClick(Dialog dialog) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).show();
                            } else {
                                SmartDialog.with(getActivity(), resp.getMsg()).show();
                            }
                        }
                    }).setTag(TAG).setIndeterminate(this).fire();
        }

    }

    private void updateBalance(double amount) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        String balance = mBalance.getText().toString();
        double balanceNum = Double.valueOf(balance);
        double balanceMoney = balanceNum - amount;
        userInfo.setMoneyUsable(balanceMoney);
        mBalance.setText(String.valueOf(balanceMoney));
    }

    @OnClick(R.id.addBankcardButton)
    void addBankcard() {
        if (!LocalUser.getUser().isRealNameFilled()) {
            Launcher.with(getActivity(), NameVerifyActivity.class)
                    .executeForResult(REQ_CODE_BASE);
            return;
        }

        Launcher.with(this, BankcardBindingActivity.class)
                .executeForResult(REQ_CODE_ADD_BANKCARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BASE && resultCode == RESULT_OK) {
            addBankcard();
        }
        if (requestCode == REQ_CODE_ADD_BANKCARD && resultCode == RESULT_OK) {
            updateBankInfoView();
        }
    }

}
