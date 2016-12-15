package com.jnhyxx.html5.activity.web;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.net.API;

public class PaymentActivity extends WebViewActivity {

    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        Log.d("recharge", "onShouldOverrideUrlLoading: " + url);
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(API.Finance.getRechargeSuccessUrl())) {
                setResult(RESULT_OK);
                finish();
                return true;
            } else if (TextUtils.equals(url, API.Finance.getRechargeFailUrl())) {
                finish();
                return true;
            } else if (TextUtils.equals(url, API.Finance.getMineWebPageUrl())) {
                setResult(RESULT_OK);
                finish();
                return true;
            }
        }
        return super.onShouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
