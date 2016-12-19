package com.jnhyxx.html5.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.market.ServerIpPort;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.FontUtil;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        FontUtil.setTt0173MFont(mAvailableGold);

        initData(getIntent());

        updateProductGridView();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductPkg pkg = (ProductPkg) parent.getItemAtPosition(position);
                if (pkg != null) {
                    requestServerIpAndPort(pkg);
                }
            }
        });
    }

    private void updateUserAvailableScore() {
        if (LocalUser.getUser().isLogin()) {
            mAvailableGold.setText(FinanceUtil.formatWithScale(LocalUser.getUser().getAvailableScore()));
        } else {
            mAvailableGold.setText(FinanceUtil.formatWithScale(0));
        }
    }

    private void requestServerIpAndPort(final ProductPkg pkg) {
        API.Market.getMarketServerIpAndPort().setTag(TAG)
                .setCallback(new Callback2<Resp<List<ServerIpPort>>, List<ServerIpPort>>() {
                    @Override
                    public void onRespSuccess(List<ServerIpPort> serverIpPorts) {
                        if (serverIpPorts != null && serverIpPorts.size() > 0) {
                            Launcher.with(getActivity(), TradeActivity.class)
                                    .putExtra(Product.EX_PRODUCT, pkg.getProduct())
                                    .putExtra(Product.EX_FUND_TYPE, Product.FUND_TYPE_SIMULATION)
                                    .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(mProductList))
                                    .putExtra(ServerIpPort.EX_IP_PORT, serverIpPorts.get(0))
                                    .execute();
                        }
                    }
                }).fire();
    }

    private void initData(Intent intent) {
        mProductPkgList = new ArrayList<>();
        mProductList = intent.getParcelableArrayListExtra(Product.EX_PRODUCT_LIST);
        ProductPkg.updateProductPkgList(mProductPkgList, mProductList, mSimulationPositionList, null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestProductList();
        updateUserAvailableScore();
        requestSimulationPositions();
    }

    private void requestSimulationPositions() {
        if (LocalUser.getUser().isLogin()) {
            API.Order.getHomePositions().setTag(TAG)
                    .setCallback(new Callback2<Resp<HomePositions>, HomePositions>() {
                        @Override
                        public void onRespSuccess(HomePositions homePositions) {
                            mSimulationPositionList = homePositions.getIntegralOpS();
                            ProductPkg.updatePositionInProductPkg(mProductPkgList, mSimulationPositionList);
                            updateProductGridView();
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
                        ProductPkg.updateProductPkgList(mProductPkgList, mProductList,
                                mSimulationPositionList, null);
                        updateProductGridView();
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

    @OnClick(R.id.goldStoreButton)
    public void onClick() {
        ToastUtil.show(R.string.coming_soon);
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
                    mHotIcon.setVisibility(View.GONE);
                    mNewTag.setVisibility(View.GONE);
                    mMarketCloseText.setVisibility(View.VISIBLE);
                    mMarketOpenTime.setVisibility(View.VISIBLE);
                    String marketOpenTime = createMarketOpenTime(product, context);
                    mMarketOpenTime.setText(marketOpenTime);
                } else {
                    mProductName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    mAdvertisement.setVisibility(View.VISIBLE);
                    mMarketCloseText.setVisibility(View.GONE);
                    mMarketOpenTime.setVisibility(View.GONE);
                    mHotIcon.setVisibility(product.getTags() == Product.TAG_HOT ? View.VISIBLE : View.GONE);
                    mNewTag.setVisibility(product.getTags() == Product.TAG_NEW ? View.VISIBLE : View.GONE);

                }
                HomePositions.Position position = pkg.getPosition(); // Position status
                //如果休市，有持仓，则优先显示持仓提示
                if (position != null && position.getHandsNum() > 0) {
                    mMarketCloseText.setVisibility(View.GONE);
                    mHoldingPosition.setVisibility(View.VISIBLE);
                    mNewTag.setVisibility(View.GONE);
                    mHotIcon.setVisibility(View.GONE);
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
