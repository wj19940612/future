package com.jnhyxx.html5.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.App;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.LiveActivity;
import com.jnhyxx.html5.activity.ProductOptionalActivity;
import com.jnhyxx.html5.activity.SimulationActivity;
import com.jnhyxx.html5.activity.TradeActivity;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.web.BannerActivity;
import com.jnhyxx.html5.activity.web.HideTitleWebActivity;
import com.jnhyxx.html5.activity.web.InvestCourseActivity;
import com.jnhyxx.html5.activity.web.PaidToPromoteActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.local.SysTime;
import com.jnhyxx.html5.domain.market.MarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.fragment.home.CalendarFinanceFragment;
import com.jnhyxx.html5.fragment.home.TradingStrategyFragment;
import com.jnhyxx.html5.fragment.home.YesterdayProfitRankFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.DividerGridItemDecoration;
import com.jnhyxx.html5.utils.HeaderAndFooterWrapper;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.HomeBanner;
import com.jnhyxx.html5.view.HomeHeader;
import com.jnhyxx.html5.view.MyNestedScrollView;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.StrUtil;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.headerTitle;


public class HomeFragment extends BaseFragment {

    @BindView(R.id.homeBanner)
    HomeBanner mHomeBanner;
    @BindView(R.id.homeHeader)
    HomeHeader mHomeHeader;

