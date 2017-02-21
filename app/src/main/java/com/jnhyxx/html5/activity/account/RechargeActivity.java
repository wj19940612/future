package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.web.PaymentActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.finance.SupportApplyWay;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.fragment.dialog.SelectRechargeWayDialogFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.fragment.dialog.SelectRechargeWayDialogFragment.PAY_WAY_ALIPAY;
import static com.jnhyxx.html5.fragment.dialog.SelectRechargeWayDialogFragment.PAY_WAY_BANK;
import static com.jnhyxx.html5.fragment.dialog.SelectRechargeWayDialogFragment.PAY_WAY_WECHAT;

public class RechargeActivity extends BaseActivity implements SelectRechargeWayDialogFragment.onPayWayListener {


    private static final int APPLY_LIMIT = 10000;
    @BindView(R.id.paymentHint)
    TextView mPaymentHint;
    @BindView(R.id.commonFail)
    CommonFailWarn mCommonFail;
    @BindView(R.id.payWayLayout)
    RelativeLayout mPayWayLayout;
    @BindView(R.id.rechargeAmount)
    EditText mRechargeAmount;
    @BindView(R.id.nextStepButton)
    TextView mNextStepButton;
    @BindView(R.id.bankCardName)
    TextView mBankCard;
    @BindView(R.id.bankCardSingleLimit)
    TextView mBankCardSingleLimit;

    private static final int REQUEST_CODE_BANK_PAY = 10000;
    private static final int REQUEST_CODE_APPLY_PAY = 6210;
    //打开同意支付协议页面
    private static final int REQ_CODE_AGREE_PAYMENT = 286;


    SupportApplyWay mSupportApplyWay;

    /**
     * 支付方式
     */
    private int mPayWay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        mRechargeAmount.addTextChangedListener(mValidationWatcher);

        mSupportApplyWay = (SupportApplyWay) getIntent().getSerializableExtra(Launcher.EX_PAYLOAD);
        handlePayWay(Preference.get().getRechargePayWay());

