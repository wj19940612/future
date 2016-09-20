package com.jnhyxx.html5.activity.account.tradedetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserFundInfo;
import com.jnhyxx.html5.fragment.FundDetailFragment;
import com.jnhyxx.html5.fragment.IntegralDetailFragment;
import com.jnhyxx.html5.fragment.TradeDetailListFragment;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TitleBar;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TradeDetailActivity extends BaseActivity {
    @BindView(R.id.tradeDetailSlidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.tradeDetailViewPager)
    ViewPager mViewPager;
    @BindView(R.id.tradeDetailTitleBar)
    TitleBar mTitleBar;
    @BindView(R.id.remainTitle)
    TextView mRemainTitle;
    @BindView(R.id.remainNumber)
    TextView mRemainNumber;
    @BindView(R.id.blockedTitle)
    TextView mBlockedTitle;
    @BindView(R.id.blockedNumber)
    TextView mBlockedNumber;

    public static final String INTENT_KEY = "userFundInfo";

    UserFundInfo mUserFundInfo;


    ArrayList<Fragment> fragmentList;


    TradeDetailFragmentAdapter mTradeDetailFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_detail);
        ButterKnife.bind(this);
        initViewPager();

        mUserFundInfo = (UserFundInfo) getIntent().getSerializableExtra(INTENT_KEY);
        if (mUserFundInfo == null) return;
        initData();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //左边头部
                        initData();
                        break;
                    case 1:
                        mRemainTitle.setText(R.string.account_trade_detail_integral_remain);
                        mRemainNumber.setText(String.valueOf(mUserFundInfo.getScoreUsable()));
                        mBlockedTitle.setText(R.string.integral_Frozen);
                        // TODO: 2016/9/19 目前不知冻结积分如何获取
                        mBlockedNumber.setText(String.valueOf(mUserFundInfo.getMarginScore()));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initData() {
        mRemainTitle.setText(R.string.remain_money);
        //资金余额
        mRemainNumber.setText(String.valueOf(mUserFundInfo.getMoneyUsable()));
        //右边头部
        mBlockedTitle.setText(R.string.money_Frozen);
        //冻结资金余额
        mBlockedNumber.setText(String.valueOf(mUserFundInfo.getMoneyFrozen()));
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
//        FundDetailFragment fundDetailFragment = FundDetailFragment.newInstance();
//        fragmentList.add(fundDetailFragment);
//        IntegralDetailFragment integralDetailFragment = IntegralDetailFragment.newInstance();
//        fragmentList.add(integralDetailFragment);

//        TradeDetailListFragment fundTradeDetailListFragment = TradeDetailListFragment.newInstance();
//        fundTradeDetailListFragment.setData(TradeDetailListFragment.TYPE_FUND);
//        fragmentList.add(fundTradeDetailListFragment);
//        TradeDetailListFragment integralTradeDetailListFragment = TradeDetailListFragment.newInstance();
//        integralTradeDetailListFragment.setData(TradeDetailListFragment.TYPE_INTEGRAL);
//        fragmentList.add(integralTradeDetailListFragment);


        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
        mTradeDetailFragmentAdapter = new TradeDetailFragmentAdapter(getSupportFragmentManager(), TradeDetailActivity.this);
//        mTradeDetailFragmentAdapter = new TradeDetailFragmentAdapter(getSupportFragmentManager(), TradeDetailActivity.this, fragmentList);
        mViewPager.setAdapter(mTradeDetailFragmentAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    class TradeDetailFragmentAdapter extends FragmentPagerAdapter {
        Context mContext;
        ArrayList<Fragment> fragments;

        public TradeDetailFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        public TradeDetailFragmentAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragmentArrayList) {
            super(fm);
            this.mContext = context;
            this.fragments = fragmentArrayList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.fund_detail);
                case 1:
                    return mContext.getString(R.string.score_detail);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TradeDetailListFragment.newInstance(TradeDetailListFragment.TYPE_FUND);
                case 1:
                    return TradeDetailListFragment.newInstance(TradeDetailListFragment.TYPE_INTEGRAL);
            }
            return null;
        }
//        // TODO: 2016/9/19 目前有问题，没有复用
//        @Override
//        public Fragment getItem(int position) {
//            if (fragments != null && !fragments.isEmpty()) {
//                return fragments.get(position);
//            }
//            return null;
//        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
