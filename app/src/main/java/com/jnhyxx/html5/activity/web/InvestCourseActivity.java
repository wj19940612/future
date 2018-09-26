package com.jnhyxx.html5.activity.web;

import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.net.API;

public class InvestCourseActivity extends WebViewActivity {

    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(API.getNewsDetailUrl())) {
            url = API.appendUrlNoHead(url);
            getWebView().loadUrl(url);
            return true;
        }

        if (url.contains("/banner/")) { // banner 里面的页面（这里是新手引导）
            url = API.appendUrlNoHead(url);
            getWebView().loadUrl(url);
            return true;
        }

        return super.onShouldOverrideUrlLoading(view, url);
    }
}
