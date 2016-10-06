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
import com.jnhyxx.html5.activity.web.LoadLocalDataWebViewActivity;
import com.jnhyxx.html5.domain.finance.SupportApplyWay;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.johnz.kutils.net.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity {

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
    @BindView(R.id.weChartPay)
    RelativeLayout mWeChartPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        mRechargeAmount.addTextChangedListener(mValidationWatcher);

        API.Finance.getSupportApplyWay()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<SupportApplyWay>>() {
                    @Override
                    public void onReceive(Resp<SupportApplyWay> supportApplyWayResp) {
                        updateView(supportApplyWayResp.getData());
                    }

                }).fire();
    }

    private void updateView(SupportApplyWay supportApplyWay) {
        if (supportApplyWay.isBank()) {
            mBankCardPay.setVisibility(View.VISIBLE);
        } else {
            mBankCardPay.setVisibility(View.GONE);
        }
        // TODO: 2016/9/28  目前支付宝在测试环境不可用，微信返回的是乱码
        if (supportApplyWay.isAlipay()) {
            mAliPayPay.setVisibility(View.VISIBLE);
        } else {
            mAliPayPay.setVisibility(View.GONE);
        }
        // TODO: 2016/9/29 微信支付必须在微信环境下，目前没有接入sdk 
//        if (supportApplyWay.isWechat()) {
//            mWeChartPay.setVisibility(View.VISIBLE);
//        } else {
//            mWeChartPay.setVisibility(View.GONE);
//        }
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

    @OnClick({R.id.nextStepButton, R.id.bankCardPay, R.id.aliPayPay, R.id.weChartPay})
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
            case R.id.weChartPay:
                selectPayMethod(2);
                break;
        }
    }

    private int getSelectedView() {
        for (int i = 0; i < mPayMethodMatherView.getChildCount(); i++) {
            if (mPayMethodMatherView.getChildAt(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    private void doNextStepButtonClick() {
        // TODO: 9/27/16 如果选择银行卡支付,要求完成实名认证和银行卡绑定,其他支付方式有待实现
//        if (isBankcardPaymentSelected()) {
        doBankcardPayment();
//        } else {
//
//        }
    }

    private void doBankcardPayment() {
        if (!LocalUser.getUser().isRealNameFilled()) {
            Launcher.with(this, NameVerifyActivity.class)
                    .executeForResult(REQ_CODE_BASE);
            return;
        }

        if (!LocalUser.getUser().isBankcardFilled()) {
            Launcher.with(this, BankcardBindingActivity.class)
                    .executeForResult(REQ_CODE_BASE);
            return;
        }

        int selectedView = getSelectedView();
        if (selectedView == -1) return;
        switch (selectedView) {
            case SupportApplyWay.DEPOSIT_BY_BANK_APPLY_PAY:
                depositByBankApply();
                break;
            case SupportApplyWay.DEPOSIT_BY_ALI_PAY_PAY:
                depositByAliPay();
                break;
            case SupportApplyWay.DEPOSIT_BY_BANK_WE_CHART_PAY:
                depositByWeChartApply();
                break;
        }
    }

    private void depositByBankApply() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        API.Finance.depositByBankApply(amount)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<String>() {
                    @Override
                    public void onReceive(String s) {
                        s = s.substring(1, s.length() - 1).replace("\\\"", "\"");
                        Launcher.with(getActivity(), LoadLocalDataWebViewActivity.class)
                                .putExtra(LoadLocalDataWebViewActivity.EX_URL, s)
                                .putExtra(LoadLocalDataWebViewActivity.EX_TITLE, getString(R.string.recharge))
                                .putExtra(LoadLocalDataWebViewActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }

    private void depositByAliPay() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        API.Finance.depositByAliPay(amount, SupportApplyWay.ALI_PAY_DEPOSIT_ANDROID)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<String>() {
                    @Override
                    public void onReceive(String s) {
                        s = s.substring(1, s.length() - 1).replace("\\\"", "\"");
                        Launcher.with(getActivity(), LoadLocalDataWebViewActivity.class)
                                .putExtra(LoadLocalDataWebViewActivity.EX_URL, s)
                                .putExtra(LoadLocalDataWebViewActivity.EX_TITLE, getString(R.string.recharge))
                                .putExtra(LoadLocalDataWebViewActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }

    private void depositByWeChartApply() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        API.Finance.depositByWeChartApply(amount)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<String>() {
                    @Override
                    public void onReceive(String s) {
                        s = s.substring(1, s.length() - 1).replace("\\\"", "\"");
                        Launcher.with(getActivity(), LoadLocalDataWebViewActivity.class)
                                .putExtra(LoadLocalDataWebViewActivity.EX_URL, s)
                                .putExtra(LoadLocalDataWebViewActivity.EX_TITLE, getString(R.string.recharge))
                                .putExtra(LoadLocalDataWebViewActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
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
        if (TextUtils.isEmpty(rechargeAmount)) {
            return false;
        }

        double amount = Double.valueOf(rechargeAmount);
        if (amount < 50) {
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
        if (requestCode == REQ_CODE_BASE && resultCode == RESULT_OK) {
            doBankcardPayment();
        }
    }

    private void selectPayMethod(int index) {
        if (index < 0 || index > 3) return;
        unSelectAll();

        mPayMethodMatherView.getChildAt(index).setSelected(true);

        boolean enable = checkNextStepButtonEnable();
        if (enable != mNextStepButton.isEnabled()) {
            mNextStepButton.setEnabled(enable);
        }
    }

    private void unSelectAll() {
        for (int i = 0; i < mPayMethodMatherView.getChildCount(); i++) {
            mPayMethodMatherView.getChildAt(i).setSelected(false);
        }
    }
}
