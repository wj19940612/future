package com.jnhyxx.html5.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class InfiniteViewPager extends ViewPager {

    private InnerAdapter mInnerAdapter;

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

        private OnPageChangeListener mExternalListener;
        private int mInnerPosition;

        public InnerPageChangeListener(OnPageChangeListener externalListener) {
            mExternalListener = externalListener;

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int pos = calExternalPosition(position, mInnerAdapter);
            if (mExternalListener != null) {
                mExternalListener.onPageScrolled(pos, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            mInnerPosition = position;
            int pos = calExternalPosition(position, mInnerAdapter);
            if (mExternalListener != null) {
                mExternalListener.onPageSelected(pos); // TODO: 8/9/16 this will be called twice, since setCurrentItem to real item. Fix latter
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mExternalListener != null) {
                mExternalListener.onPageScrollStateChanged(state);
            }

            if (state == ViewPager.SCROLL_STATE_IDLE) { // When viewpager is settled
                if (mInnerPosition == 0) { // fake obj: head, move to last second
                    setCurrentItem(mInnerAdapter.getCount() - 2, false);
                } else if (mInnerPosition == mInnerAdapter.getCount() - 1) { // fake obj: tail, move to the second
                    setCurrentItem(1, false);
                }
            }
        }
    }

    private class InnerAdapter extends PagerAdapter {

        private PagerAdapter mExternalAdapter;

        public InnerAdapter(PagerAdapter adapter) {
            mExternalAdapter = adapter;
            mExternalAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    notifyDataSetChanged();
                }
            });
        }

        public PagerAdapter getExternalAdapter() {
            return mExternalAdapter;
        }

        @Override
        public int getCount() {
            return mExternalAdapter.getCount() + 2; // fake object at head and tail
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mExternalAdapter.isViewFromObject(view, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mExternalAdapter.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = calExternalPosition(position, this);
            return mExternalAdapter.instantiateItem(container, position);
        }
    }

    private int calExternalPosition(int innerPos, InnerAdapter innerAdapter) {
        if (innerPos == 0) { // first -> external last
            return innerAdapter.getExternalAdapter().getCount() - 1;
        } else if (innerPos == innerAdapter.getCount() - 1) { // last -> external 0
            return 0;
        } else {
            return innerPos - 1;
        }
    }
}
