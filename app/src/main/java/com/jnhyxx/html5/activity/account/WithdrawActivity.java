package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.withdrawAmount)
    EditText mWithdrawAmount;
    @BindView(R.id.allWithdraw)
    TextView mAllWithdraw;
    @BindView(R.id.bankcardInfoArea)
    LinearLayout mBankcardInfoArea;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;
    private double mMoneyDrawUsable;
    private UserFundInfo userFundInfo;

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
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.WITHDRAW_RECORD);
                Launcher.with(getActivity(), WithdrawRecordActivity.class).execute();
            }
        });

        mWithdrawAmount.addTextChangedListener(mValidationWatcher);

        getMoneyDrawUsable();
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
                        mWithdrawAmount.setHint(String.valueOf(mMoneyDrawUsable));
                    }
                }).fire();
    }

    @OnClick(R.id.confirmButton)
    void doConfirmButtonClick() {
        String withdrawAmount = ViewUtil.getTextTrim(mWithdrawAmount);
        if (!TextUtils.isEmpty(withdrawAmount)) {
            MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.WITHDRAW_OK);
            final double amount = Double.valueOf(withdrawAmount);
            API.Finance.withdraw(amount)
                    .setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        public void onReceive(Resp resp) {
                            if (resp.isSuccess()) {
                                updateUserInfoBalance(amount);

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
                    }).fire();
        }
    }

    private void updateUserInfoBalance(double withdrawAmount) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        userInfo.setMoneyUsable(FinanceUtil.subtraction(userFundInfo.getMoneyUsable(), withdrawAmount).doubleValue());
        mWithdrawAmount.setHint(FinanceUtil.formatWithScale(FinanceUtil.subtraction(mMoneyDrawUsable, withdrawAmount).doubleValue()));
    }

    @OnClick({R.id.withdrawRule, R.id.withdrawRecord, R.id.allWithdraw, R.id.confirmButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdrawRule:
                break;
            case R.id.withdrawRecord:
                break;
            case R.id.allWithdraw:
                break;
            case R.id.confirmButton:
                break;
        }
    }
}
