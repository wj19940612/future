package com.jnhyxx.html5.fragment;


import android.content.Context;
import android.graphics.Color;
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

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.TradeActivity;
import com.jnhyxx.html5.domain.HomeAdvertisement;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.domain.market.MarketBrief;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.OrderReport;
import com.jnhyxx.html5.domain.order.PositionBrief;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.adapter.GroupAdapter;
import com.jnhyxx.html5.view.HomeListHeader;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 首页的fragment
 */
public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";
    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;

    private Unbinder mBinder;

    private List<ProductPkg> mProductPkgList;
    private List<Product> mProductList;
    private List<PositionBrief> mPositionBriefList;
    private List<MarketBrief> mMarketBriefList;

    private ProductPkgAdapter mProductPkgAdapter;
    private HomeListHeader mHomeListHeader;

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
        mList.addHeaderView(mHomeListHeader);
        mList.setEmptyView(mEmpty);
        mProductPkgAdapter = new ProductPkgAdapter(getContext(), mProductPkgList);
        mList.setAdapter(mProductPkgAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ProductPkg pkg = (ProductPkg) adapterView.getItemAtPosition(position);
                if (pkg != null) {
                    Launcher.with(getActivity(), TradeActivity.class)
                            .putExtra(Product.EX_PRODUCT, pkg.getProduct())
                            .putExtra(Product.EX_FUND_TYPE, Product.FUND_TYPE_CASH)
                            .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(mProductList))
                            .execute();
                }
            }
        });

        requestHomeAdvertisement();
        requestOrderReport();
        requestProductList();
        requestProductMarketBriefList();

        startScheduleJob(5 * 1000);
    }

    private void requestOrderReport() {
        API.Order.getReportData().setCallback(new Callback<Resp<OrderReport>>() {
            @Override
            public void onReceive(Resp<OrderReport> orderReportResp) {
                if (orderReportResp.isSuccess()) {
                    mHomeListHeader.setOrderReport(orderReportResp.getData());
                }
            }
        }).setTag(TAG).fire();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (getUserVisibleHint()) {
            requestProductMarketBriefList();
            mHomeListHeader.nextOrderReport();
            mHomeListHeader.nextAdvertisement();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPositionBriefList();
    }

    private void requestHomeAdvertisement() {
        API.Account.getHomeAdvertisements()
                .setCallback(new Callback2<Resp<HomeAdvertisement>, HomeAdvertisement>() {
                    @Override
                    public void onRespSuccess(HomeAdvertisement homeAdvertisement) {
                        mHomeListHeader.setHomeAdvertisement(homeAdvertisement);
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
                                mPositionBriefList, mMarketBriefList);
                        updateProductListView();
                    }
                }).fire();
    }

    private void requestProductMarketBriefList() {
        API.Market.getProductMarketBriefList()
                .setCallback(new Callback<Resp<List<MarketBrief>>>() {
                    @Override
                    public void onReceive(Resp<List<MarketBrief>> listResp) {
                        if (listResp.isSuccess()) {
                            mMarketBriefList = listResp.getData();
                            boolean updateProductList =
                                    ProductPkg.updateMarketInProductPkgList(mProductPkgList, mMarketBriefList);
                            if (updateProductList) {
                                // requestProductList(); // TODO: 8/10/16 add later
                            } else {
                                updateProductListView();
                            }
                        }
                    }
                }).setTag(TAG).fire();
    }

    private void requestPositionBriefList() {
        if (User.getUser().isLogin()) {
            API.Order.getOrderPositionList(User.getUser().getToken())
                    .setCallback(new Callback2<Resp<List<PositionBrief>>, List<PositionBrief>>() {
                        @Override
                        public void onRespSuccess(List<PositionBrief> positionBriefs) {
                            mPositionBriefList = positionBriefs;
                            boolean updateProductList =
                                    ProductPkg.updatePositionInProductPkg(mProductPkgList, mPositionBriefList);
                            if (updateProductList) {
                                requestProductList();
                            } else {
                                updateProductListView();
                            }
                        }
                    }).setTag(TAG).fire();
        } else { // clear all product position
            ProductPkg.clearPositionBriefs(mProductPkgList);
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
                mProductName.setText(pkg.getProduct().getVarietyName());
                mAdvertisement.setText(pkg.getProduct().getAdvertisement());
                Product product = pkg.getProduct();
                mHotIcon.setVisibility((product.getTags() == Product.TAG_HOT) ? View.VISIBLE : View.GONE);
                if (product.getExchangeStatus() == Product.MARKET_STATUS_CLOSE) {
                    mProductName.setTextColor(ContextCompat.getColor(context, R.color.blackHalfTransparent));
                    mAdvertisement.setTextColor(Color.parseColor("#7FA8A8A8"));
                    mHoldingPosition.setVisibility(View.GONE);
                    mMarketCloseText.setVisibility(View.VISIBLE);
                    mMarketCloseArea.setVisibility(View.VISIBLE);
                    mPriceChangeArea.setVisibility(View.GONE);
                    // TODO: 2016/8/19 刚进入界面程序崩溃，空指针； 
                    String marketOpenTime = createMarketOpenTime(product, context);
                    if (!TextUtils.isDigitsOnly(marketOpenTime)) {
                        mMarketOpenTime.setText(marketOpenTime);
                    } else {
                        mMarketOpenTime.setText("开市时间");
                    }
                } else {
                    mProductName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    mAdvertisement.setTextColor(Color.parseColor("#A8A8A8"));
                    mMarketCloseText.setVisibility(View.GONE);
                    mMarketCloseArea.setVisibility(View.GONE);
                    mPriceChangeArea.setVisibility(View.VISIBLE);
                    MarketBrief marketBrief = pkg.getMarketBrief(); // Market status
                    if (marketBrief != null) {
                        mLastPrice.setText(FinanceUtil.formatWithScale(marketBrief.getLastPrice(),
                                product.getDecimalScale()));
                        mPriceChangePercent.setText(marketBrief.getUnsignPercentage());
                        String priceChangePercent = marketBrief.getPercentage();
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
                    PositionBrief positionBrief = pkg.getPositionBrief(); // Position status
                    if (positionBrief != null && positionBrief.getCash() > 0) {
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
                    startTime = addChinesePrefix(startTime, context);
                    endTime = addChinesePrefix(endTime, context);
                    return startTime + "~" + endTime;
                }
                return "";
            }

            private String addChinesePrefix(String time, Context context) {
                if (time.compareTo("06:00") < 0) {
                    return context.getString(R.string.dawn) + time;
                } else if (time.compareTo("12:00") < 0) {
                    return context.getString(R.string.forenoon) + time;
                } else if (time.compareTo("18:00") < 0) {
                    return context.getString(R.string.afternoon) + time;
                } else if (time.compareTo("24:00") < 0) {
                    return context.getString(R.string.night) + time;
                }
                return "";
            }
        }
    }
}
