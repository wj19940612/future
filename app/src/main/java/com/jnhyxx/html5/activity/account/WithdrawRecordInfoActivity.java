package com.jnhyxx.html5.activity.account;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.finance.WithDrawRecordInfo;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawRecordInfoActivity extends BaseActivity {

    public static final String WITHDRAW_RECORD_INFO_ID = "withdrawInfo";

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.applyFor)
    TextView mApplyFor;
    //    @BindView(R.id.detail)
//    TextView mDetail;
    @BindView(R.id.auditing)
    TextView mAuditing;
    @BindView(R.id.transfer)
    TextView mTransfer;
    @BindView(R.id.withdrawTitleStatus)
    TextView mWithdrawTitleStatus;
    @BindView(R.id.moneyNumber)
    TextView mMoneyNumber;
    @BindView(R.id.withdrawStatus)
    TextView mWithdrawStatus;
    @BindView(R.id.realAccount)
    TextView mRealAccount;
    @BindView(R.id.poundageNumber)
    TextView mPoundageNumber;
    @BindView(R.id.bankName)
    TextView mBankName;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.accountTime)
    TextView mAccountTime;
    @BindView(R.id.activity_withdraw_record_info)
    LinearLayout mActivityWithdrawRecordInfo;
    @BindView(R.id.accountTimeHint)
    TextView mAccountTimeHint;
    @BindView(R.id.dashLine1)
    TextView mDashLine1;
    @BindView(R.id.dashLine2)
    TextView mDashLine2;
    @BindView(R.id.dashLine3)
    TextView mDashLine3;


    private int mWithDrawId;
    private WithDrawRecordInfo mWithDrawRecordInfo;
    //代表是否完成
    private boolean mCompleteStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record_info);
        ButterKnife.bind(this);

        //shape虚线显示实线问题
        mDashLine1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mDashLine2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mDashLine3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWithDrawId = getIntent().getIntExtra(WITHDRAW_RECORD_INFO_ID, -1);


        API.Finance.getWithdrawRecordInfo(-1, mWithDrawId)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback1<Resp<WithDrawRecordInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WithDrawRecordInfo> resp) {
                        mWithDrawRecordInfo = resp.getData();

                        updateView(mWithDrawRecordInfo);
                    }
                }).fire();

    }


    private void updateView(WithDrawRecordInfo withDrawRecordInfo) {
        if (withDrawRecordInfo == null) return;
        Drawable mNormalDrawable = ContextCompat.getDrawable(this, R.drawable.ic_apply_normal);
        mNormalDrawable.setBounds(0, 0, mNormalDrawable.getMinimumWidth(), mNormalDrawable.getMinimumHeight());

        Drawable mFailDrawable = ContextCompat.getDrawable(this, R.drawable.ic_apply_fail);
        mFailDrawable.setBounds(0, 0, mFailDrawable.getMinimumWidth(), mFailDrawable.getMinimumHeight());

        Drawable mSuccessDrawable = ContextCompat.getDrawable(this, R.drawable.ic_apply_succeed);
        mSuccessDrawable.setBounds(0, 0, mSuccessDrawable.getMinimumWidth(), mSuccessDrawable.getMinimumHeight());

        if (WithDrawRecordInfo.isTransfer(withDrawRecordInfo.getStatus())) {
            mTransfer.setEnabled(true);
        }

        //刚刚发起
        if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.START_TRADE) {
            mCompleteStatus = false;
            mWithdrawStatus.setText(R.string.withdraw_auditing);
            //审批通过
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.AUDIT_PASSING) {
            mCompleteStatus = false;
            mWithdrawStatus.setText(R.string.transfer);
            //转账中
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.FUND_TRANSFER) {
            mCompleteStatus = false;
            mWithdrawStatus.setText(R.string.transfer);
            //冲提成功
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.RECHARGE_OR_WITHDRAW_SUCCESS) {
            mCompleteStatus = true;
            mWithdrawStatus.setText(R.string.withdraw_record_success);
            mWithdrawTitleStatus.setCompoundDrawables(null, mSuccessDrawable, null, null);
            mWithdrawTitleStatus.setText(R.string.withdraw_record_success);

            mAccountTimeHint.setVisibility(View.GONE);
            mAccountTime.setVisibility(View.VISIBLE);
            mAccountTime.setText(DateUtil.format(withDrawRecordInfo.getUpdateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm"));
            //提现拒绝
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.WITHDRAW_refuse) {
            mCompleteStatus = true;
            mWithdrawStatus.setText(R.string.withdraw_fail);
            mWithdrawTitleStatus.setText(R.string.withdraw_fail);
            mWithdrawTitleStatus.setCompoundDrawables(null, mFailDrawable, null, null);
            //转账失败
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.TRANSFER_FAIL) {
            mCompleteStatus = true;
            mWithdrawStatus.setText(R.string.withdraw_fail);
            mWithdrawTitleStatus.setText(R.string.withdraw_fail);
            mWithdrawTitleStatus.setCompoundDrawables(null, mFailDrawable, null, null);
        } else {
            mCompleteStatus = false;
            Log.d(TAG, "withdraw record info status is has not konwn status");
        }

        mMoneyNumber.setText(getString(R.string.withdraw_record_number, FinanceUtil.formatWithScale(withDrawRecordInfo.getMoney())));

        //实际到账
        mRealAccount.setText(getString(R.string.withdraw_record_real_account, FinanceUtil.formatWithScale(withDrawRecordInfo.getMoney() - withDrawRecordInfo.getCommission())));
        //手续费
        mPoundageNumber.setText(getString(R.string.withdraw_record_poundage, FinanceUtil.formatWithScale(withDrawRecordInfo.getCommission())));


        String cardNumber = withDrawRecordInfo.getCardNumber();
        cardNumber = cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        String bankName = withDrawRecordInfo.getIssuingBankName();
        mBankName.setText(getString(R.string.withdraw_record_bank, bankName, cardNumber));

        String createTime = withDrawRecordInfo.getCreateTime();
        String withdrawTime = DateUtil.format(createTime, DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm");
        mTime.setText(withdrawTime);


    }
}
