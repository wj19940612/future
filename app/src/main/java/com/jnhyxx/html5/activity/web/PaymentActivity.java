package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.ToastUtil;

import java.net.URISyntaxException;

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
            } else if (url.startsWith("alipays:") || url.contains("Intent;scheme=alipays")) {
                openAlipay(view, url);
                return true;
            }
        }
        return super.onShouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void openAlipay(WebView webView, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                ToastUtil.show(R.string.install_alipay_first);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
