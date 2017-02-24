package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.Information;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MR.YANG on 2017/2/14.
 */

public class HomeBanner extends FrameLayout {
    @BindView(R.id.viewPager)
    InfiniteViewPager mViewPager;
    @BindView(R.id.pageIndicator)
    PageIndicator mPageIndicator;

    private AdvertisementAdapter mAdapter;

    public interface OnViewClickListener {
        void onBannerClick(Information information);
    }

    private OnViewClickListener mListener;

    public void setListener(OnViewClickListener listener) {
        mListener = listener;
    }

    public HomeBanner(Context context) {
        super(context);
        init();
    }

    public HomeBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.home_banner, this, true);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mViewPager != null) {
            mViewPager.clearOnPageChangeListeners();
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mPageIndicator != null) {
                mPageIndicator.move(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void nextAdvertisement() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void setHomeAdvertisement(List<Information> informationList) {
        filterEmptyInformation(informationList);
        if (!informationList.isEmpty()) {
            int size = informationList.size();
            if (size < 2) {
                mPageIndicator.setVisibility(INVISIBLE);
            } else {
                mPageIndicator.setVisibility(VISIBLE);
            }
            mPageIndicator.setCount(size);
            if (mAdapter == null) {
                mAdapter = new AdvertisementAdapter(getContext(), informationList, mListener);
                mViewPager.addOnPageChangeListener(mOnPageChangeListener);
                mViewPager.setAdapter(mAdapter);
            } else {
                mAdapter.setNewAdvertisements(informationList);
            }
        }
    }

    private void filterEmptyInformation(List<Information> informationList) {
        List<Information> removeList = new ArrayList<>();
        for (int i = 0; i < informationList.size(); i++) {
            Information information = informationList.get(i);
            if (TextUtils.isEmpty(information.getCover())) {
                removeList.add(information);
            }
        }
        for (int i = 0; i < removeList.size(); i++) {
            informationList.remove(removeList.get(i));
        }
    }

    private static class AdvertisementAdapter extends PagerAdapter {

        private List<Information> mList;
        private Context mContext;
        private OnViewClickListener mListener;

        public AdvertisementAdapter(Context context, List<Information> informationList, OnViewClickListener listener) {
            mContext = context;
            mList = informationList;
            mListener = listener;
        }

        public void setNewAdvertisements(List<Information> informationList) {
            mList = informationList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int pos = position;
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final Information information = mList.get(pos);
            container.addView(imageView, 0);
            if (!TextUtils.isEmpty(information.getCover())) {
                Picasso.with(mContext).load(information.getCover()).into(imageView);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBannerClick(information);
                    }
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
