package com.jnhyxx.html5.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.SimulationActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.web.BannerActivity;
import com.jnhyxx.html5.activity.web.HideTitleWebActivity;
import com.jnhyxx.html5.activity.web.InvestCourseActivity;
import com.jnhyxx.html5.activity.web.NewbieActivity;
import com.jnhyxx.html5.activity.web.PaidToPromoteActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.OrderReport;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.StatusBarUtil;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.HomeBanner;
import com.jnhyxx.html5.view.HomeHeader;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.homeHeader)
    HomeHeader mHomeHeader;
    @BindView(R.id.homeBanner)
    HomeBanner mHomeBanner;
    @BindView(R.id.riskEvaluation)
    TextView mRiskEvaluation;
    @BindView(R.id.contactService)
    TextView mContactService;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout mCollapsing;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    private Unbinder mBind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBind = ButterKnife.bind(this, view);
        StatusBarUtil.setTitleBar(mToolbar, getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeHeader.setOnViewClickListener(mOnViewClickListener);
        mHomeBanner.setListener(new HomeBanner.OnViewClickListener() {
            @Override
            public void onBannerClick(Information information) {
                if (information.isH5Style()) {
                    Launcher.with(getActivity(), HideTitleWebActivity.class)
                            .putExtra(HideTitleWebActivity.EX_URL, information.getContent())
                            .putExtra(HideTitleWebActivity.EX_TITLE, information.getTitle())
                            .putExtra(HideTitleWebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                } else {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.BANNER);
                    Launcher.with(getActivity(), BannerActivity.class)
                            .putExtra(BannerActivity.EX_HTML, information.getContent())
                            .putExtra(BannerActivity.EX_TITLE, information.getTitle())
                            .putExtra(BannerActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                }
            }
        });
        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mToolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.colorPrimary),
                        Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange()));
            }
        });
        requestHomeInformation();
    }

    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestHomeInformation();
        requestOrderReport();
        startScheduleJob(5 * 1000);
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        mHomeHeader.nextOrderReport();
        mHomeBanner.nextAdvertisement();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private HomeHeader.OnViewClickListener mOnViewClickListener = new HomeHeader.OnViewClickListener() {

        // 模拟交易
        @Override
        public void onSimulationClick() {
            MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.SIMULATION_TRADE);
            API.Market.getProductList().setTag(TAG)
                    .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                        @Override
                        public void onRespSuccess(List<Product> products) {
                            Launcher.with(getActivity(), SimulationActivity.class)
                                    .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(products))
                                    .execute();
                        }
                    }).fire();
        }

        // 推广赚钱
        @Override
        public void onPaidToPromoteClick() {
            openPaidToPromotePage();
        }

        // 投资课堂
        @Override
        public void onInvestCourseClick() {
            Launcher.with(getActivity(), InvestCourseActivity.class)
                    .putExtra(InvestCourseActivity.EX_URL, API.getInvestCourseUrl())
                    .putExtra(InvestCourseActivity.EX_TITLE, getString(R.string.investor_course))
                    .putExtra(InvestCourseActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                    .execute();
        }

        // 新手引导
        @Override
        public void onNewerGuideClick() {
            MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HOME_PAGE_NEWBIE_GUIDE);
            Launcher.with(getActivity(), NewbieActivity.class)
                    .putExtra(NewbieActivity.EX_URL, API.getNewbieUrl())
                    .putExtra(NewbieActivity.EX_TITLE, getString(R.string.newbie_title))
                    .putExtra(NewbieActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                    .execute();
        }
    };

    private void openPaidToPromotePage() {
        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.EXPAND_EARN_MONEY);
        if (LocalUser.getUser().isLogin()) {
            API.User.getPromoteCode().setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        public void onReceive(Resp<JsonObject> resp) {
                            if (resp.isSuccess()) {
                                Launcher.with(getActivity(), PaidToPromoteActivity.class)
                                        .putExtra(PaidToPromoteActivity.EX_URL, API.getPromotePage())
                                        .putExtra(PaidToPromoteActivity.EX_TITLE, getString(R.string.paid_to_promote))
                                        .putExtra(PaidToPromoteActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                        .execute();
                            } else if (resp.getCode() == Resp.CODE_GET_PROMOTE_CODE_FAILED) {
                                showAskApplyPromoterDialog();
                            } else {
                                ToastUtil.show(resp.getMsg());
                            }
                        }
                    }).fire();
        } else {
            Launcher.with(getActivity(), SignInActivity.class).execute();
        }
    }

    private void showAskApplyPromoterDialog() {
        SmartDialog.with(getActivity(), R.string.dialog_you_are_not_promoter_yet)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        applyForPromoter();
                    }
                })
                .setNegative(R.string.cancel)
                .show();
    }

    private void applyForPromoter() {
        API.User.becomePromoter().setTag(TAG)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(resp.getMsg());
                            Launcher.with(getActivity(), PaidToPromoteActivity.class)
                                    .putExtra(PaidToPromoteActivity.EX_URL, API.getPromotePage())
                                    .putExtra(PaidToPromoteActivity.EX_TITLE, getString(R.string.paid_to_promote))
                                    .putExtra(PaidToPromoteActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                    .execute();
                        }
                    }
                }).fire();
    }

    private void requestHomeInformation() {
        API.User.getNewsList(Information.TYPE_BANNER, 0, 10)
                .setCallback(new Callback2<Resp<List<Information>>, List<Information>>() {
                    @Override
                    public void onRespSuccess(List<Information> informationList) {
                        mHomeBanner.setHomeAdvertisement(informationList);
                    }
                }).setTag(TAG).fire();
    }

    private void requestOrderReport() {
        API.Order.getReportData()
                .setCallback(new Callback2<Resp<List<OrderReport>>, List<OrderReport>>(false) {
                    @Override
                    public void onRespSuccess(List<OrderReport> orderReports) {
                        mHomeHeader.setOrderReports(orderReports);
                    }
                }).setTag(TAG).fire();
    }

    @OnClick({R.id.riskEvaluation, R.id.contactService})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.riskEvaluation:
                break;
            case R.id.contactService:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HOME_PAGE_CONNECT_SERVICE);
                String serviceQQUrl = API.getServiceQQ(Preference.get().getServiceQQ());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceQQUrl));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    ToastUtil.show(R.string.install_qq_first);
                }
                break;
        }
    }
}
