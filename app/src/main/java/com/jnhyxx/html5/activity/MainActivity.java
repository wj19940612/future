package com.jnhyxx.html5.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Web";

    private ProgressBar mProgressBar;
    private WebView mWebView;

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
        mProgressBar.setIndeterminateDrawable(new ColorDrawable(Color.BLACK));
        mProgressBar.setMax(100);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadUrl(Api.getMainUrl());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAppCachePath(getExternalCacheDir().getPath());
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.addJavascriptInterface(new AppJs(this), "AppJs");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //Log.i("onPageStarted", " url = " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Log.i("onPageFinished", " url = " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Log.i(TAG, " url = " + url);
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
