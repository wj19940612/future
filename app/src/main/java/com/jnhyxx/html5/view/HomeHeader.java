package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.domain.order.OrderReport;
import com.johnz.kutils.StrUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeHeader extends FrameLayout {

    @BindView(R.id.simulation)
    TextView mSimulation;
    @BindView(R.id.paidToPromote)
    TextView mPaidToPromote;
    @BindView(R.id.investCourse)
    TextView mInvestCourse;
    @BindView(R.id.live)
    TextView mLive;
    @BindView(R.id.viewSwitcher)
    ViewSwitcher mViewSwitcher;
    @BindView(R.id.announcement)
    LinearLayout mAnnouncement;

    public interface OnViewClickListener {

        void onSimulationClick();

        void onPaidToPromoteClick();

        void onInvestCourseClick();

        void onLiveClick();

    }

    private OnViewClickListener mListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mListener = onViewClickListener;
    }

    private List<OrderReport> mOrderReportList;
    private int mCount;

    public HomeHeader(Context context) {
        super(context);
        init();
    }

    public HomeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.header_home, this, true);
        ButterKnife.bind(this);
        mViewSwitcher.setInAnimation(getContext(), R.anim.slide_in_from_right);
        mViewSwitcher.setOutAnimation(getContext(), R.anim.slide_out_to_left);
        mViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getContext());
                textView.setTextSize(12);
                textView.setPadding(10, 0, 0, 0);
                textView.setTextColor(Color.parseColor("#666666"));
                return textView;
            }
        });
    }

    public void setOrderReports(List<OrderReport> orderReports) {
        mOrderReportList = orderReports;
        mCount = 0;
        nextOrderReport();
    }

    public void nextOrderReport() {
        if (mOrderReportList == null || mOrderReportList.size() == 0) {
            mAnnouncement.setVisibility(GONE);
        } else {
            mAnnouncement.setVisibility(VISIBLE);
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

    public void setSimulationHolding(List<HomePositions.IntegralOpSBean> integralOpSBeanList) {
        if (integralOpSBeanList != null && integralOpSBeanList.size() > 0) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_home_simulating);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mSimulation.setCompoundDrawables(null, drawable, null, null);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_home_simulation);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mSimulation.setCompoundDrawables(null, drawable, null, null);
        }
    }

    @OnClick({R.id.simulation, R.id.paidToPromote, R.id.investCourse, R.id.live})
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
            case R.id.live:
                if (mListener != null) {
                    mListener.onLiveClick();
                }
                break;
        }
    }
}
