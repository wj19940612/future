package com.jnhyxx.html5.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.App;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.ProductOptionalActivity;
import com.jnhyxx.html5.activity.SimulationActivity;
import com.jnhyxx.html5.activity.TradeActivity;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.web.BannerActivity;
import com.jnhyxx.html5.activity.web.HideTitleWebActivity;
import com.jnhyxx.html5.activity.web.InvestCourseActivity;
import com.jnhyxx.html5.activity.web.NewbieActivity;
import com.jnhyxx.html5.activity.web.PaidToPromoteActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.MarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.domain.order.OrderReport;
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
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.viewPager;


public class HomeFragment extends BaseFragment {
    @BindView(R.id.homeBanner)
    HomeBanner mHomeBanner;
    @BindView(R.id.riskEvaluation)
    LinearLayout mRiskEvaluation;
    @BindView(R.id.contactService)
    LinearLayout mContactService;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout mCollapsing;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.homeHeader)
    HomeHeader mHomeHeader;
    @BindView(R.id.optionalForeignList)
    RecyclerView mOptionalForeignList;
    @BindView(R.id.optionalDomesticList)
    RecyclerView mOptionalDomesticList;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.replaceLayout)
    RelativeLayout mReplaceLayout;

    private Unbinder mBind;

    private List<ProductPkg> mProductPkgList;
    private List<Product> mProductList;
    private List<HomePositions.CashOpSBean> mCashPositionList;
    private List<MarketData> mMarketDataList;
    private List<ProductPkg> mForeignPackgae;
    private List<ProductPkg> mDomesticPackgae;
    private HeaderAndFooterWrapper mOptinalForeignWrapper;
    private HeaderAndFooterWrapper mOptionalDomesticWrapper;
    public static final int REQ_CODE_FOREIGN = 100;
    public static final int REQ_CODE_DOMESTIC = 101;


    public interface OnListViewHeightListener {
        /**
         *
         * @param height  控件高度
         */
        void listViewHeight(int height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newhome, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSlidingTabLayout();

        mProductPkgList = new ArrayList<>();
        mForeignPackgae = new ArrayList<ProductPkg>();
        mDomesticPackgae = new ArrayList<ProductPkg>();
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

        setOptionalProduct();
        requestHomeInformation();
        requestProductMarketList();
    }

    private void setOptionalProduct() {
        MyAdapter foreignAdapter = new MyAdapter(getContext(), mForeignPackgae);
        mOptionalForeignList.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        mOptionalForeignList.addItemDecoration(new DividerGridItemDecoration(getContext()));
        mOptinalForeignWrapper = new HeaderAndFooterWrapper(foreignAdapter);
        View foreignHeadView = View.inflate(getContext(), R.layout.optional_list_head, null);
        TextView headTitle1 = (TextView) foreignHeadView.findViewById(R.id.headerTitle);
        ImageView optionalAdd1 = (ImageView) foreignHeadView.findViewById(R.id.optionalAdd);
        optionalAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), ProductOptionalActivity.class)
                        .putExtra("isDomestic", false)
                        .executeForResult(REQ_CODE_DOMESTIC);
            }
        });
        headTitle1.setText("国际期货");
        mOptinalForeignWrapper.addHeaderView(foreignHeadView);
        mOptionalForeignList.setAdapter(mOptinalForeignWrapper);

        MyAdapter domesticAdapter = new MyAdapter(getContext(), mDomesticPackgae);
        mOptionalDomesticWrapper = new HeaderAndFooterWrapper(domesticAdapter);
        mOptionalDomesticList.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        mOptionalDomesticList.addItemDecoration(new DividerGridItemDecoration(getContext()));
        View domesticHeadView = View.inflate(getContext(), R.layout.optional_list_head, null);
        TextView headTitle2 = (TextView) domesticHeadView.findViewById(R.id.headerTitle);
        ImageView optionalAdd2 = (ImageView) domesticHeadView.findViewById(R.id.optionalAdd);
        optionalAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), ProductOptionalActivity.class)
                        .putExtra("isDomestic", true)
                        .executeForResult(REQ_CODE_FOREIGN);
            }
        });
        headTitle2.setText("国际期货");
        mOptionalDomesticWrapper.addHeaderView(domesticHeadView);
        mOptionalDomesticList.setAdapter(mOptionalDomesticWrapper);
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
        requestProductList();
        requestHomePositions();
        requestOrderReport();
        startScheduleJob(5 * 1000);
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestProductMarketList();
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
        calendarFinanceFragment.setOnListViewHeightListener(new OnListViewHeightListener() {
            @Override
            public void listViewHeight(int height) {
                ViewGroup.LayoutParams layoutParams = mReplaceLayout.getLayoutParams();
                layoutParams.height = height;
                mReplaceLayout.setLayoutParams(layoutParams);
            }
        });
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

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList = products;
                        ProductPkg.updateProductPkgList(mProductPkgList, products,
                                mCashPositionList, mMarketDataList);
                        updateOptionalList();
                    }
                }).fire();
    }

    private void updateOptionalList() {
        String productOptionalForeign = Preference.get().getProductOptionalForeign();
        String productOptionalDomestic = Preference.get().getProductOptionalDomestic();
        List<String> optionalForeigns = getOptionalList(productOptionalForeign);
        List<String> optionalDomestics = getOptionalList(productOptionalDomestic);
        mForeignPackgae.clear();
        mDomesticPackgae.clear();
        for (ProductPkg productPkg : mProductPkgList) {
            Product product = productPkg.getProduct();
            if (product.isForeign()) {
                if (optionalForeigns != null) {
                    addOptionalProduct(optionalForeigns, productPkg, mForeignPackgae);
                } else {
                    if (mForeignPackgae.size() < 3) {
                        mForeignPackgae.add(productPkg);
                    }
                }
            } else {
                if (optionalDomestics != null) {
                    addOptionalProduct(optionalDomestics, productPkg, mDomesticPackgae);
                } else {
                    if (mDomesticPackgae.size() < 3) {
                        mDomesticPackgae.add(productPkg);
                    }
                }
            }
        }
        mOptinalForeignWrapper.notifyDataSetChanged();
        mOptionalDomesticWrapper.notifyDataSetChanged();
    }

    private void addOptionalProduct(List<String> optionals, ProductPkg productPkg, List<ProductPkg> target) {
        for (String optionalForeign : optionals) {
            if (String.valueOf(productPkg.getProduct().getVarietyId()).equals(optionalForeign)) {
                target.add(productPkg);
                break;
            }
        }
    }

    private List<String> getOptionalList(String productOptionalForeign) {
        if (!TextUtils.isEmpty(productOptionalForeign)) {
            String[] foreignSplit = productOptionalForeign.split(",");
            return Arrays.asList(foreignSplit);
        }
        return null;
    }

    private void requestProductMarketList() {
        API.Market.getProductMarketList().setTag(TAG)
                .setCallback(new Callback<Resp<List<MarketData>>>(false) {
                    @Override
                    public void onReceive(Resp<List<MarketData>> listResp) {
                        if (listResp.isSuccess()) {
                            mMarketDataList = listResp.getData();
                            ProductPkg.updateMarketInProductPkgList(mProductPkgList, mMarketDataList);
                            updateOptionalList();
                        }
                    }
                }).fire();
    }

    private void requestHomePositions() {
        if (LocalUser.getUser().isLogin()) {
            API.Order.getHomePositions().setTag(TAG)
                    .setCallback(new Callback<Resp<HomePositions>>(false) {
                        @Override
                        public void onSuccess(Resp<HomePositions> homePositionsResp) {
                            if (homePositionsResp.isSuccess()) {
                                HomePositions homePositions = homePositionsResp.getData();
                                updateSimulateButton(homePositions);

                                mCashPositionList = homePositions.getCashOpS();
                                ProductPkg.updatePositionInProductPkg(mProductPkgList, mCashPositionList);
                                updateOptionalList();
                            }
                        }

                        @Override
                        public void onReceive(Resp<HomePositions> homePositionsResp) {
                        }
                    }).fire();
        } else { // clearHoldingOrderList all product position
            ProductPkg.clearPositions(mProductPkgList);
            updateOptionalList();
            mHomeHeader.setSimulationHolding(null);
            mCashPositionList = null;
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
        if (data != null) {
            if (resultCode == ProductOptionalActivity.REQ_CODE_RESULT) {
                updateOptionalList();
            }
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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context mContext;
        private List<ProductPkg> mList;
        private View mHeaderView;

        public void setHeaderView(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return mHeaderView;
        }

        public MyAdapter(Context context, List<ProductPkg> datas) {
            mContext = context;
            mList = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = View.inflate(App.getAppContext(), R.layout.row_home_product, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(mContext, mList.get(position));
            Log.e(TAG, "onBindViewHolder: " + mList.get(position).getProduct().getVarietyName());
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
            @BindView(R.id.advertisement)
            TextView mAdvertisement;
            @BindView(R.id.marketOpenTime)
            TextView mMarketOpenTime;
            @BindView(R.id.marketCloseArea)
            LinearLayout mMarketCloseArea;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.priceChangePercent)
            TextView mPriceChangePercent;
            @BindView(R.id.priceChangeArea)
            LinearLayout mPriceChangeArea;
            private View mView;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                ButterKnife.bind(this, mView);
            }

            public void bindData(Context context, final ProductPkg pkg) {
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
                mProductName.setText(product.getVarietyName());
                mAdvertisement.setText(product.getAdvertisement());
                if (product.getExchangeStatus() == Product.MARKET_STATUS_CLOSE) {
                    mProductName.setTextColor(ContextCompat.getColor(context, R.color.blackHalfTransparent));
                    mAdvertisement.setTextColor(Color.parseColor("#7FA8A8A8"));
                    mHotIcon.setVisibility(View.GONE);
                    mNewTag.setVisibility(View.GONE);
                    mHoldingPosition.setVisibility(View.GONE);
                    mMarketCloseText.setVisibility(View.VISIBLE);
                    mMarketCloseArea.setVisibility(View.VISIBLE);
                    mPriceChangeArea.setVisibility(View.GONE);
                    String marketOpenTime = createMarketOpenTime(product, context);
                    mMarketOpenTime.setText(marketOpenTime);
                } else {
                    mHotIcon.setVisibility(product.getTags() == Product.TAG_HOT ? View.VISIBLE : View.GONE);
                    mNewTag.setVisibility(product.getTags() == Product.TAG_NEW ? View.VISIBLE : View.GONE);
                    mProductName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    mAdvertisement.setTextColor(Color.parseColor("#A8A8A8"));
                    mMarketCloseText.setVisibility(View.GONE);
                    mMarketCloseArea.setVisibility(View.GONE);
                    mPriceChangeArea.setVisibility(View.VISIBLE);
                    MarketData marketData = pkg.getMarketData(); // Market status
                    if (marketData != null) {
                        mLastPrice.setText(FinanceUtil.formatWithScale(marketData.getLastPrice(),
                                product.getPriceDecimalScale()));
                        mPriceChangePercent.setText(marketData.getUnsignedPercentage());
                        String priceChangePercent = marketData.getPercentage();
                        if (priceChangePercent.startsWith("-")) {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                            mPriceChangePercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
                            ViewGroup parent = (ViewGroup) mPriceChangePercent.getParent();
                            parent.setBackgroundResource(R.drawable.bg_green_primary);
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mPriceChangePercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_arrow, 0, 0, 0);
                            ViewGroup parent = (ViewGroup) mPriceChangePercent.getParent();
                            parent.setBackgroundResource(R.drawable.bg_red_primary);
                        }
                    } else {
                        mLastPrice.setText("——");
                        mPriceChangePercent.setText("——%");
                        mPriceChangePercent.setCompoundDrawables(null, null, null, null);
                    }
                    HomePositions.Position position = pkg.getPosition(); // Position status
                    if (position != null && position.getHandsNum() > 0) {
                        mHoldingPosition.setVisibility(View.VISIBLE);
                    } else {
                        mHoldingPosition.setVisibility(View.GONE);
                    }
                }

            }

            private String createMarketOpenTime(Product product, Context context) {
                String timeLine = product.getOpenMarketTime();
                if (!TextUtils.isEmpty(timeLine)) {
                    String[] timeSplit = timeLine.split(";");
                    String startTime = timeSplit[0];
                    String endTime = timeSplit[timeSplit.length - 1];
                    endTime = addChinesePrefix(startTime, endTime, context);
                    return startTime + "~" + endTime;
                }
                return "";
            }

            private String addChinesePrefix(String startTime, String endTime, Context context) {
                if (startTime.compareTo(endTime) > 0) {
                    return context.getString(R.string.next_day) + endTime;
                }
                return endTime;
            }
        }
    }


    public static class HomeInfoFragmentPagerAdapter extends FragmentPagerAdapter {

        Context mContext;
        FragmentManager mFragmentManager;

        public HomeInfoFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new YesterdayProfitRankFragment();
                case 1:
                    return TradingStrategyFragment.newInstance();
                case 2:
                    return CalendarFinanceFragment.newInstance();
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

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + viewPager + ":" + position);
        }
    }
}
