package com.jnhyxx.html5.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.view.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InfoFragment extends BaseFragment {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    private Unbinder mBinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
        mViewPager.setAdapter(new InfoPagersAdapter(getChildFragmentManager(), getActivity()));
        mViewPager.setOffscreenPageLimit(3);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    static class InfoPagersAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public InfoPagersAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.news_living);
                case 1:
                    return mContext.getString(R.string.market_analysing);
                case 2:
                    return mContext.getString(R.string.industry_news);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return InfoLiveFragment.newInstance();
                case 1:
                    return IndustryAnalyzeFragment.newInstance(Information.TYPE_MARKET_ANALYSIS);
                case 2:
                    return IndustryMessageFragment.newInstance(Information.TYPE_INDUSTRY_ANALYSIS);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }
}
