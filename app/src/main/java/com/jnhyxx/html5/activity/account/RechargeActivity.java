package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity {

    public static final String RESULT_BANKCARD_AUTH = "bankcardAuthResult";

    @BindView(R.id.nextStepButton)
    TextView mNextStepButton;
    @BindView(R.id.rechargeAmount)
    EditText mRechargeAmount;
    @BindView(R.id.paymentGroup)
    RadioGroup mPaymentGroup;

    private RadioButton[] mPaymentButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        mPaymentButtons = new RadioButton[3];
        int[] buttonTexts = new int[]{R.string.bankcard_payment, R.string.alipay_payment, R.string.wechat_payment};
        for (int i = 0; i < mPaymentButtons.length; i++) {
            mPaymentButtons[i] = createRadioButton(buttonTexts[i]);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mPaymentGroup.addView(mPaymentButtons[i], params);
        }

        checkBankcardAuth(getIntent());
    }

    private void checkBankcardAuth(Intent intent) {
        final BankcardAuth bankcardAuth = (BankcardAuth) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        if (bankcardAuth.getStatus() == BankcardAuth.STATUS_NOT_FILLED ||
                TextUtils.isEmpty(bankcardAuth.getPhone())) {
            SmartDialog.with(getActivity(), R.string.dialog_your_bankcard_info_is_not_complete)
                    .setCancelableOnTouchOutside(false)
                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            Launcher.with(getActivity(), BankcardAuthActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
                                    .executeForResult(REQUEST_CODE);
                        }
                    })
                    .show();
        }
    }

    private RadioButton createRadioButton(int buttonText) {
        RadioButton button = new RadioButton(this);
        button.setText(buttonText);
        return button;
    }

    @OnClick(R.id.nextStepButton)
    public void onClick() {
        if (checkValidation()) {

        }
    }

    private boolean checkValidation() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        if (TextUtils.isEmpty(rechargeAmount)) {
            ToastUtil.show(getString(R.string.recharge_amount_cannot_be_empty));
            return false;
        }

        double amount = Double.valueOf(rechargeAmount);
        if (amount < 50) {
            ToastUtil.show(getString(R.string.recharge_amount_should_larger_than_50));
            return false;
        }

        boolean hasPayment = false;
        for (int i = 0; i < mPaymentButtons.length; i++) {
            if (mPaymentButtons[i].isChecked()) {
                hasPayment = true;
                break;
            }
        }
        if (!hasPayment) {
            ToastUtil.show(getString(R.string.payments_cannot_be_empty));
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            BankcardAuth bankcardAuth = (BankcardAuth) data.getSerializableExtra(RESULT_BANKCARD_AUTH);
            if (bankcardAuth.getStatus() != BankcardAuth.STATUS_NOT_FILLED) {

                API.Account.getBankcardInfo(User.getUser().getToken()).setTag(TAG)
                        .setCallback(new Resp.Callback<Resp<BankcardAuth>, BankcardAuth>() {
                            @Override
                            public void onRespReceive(BankcardAuth bankcardAuth) {
                                if (bankcardAuth.getStatus() != BankcardAuth.STATUS_NOT_FILLED
                                    && !TextUtils.isEmpty(bankcardAuth.getPhone())) {
                                    SmartDialog.dismiss(getActivity());
                                }
                            }
                        }).post();

            }
        }
    }
}
