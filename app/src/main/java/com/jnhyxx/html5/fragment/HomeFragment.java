package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.home.TradingStrategyFragment;
import com.jnhyxx.html5.fragment.home.YesterdayProfitRankFragment;
import com.jnhyxx.html5.view.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSlidingTabLayout();
    }

    private void initSlidingTabLayout() {
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mViewPager.setAdapter(new HomeInfoFragmentPagerAdapter(getChildFragmentManager(), getActivity()));
        mViewPager.setOffscreenPageLimit(3);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class HomeInfoFragmentPagerAdapter extends FragmentPagerAdapter {

        Context mContext;

        public HomeInfoFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new YesterdayProfitRankFragment();
                case 1:
                    return TradingStrategyFragment.newInstance();
                case 2:
                    return new Fragment();
                default:
                    break;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.yesterday_the_profit_list);
                case 1:
                    return mContext.getString(R.string.trading_strategy);
                case 2:
                    return mContext.getString(R.string.calendar_of_finance);
                default:
                    break;
            }

            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
