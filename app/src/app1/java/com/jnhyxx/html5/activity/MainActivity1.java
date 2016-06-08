package com.jnhyxx.html5.activity;

import android.os.Bundle;

import com.wo.main.WP_JS_Main;

public class MainActivity1 extends MainActivity {

    private WP_JS_Main mJSMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mJSMain = new WP_JS_Main(mWebView);
        mWebView.addJavascriptInterface(mJSMain, "VIA_SDK");
    }

    public WP_JS_Main getJSMain() {
        return mJSMain;
    }
}
