package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class InfiniteViewPager extends ViewPager {

    InnerAdapter mInnerAdapter;

    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mInnerAdapter = new InnerAdapter(adapter);
        super.setAdapter(mInnerAdapter);
        setCurrentItem(1);
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(new InnerPageChangeListener(listener));
    }

    private class InnerPageChangeListener implements OnPageChangeListener {

        private OnPageChangeListener mListener;
        private int pos;

        public InnerPageChangeListener(OnPageChangeListener listener) {
            mListener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mListener != null) {
                mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            pos = position;
            if (mListener != null) {
                mListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mListener != null) {
                mListener.onPageScrollStateChanged(state);
            }

            if (state == ViewPager.SCROLL_STATE_IDLE) { // When viewpager is settled
                if (pos == mInnerAdapter.getCount() - 1) { // fake obj: tail, move to the second
                    setCurrentItem(1, false);
                } else if (pos == 0) { // fake obj: head, move to last second
                    setCurrentItem(mInnerAdapter.getCount() - 2, false);
                }
            }
        }
    }

    private class InnerAdapter extends PagerAdapter {

        private PagerAdapter mPagerAdapter;

        public InnerAdapter(PagerAdapter adapter) {
            mPagerAdapter = adapter;
        }

        @Override
        public int getCount() {
            return mPagerAdapter.getCount() + 2; // fake object at head and tail
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mPagerAdapter.isViewFromObject(view, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mPagerAdapter.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) { // first -> mAdapter last
                position = mPagerAdapter.getCount() - 1;
            } else if (position == getCount() - 1) { // last -> mAdapter 0
                position = 0;
            } else {
                position = position - 1;
            }
            return mPagerAdapter.instantiateItem(container, position);
        }
    }
}
