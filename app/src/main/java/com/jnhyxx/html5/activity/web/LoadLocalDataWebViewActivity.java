package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jnhyxx.html5.activity.WebViewActivity;

/**
 * Created by ${wangJie} on 2016/9/30.
 *
 * 加载本地Html
 */

public class LoadLocalDataWebViewActivity extends WebViewActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData(getIntent());
    }

    protected void initData(Intent intent) {
        mTitle = intent.getStringExtra(EX_TITLE);
        mPageUrl = intent.getStringExtra(EX_URL);
        mRawCookie = intent.getStringExtra(EX_RAW_COOKIE);
        getWebView().loadData(mPageUrl, "text/html", "utf-8");
    }

}
