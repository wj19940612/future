package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.Launcher;

import java.net.URISyntaxException;

public class PaymentActivity extends WebViewActivity {

    /**
     * 银行卡支付的标志
     */
    public static final String BANK_CARD_PAYMENT = "BANK_CARD_PAYMENT";
    private boolean mIsBankCardPayment;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Intent intent = getIntent();
        mIsBankCardPayment = intent.getBooleanExtra(BANK_CARD_PAYMENT,false);
    }

    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        Log.d("recharge", "onShouldOverrideUrlLoading: " + url);
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(API.Finance.getRechargeSuccessUrl())) {
                if(mIsBankCardPayment){
                    LocalUser.getUser().getUserInfo().setCardState(UserInfo.BANKCARD_STATUS_BOUND);
                }
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
            } else if (url.contains(API.Finance.getUserAggressPaymentConfirmPagePath())) {
                setResult(RESULT_OK);
                finish();
                return true;
            } else if (url.contains(API.Finance.getBankcardPaymentPagePartUrl())) {
                getWebView().loadUrl(API.appendUrlNoHead(url));
                return true;
            } else if (url.contains(API.Finance.getBankcardPaymentErrorPartUrl())) {
                getWebView().loadUrl(API.appendUrlNoHead(url));
                return true;
            } else if (url.equalsIgnoreCase(API.Finance.getBankcardPaymentAgreememtUrl())) {
                Launcher.with(getActivity(), PaymentActivity.class)
                        .putExtra(PaymentActivity.EX_URL, API.appendUrlNoHead(url))
                        .putExtra(PaymentActivity.EX_TITLE, getString(R.string.payment_service_agreement))
                        .execute();
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
