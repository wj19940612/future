package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.RechargeAsyncTask;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.CommonFailWarn;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity implements RechargeAsyncTask.RechargeListener {

    public static final String RESULT_BANKCARD_AUTH = "bankcardAuthResult";

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
//    @BindView(R.id.paymentGroup)
//    RadioGroup mPaymentGroup;
//
//    private RadioButton[] mPaymentButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

//        mPaymentButtons = new RadioButton[3];
//        int[] buttonTexts = new int[]{R.string.bankcard_payment, R.string.alipay_payment, R.string.wechat_payment};
//        for (int i = 0; i < mPaymentButtons.length; i++) {
//            mPaymentButtons[i] = createRadioButton(buttonTexts[i]);
//            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            mPaymentGroup.addView(mPaymentButtons[i], params);
//        }
        mRechargeAmount.addTextChangedListener(mValidationWatcher);
        checkBankcardAuth();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean validation = checkValidation();
            if (validation != mNextStepButton.isEnabled()) {
                mNextStepButton.setEnabled(validation);
            }
        }
    };

    private void checkBankcardAuth() {
        // TODO: 2016/9/13 判断银行卡信息，后面可能用到
//        final BankcardAuth bankcardAuth = (BankcardAuth) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
//        if (bankcardAuth.getStatus() == BankcardAuth.STATUS_NOT_FILLED ||
//                TextUtils.isEmpty(bankcardAuth.getPhone())) {
//            SmartDialog.with(getActivity(), R.string.dialog_your_bankcard_info_is_not_complete)
//                    .setCancelableOnTouchOutside(false)
//                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
//                        @Override
//                        public void onClick(Dialog dialog) {
//                            Launcher.with(getActivity(), BankcardAuthActivity.class)
//                                    .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
//                                    .executeForResult(REQUEST_CODE);
//                        }
//                    })
//                    .show();
//        }
    }

//    private RadioButton createRadioButton(int buttonText) {
//        RadioButton button = new RadioButton(this);
//        button.setText(buttonText);
//        return button;
//    }

    @OnClick({R.id.nextStepButton, R.id.bankCardPay, R.id.aliPayPay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextStepButton:
                submitRechargeNUmber();
                break;
            case R.id.bankCardPay:
                selectPayMethod(0);
                break;
            case R.id.aliPayPay:
                selectPayMethod(1);
                break;
        }
    }

    private void submitRechargeNUmber() {
        String rechargeNumber = mRechargeAmount.getText().toString().trim();
        double amount = Double.valueOf(rechargeNumber);
        if (amount < 50) {
            ToastUtil.show(getString(R.string.recharge_amount_should_larger_than_50));
            return;
        }
//        /user/finance/deposit.do
        if (checkValidation()) {
            new RechargeAsyncTask(amount, this).execute("http://newtest.jnhyxx.com/user/finance/deposit.do?money=" + amount);
            // TODO: 2016/9/20 返回的是html，不能直接解析
            API.Finance.rechargeMoney(amount).setTag(TAG).setIndeterminate(this).setCallback(new Callback1<Resp<String>>() {

                @Override
                protected void onRespSuccess(Resp<String> resp) {
                    String data = resp.getData();
                    Launcher.with(RechargeActivity.this, RechargeWebViewActivity.class).putExtra("url", data).execute();
                }
            }).fire();

            String ss = "http://newtest.jnhyxx.com/user/finance/deposit.do?money=" + amount;
            String url = "http://newtest.jnhyxx.com/user/finance/deposit.do?money=" + amount;
        }
    }

    private boolean checkValidation() {
        String rechargeAmount = ViewUtil.getTextTrim(mRechargeAmount);
        if (TextUtils.isEmpty(rechargeAmount)) {
            ToastUtil.show(getString(R.string.recharge_amount_cannot_be_empty));
            return false;
        }

//        double amount = Double.valueOf(rechargeAmount);
//        if (amount < 50) {
//            ToastUtil.show(getString(R.string.recharge_amount_should_larger_than_50));
//            return false;
//        }

        boolean hasPayment = false;
        // TODO: 2016/9/13 判断支付方式
//        for (int i = 0; i < mPaymentButtons.length; i++) {
//            if (mPaymentButtons[i].isChecked()) {
//                hasPayment = true;
//                break;
//            }
//        }
        for (int i = 0; i < mPayMethodMatherView.getChildCount(); i++) {
            if (mPayMethodMatherView.getChildAt(i).isSelected()) {
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

                API.User.getBankcardInfo(LocalUser.getUser().getToken()).setTag(TAG)
                        .setCallback(new Callback2<Resp<BankcardAuth>, BankcardAuth>() {
                            @Override
                            public void onRespSuccess(BankcardAuth bankcardAuth) {
                                if (bankcardAuth.getStatus() != BankcardAuth.STATUS_NOT_FILLED
                                        && !TextUtils.isEmpty(bankcardAuth.getPhone())) {
                                    SmartDialog.dismiss(getActivity());
                                }
                            }
                        }).fire();

            }
        }
    }

    private void selectPayMethod(int index) {
        if (index < 0 || index > 2) return;
        unSelectAll();
        mPayMethodMatherView.getChildAt(index).setSelected(true);
    }

    private void unSelectAll() {
        for (int i = 0; i < mPayMethodMatherView.getChildCount(); i++) {
            mPayMethodMatherView.getChildAt(i).setSelected(false);
        }
    }

    @Override
    public void getData(String result) {
        String data = null;
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                // TODO: 2016/9/21 目前是截取的数据，容易出问题 
                String message = e.getMessage();
                data = message.substring(message.indexOf("<"), message.lastIndexOf(">") + 1);
                Log.d(TAG, "截取后的充值界面的html代码" + data);
                e.printStackTrace();
            } finally {
                if (!TextUtils.isEmpty(data)) {
                    Launcher.with(RechargeActivity.this, RechargeWebViewActivity.class).putExtra("url", data).execute();
                }
            }

        }
    }
}
