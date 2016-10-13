package com.jnhyxx.html5.activity.web;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;

/**
 * Created by ${wangJie} on 2016/9/30.
 */
public class PaymentWebActivity extends WebViewActivity {
    private static final String TAG = "PaymentWebActivity";

    @Override
    protected void initWebView() {
        super.initWebView();
        getWebView().setWebViewClient(new LocalWebViewClient());
    }

    class LocalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                Log.d(TAG, "充值的webView地址" + url);
                getWebView().loadUrl(url);
            }
            return true;
        }
    }
}
