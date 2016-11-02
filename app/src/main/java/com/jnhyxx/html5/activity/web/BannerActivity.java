package com.jnhyxx.html5.activity.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;

/**
 * Created by ${wangJie} on 2016/11/1.
 */

public class BannerActivity extends WebViewActivity {
    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "banner的内容" + mPureHtml);
        WebView webView = getWebView();
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕，内容将自动缩放
        String content = INFO_HTML_META + "<body>" + mPureHtml + "</body>";
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    @Override
    protected boolean isNotNeedNetTitle() {
        return true;
    }
}
