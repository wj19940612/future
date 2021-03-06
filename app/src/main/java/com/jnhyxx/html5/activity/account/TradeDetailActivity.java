package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.UserFundInfo;
import com.jnhyxx.html5.fragment.FundDetailFragment;
import com.jnhyxx.html5.fragment.ScoreDetailListFragment;
import com.jnhyxx.html5.utils.FontUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.umeng.analytics.MobclickAgent;

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

    UserFundInfo mUserFundInfo;

    TradeDetailFragmentAdapter mTradeDetailFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_detail);
        ButterKnife.bind(this);
        initViewPager();

        mUserFundInfo = (UserFundInfo) getIntent().getSerializableExtra(Launcher.EX_PAYLOAD);
        if (mUserFundInfo == null) return;
        initData();

        FontUtil.setTt0173MFont(mRemainNumber);
        FontUtil.setTt0173MFont(mBlockedNumber);

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
                        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.FUND_DETAIL);
                        break;
                    case 1:
                        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.GOLD_DETAIL);
                        mRemainTitle.setText(R.string.account_trade_detail_integral_remain);
                        mRemainNumber.setText(FinanceUtil.formatWithScale(mUserFundInfo.getScoreUsable()));
                        mBlockedTitle.setText(R.string.integral_frozen);
                        mBlockedNumber.setText(FinanceUtil.formatWithScale(mUserFundInfo.getMarginScore()));
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
        mRemainNumber.setText(FinanceUtil.formatWithScale(mUserFundInfo.getMoneyUsable()));
        //右边头部
        mBlockedTitle.setText(R.string.money_frozen);
        //冻结资金余额
        mBlockedNumber.setText(FinanceUtil.formatWithScale(mUserFundInfo.getMargin()));
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0, false);
        mTradeDetailFragmentAdapter = new TradeDetailFragmentAdapter(getSupportFragmentManager(), TradeDetailActivity.this);
        mViewPager.setAdapter(mTradeDetailFragmentAdapter);


        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(TradeDetailActivity.this, android.R.color.transparent));
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    class TradeDetailFragmentAdapter extends FragmentPagerAdapter {
        Context mContext;

        public TradeDetailFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
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
                    return FundDetailFragment.newInstance(FundDetailFragment.TYPE_FUND);
                case 1:
                    return ScoreDetailListFragment.newInstance(ScoreDetailListFragment.TYPE_INTEGRAL);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
