package com.jnhyxx.html5.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimulationActivity extends BaseActivity {

    @BindView(R.id.availableGold)
    TextView mAvailableGold;
    @BindView(R.id.goldStoreButton)
    TextView mGoldStoreButton;
    @BindView(R.id.activity_score)
    LinearLayout mActivityScore;
    @BindView(R.id.gridView)
    GridView mGridView;

    private List<HomePositions.IntegralOpSBean> mSimulationPositionList;
    private List<ProductPkg> mProductPkgList;
    private List<Product> mProductList;
    private ProductAdapter mProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        ButterKnife.bind(this);

        initData(getIntent());
        updateProductGridView();
    }

    private void initData(Intent intent) {
        mProductPkgList = new ArrayList<>();
        mProductList = intent.getParcelableArrayListExtra(Product.EX_PRODUCT_LIST);
        ProductPkg.updateProductPkgList(mProductPkgList, mProductList, mSimulationPositionList, null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestSimulationPositions();
    }

    private void requestSimulationPositions() {
        if (LocalUser.getUser().isLogin()) {
            API.Order.getHomePositions().setTag(TAG)
                    .setCallback(new Callback2<Resp<HomePositions>, HomePositions>() {
                        @Override
                        public void onRespSuccess(HomePositions homePositions) {
                            mSimulationPositionList = homePositions.getIntegralOpS();
                            boolean updateProductList =
                                    ProductPkg.updatePositionInProductPkg(mProductPkgList, mSimulationPositionList);
                            if (updateProductList) {
                                requestProductList();
                            } else {
                                updateProductGridView();
                            }
                        }
                    }).fire();
        } else { // clearHoldingOrderList all product position
            ProductPkg.clearPositions(mProductPkgList);
        }
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList = products;
                        ProductPkg.updateProductPkgList(mProductPkgList, products,
                                mSimulationPositionList, null);
                    }
                }).fire();
    }

    private void updateProductGridView() {
        if (mProductAdapter == null) {
            mProductAdapter = new ProductAdapter(getActivity(), mProductPkgList);
            mGridView.setAdapter(mProductAdapter);
        } else {
            mProductAdapter.setProductPkgList(mProductPkgList);
        }
    }

    static class ProductAdapter extends BaseAdapter {

        private Context mContext;
        private List<ProductPkg> mProductPkgList;

        public ProductAdapter(Context context, List<ProductPkg> productPkgList) {
            mContext = context;
            mProductPkgList = productPkgList;
        }

        public void setProductPkgList(List<ProductPkg> productPkgList) {
            mProductPkgList = productPkgList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mProductPkgList.size();
        }

        @Override
        public Object getItem(int position) {
            return mProductPkgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_product_gold, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.bindingData(getItem(position), mContext);

            return convertView;
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
            @BindView(R.id.marketOpenTime)
            TextView mMarketOpenTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Object item, Context context) {
                ProductPkg pkg = (ProductPkg) item;
                Product product = pkg.getProduct();
                mProductName.setText(product.getVarietyName());
                mAdvertisement.setText(product.getAdvertisement());
                mHotIcon.setVisibility((product.getTags() == Product.TAG_HOT) ? View.VISIBLE : View.GONE);
                if (product.getExchangeStatus() == Product.MARKET_STATUS_CLOSE) {
                    mProductName.setTextColor(ContextCompat.getColor(context, R.color.blackHalfTransparent));
                    mAdvertisement.setVisibility(View.GONE);
                    mHoldingPosition.setVisibility(View.GONE);
                    mMarketCloseText.setVisibility(View.VISIBLE);
                    mMarketOpenTime.setVisibility(View.VISIBLE);
                    String marketOpenTime = createMarketOpenTime(product, context);
                    mMarketOpenTime.setText(marketOpenTime);
                } else {
                    mProductName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    mAdvertisement.setVisibility(View.VISIBLE);
                    mMarketCloseText.setVisibility(View.GONE);
                    mMarketOpenTime.setVisibility(View.GONE);
                }
                HomePositions.Position position = pkg.getPosition(); // Position status
                if (position != null && position.getHandsNum() > 0) {
                    mHoldingPosition.setVisibility(View.VISIBLE);
                } else {
                    mHoldingPosition.setVisibility(View.GONE);
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