    @BindView(R.id.riskEvaluation)
    TextView mRiskEvaluation;
    @BindView(R.id.contactService)
    TextView mContactService;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.optionalForeignView)
    RecyclerView mOptionalForeignView;
    @BindView(R.id.optionalDomesticView)
    RecyclerView mOptionalDomesticView;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.replaceLayout)
    FrameLayout mReplaceLayout;

    @BindView(R.id.nestedScrollView)
    MyNestedScrollView mNestedScrollView;
    private Unbinder mBind;

    public static final int REQ_CODE_FOREIGN = 100;
    public static final int REQ_CODE_DOMESTIC = 101;
    public static final String IS_DOMESTIC = "isDomestic";

    private List<ProductPkg> mProductPkgList;
    private List<Product> mProductList;
    private List<HomePositions.CashOpSBean> mCashPositionList;
    private List<MarketData> mMarketDataList;
    private List<ProductPkg> mForeignPkgList;
    private List<ProductPkg> mDomesticPkgList;

    private List<Double> mTheLastForeignPrice;
    private List<Double> mTheLastDomesticPrice;

    private HeaderAndFooterWrapper mOptionalForeignWrapper;
    private HeaderAndFooterWrapper mOptionalDomesticWrapper;
    private View mOptionalDomesticHeader;

    private boolean mIsScrolling;

    public interface OnListViewHeightListener {
        /**
         * @param height 控件高度
         */
        void listViewHeight(int height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSlidingTabLayout();
        mProductPkgList = new ArrayList<>();
        mForeignPkgList = new ArrayList<>();
        mDomesticPkgList = new ArrayList<>();
        mTheLastForeignPrice = new ArrayList<>();
        mTheLastDomesticPrice = new ArrayList<>();

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

        mNestedScrollView.smoothScrollTo(0, 20);
        mNestedScrollView.setHandler(new Handler());
        mNestedScrollView.setOnScrollStateChangedListener(new MyNestedScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MyNestedScrollView.ScrollType scrollType) {
                switch (scrollType) {
                    case IDLE:
                        mIsScrolling = false;
                        break;
                    case TOUCH_SCROLL:
                        mIsScrolling = true;
                        break;
                    case FLING:
                        mIsScrolling = true;
                        break;
                }
            }
        });

        setOptionalProductRecyclerView();

        requestHomeInformation();
        requestProductMarketList();
    }

    private void setOptionalProductRecyclerView() {
        mOptionalForeignView.setHasFixedSize(true);
        OptionalListAdapter foreignAdapter = new OptionalListAdapter(getContext(), mForeignPkgList, mTheLastForeignPrice);
        mOptionalForeignView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        mOptionalForeignView.addItemDecoration(new DividerGridItemDecoration(getContext()));
        mOptionalForeignWrapper = new HeaderAndFooterWrapper(foreignAdapter);
        View foreignHeadView = View.inflate(getContext(), R.layout.optional_list_head, null);
        TextView headTitle1 = (TextView) foreignHeadView.findViewById(headerTitle);
        ImageView optionalAdd1 = (ImageView) foreignHeadView.findViewById(R.id.optionalEdit);
        optionalAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), ProductOptionalActivity.class)
                        .putExtra(IS_DOMESTIC, false)
                        .executeForResult(REQ_CODE_DOMESTIC);
            }
        });
        headTitle1.setText(getString(R.string.foreign_futures));
        mOptionalForeignWrapper.addHeaderView(foreignHeadView);
        mOptionalForeignView.setAdapter(mOptionalForeignWrapper);

        mOptionalDomesticView.setHasFixedSize(true);
        OptionalListAdapter domesticAdapter = new OptionalListAdapter(getContext(), mDomesticPkgList, mTheLastDomesticPrice);
        mOptionalDomesticWrapper = new HeaderAndFooterWrapper(domesticAdapter);
        mOptionalDomesticView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        mOptionalDomesticView.addItemDecoration(new DividerGridItemDecoration(getContext()));
        mOptionalDomesticHeader = View.inflate(getContext(), R.layout.optional_list_head, null);
        TextView headTitle2 = (TextView) mOptionalDomesticHeader.findViewById(headerTitle);
        ImageView optionalAdd2 = (ImageView) mOptionalDomesticHeader.findViewById(R.id.optionalEdit);
        optionalAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), ProductOptionalActivity.class)
                        .putExtra(IS_DOMESTIC, true)
                        .executeForResult(REQ_CODE_FOREIGN);
            }
        });
        headTitle2.setText(getString(R.string.domestic_futures));
        mOptionalDomesticWrapper.addHeaderView(mOptionalDomesticHeader);
        mOptionalDomesticView.setAdapter(mOptionalDomesticWrapper);
    }

    @Override
    public void onResume() {
        super.onResume();
        startScheduleJob(1 * 1000);
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (getUserVisibleHint()) {
            requestProductMarketList();
            if (count % 5 == 0) {
                mHomeBanner.nextAdvertisement();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            startScheduleJob(1 * 1000);
        } else {
            stopScheduleJob();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void initSlidingTabLayout() {
        showProfitRankFragment();
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.yesterday_the_profit_list));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trading_strategy));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.calendar_of_finance));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    showProfitRankFragment();
                    break;
                case 1:
                    showTradingStrategyFragment();
                    break;
                case 2:
                    showCalendarFinanceFragment();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void showProfitRankFragment() {
        final Fragment fragment = getChildFragmentManager().findFragmentById(R.id.replaceLayout);
        YesterdayProfitRankFragment yesterdayProfitRankFragment = YesterdayProfitRankFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.replaceLayout, yesterdayProfitRankFragment)
                .commit();
        yesterdayProfitRankFragment.setOnListViewHeightListener(new OnListViewHeightListener() {
            @Override
            public void listViewHeight(int height) {
                ViewGroup.LayoutParams layoutParams = mReplaceLayout.getLayoutParams();
                layoutParams.height = height;
                mReplaceLayout.setLayoutParams(layoutParams);
            }
        });

    }


    private void showTradingStrategyFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.replaceLayout);
        TradingStrategyFragment tradingStrategyFragment = TradingStrategyFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.replaceLayout, tradingStrategyFragment)
                .commit();
        tradingStrategyFragment.setOnListViewHeightListener(new OnListViewHeightListener() {
            @Override
            public void listViewHeight(int height) {
                ViewGroup.LayoutParams layoutParams = mReplaceLayout.getLayoutParams();
                layoutParams.height = height;
                mReplaceLayout.setLayoutParams(layoutParams);
            }
        });

    }

    private void showCalendarFinanceFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.replaceLayout);
        CalendarFinanceFragment calendarFinanceFragment = CalendarFinanceFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.replaceLayout, calendarFinanceFragment)
                .commit();
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

        // 直播
        @Override
        public void onLiveClick() {
            MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HOME_PAGE_NEWBIE_GUIDE);
            Launcher.with(getActivity(), LiveActivity.class).execute();
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
                }).setTag(TAG).fireSync();
    }

    // call from MainActivity
    public void updateProductList(List<Product> productList) {
        mProductList = productList;
        ProductPkg.updateProductPkgList(mProductPkgList, productList,
                mCashPositionList, mMarketDataList);
        updateOptionalLists();
    }

    private void updateOptionalLists() {
        new Thread() {
            @Override
            public void run() {
                List<String> optionalForeignList = Preference.get().getOptionalForeignProductList();
                List<String> optionalDomesticList = Preference.get().getOptionalDomesticProductList();
                mForeignPkgList.clear();
                mDomesticPkgList.clear();
                updateOptionalList(optionalForeignList, mForeignPkgList);
                updateOptionalList(optionalDomesticList, mDomesticPkgList);

                if (mTheLastForeignPrice.isEmpty() || mTheLastForeignPrice.size() < mForeignPkgList.size()) {
                    for (ProductPkg productPkg : mForeignPkgList) {
                        MarketData marketData = productPkg.getMarketData();
                        if (marketData != null) {
                            mTheLastForeignPrice.add(marketData.getLastPrice());
                        } else {
                            mTheLastForeignPrice.add(0d);
                        }
                    }
                }
                if (mTheLastDomesticPrice.isEmpty() || mTheLastDomesticPrice.size() < mDomesticPkgList.size()) {
                    for (ProductPkg productPkg : mDomesticPkgList) {
                        MarketData marketData = productPkg.getMarketData();
                        if (marketData != null) {
                            mTheLastDomesticPrice.add(marketData.getLastPrice());
                        } else {
                            mTheLastDomesticPrice.add(0d);
                        }
                    }
                }

                mOptionalForeignView.setVisibility(mForeignPkgList.isEmpty() ? View.GONE : View.VISIBLE);
                mOptionalDomesticView.setVisibility(mDomesticPkgList.isEmpty() ? View.GONE : View.VISIBLE);

                if (!mIsScrolling) {
                    Log.e(TAG, "run: " + mIsScrolling);
                    mOptionalForeignWrapper.notifyDataSetChanged();
                    mOptionalDomesticWrapper.notifyDataSetChanged();
                }

                if (!mDomesticPkgList.isEmpty()) {
                    ProductPkg productPkg = mDomesticPkgList.get(0);
                    if (productPkg != null) {
                        Product firstProduct = productPkg.getProduct();
                        TextView headerTitle = (TextView) mOptionalDomesticHeader.findViewById(R.id.headerTitle);
                        if (firstProduct.getExchangeStatus() == Product.MARKET_STATUS_CLOSE) {
                            String openMarketTime = firstProduct.getOpenMarketTime();
                            String exchangeNextOpenTime = getExchangeNextOpenTime(openMarketTime);
                            exchangeNextOpenTime = "    " + getString(R.string.exchange_next_open_time, exchangeNextOpenTime);
                            SpannableString spannableString = StrUtil.mergeTextWithRatioColor(
                                    getString(R.string.domestic_futures),
                                    exchangeNextOpenTime, 0.8f, Color.parseColor("#C0C0C0"));
                            headerTitle.setText(spannableString);
                        } else {
                            headerTitle.setText(getString(R.string.domestic_futures));
                        }
                    }
                }
            }
        }.run();
    }

    private String getExchangeNextOpenTime(String openMarketTime) {
        String curTime = DateUtil.format(SysTime.getSysTime().getSystemTimestamp(), "HH:mm");
        if (!TextUtils.isEmpty(openMarketTime)) {
            String[] openTimes = openMarketTime.split(";");
            if (openTimes.length > 0 && openTimes.length % 2 == 0) {
                int i = 1;
                for (; i < openTimes.length; i++) {
                    if (isBetween(openTimes[i - 1], openTimes[i], curTime)) {
                        return openTimes[i];
                    }
                }
                if (i == openTimes.length) {
                    if (isBetween(openTimes[i - 1], openTimes[0], curTime)) {
                        return openTimes[0];
                    }
                }
            }
        }
        return null;
    }

    private boolean isBetween(String time1, String time2, String time) {
        if (time1.compareTo(time2) <= 0) {
            return time.compareTo(time1) >= 0 && time.compareTo(time2) < 0;
        } else {
            return time.compareTo(time1) >= 0 || time.compareTo(time2) < 0;
        }
    }

    private void updateOptionalList(List<String> optionals, List<ProductPkg> targetList) {
        if (optionals != null) {
            for (String str : optionals) {
                for (ProductPkg productPkg : mProductPkgList) {
                    if (String.valueOf(productPkg.getProduct().getVarietyType()).equals(str)) {
                        targetList.add(productPkg);
                    }
                }
            }
        } else {
            for (ProductPkg productPkg : mProductPkgList) {
                if (targetList == mForeignPkgList && productPkg.getProduct().isForeign()) {
                    targetList.add(productPkg);
                }
                if (targetList == mDomesticPkgList && productPkg.getProduct().isDomestic()) {
                    targetList.add(productPkg);
                }
            }
        }
    }

    private void requestProductMarketList() {
        API.Market.getProductMarketList().setTag(TAG)
                .setCallback(new Callback<Resp<List<MarketData>>>(false) {
                    @Override
                    public void onReceive(Resp<List<MarketData>> listResp) {
                        if (listResp.isSuccess()) {
                            mMarketDataList = listResp.getData();
                            ProductPkg.updateMarketInProductPkgList(mProductPkgList, mMarketDataList);
                            updateOptionalLists();
                        }
                    }
                }).fireSync();
    }

    // call from MainActivity
    public void updatePositions(HomePositions homePositions) {
        if (homePositions != null) {
            mCashPositionList = homePositions.getCashOpS();
            ProductPkg.updatePositionInProductPkg(mProductPkgList, mCashPositionList);
            updateOptionalLists();

            updateSimulateButton(homePositions);
        } else {
            ProductPkg.clearPositions(mProductPkgList);
            mCashPositionList = null;
            updateOptionalLists();

            mHomeHeader.setSimulationHolding(null);
        }
    }

    private void updateSimulateButton(HomePositions homePositions) {
        if (mHomeHeader == null) return;
        if (homePositions.getIntegralOpS().size() > 0) {
            mHomeHeader.setSimulationHolding(homePositions.getIntegralOpS());
        } else {
            mHomeHeader.setSimulationHolding(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ProductOptionalActivity.REQ_CODE_RESULT) {
            mTheLastForeignPrice.clear();
            mTheLastDomesticPrice.clear();
            updateOptionalLists();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.riskEvaluation, R.id.contactService})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.riskEvaluation:
                Launcher.with(getActivity(), WebViewActivity.class)
                        .putExtra(WebViewActivity.EX_TITLE, getContext().getString(R.string.futures_risk_tips_title))
                        .putExtra(WebViewActivity.EX_URL, API.getFuturesRiskTips())
                        .execute();
                break;
            case R.id.contactService:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HOME_PAGE_CONNECT_SERVICE);
                String serviceQQUrl = API.getServiceQQ(Preference.get().getServiceQQ(), Preference.get().getQQType());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceQQUrl));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    ToastUtil.show(R.string.install_qq_first);
                }
                break;
        }
    }

    class OptionalListAdapter extends RecyclerView.Adapter<OptionalListAdapter.ViewHolder> {

        private Context mContext;
        private List<ProductPkg> mList;
        private View mHeaderView;
        List<Double> mTempPrice;

        public void setHeaderView(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return mHeaderView;
        }

        public OptionalListAdapter(Context context, List<ProductPkg> data, List<Double> domesticPrice) {
            mContext = context;
            mList = data;
            mTempPrice = domesticPrice;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = View.inflate(App.getAppContext(), R.layout.row_home_product, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(mContext, mList.get(position), mTempPrice, position);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.productName)
            TextView mProductName;
            @BindView(R.id.hotIcon)
            ImageView mHotIcon;
            @BindView(R.id.newTag)
            TextView mNewTag;
            @BindView(R.id.marketCloseText)
            TextView mMarketCloseText;
            @BindView(R.id.holdingPosition)
            TextView mHoldingPosition;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.priceChangePercent)
            TextView mPriceChangePercent;
            @BindView(R.id.bgTwinkle)
            LinearLayout mBgTwinkle;

            private View mView;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                ButterKnife.bind(this, mView);
            }

            public void bindData(Context context, final ProductPkg pkg, List<Double> tempList, int pos) {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pkg != null) {
                            MobclickAgent.onEvent(getActivity(),
                                    UmengCountEventIdUtils.getProductUmengEventId(pkg.getProduct(), Product.FUND_TYPE_CASH));
                            Launcher.with(getActivity(), TradeActivity.class)
                                    .putExtra(Product.EX_PRODUCT, pkg.getProduct())
                                    .putExtra(Product.EX_FUND_TYPE, Product.FUND_TYPE_CASH)
                                    .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(mProductList))
                                    .execute();
                        }
                    }
                });
                Product product = pkg.getProduct();
                MarketData marketData = pkg.getMarketData(); // Market status
                mProductName.setText(product.getVarietyName());
                if (product.getExchangeStatus() == Product.MARKET_STATUS_CLOSE) {
                    mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.market_close_color));
                    mPriceChangePercent.setTextColor(ContextCompat.getColor(context, R.color.market_close_color));
                    mHotIcon.setVisibility(View.GONE);
                    mNewTag.setVisibility(View.GONE);
                    mHoldingPosition.setVisibility(View.GONE);
                    mMarketCloseText.setVisibility(View.VISIBLE);
                    if ((marketData != null)) {
                        String priceChangePercent = marketData.getPercentage();
                        mLastPrice.setText(FinanceUtil.formatWithScale(marketData.getLastPrice(),
                                product.getPriceDecimalScale()));
                        if (priceChangePercent.startsWith("-")) {
                            mPriceChangePercent.setText(priceChangePercent);
                        } else {
                            mPriceChangePercent.setText("+" + priceChangePercent);
                        }
                    } else {
                        mLastPrice.setText("——");
                        mPriceChangePercent.setText("——%");
                    }
                } else {
                    mHotIcon.setVisibility(product.getTags() == Product.TAG_HOT ? View.VISIBLE : View.GONE);
                    mNewTag.setVisibility(product.getTags() == Product.TAG_NEW ? View.VISIBLE : View.GONE);
                    mProductName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    mMarketCloseText.setVisibility(View.GONE);
                    if (marketData != null) {
                        mLastPrice.setText(FinanceUtil.formatWithScale(marketData.getLastPrice(),
                                product.getPriceDecimalScale()));
                        String priceChangePercent = marketData.getPercentage();
                        if (priceChangePercent.startsWith("-")) {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                            mPriceChangePercent.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                            mPriceChangePercent.setText(priceChangePercent);
                            setTwinkleColor(marketData, R.color.twentyGreen, tempList.get(pos));
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mPriceChangePercent.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mPriceChangePercent.setText("+" + priceChangePercent);
                            setTwinkleColor(marketData, R.color.twentyRed, tempList.get(pos));
                        }
                        tempList.remove(pos);
                        tempList.add(pos, marketData.getLastPrice());
                    } else {
                        mLastPrice.setText("——");
                        mPriceChangePercent.setText("——%");
                    }
                    HomePositions.Position position = pkg.getPosition(); // Position status
                    if (position != null && position.getHandsNum() > 0) {
                        mHoldingPosition.setVisibility(View.VISIBLE);
                        if (product.getTags() == Product.TAG_HOT || product.getTags() == Product.TAG_NEW) {
                            mHotIcon.setVisibility(View.GONE);
                            mNewTag.setVisibility(View.GONE);
                        }
                    } else {
                        mHoldingPosition.setVisibility(View.GONE);
                    }
                }
            }

            private void setTwinkleColor(MarketData marketData, int color, Double tempPrice) {
                if (tempPrice != marketData.getLastPrice()) {
                    mBgTwinkle.setBackgroundColor(ContextCompat.getColor(getContext(), color));
                    mBgTwinkle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBgTwinkle.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 500);
                }
            }
        }
    }
}
