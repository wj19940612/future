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
    TextView tv_brokerage_number;
    //提取佣金按钮
    @BindView(R.id.account_expand_make_money_brokerage_pick_up)
    TextView tv_brokerage_pickUp;
    //代理等级提示 金、银、铜三个等级
    @BindView(R.id.account_expand_make_money_proxy_grade)
    TextView tv_proxy_grade;
    //我的用户的数量
    @BindView(R.id.account_expand_make_money_mine_account)
    TextView tv_mine_account;
    //佣金比例
    @BindView(R.id.account_expand_make_money_brokerage_scle_number)
    TextView tv_brokerage_scle;
    //交易用户数量
    @BindView(R.id.account_expand_make_money_exchange_account_number)
    TextView tv_exchange_account_number;
    //二维码图片，长按保存到手机
    @BindView(R.id.account_expand_make_money_iv_two_dimensional_code)
    ImageView iv_twoDimensionalCode;
    //专属链接网址
    @BindView(R.id.account_expand_make_money_tv_share_web)
    TextView tv_shareWeb;
    //复制网址的按钮
    @BindView(R.id.account_expand_make_money_tv_share_web_copy)
    TextView tv_shareWebCopy;
    //邀请码
    @BindView(R.id.account_expand_make_money_invite_code)
    TextView tv_inviteCode;
    //复制邀请码
    @BindView(R.id.account_expand_make_money_invite_code_copy)
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
