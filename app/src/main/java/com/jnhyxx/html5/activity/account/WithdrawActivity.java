package com.jnhyxx.html5.activity.account;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawActivity extends BaseActivity {

    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.withdrawAmount)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);

        requestFundInfo();
        requestBankcardInfo();

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void requestBankcardInfo() {
        API.Account.getBankcardInfo(User.getUser().getLoginInfo().getTokenInfo().getToken())
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp<BankcardAuth>>() {
                    @Override
                    public void onSuccess(Resp<BankcardAuth> bankcardResp) {
                        if (bankcardResp.isSuccess()) {
                            BankcardAuth bankcard = bankcardResp.getData();
                            updateBankInfoView(bankcard);
                        } else {
                            ToastUtil.show(bankcardResp.getMsg());
                        }
                    }
                }).post();
    }

    private void updateBankInfoView(BankcardAuth bankcard) {
        if (bankcard.getStatus() == BankcardAuth.STATUS_NOT_FILLED) {
            mBankcardNotFilledArea.setVisibility(View.VISIBLE);
            mBankcardInfoArea.setVisibility(View.GONE);
        } else {
            mBankcardNotFilledArea.setVisibility(View.GONE);
            mBankcardInfoArea.setVisibility(View.VISIBLE);
            mWithdrawBankcard.setText(bankcard.getHiddenSummary());
        }
    }

    private void requestFundInfo() {
        API.Finance.getFundInfo(User.getUser().getLoginInfo().getTokenInfo().getToken())
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Callback<Resp<FundInfo>>() {
                    @Override
                    public void onSuccess(Resp<FundInfo> fundInfoResp) {
                        if (fundInfoResp.isSuccess()) {
                            FundInfo fundInfo = fundInfoResp.getData();
                            mBalance.setText(FinanceUtil.formatWithScale(fundInfo.getUsedAmt()));
                        } else {
                            ToastUtil.show(fundInfoResp.getMsg());
                        }
                    }
                }).post();
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
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp>() {
                    @Override
                    public void onSuccess(Resp resp) {
                        SmartDialog.with(getActivity())
                                .setMessage(resp.getMsg())
                                .show();
                    }
                }).post();
    }

    @OnClick(R.id.addBankcardButton)
    void addBankcard() {

    }
}
