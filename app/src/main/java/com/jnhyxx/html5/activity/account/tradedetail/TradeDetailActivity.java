package com.jnhyxx.html5.activity.account.tradedetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.fragment.InfoFragment;
import com.jnhyxx.html5.fragment.InfoListFragment;
import com.jnhyxx.html5.fragment.TradeDetailListFragment;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TradeDetailActivity extends BaseActivity {
    @BindView(R.id.tradeDetailSlidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.tradeDetailViewPager)
    ViewPager mViewPager;
    @BindView(R.id.tradeDetailTitleBar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_detail);
        ButterKnife.bind(this);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
        mViewPager.setAdapter(new tradeDetailFragmentAdapter(getSupportFragmentManager(), getActivity()));
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    class tradeDetailFragmentAdapter extends FragmentPagerAdapter {
        Context mContext;

        public tradeDetailFragmentAdapter(FragmentManager fm, Context context) {
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
                    return TradeDetailListFragment.newInstance(TradeDetailListFragment.TYPE_FUND);
                case 1:
                    return TradeDetailListFragment.newInstance(TradeDetailListFragment.TYPE_INTEGRAL);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
