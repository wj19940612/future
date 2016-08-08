package com.jnhyxx.html5.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.HomeAdvertisement;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.domain.market.MarketBrief;
import com.jnhyxx.html5.domain.market.PositionBrief;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.adapter.GroupAdapter;
import com.jnhyxx.html5.view.HomeListHeader;
import com.johnz.kutils.FinanceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;

    private Unbinder mBinder;

    private List<ProductPkg> mProductPkgList;
    private List<PositionBrief> mPositionBriefList;
    private List<MarketBrief> mMarketBriefList;

    private ProductPkgAdapter mProductPkgAdapter;
    private HomeListHeader mHomeListHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hall, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductPkgList = new ArrayList<>();

        if (mHomeListHeader == null) {
            mHomeListHeader = new HomeListHeader(getContext());
            mList.addHeaderView(mHomeListHeader);
        }
        mList.setEmptyView(mEmpty);

        requestHomeAdvertisement();
        requestProductList();
        requestProductMarketBriefList();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPositionBriefList();
    }

    private void requestHomeAdvertisement() {
        API.Account.getHomeAdvertisements()
                .setCallback(new Resp.Callback<Resp<HomeAdvertisement>, HomeAdvertisement>() {
                    @Override
                    public void onRespSuccess(HomeAdvertisement homeAdvertisement) {
                        mHomeListHeader.setHomeAdvertisement(homeAdvertisement);
                    }
                }).setTag(TAG).post();
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG).setIndeterminate(this)
                .setCallback(new Resp.Callback<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        ProductPkg.updateProductPkgList(mProductPkgList, products,
                                mPositionBriefList, mMarketBriefList);
                        updateProductListView();
                    }
                }).post();
    }

    private void requestProductMarketBriefList() {
        API.Market.getProductMarketBreifList()
                .setCallback(new Callback<Resp<List<MarketBrief>>>() {
                    @Override
                    public void onSuccess(Resp<List<MarketBrief>> listResp) {
                        if (listResp.isSuccess()) {
                            mMarketBriefList = listResp.getData();
                            boolean updateProductList =
                                    ProductPkg.updateMarketInProductPkgList(mProductPkgList, mMarketBriefList);
                            if (updateProductList) {
                                requestProductList();
                            }
                        }
                    }
                }).setTag(TAG).post();
    }

    private void requestPositionBriefList() {
        if (User.getUser().isLogin()) {
            API.Order.getOrderPositionList(User.getUser().getToken())
                    .setCallback(new Resp.Callback<Resp<List<PositionBrief>>, List<PositionBrief>>() {
                        @Override
                        public void onRespSuccess(List<PositionBrief> positionBriefs) {
                            mPositionBriefList = positionBriefs;
                            boolean updateProductList =
                                    ProductPkg.updatePositionInProductPkg(mProductPkgList, mPositionBriefList);
                            if (updateProductList) {
                                requestProductList();
                            }
                        }
                    }).setTag(TAG).post();
        } else { // clear all product position
            ProductPkg.clearPositionBriefs(mProductPkgList);
        }
    }

    private void updateProductListView() {
        if (mProductPkgList == null || mProductPkgList.size() == 0) return;

        if (mProductPkgAdapter == null) {
            mProductPkgAdapter = new ProductPkgAdapter(getContext(), mProductPkgList);
            mList.setAdapter(mProductPkgAdapter);
        } else {
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
                mProductName.setText(pkg.getProduct().getCommodityName());
                mAdvertisement.setText(pkg.getProduct().getAdvertisement());
                Product product = pkg.getProduct();
                if (product.getTag() == Product.TAG_HOT) {
                    mHotIcon.setVisibility(View.VISIBLE);
                } else {
                    mHotIcon.setVisibility(View.GONE);
                }
                if (product.getMarketStatus() == Product.MARKET_STATUS_CLOSE) {
                    mProductName.setTextColor(context.getResources().getColor(R.color.blackHalfTransparent));
                    mAdvertisement.setTextColor(Color.parseColor("#7FA8A8A8"));
                    mHoldingPosition.setVisibility(View.GONE);
                    mMarketCloseText.setVisibility(View.VISIBLE);
                    mMarketCloseArea.setVisibility(View.VISIBLE);
                    mPriceChangeArea.setVisibility(View.GONE);
                    mMarketOpenTime.setText(createMarketOpenTime(product, context));
                } else {
                    mProductName.setTextColor(context.getResources().getColor(android.R.color.black));
                    mAdvertisement.setTextColor(Color.parseColor("#A8A8A8"));
                    mMarketCloseText.setVisibility(View.GONE);
                    mMarketCloseArea.setVisibility(View.GONE);
                    mPriceChangeArea.setVisibility(View.VISIBLE);
                    MarketBrief marketBrief = pkg.getMarketBrief(); // Market status
                    if (marketBrief != null) {
                        mLastPrice.setText(FinanceUtil.formatWithScale(marketBrief.getLastPrice(),
                                product.getDecimalPlaces()));
                        mPriceChangePercent.setText(marketBrief.getUnsignPercentage());
                        String priceChangePercent = marketBrief.getPercentage();
                        if (priceChangePercent.startsWith("-")) {
                            mLastPrice.setTextColor(context.getResources().getColor(R.color.greenPrimary));
                            mPriceChangePercent.setBackgroundResource(R.drawable.bg_green_primary);
                            mPriceChangePercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
                        } else {
                            mLastPrice.setTextColor(context.getResources().getColor(R.color.redPrimary));
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
                String timeLine = product.getTimeline();
                String[] timeSplit = timeLine.split(";");
                String startTime = timeSplit[0];
                String endTime = timeSplit[timeSplit.length - 1];
                startTime = addChinesePrefix(startTime, context);
                endTime = addChinesePrefix(endTime, context);
                return startTime + "~" + endTime;
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
