package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.net.CookieManger;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 充值
 */
public class RechargeWebViewActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView mWebview;
    @BindView(R.id.cookis)
    TextView mCookis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_web_view);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            ToastUtil.curt("充值界面地址不能为空");
        } else {
            synCookies(RechargeWebViewActivity.this, url);
            //加载需要显示的网页
            mWebview.loadUrl(url);
            //设置Web视图
//            mWebview.setWebViewClient(new HelloWebViewClient());
//            // 设置可以访问文件
//            mWebview.getSettings().setAllowFileAccess(true);
//            //如果访问的页面中有Javascript，则webview必须设置支持Javascript
//            WebSettings mWebViewSettings = mWebview.getSettings();
////            //设置WebView属性，能够执行Javascript脚本
//            mWebViewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//            mWebViewSettings.setAllowFileAccess(true);
//            mWebViewSettings.setAppCacheEnabled(true);
//            mWebViewSettings.setDomStorageEnabled(true);
//            mWebViewSettings.setDatabaseEnabled(true);

        }
    }

    /**
     * 同步一下cookie
     */
    public void synCookies(Context context, String url) {
        String cookies = CookieManger.getInstance().getCookies();
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
//        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
//        CookieSyncManager.getInstance().sync();


        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();


        String cookie = cookieManager.getCookie(url);
        Log.d(TAG, "本地CookieManger所获取的 " + cookies + "\n通过CookieManager.getCookies(url)获取的cookies" + cookie);
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
            mWebview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
