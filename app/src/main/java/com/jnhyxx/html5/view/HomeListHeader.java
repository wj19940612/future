package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.domain.order.OrderReport;
import com.johnz.kutils.StrUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeListHeader extends FrameLayout {

    @OnClick({R.id.simulation, R.id.paidToPromote, R.id.investCourse, R.id.newerVideo, R.id.contactService})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.simulation:
                if (mListener != null) {
                    mListener.onSimulationClick();
                }
                break;
            case R.id.paidToPromote:
                if (mListener != null) {
                    mListener.onPaidToPromoteClick();
                }
                break;
            case R.id.investCourse:
                if (mListener != null) {
                    mListener.onInvestCourseClick();
                }
                break;
            case R.id.newerVideo:
                if (mListener != null) {
                    mListener.onNewerGuideClick();
                }
                break;
            case R.id.contactService:
                if (mListener != null) {
                    mListener.onContactServiceClick();
                }
                break;
            case R.id.futuresRiskTips:
                if (mListener != null) {
                    mListener.onFuturesRiskTipsClick();
                }
                break;
        }
    }

    public interface OnViewClickListener {
        void onBannerClick(Information information);

        void onFuturesRiskTipsClick();

        void onSimulationClick();

        void onPaidToPromoteClick();

        void onInvestCourseClick();

        void onNewerGuideClick();

        void onContactServiceClick();
    }

    private OnViewClickListener mListener;

    @BindView(R.id.viewPager)
    InfiniteViewPager mViewPager;
    @BindView(R.id.pageIndicator)
    PageIndicator mPageIndicator;
    @BindView(R.id.viewSwitcher)
    ViewSwitcher mViewSwitcher;
    @BindView(R.id.newerVideo)
    TextView mNewerGuide;
    @BindView(R.id.contactService)
    TextView mContactService;
    @BindView(R.id.holdingNumber)
    TextView mHoldingNumber;

    private AdvertisementAdapter mAdapter;
    private List<OrderReport> mOrderReportList;
    private int mCount;

    public HomeListHeader(Context context) {
        super(context);
        init();
    }

    public HomeListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_header_home, this, true);
        ButterKnife.bind(this);

        mViewSwitcher.setInAnimation(getContext(), R.anim.slide_in_from_bottom);
        mViewSwitcher.setOutAnimation(getContext(), R.anim.slide_out_to_top);
        mViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(getContext());
            }
        });
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mViewPager != null) {
            mViewPager.clearOnPageChangeListeners();
        }
    }

    public void setSimulationHolding(List<HomePositions.IntegralOpSBean> integralOpSBeanList) {
        if (integralOpSBeanList != null && integralOpSBeanList.size() > 0) {
            int holdingNumber = 0;
            for (HomePositions.IntegralOpSBean integralOpSBean : integralOpSBeanList) {
                holdingNumber += integralOpSBean.getHandsNum();
            }
            if (getContext() != null) {
                mHoldingNumber.setText(getContext().getString(R.string.holding_number, holdingNumber));
            }
        } else {
            mHoldingNumber.setText(R.string.enter_right_now);
        }
    }

    public void setOrderReports(List<OrderReport> orderReports) {
        mOrderReportList = orderReports;
        mCount = 0;
        nextOrderReport();
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mListener = onViewClickListener;
    }

    public void nextOrderReport() {
        if (mOrderReportList == null || mOrderReportList.size() == 0) {
            mViewSwitcher.setVisibility(GONE);
        } else {
            mViewSwitcher.setVisibility(VISIBLE);
            TextView orderReportView = (TextView) mViewSwitcher.getNextView();
            OrderReport report = mOrderReportList.get(mCount++ % mOrderReportList.size());
            SpannableString orderReport = StrUtil.mergeTextWithColor(
                    report.getNick() + " " + report.getTime() + " ",
                    report.getTradeType(), report.isShortSelling() ? ContextCompat.getColor(getContext(), R.color.greenPrimary) : ContextCompat.getColor(getContext(), R.color.redPrimary),
                    " " + report.getFuturesType());
            orderReportView.setText(orderReport);
            mViewSwitcher.showNext();
        }
    }

    public void nextAdvertisement() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void setHomeAdvertisement(List<Information> informationList) {
        filterEmptyInformation(informationList);
        if (!informationList.isEmpty()) {
            mPageIndicator.setCount(informationList.size());
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
