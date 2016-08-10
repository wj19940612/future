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
import com.jnhyxx.html5.domain.finance.FundInfo;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;
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

        updateBankInfoView(getIntent());

        API.Finance.getFundInfo(User.getUser().getToken()).setTag(TAG)
                .setCallback(new Resp.Callback<Resp<FundInfo>, FundInfo>() {
                    @Override
                    public void onRespReceive(FundInfo fundInfo) {
                        mBalance.setText(FinanceUtil.formatWithScale(fundInfo.getUsedAmt()));
                    }
                }).post();

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void updateBankInfoView(Intent intent) {
        mBankcardAuth = (BankcardAuth) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        updateBankInfoView(mBankcardAuth);
    }

    private void updateBankInfoView(BankcardAuth bankcardAuth) {
        if (bankcardAuth.getStatus() == BankcardAuth.STATUS_NOT_FILLED) {
            mBankcardNotFilledArea.setVisibility(View.VISIBLE);
            mBankcardInfoArea.setVisibility(View.GONE);
        } else {
            mBankcardNotFilledArea.setVisibility(View.GONE);
            mBankcardInfoArea.setVisibility(View.VISIBLE);
            mWithdrawBankcard.setText(bankcardAuth.getHiddenSummary());
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
        API.Finance.withdraw(User.getUser().getLoginInfo().getTokenInfo().getToken(), amount)
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
                }).setTag(TAG).setIndeterminate(this).post();
    }

    @OnClick(R.id.addBankcardButton)
    void addBankcard() {
        Launcher.with(this, BankcardAuthActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, mBankcardAuth)
                .executeForResult(REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            BankcardAuth bankcardAuth = (BankcardAuth) data.getSerializableExtra(RESULT_BANKCARD_AUTH);
            updateBankInfoView(bankcardAuth);
        }
    }

}
