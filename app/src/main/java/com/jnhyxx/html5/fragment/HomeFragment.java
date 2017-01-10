package com.jnhyxx.html5.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.SimulationActivity;
import com.jnhyxx.html5.activity.TradeActivity;
import com.jnhyxx.html5.activity.web.BannerActivity;
import com.jnhyxx.html5.activity.web.HideTitleWebActivity;
import com.jnhyxx.html5.activity.web.NewbieActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.MarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.OnItemOneClickListener;
import com.jnhyxx.html5.utils.StrFormatter;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.adapter.GroupAdapter;
import com.jnhyxx.html5.view.HomeListHeader;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.string.service_phone;

public class HomeFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;

    private Unbinder mBinder;

    private List<ProductPkg> mProductPkgList;
    private List<Product> mProductList;
    private List<HomePositions.CashOpSBean> mCashPositionList;
    private List<MarketData> mMarketDataList;

    private ProductPkgAdapter mProductPkgAdapter;
    private HomeListHeader mHomeListHeader;
    private View mHomeListFooter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductPkgList = new ArrayList<>();
        mHomeListHeader = new HomeListHeader(getContext());
        mHomeListFooter = LayoutInflater.from(getContext()).inflate(R.layout.footer_home, null);
        mHomeListHeader.setOnViewClickListener(new HomeListHeader.OnViewClickListener() {
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

            //新手引导
            @Override
            public void onNewerGuideClick() {
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HOME_PAGE_NEWBIE_GUIDE);
                Launcher.with(getActivity(), NewbieActivity.class)
                        .putExtra(NewbieActivity.EX_URL, API.getNewbieUrl())
                        .putExtra(NewbieActivity.EX_TITLE, getString(R.string.newbie_title))
                        .putExtra(NewbieActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                        .execute();
            }

            @Override
            public void onContactService() {
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.HOME_PAGE_CONNECT_SERVICE);
                String serviceQQUrl = API.getServiceQQ(Preference.get().getServiceQQ());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceQQUrl));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    ToastUtil.show(R.string.install_qq_first);
                }
            }
        });
        mList.addHeaderView(mHomeListHeader);
        mList.setEmptyView(mEmpty);
        mList.addFooterView(mHomeListFooter);
        initHomeListFooterListeners();
        mProductPkgAdapter = new ProductPkgAdapter(getContext(), mProductPkgList);
        mList.setAdapter(mProductPkgAdapter);
        mList.setOnItemClickListener(new OnItemOneClickListener() {
            @Override
            public void onItemOneClick(AdapterView<?> parent, View view, int position, long id) {
                ProductPkg pkg = (ProductPkg) parent.getItemAtPosition(position);
                if (pkg != null) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.getProductUmengEventId(pkg.getProduct(), Product.FUND_TYPE_CASH));
                    Launcher.with(getActivity(), TradeActivity.class)
                            .putExtra(Product.EX_PRODUCT, pkg.getProduct())
                            .putExtra(Product.EX_FUND_TYPE, Product.FUND_TYPE_CASH)
                            .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(mProductList))
                            .execute();
                }
            }
        });

        requestHomeInformation();
        //requestOrderReport();
        requestProductMarketList();
    }

    private void initHomeListFooterListeners() {
        mHomeListFooter.findViewById(R.id.fundSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mHomeListFooter.findViewById(R.id.riskInformed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mHomeListFooter.findViewById(R.id.mediaReports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView servicePhone = (TextView) mHomeListFooter.findViewById(R.id.servicePhone);
        String servicePhoneNum = Preference.get().getServicePhone();
        if (TextUtils.isEmpty(servicePhoneNum)) {
            servicePhone.setVisibility(View.GONE);
        } else {
            servicePhoneNum = StrFormatter.getFormatServicePhone(servicePhoneNum);
            servicePhone.setText(getString(service_phone, servicePhoneNum));
        }
    }

//    private void requestOrderReport() {
//        API.Order.getReportData().setCallback(new Callback<Resp<OrderReport>>() {
//            @Override
//            public void onReceive(Resp<OrderReport> orderReportResp) {
//                if (orderReportResp.isSuccess()) {
//                    mHomeListHeader.setOrderReport(orderReportResp.getData());
//                }
//            }
//        }).setTag(TAG).fire();
//    }

    @Override
    public void onTimeUp(int count) {
        if (getUserVisibleHint()) {
            requestProductMarketList();
            mHomeListHeader.nextOrderReport();
            mHomeListHeader.nextAdvertisement();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestProductList();
        requestHomePositions();
        startScheduleJob(5 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    private void requestHomeInformation() {
        API.User.getNewsList(Information.TYPE_BANNER, 0, 10)
                .setCallback(new Callback2<Resp<List<Information>>, List<Information>>() {
                    @Override
                    public void onRespSuccess(List<Information> informationList) {
                        mHomeListHeader.setHomeAdvertisement(informationList);
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
                        updateProductListView();
                    }
                }).fire();
    }

    private void requestProductMarketList() {
        API.Market.getProductMarketList().setTag(TAG)
                .setCallback(new Callback<Resp<List<MarketData>>>(false) {
                    @Override
                    public void onReceive(Resp<List<MarketData>> listResp) {
                        if (listResp.isSuccess()) {
                            mMarketDataList = listResp.getData();
                            ProductPkg.updateMarketInProductPkgList(mProductPkgList, mMarketDataList);
                            updateProductListView();
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
                            Log.d("VolleyHttp", getUrl() + " onSuccess: " + homePositionsResp.toString());
                            if (homePositionsResp.isSuccess()) {
                                HomePositions homePositions = homePositionsResp.getData();
                                updateSimulateButton(homePositions);

                                mCashPositionList = homePositions.getCashOpS();
                                ProductPkg.updatePositionInProductPkg(mProductPkgList, mCashPositionList);
                                updateProductListView();
                            }
                        }

                        @Override
                        public void onReceive(Resp<HomePositions> homePositionsResp) {
                        }
                    }).fire();
        } else { // clearHoldingOrderList all product position
            ProductPkg.clearPositions(mProductPkgList);
            mHomeListHeader.setSimulationHolding(false);
            mCashPositionList = null;
            updateProductListView();
        }
    }

    private void updateSimulateButton(HomePositions homePositions) {
        if (mHomeListHeader == null) return;
        if (homePositions.getIntegralOpS().size() > 0) {
            mHomeListHeader.setSimulationHolding(true);
        } else {
            mHomeListHeader.setSimulationHolding(false);
        }
    }

    private void updateProductListView() {
        if (mProductPkgList == null || mProductPkgList.size() == 0) return;
        if (mProductPkgAdapter != null) {
            mProductPkgAdapter.setGroupableList(mProductPkgList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    static class ProductPkgAdapter extends GroupAdapter<ProductPkg> {

        public ProductPkgAdapter(Context context, List<ProductPkg> groupableList) {
            super(context, groupableList);
        }

        @Override
        protected View getView(int position, View view, ViewGroup viewGroup, int type) {
            switch (type) {
                case HEAD:
                    if (view == null) {
                        view = LayoutInflater.from(getContext()).inflate(R.layout.row_product_header, null);
                    }
                    Groupable groupable = (Groupable) getItem(position);
                    TextView textView = (TextView) view.findViewById(R.id.headerTitle);
                    textView.setText(groupable.getGroupName());
                    break;
                case ITEM:
                    ViewHolder viewHolder;
                    if (view == null) {
                        view = LayoutInflater.from(getContext()).inflate(R.layout.row_product, null);
                        viewHolder = new ViewHolder(view);
                        view.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) view.getTag();
                    }
                    viewHolder.bindingData(getItem(position), getContext());
                    break;
            }
            return view;
        }

        static class ViewHolder {
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
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.priceChangePercent)
            TextView mPriceChangePercent;
            @BindView(R.id.priceChangeArea)
            LinearLayout mPriceChangeArea;
            @BindView(R.id.marketOpenTime)
            TextView mMarketOpenTime;
            @BindView(R.id.marketCloseArea)
            LinearLayout mMarketCloseArea;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Object item, Context context) {
                if (!(item instanceof ProductPkg)) return;

                ProductPkg pkg = (ProductPkg) item;
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
                            mPriceChangePercent.setBackgroundResource(R.drawable.bg_green_primary);
                            mPriceChangePercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mPriceChangePercent.setBackgroundResource(R.drawable.bg_red_primary);
                            mPriceChangePercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_arrow, 0, 0, 0);
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
}
