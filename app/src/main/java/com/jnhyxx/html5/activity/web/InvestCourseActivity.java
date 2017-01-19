package com.jnhyxx.html5.activity.web;

import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.net.API;

public class InvestCourseActivity extends WebViewActivity {

    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(API.getNewsDetailUrl())) {
            if (url.indexOf("?") != -1) {
                url += "&nohead=1";
            } else {
                url += "?nohead=1";
            }
            getWebView().loadUrl(url);
            return true;
        }
        return super.onShouldOverrideUrlLoading(view, url);
    }
}
