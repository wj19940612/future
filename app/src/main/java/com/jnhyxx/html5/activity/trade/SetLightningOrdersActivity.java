package com.jnhyxx.html5.activity.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.MarketServer;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.fragment.order.PlaceOrderFragment;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetLightningOrdersActivity extends BaseActivity implements PlaceOrderFragment.Callback {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.lightningOrdersData)
    FrameLayout mLightningOrdersData;


    @BindView(R.id.closeLightningOrder)
    TextView mCloseLightningOrder;
    @BindView(R.id.restartLightningOrder)
    TextView mRestartLightningOrder;
    @BindView(R.id.openLightningOrder)
    TextView mOpenLightningOrder;


    @BindView(R.id.lightningOrdersOpen)
    LinearLayout mLightningOrdersOpen;

    private Product mProduct;
    private int mFundType;
    private String mFundUnit;
    private List<Product> mProductList;


    private NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveData(FullMarketData data) {
            if (data != null) {
                updatePlaceOrderFragment(data);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightning_orsers_set);
        ButterKnife.bind(this);

        initData(getIntent());
        showPlaceOrderFragment();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        NettyClient.getInstance().addNettyHandler(mNettyHandler);
        NettyClient.getInstance().start(mProduct.getContractsCode());
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
        NettyClient.getInstance().stop();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNettyHandler = null;
    }


    private void initData(Intent intent) {
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mProductList = intent.getParcelableArrayListExtra(Product.EX_PRODUCT_LIST);
//        mExchangeStatus = (ExchangeStatus) intent.getSerializableExtra(ExchangeStatus.EX_EXCHANGE_STATUS);
        MarketServer mMarketServer = (MarketServer) intent.getSerializableExtra(MarketServer.EX_MARKET_SERVER);
        NettyClient.getInstance().setIpAndPort(mMarketServer.getIp(), mMarketServer.getPort());
    }

    private void updatePlaceOrderFragment(FullMarketData data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.lightningOrdersData);
        if (fragment != null && fragment instanceof PlaceOrderFragment) {
            ((PlaceOrderFragment) fragment).setMarketData(data);
        }
    }


    private void showPlaceOrderFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.lightningOrdersData);
        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.lightningOrdersData, PlaceOrderFragment.newInstance(0, mProduct, mFundType, true))
                    .commit();
        }
    }

    private void hideFragmentOfContainer() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.lightningOrdersData);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.lightningOrdersData);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfirmBtnClick(SubmittedOrder submittedOrder) {

    }

    @Override
    public void onPlaceOrderFragmentEmptyAreaClick() {

    }

    @Override
    public void onPlaceOrderFragmentShow() {

    }

    @Override
    public void onPlaceOrderFragmentExited() {

    }
}
