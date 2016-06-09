package com.jnhyxx.html5.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jnhyxx.html5.AppJs;
import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.net.Api;
import com.jnhyxx.html5.utils.ToastUtil;
import com.wo.main.WP_JS_Main;

import java.net.URISyntaxException;

public class MainActivity extends BaseActivity {

    private static final String TAG = "WebView";

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private LinearLayout mErrorPage;
    private Button mRefreshButton;

    private WebHandler mHandler;
    private boolean mLoadSuccess;

    private static class WebHandler extends Handler {

        private Context mContext;

        public WebHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    private class WebVClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, " url = " + url);
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

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "onPageStarted: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished: " + url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode <= ERROR_UNKNOWN) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mHandler = new WebHandler(this);
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mErrorPage = (LinearLayout) findViewById(R.id.errorPage);
        mWebView = (WebView) findViewById(R.id.webView);

        mRefreshButton = (Button) findViewById(R.id.refreshButton);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });

        // init webSettings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setUserAgentString(getString(R.string.android_web_agent));
        //mWebView.getSettings().setAppCacheEnabled(true);
        webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setAllowFileAccess(true);

        // performance improve
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);

        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.addJavascriptInterface(new AppJs(this), "AppJs");
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (BuildConfig.APP1) {
            WP_JS_Main wpJsMain = new WP_JS_Main(mWebView);
            mWebView.addJavascriptInterface(wpJsMain, "VIA_SDK");
        }

        mWebView.setWebViewClient(new WebVClient());
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

        mHandler.removeCallbacksAndMessages(null);
    }

    private void cleanCookie() {
        CookieSyncManager.createInstance(this.getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }
}
