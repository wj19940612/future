package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity {

    private static final int REQ_CODE_REAL_NAME_VERIFY = 1;
    private static final int REQ_CODE_BANKCARD_BINDING = 2;

    @BindView(R.id.nextStepButton)
    TextView mNextStepButton;
    @BindView(R.id.rechargeAmount)
    EditText mRechargeAmount;
    @BindView(R.id.bankCardPay)
    RelativeLayout mBankCardPay;
    @BindView(R.id.aliPayPay)
    RelativeLayout mAliPayPay;
    @BindView(R.id.payMethodMatherView)
    LinearLayout mPayMethodMatherView;
    @BindView(R.id.commonFail)
    CommonFailWarn mCommonFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        mRechargeAmount.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkNextStepButtonEnable();
            if (enable != mNextStepButton.isEnabled()) {
                mNextStepButton.setEnabled(enable);
            }
        }
    };

    @OnClick({R.id.nextStepButton, R.id.bankCardPay, R.id.aliPayPay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextStepButton:
                doNextStepButtonClick();
                break;
            case R.id.bankCardPay:
                selectPayMethod(0);
                break;
            case R.id.aliPayPay:
                selectPayMethod(1);
                break;
        }
    }

    private void doNextStepButtonClick() {
        // TODO: 9/27/16 如果选择银行卡支付,要求完成实名认证和银行卡绑定,其他支付方式有待实现
        if (isBankcardPaymentSelected()) {
            doBankcardPayment();
        } else {

        }
    }

    private void doBankcardPayment() {
        if (!LocalUser.getUser().isRealNameFilled()) {
            Launcher.with(this, NameVerifyActivity.class).executeForResult(REQ_CODE_REAL_NAME_VERIFY);
        }

        if (!LocalUser.getUser().isBankcardFilled()) {
            Launcher.with(this, BankcardBindingActivity.class).executeForResult(REQ_CODE_BANKCARD_BINDING);
        }

        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        API.Finance.rechargeMoney(amount)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<String>() {
                    @Override
                    public void onReceive(String s) {
                        s = s.substring(1, s.length() - 1).replace("\\\"", "\"");
                        Launcher.with(getActivity(), RechargeWebViewActivity.class)
                                .putExtra("url", s).execute();
                    }
                }).fire();
    }

    private boolean isBankcardPaymentSelected() {
        if (mPayMethodMatherView.getChildAt(0).isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkNextStepButtonEnable() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        if (TextUtils.isEmpty(rechargeAmount) || amount < 50) {
            return false;
        }

        boolean hasPayment = false;
        for (int i = 0; i < mPayMethodMatherView.getChildCount(); i++) {
            if (mPayMethodMatherView.getChildAt(i).isSelected()) {
                hasPayment = true;
                break;
            }
        }
        if (!hasPayment) {
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_REAL_NAME_VERIFY && resultCode == RESULT_OK) {
            doBankcardPayment();
        }
        if (requestCode == REQ_CODE_BANKCARD_BINDING && resultCode == RESULT_OK) {
            doBankcardPayment();
        }
    }

    private void selectPayMethod(int index) {
        if (index < 0 || index > 2) return;

        unselectAll();

        mPayMethodMatherView.getChildAt(index).setSelected(true);

        boolean enable = checkNextStepButtonEnable();
        if (enable != mNextStepButton.isEnabled()) {
            mNextStepButton.setEnabled(enable);
        }
    }

    private void unselectAll() {
        for (int i = 0; i < mPayMethodMatherView.getChildCount(); i++) {
            mPayMethodMatherView.getChildAt(i).setSelected(false);
        }
    }
}
