package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.jnhyxx.html5.activity.SimulationActivity;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends WebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBar().setVisibility(View.GONE);
    }

    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(API.getWebLoginPage())) {
            Launcher.with(getActivity(), SignInActivity.class)
                    .executeForResult(REQ_CODE_LOGIN);
            return true;
        } else if (url.equalsIgnoreCase(API.getWebHomePage())) {
            finish();
            return true;
        } else if (url.equalsIgnoreCase(API.getWebSimulation())) {
            API.Market.getProductList().setTag(TAG)
                    .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                        @Override
                        public void onRespSuccess(List<Product> products) {
                            Launcher.with(getActivity(), SimulationActivity.class)
                                    .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(products))
                                    .execute();
                        }
                    }).fire();
            return true;
        }
        return super.onShouldOverrideUrlLoading(view, url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), MatchActivity.class)
                    .putExtra(MatchActivity.EX_URL, mPageUrl)
                    .putExtra(MatchActivity.EX_TITLE, mTitle)
                    .putExtra(MatchActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                    .execute();
            finish();
        }
    }
}
