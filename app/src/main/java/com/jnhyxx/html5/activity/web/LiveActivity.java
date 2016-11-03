package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.TradeActivity;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.MarketServer;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2016/10/25.
 */

public class LiveActivity extends WebViewActivity {
    private static final String TAG = "LiveActivity";

    public static final String liveRoomId = "liveRoomId";
    private WebView mWebView;


    private List<ProductPkg> mProductPkgList = new ArrayList<>();
    private List<Product> mProductList;
    private List<HomePositions.IntegralOpSBean> mSimulationPositionList;

    private static final int REQUEST_CODE_LOGIN = 905;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = getWebView();

        Log.d(TAG, "地址" + mPageUrl);
        getTitleBar().setRightText(R.string.live_right_title);
        getTitleBar().setRightVisible(true);
        getTitleBar().setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestProductList();
                requestSimulationPositions();
            }
        });
    }

    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(API.getLoginUrl())) {
            Launcher.with(LiveActivity.this, SignInActivity.class).executeForResult(REQUEST_CODE_LOGIN);
            return true;
        }
        if (url.equals(API.getShutUpHtmlUrl())) {
            Launcher.with(LiveActivity.this, WebViewActivity.class)
                    .putExtra(WebViewActivity.EX_URL, API.getShutUpHtmlUrl())
                    .putExtra(WebViewActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                    .putExtra(WebViewActivity.EX_TITLE, getString(R.string.live_manager))
                    .execute();
            return true;
        }
        return super.onShouldOverrideUrlLoading(view, url);
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
//                            if (updateProductList) {
//                                requestProductList();
//                            }
                        }
                    }).fire();
        } else { // clearHoldingOrderList all product position
            ProductPkg.clearPositions(mProductPkgList);
        }
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList = products;
                        ProductPkg.updateProductPkgList(mProductPkgList, products, mSimulationPositionList, null);

                        if (mProductPkgList != null && !mProductPkgList.isEmpty()) {
                            //美原油
                            int crudeId = 1;
                            for (int i = 0; i < mProductPkgList.size(); i++) {
                                if (Product.US_CRUDE_ID == mProductPkgList.get(i).getProduct().getVarietyId()) {
                                    crudeId = i;
                                    break;
                                }
                            }
                            ProductPkg productPkg = mProductPkgList.get(crudeId);
                            requestServerIpAndPort(productPkg);
                        }
                    }
                }).fire();

    }

    private void requestServerIpAndPort(final ProductPkg productPkg) {
        API.Market.getMarketServerIpAndPort().setTag(TAG)
                .setCallback(new Callback2<Resp<List<MarketServer>>, List<MarketServer>>() {
                    @Override
                    public void onRespSuccess(List<MarketServer> marketServers) {
                        if (marketServers != null && marketServers.size() > 0) {
                            requestProductExchangeStatus(productPkg.getProduct(), marketServers);
                        }
                    }
                }).fire();
    }

    private void requestProductExchangeStatus(final Product product, final List<MarketServer> marketServers) {
        API.Order.getExchangeTradeStatus(product.getExchangeId(), product.getVarietyType())
                .setTag(TAG)
                .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                    @Override
                    public void onRespSuccess(ExchangeStatus exchangeStatus) {
                        product.setExchangeStatus(exchangeStatus.isTradeable()
                                ? Product.MARKET_STATUS_OPEN : Product.MARKET_STATUS_CLOSE);

                        Launcher.with(LiveActivity.this, TradeActivity.class)
                                .putExtra(Product.EX_PRODUCT, product)
                                .putExtra(Product.EX_FUND_TYPE, Product.FUND_TYPE_CASH)
                                .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(mProductList))
                                .putExtra(ExchangeStatus.EX_EXCHANGE_STATUS, exchangeStatus)
                                .putExtra(MarketServer.EX_MARKET_SERVER, new ArrayList<Parcelable>(marketServers))
                                .execute();
                    }
                }).fire();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            setResult(RESULT_OK);
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            initCookies(CookieManger.getInstance().getRawCookie(), mPageUrl);
            mWebView.reload();
        }
    }
}
