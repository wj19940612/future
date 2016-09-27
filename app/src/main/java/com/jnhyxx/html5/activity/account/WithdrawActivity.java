package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawActivity extends BaseActivity {

    public static final String RESULT_BANKCARD_AUTH = "bankcardAuthResult";

    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.rechargeAmount)
    EditText mWithdrawAmount;
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

    private BankcardAuth mBankcardAuth;

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
    }

    private void updateBankInfoView() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            if (userInfo.getCardState() == UserInfo.BANKCARD_STATUS_UNFILLED) {
                mBankcardNotFilledArea.setVisibility(View.VISIBLE);
                mBankcardInfoArea.setVisibility(View.GONE);
            } else {
                mBankcardNotFilledArea.setVisibility(View.GONE);
                mBankcardInfoArea.setVisibility(View.VISIBLE);
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
            }
        }
    }

    @OnClick(R.id.confirmButton)
    void doConfirmButtonClick() {
        String withdrawAmount = mWithdrawAmount.getText().toString().trim();
        if (TextUtils.isEmpty(withdrawAmount)) {
            ToastUtil.show(R.string.please_input_amount);
            return;
        }

        double amount = Double.valueOf(withdrawAmount);
        API.Finance.withdraw(amount)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onReceive(Resp resp) {
                        if (resp.isSuccess()) {
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

    @OnClick(R.id.addBankcardButton)
    void addBankcard() {
        Launcher.with(this, BankcardBindingActivity.class)
                .executeForResult(REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            updateBankInfoView();
        }
    }

}
