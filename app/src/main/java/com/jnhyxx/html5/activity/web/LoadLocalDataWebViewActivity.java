package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;

/**
 * Created by ${wangJie} on 2016/9/30.
 * <p>
 * 加载本地Html
 */

public class LoadLocalDataWebViewActivity extends WebViewActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData(getIntent());
        WebView mWebView = getWebView();
        mWebView.setClickable(true);

        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView.setWebViewClient(new LocalWebViewClient());

    }

    class LocalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            finish();
            return true;
        }

//        @Override
//        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//            ToastUtil.curt("WebView被点击了");
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                finish();
//                return true;
//            }
//            return super.shouldOverrideKeyEvent(view, event);
//        }
    }

    protected void initData(Intent intent) {
        mTitle = intent.getStringExtra(EX_TITLE);
        mPageUrl = intent.getStringExtra(EX_URL);
        mRawCookie = intent.getStringExtra(EX_RAW_COOKIE);
        getWebView().loadData(mPageUrl, "text/html", "utf-8");
    }
}