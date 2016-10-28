package com.jnhyxx.html5.activity.web;

import com.jnhyxx.html5.activity.WebViewActivity;

public class NewbieActivity extends WebViewActivity {

    @Override
    protected void onPause() {
        super.onPause();
        getWebView().reload();
    }
}
