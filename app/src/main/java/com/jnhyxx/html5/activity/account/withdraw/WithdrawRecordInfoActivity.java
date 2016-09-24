package com.jnhyxx.html5.activity.account.withdraw;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.finance.WithDrawRecordInfo;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawRecordInfoActivity extends BaseActivity {

    public static final String WITHDRAW_RECORD_INFO_ID = "withdrawInfo";


    /**
     * 表示刚刚发起
     */
    private static final String START_TRADE = "刚刚发起";
    /**
     * 1表示审批通过
     */
    private static final String AUDIT_PASSING = "审批通过";
    /**
     * 2表示转账中
     */
    private static final String FUND_TRANSFER = "转账中";
    /**
     * 3表示充提成功
     */
    private static final String RECHARGE_OR_WITHDRAW_SUCCESS = "提现成功";
    /**
     * 4表示提现拒绝
     */
    private static final String WITHDRAW_refuse = "提现拒绝";
    /**
     * 5表示转账失败
     */
    private static final String TRANSFER_FAIL = "转账失败";


    private static final String SUCCESS = "成功";

    private static final String FAIL = "失败";

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    // TODO: 2016/9/22 原来的布局，不符合UI
    @BindView(R.id.applyFor)
    TextView mApplyFor;
    @BindView(R.id.leftLine)
    TextView mLeftLine;
    @BindView(R.id.detail)
    TextView mDetail;
    @BindView(R.id.rightLine)
    TextView mRightLine;
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
    //    @BindView(R.id.leftLine)
//    TextView mLeftLine;
//    @BindView(R.id.rightLine)
//    TextView mRightLine;
//    @BindView(R.id.handleResultImg)
//    ImageView mHandleResultImg;
//    @BindView(R.id.handleResultTxt)
//    TextView mHandleResultTxt;
//    @BindView(R.id.moneyNumber)
//    TextView mMoneyNumber;
//    @BindView(R.id.withdrawStatus)
//    TextView mWithdrawStatus;
//    @BindView(R.id.realAccount)
//    TextView mRealAccount;
//    @BindView(R.id.poundageNumber)
//    TextView mPoundageNumber;
//    @BindView(R.id.bankName)
//    TextView mBankName;
//    @BindView(R.id.time)
//    TextView mTime;
//    @BindView(R.id.accountTime)
//    TextView mAccountTime;
    @BindView(R.id.activity_withdraw_record_info)
    LinearLayout mActivityWithdrawRecordInfo;


    private int mWithDrawId;
    private WithDrawRecordInfo mWithDrawRecordInfo;
    //代表是否完成
    private boolean mCompleteStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record_info);
        ButterKnife.bind(this);

        mWithDrawId = getIntent().getIntExtra(WITHDRAW_RECORD_INFO_ID, -1);


        API.Finance.getWithdrawRecordInfo(-1, mWithDrawId).setTag(TAG).setIndeterminate(this).setCallback(new Callback1<Resp<WithDrawRecordInfo>>() {

            @Override
            protected void onRespSuccess(Resp<WithDrawRecordInfo> resp) {
                mWithDrawRecordInfo = resp.getData();

                updateView(mWithDrawRecordInfo);
            }
        }).fire();

    }


    private void updateView(WithDrawRecordInfo withDrawRecordInfo) {
        // TODO: 2016/9/22 原来的方法，不符合UI,后面可能会用到
        if (withDrawRecordInfo == null) return;
            Drawable mNormalDrawable = getResources().getDrawable(R.drawable.ic_apply_normal);
            mNormalDrawable.setBounds(0, 0, mNormalDrawable.getMinimumWidth(), mNormalDrawable.getMinimumHeight());

            Drawable mFailDrawable = getResources().getDrawable(R.drawable.ic_apply_fail);
            mFailDrawable.setBounds(0, 0, mFailDrawable.getMinimumWidth(), mFailDrawable.getMinimumHeight());

            Drawable mSuccessDrawable = getResources().getDrawable(R.drawable.ic_apply_succeed);
            mSuccessDrawable.setBounds(0, 0, mSuccessDrawable.getMinimumWidth(), mSuccessDrawable.getMinimumHeight());

        if (withDrawRecordInfo == null) return;

        //刚刚发起
        if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.START_TRADE) {
            mCompleteStatus = false;
            mWithdrawStatus.setText(START_TRADE);
            //审批通过
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.AUDIT_PASSING) {
            mCompleteStatus = false;
            mWithdrawStatus.setText(AUDIT_PASSING);
            //转账中
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.FUND_TRANSFER) {
            mCompleteStatus = false;
            mWithdrawStatus.setText(FUND_TRANSFER);

            //冲提成功
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.RECHARGE_OR_WITHDRAW_SUCCESS) {
            mCompleteStatus = true;
            mWithdrawStatus.setText(SUCCESS);
            mWithdrawTitleStatus.setCompoundDrawables(null, mSuccessDrawable, null, null);
//            mHandleResultImg.setImageResource(R.drawable.ic_apply_succeed);
//            mHandleResultTxt.setText(RECHARGE_OR_WITHDRAW_SUCCESS);
            mLeftLine.setEnabled(true);
            mRightLine.setEnabled(true);
            mWithdrawTitleStatus.setText(SUCCESS);
            //提现拒绝
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.WITHDRAW_refuse) {
            mCompleteStatus = true;
            mWithdrawStatus.setText(WITHDRAW_refuse);
//            mHandleResultImg.setImageResource(R.drawable.ic_apply_fail);
//            mHandleResultTxt.setText(FAIL);
            mWithdrawTitleStatus.setText(FAIL);
            mWithdrawTitleStatus.setCompoundDrawables(null, mFailDrawable, null, null);
            //转账失败
        } else if (withDrawRecordInfo.getStatus() == WithDrawRecordInfo.TRANSFER_FAIL) {
            mCompleteStatus = true;
            mWithdrawStatus.setText(TRANSFER_FAIL);
//            mHandleResultImg.setImageResource(R.drawable.ic_apply_fail);
//            mHandleResultTxt.setText(FAIL);
            mWithdrawTitleStatus.setText(FAIL);
            mWithdrawTitleStatus.setCompoundDrawables(null, mFailDrawable, null, null);
        } else {
            mCompleteStatus = false;
            Log.d(TAG, "withdraw record info status is has not konwn status");
        }

        mMoneyNumber.setText(getString(R.string.withdraw_record_number, String.valueOf(withDrawRecordInfo.getMoney())));

        //实际到账
        mRealAccount.setText(getString(R.string.withdraw_record_real_account, String.valueOf(withDrawRecordInfo.getMoney() - withDrawRecordInfo.getCommission())));
        //手续费
        mPoundageNumber.setText(getString(R.string.withdraw_record_poundage, String.valueOf(withDrawRecordInfo.getCommission())));


        String cardNumber = withDrawRecordInfo.getCardNumber();
        cardNumber = cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        String bankName = withDrawRecordInfo.getIssuingBankName();
        mBankName.setText(getString(R.string.withdraw_record_bank, bankName, cardNumber));

        String createTime = withDrawRecordInfo.getCreateTime();
        createTime = createTime.substring(0, createTime.lastIndexOf(":"));
        mTime.setText(createTime);

        // TODO: 2016/9/22 到账时间无法确定，目前填写的是更新时间 
        mAccountTime.setText(withDrawRecordInfo.getUpdateTime());
    }
}
