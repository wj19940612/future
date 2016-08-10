package com.jnhyxx.html5.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.market.Product;
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

    private Product mProduct;
    private int mFundType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        initData(getIntent());

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
        mViewPager.setAdapter(new OrderAdapter(getSupportFragmentManager(), this, mProduct, mFundType));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void initData(Intent intent) {
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
    }

    static class OrderAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private Product mProduct;
        private int mFundType;

        public OrderAdapter(FragmentManager fm, Context context, Product product, int fundType) {
            super(fm);
            mContext = context;
            mProduct = product;
            mFundType = fundType;
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
                    return SettlementFragment.newInstance(mProduct, mFundType);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
