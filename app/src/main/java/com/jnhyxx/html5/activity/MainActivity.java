package com.jnhyxx.html5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jnhyxx.html5.AppJs;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.net.Api;
import com.jnhyxx.html5.utils.ToastUtil;

import java.net.URISyntaxException;

public class MainActivity extends BaseActivity {

    private ProgressBar mProgressBar;
    protected WebView mWebView;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mHandler = new Handler();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        mWebView = (WebView) findViewById(R.id.webView);

        // init webSettings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setUserAgentString(getString(R.string.android_web_agent));
        //mWebView.getSettings().setAppCacheEnabled(true);
        webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);

        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.addJavascriptInterface(new AppJs(this), "AppJs");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(getTAG(), " url = " + url);
                if (url.contains("qr.alipay.com")) {
                    openAlipay(view, url);
                    return true;
                }

                if (url.startsWith("mqqwpa:")) {
                    openQQChat(view, url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (mProgressBar.getVisibility() == View.GONE) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mWebView.loadUrl(Api.getMainUrl());
    }

    private void openQQChat(WebView webView, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                ToastUtil.show(R.string.install_qq_first);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        while (webView.canGoBack()) {
            webView.goBack();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(Api.getMainUrl());
            }
        }, 200);
    }

    private void openAlipay(WebView webView, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        while (webView.canGoBack()) {
            webView.goBack();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(Api.getMime());
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        cleanCookie();
        super.onDestroy();
    }

    private void cleanCookie() {
        CookieSyncManager.createInstance(this.getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }
}
