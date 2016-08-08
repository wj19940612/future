package com.jnhyxx.html5.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.HomeAdvertisement;
import com.jnhyxx.html5.domain.order.OrderReport;
import com.johnz.kutils.StrUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeListHeader extends FrameLayout {

    public interface OnViewClickListener {

    }

    private OnViewClickListener mListener;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.pageIndicator)
    PageIndicator mPageIndicator;
    @BindView(R.id.currentOnlineNumber)
    TextView mCurrentOnlineNumber;
    @BindView(R.id.viewSwitcher)
    ViewSwitcher mViewSwitcher;
    @BindView(R.id.simulation)
    TextView mSimulation;
    @BindView(R.id.newerGuide)
    TextView mNewerGuide;
    @BindView(R.id.contactService)
    TextView mContactService;

    private AdvertisementAdapter mAdapter;
    private OrderReport mOrderReport;
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

    public void setOrderReport(OrderReport orderReport) {
        mOrderReport = orderReport;
        SpannableString currentOnlineNumber = StrUtil.mergeTextWithColor("当前在线",
                mOrderReport.getCount() + "", getContext().getResources().getColor(R.color.redPrimary),
                "人");
        mCurrentOnlineNumber.setText(currentOnlineNumber);
        mCount = 0;
        nextOrderReport();
    }

    public void nextOrderReport() {
        if (mOrderReport == null) return;
        TextView orderReportView = (TextView) mViewSwitcher.getNextView();
        List<OrderReport.ResultListBean> listBeen = mOrderReport.getResultList();
        if (listBeen.size() > 0) {
            OrderReport.ResultListBean resultListBean = listBeen.get(mCount++ % listBeen.size());
            SpannableString orderReport = StrUtil.mergeTextWithColor(
                    resultListBean.getNick() + " " + resultListBean.getTime() + " ",
                    resultListBean.getTradeType(), getContext().getResources().getColor(R.color.redPrimary),
                    " " + resultListBean.getFuturesType());
            orderReportView.setText(orderReport);
            mViewSwitcher.showNext();
        }
    }

    public void setHomeAdvertisement(HomeAdvertisement homeAdvertisement) {
        if (mAdapter == null) {
            mAdapter = new AdvertisementAdapter(getContext(), homeAdvertisement.getNews_notice_img_list());
            mViewPager.setAdapter(mAdapter);
            mViewPager.addOnPageChangeListener(mOnPageChangeListener);
            mViewPager.setCurrentItem(mAdapter.getCount() / 2);
        } else {
            mAdapter.setNewAdvertisements(homeAdvertisement.getNews_notice_img_list());
        }
    }

    private static class AdvertisementAdapter extends PagerAdapter {

        private List<HomeAdvertisement.NewsNoticeImgListBean> mList;
        private List<ImageView> mImageViewList;
        private Context mContext;

        public AdvertisementAdapter(Context context, List<HomeAdvertisement.NewsNoticeImgListBean> imgListBeen) {
            mContext = context;
            mList = imgListBeen;
            mImageViewList = createImageViewList(mList.size());
        }

        public void setNewAdvertisements(List<HomeAdvertisement.NewsNoticeImgListBean> imgListBeen) {
            mList = imgListBeen;
            mImageViewList = createImageViewList(mList.size());
            notifyDataSetChanged();
        }

        private List<ImageView> createImageViewList(int size) {
            List<ImageView> imageViewList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                imageViewList.add(new ImageView(mContext));
            }
            return imageViewList;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int pos = position % mList.size();
            ImageView imageView = mImageViewList.get(pos);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            HomeAdvertisement.NewsNoticeImgListBean bean = mList.get(pos);
            Picasso.with(mContext).load(BuildConfig.API_HOST + bean.getMiddleBanner()).into(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int pos = position % mList.size();
            ImageView imageView = mImageViewList.get(pos);
            if (imageView != null) {
                container.removeView(imageView);
            }
        }
    }
}
