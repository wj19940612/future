package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandMakeMoneyActivity extends BaseActivity {
    //可提佣金数目
    @BindView(R.id.account_expand_make_money_brokerage_number)
    TextView mTvBrokerageNumber;
    //提取佣金按钮
    @BindView(R.id.accountExpandMakeMoneyBrokeragePickUp)
    TextView mTvBrokeragePickUp;
    //代理等级提示 金、银、铜三个等级
    @BindView(R.id.accountExpandMakeMoneyProxyGrade)
    TextView mTvProxyGrade;
    //我的用户的数量
    @BindView(R.id.accountExpandMakeMoneyMineAccount)
    TextView mTvMineAccount;
    //佣金比例
    @BindView(R.id.accountExpandMakeMoneyBrokerageScleNumber)
    TextView mTvBrokerageScle;
    //交易用户数量
    @BindView(R.id.accountExpandMakeMoneyExchangeAccountNumber)
    TextView mtvExchangeAccountNumber;
    //二维码图片，长按保存到手机
    @BindView(R.id.accountExpandMakeMoneyIvTwoDimensionalCode)
    ImageView mIvTwoDimensionalCode;
    //专属链接网址
    @BindView(R.id.accountExpandMakeMoneyTvShareWeb)
    TextView mTvShareWeb;
    //复制网址的按钮
    @BindView(R.id.accountExpandMakeMoneyTvShareWebCopy)
    TextView mTvShareWebCopy;
    //邀请码
    @BindView(R.id.accountExpandMakeMoneyInviteCode)
    TextView mTvInviteCode;
    //复制邀请码
    @BindView(R.id.accountExpandMakeMoneyInviteCodeCopy)
    TextView tv_inviteCodeCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_make_money);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {

    }
}
