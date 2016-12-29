package com.jnhyxx.html5.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.domain.order.StopProfitLossConfig;
import com.jnhyxx.html5.fragment.order.HoldingFragment;
import com.jnhyxx.html5.fragment.order.SetStopProfitLossFragment;
import com.jnhyxx.html5.fragment.order.SettlementFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity implements
        HoldingFragment.Callback,
        SetStopProfitLossFragment.Callback {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private Product mProduct;
    private int mFundType;
    private FullMarketData mMarketData;
    private OrderAdapter mOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        initData(getIntent());

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(this, android.R.color.transparent));
        mOrderAdapter = new OrderAdapter(getSupportFragmentManager(), this, mProduct, mFundType, mMarketData);
        mViewPager.setAdapter(mOrderAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);

        setViewPager();
    }

    private void setViewPager() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.ORDER_POSITIONS);
                } else if (position == 1) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.ORDER_CLEANING);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData(Intent intent) {
        mProduct = intent.getParcelableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
        mMarketData = intent.getParcelableExtra(FullMarketData.EX_MARKET_DATA);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        NettyClient.getInstance().start(mProduct.getContractsCode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        NettyClient.getInstance().stop();
    }

    @Override
    public void onHoldingFragmentClosePositionEventTriggered() {
        SettlementFragment fragment = (SettlementFragment) mOrderAdapter.getFragment(1);
        if (fragment != null) {
            fragment.setHoldingFragmentClosedPositions(true);
        }
    }

    @Override
    public void onHoldingFragmentSetStopProfitLossClick(final HoldingOrder order, final FullMarketData marketData) {
        API.Order.getStopProfitLossConfig(order.getShowId(), mFundType).setTag(TAG)
                .setCallback(new Callback<Resp<StopProfitLossConfig>>() {
                    @Override
                    public void onReceive(Resp<StopProfitLossConfig> stopProfitLossConfigResp) {
                        if (stopProfitLossConfigResp.isSuccess()) {
                            StopProfitLossConfig stopProfitLossConfig = stopProfitLossConfigResp.getData();
                            showSetStopProfitLossFragment(order, marketData, stopProfitLossConfig);
                        } else {
                            SmartDialog.with(getActivity(), stopProfitLossConfigResp.getMsg())
                                    .setPositive(R.string.ok)
                                    .show();
                        }
                    }
                }).fire();
    }

    @Override
    public void onHoldingFragmentRiskControlTriggered(String orders, String orderSplit, String stopLossSplit) {
        if (TextUtils.isEmpty(orders)) return;

        Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragmentById != null && fragmentById instanceof SetStopProfitLossFragment) {
            HoldingOrder beingSetOrder = ((SetStopProfitLossFragment) fragmentById).getBeingSetOrder();
            String[] closingOrders = orders.split(orderSplit);
            for (String closingOrder : closingOrders) {
                String[] splits = closingOrder.split(stopLossSplit);
                String showId = splits[0];
                boolean stopLoss = Integer.valueOf(splits[1]).intValue() == HoldingOrder.SELL_OUT_STOP_LOSS;
                if (!TextUtils.isEmpty(showId) && showId.equals(beingSetOrder.getShowId())) {
                    onSetStopProfitLossFragmentCloseTriggered();
                    String message = stopLoss ?
                            getString(R.string.being_set_order_is_closed, getString(R.string.stop_loss)) :
                            getString(R.string.being_set_order_is_closed, getString(R.string.stop_profit));
                    SmartDialog.single(getActivity(), message)
                            .setPositive(R.string.ok)
                            .show();
                }
            }
        }
    }

    private void showSetStopProfitLossFragment(HoldingOrder order, FullMarketData marketData,
                                               StopProfitLossConfig stopProfitLossConfig) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, SetStopProfitLossFragment
                            .newInstance(mProduct, mFundType, order, marketData, stopProfitLossConfig))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSetStopProfitLossFragmentCloseTriggered() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onSetStopProfitLossFragmentConfirmed(HoldingOrder order, double newStopLossPrice, double newStopProfitPrice) {
        API.Order.updateStopProfitLoss(order.getShowId(), mFundType, newStopLossPrice, newStopProfitPrice)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback1<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        onSetStopProfitLossFragmentCloseTriggered();
                        HoldingFragment fragment = (HoldingFragment) mOrderAdapter.getFragment(0);
                        if (fragment != null) {
                            fragment.updateHoldingOrderList();
                        }
                        ToastUtil.center(R.string.set_success, R.dimen.toast_offset);
                    }
                }).fire();
    }

    static class OrderAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private Product mProduct;
        private int mFundType;
        private FullMarketData mMarketData;
        private FragmentManager mFragmentManager;

        public OrderAdapter(FragmentManager fm, Context context,
                            Product product, int fundType, FullMarketData marketData) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
            mProduct = product;
            mFundType = fundType;
            mMarketData = marketData;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.holding);
                case 1:
                    return mContext.getString(R.string.settlement);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HoldingFragment.newInstance(mProduct, mFundType, mMarketData);
                case 1:
                    return SettlementFragment.newInstance(mProduct, mFundType);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
