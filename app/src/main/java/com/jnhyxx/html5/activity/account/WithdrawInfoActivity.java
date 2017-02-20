package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.view.IconTextRow;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawInfoActivity extends BaseActivity {


    @BindView(R.id.amountToAccount)
    IconTextRow mAmountToAccount;
    @BindView(R.id.poundage)
    IconTextRow mPoundage;
    @BindView(R.id.the_account_to_the_bank)
    IconTextRow mTheAccountToTheBank;
    @BindView(R.id.confirmButton)
    TextView mConfirmButton;
    @BindView(R.id.dashLine1)
    TextView mDashLine1;
    @BindView(R.id.dashLine2)
    TextView mDashLine2;
    @BindView(R.id.dashLine3)
    TextView mDashLine3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_info);
        ButterKnife.bind(this);

        //shape虚线显示实线问题
        mDashLine1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mDashLine2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mDashLine3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Intent intent = getIntent();
        double amount = intent.getDoubleExtra(Launcher.EX_PAYLOAD, 0);
        double poundage = intent.getDoubleExtra(Launcher.EX_PAYLOAD_1, 0);
        mPoundage.setSubText(getString(R.string.withdraw_money, FinanceUtil.formatWithScale(poundage)));
        mAmountToAccount.setSubText(getString(R.string.withdraw_money, FinanceUtil.formatWithScale(FinanceUtil.subtraction(amount, poundage).doubleValue())));

        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        String bankCardEndNumber = userInfo.getCardNumber().substring(userInfo.getCardNumber().length() - 4);
        mTheAccountToTheBank.setSubText(getString(R.string.bank_name_card_number, userInfo.getIssuingbankName(), bankCardEndNumber));

    }

    @OnClick(R.id.confirmButton)
    public void onClick() {
        setResult(RESULT_OK);
        finish();
    }
}