        if (LocalUser.getUser().isBankcardFilled()) {
            getUserBankSingleLimitAndIsOpenPayPage(false);
        }
    }

    private void getUserBankSingleLimitAndIsOpenPayPage(final boolean isOpenPayPage) {
        API.User.getUserBankInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<UserInfo>>() {

                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        updateUserBankInfo(resp);
                        updateView();
                        if (isOpenPayPage) {
                            depositByBankApply();
                        }
                    }
                })
                .fireSync();
    }

    private void updateView() {
        if (Preference.get().getRechargePayWay() == PAY_WAY_BANK) {
            if (!LocalUser.getUser().isBankcardFilled()) {
                mBankCard.setText(R.string.bankcard);
            } else {
                updateBankNameAndBankLimit();
            }
        }
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


    public String getBankNameAndBankCard() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        String cardNumber = userInfo.getCardNumber();
        cardNumber = cardNumber.substring(cardNumber.length() - 4);
        return getString(R.string.bank_name, userInfo.getIssuingbankName(), cardNumber);
    }


    /**
     * 如果未绑定银行卡或者支付宝/微信支付  显示支付名称
     * 如果绑定银行卡并使用银行卡支付，则显示对应的卡和限制额度
     *
     * @param
     * @param
     * @param
     */
    private void updateBankNameAndBankLimit() {
        Log.d(TAG,"限制e度  "+LocalUser.getUser().getUserInfo().getLimitSingle());
        mBankCardSingleLimit.setVisibility(View.VISIBLE);
        String bankSingleLimit = getString(R.string.once_recharge_limit, FinanceUtil.formatWithThousandsSeparator(LocalUser.getUser().getUserInfo().getLimitSingle()));
        mBankCardSingleLimit.setText(bankSingleLimit);
        mBankCard.setText(getBankNameAndBankCard());
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

    public boolean isAliPayPay() {
        if (mPayWay == PAY_WAY_ALIPAY) {
            return true;
        }
        return false;
    }

    @OnClick({R.id.payWayLayout, R.id.nextStepButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payWayLayout:
                SelectRechargeWayDialogFragment.newInstance(mSupportApplyWay).show(getSupportFragmentManager());
                break;
            case R.id.nextStepButton:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.RECHARGE_SUBMIT);
                doNextStepButtonClick();
                break;
        }
    }

    private void doNextStepButtonClick() {
        API.Finance.isUserAgreePayment()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<Boolean>>() {

                    @Override
                    protected void onRespSuccess(Resp<Boolean> resp) {
                        if (resp.isSuccess() && resp.hasData()) {
                            Log.d(TAG, "签署协议" + resp.getData());
                            if (resp.getData()) {
                                doPayment();
                            } else {
                                Launcher.with(getActivity(), PaymentActivity.class)
                                        .putExtra(PaymentActivity.EX_URL, API.Finance.getUserAgreePaymentPagePath())
                                        .putExtra(PaymentActivity.EX_TITLE, getString(R.string.recharge_agree_payment))
                                        .putExtra(PaymentActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                        .executeForResult(REQ_CODE_AGREE_PAYMENT);
                            }
                        }
                    }
                })
                .fire();
    }

    private void doPayment() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        if (amount < 50) {
            mCommonFail.show(R.string.recharge_once_least_limit);
            return;
        }
        switch (mPayWay) {
            case PAY_WAY_BANK:
                depositByBankApply();
                break;
            case PAY_WAY_ALIPAY:
                depositByAliPay();
                break;
            case PAY_WAY_WECHAT:
                depositByWeChartApply();
                break;
            default:
                break;
        }
    }

    private void depositByBankApply() {

        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        if (!LocalUser.getUser().isBankcardFilled()) {
            Launcher.with(this, BankcardBindingActivity.class)
                    .executeForResult(BankcardBindingActivity.REQ_CODE_BIND_BANK);
            return;
        } else if (amount > LocalUser.getUser().getUserInfo().getLimitSingle()) {
            mCommonFail.show(R.string.recharge_bank_apply_limit);
            return;
        }

        Launcher.with(getActivity(), PaymentActivity.class)
                .putExtra(PaymentActivity.BANK_CARD_PAYMENT, true)
                .putExtra(PaymentActivity.EX_URL, API.Finance.depositByBankApply(amount))
                .putExtra(PaymentActivity.EX_TITLE, getString(R.string.recharge))
                .putExtra(PaymentActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                .executeForResult(REQUEST_CODE_BANK_PAY);
    }

    private void depositByAliPay() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        if (amount > APPLY_LIMIT) {
            mCommonFail.show(R.string.recharge_apply_limit);
            mNextStepButton.setEnabled(false);
            return;
        }
        Launcher.with(getActivity(), PaymentActivity.class)
                .putExtra(PaymentActivity.EX_URL, API.Finance.depositByAliPay(amount))
                .putExtra(PaymentActivity.EX_TITLE, getString(R.string.recharge))
                .putExtra(PaymentActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                .executeForResult(REQUEST_CODE_APPLY_PAY);
    }

    private void depositByWeChartApply() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        double amount = Double.valueOf(rechargeAmount);
        Launcher.with(getActivity(), PaymentActivity.class)
                .putExtra(PaymentActivity.EX_URL, API.Finance.depositByWeChartApply(amount))
                .putExtra(PaymentActivity.EX_TITLE, getString(R.string.recharge))
                .putExtra(PaymentActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                .execute();
    }

    private boolean checkNextStepButtonEnable() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        if (TextUtils.isEmpty(rechargeAmount)) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BankcardBindingActivity.REQ_CODE_BIND_BANK && resultCode == RESULT_OK) {
            updateView();
            getUserBankSingleLimitAndIsOpenPayPage(true);
        }
        if (requestCode == REQUEST_CODE_BANK_PAY && resultCode == RESULT_OK) {
            finish();
        }

        if (requestCode == REQUEST_CODE_APPLY_PAY && resultCode == RESULT_OK) {
            finish();
        }

        if (requestCode == REQ_CODE_AGREE_PAYMENT && resultCode == RESULT_OK) {
            doPayment();
        }
    }

    @Override
    public void selectPayWay(int payWay) {
        handlePayWay(payWay);
    }

    private void handlePayWay(int payWay) {
        mPayWay = payWay;
        switch (mPayWay) {
            case PAY_WAY_BANK:
                if (LocalUser.getUser().isBankcardFilled()) {
                    updateBankNameAndBankLimit();
                } else {
                    mBankCardSingleLimit.setVisibility(View.GONE);
                    mBankCard.setText(R.string.bankcard);
                }
                break;
            case PAY_WAY_ALIPAY:
                mBankCardSingleLimit.setVisibility(View.GONE);
                mBankCard.setText(R.string.aliPay_pay);
                break;
            case PAY_WAY_WECHAT:
                mBankCardSingleLimit.setVisibility(View.GONE);
                mBankCard.setText(R.string.wechat_pay);
                break;
            default:
                break;
        }
    }
}
