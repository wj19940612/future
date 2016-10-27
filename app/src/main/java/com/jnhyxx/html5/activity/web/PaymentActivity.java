package com.jnhyxx.html5.activity.web;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.net.API;
import com.johnz.kutils.net.CookieManger;

public class PaymentActivity extends WebViewActivity {

    @Override
    protected void loadPage() {
        // TODO: 2016/10/26 防止Cookies没有及时设置成功
        Log.d("rechargeTest", "网页 " + mPageUrl + "\n网页中cookies" + CookieManager.getInstance().getCookie(mPageUrl));
        if (mPageUrl.contains(API.Finance.depositByBankApply())) {
            int length = CookieManger.getInstance().getRawCookie().split("\n").length;
            String cookie = CookieManager.getInstance().getCookie(mPageUrl);
            String[] cookies = cookie.split(";");
            Log.d("rechargeTest", "长度" + length + "网址中的cookies长度" + cookies.length);
            if (TextUtils.isEmpty(cookie) || cookies.length != length) {
                initCookies(CookieManger.getInstance().getRawCookie(), mPageUrl);
                getWebView().reload();
                return;
            } else {
                getWebView().loadUrl(mPageUrl);
            }
        }
        super.loadPage();
    }

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
}
