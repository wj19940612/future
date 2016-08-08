package com.jnhyxx.html5.activity.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.fragment.order.HoldingFragment;
import com.jnhyxx.html5.fragment.order.SettlementFragment;
import com.jnhyxx.html5.view.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
        mViewPager.setAdapter(new OrderAdapter(getSupportFragmentManager(), this));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    static class OrderAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public OrderAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.holding);
                case 1:
                    return mContext.getString(R.string.settlement);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HoldingFragment();
                case 1:
                    return new SettlementFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
