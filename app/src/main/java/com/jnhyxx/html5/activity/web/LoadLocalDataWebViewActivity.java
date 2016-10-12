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

    public static final String EX_LOCAL_HTML = "LocalHtml";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData(Intent intent) {
        super.initData(intent);

        getWebView().loadData(mPageUrl, "text/html", "utf-8");
    }
}
