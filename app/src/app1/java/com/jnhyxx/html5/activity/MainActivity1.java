package com.jnhyxx.html5.activity;

import android.os.Bundle;

import com.wo.main.WP_JS_Main;

public class MainActivity1 extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WP_JS_Main sdkJsInterface = new WP_JS_Main(mWebView);
        mWebView.addJavascriptInterface(sdkJsInterface, "VIA_SDK");
    }
}
