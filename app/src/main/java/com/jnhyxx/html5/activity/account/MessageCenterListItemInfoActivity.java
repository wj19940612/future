package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jnhyxx.html5.AppJs;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jnhyxx.html5.utils.Network.isNetworkAvailable;

public class MessageCenterListItemInfoActivity extends BaseActivity {
    @BindView(R.id.messageTitle)
    TextView mTvMessageTitle;
    @BindView(R.id.time)
    TextView mTvMessageTime;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_list_item_info);
        ButterKnife.bind(this);

        initData();
        initWebView();
    }

    private void initData() {
        Intent intent = getIntent();
        SysMessage mSysMessage = (SysMessage) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        mTvMessageTitle.setText(mSysMessage.getPushTopic());
        mTvMessageTime.setText(DateUtil.format(mSysMessage.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm:ss"));
        if (mSysMessage.isText() || mSysMessage.getIsText()) {
            if (!TextUtils.isEmpty(mSysMessage.getHtmlLink())) {
                setWebViewMargin();
                mWebView.loadUrl(mSysMessage.getHtmlLink());
            }
        } else {
            String s = INFO_HTML_META + "<body>" + mSysMessage.getPushMsg() + "</body>";
            mWebView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
        }
    }

    private void setWebViewMargin() {
        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        mWebView.setLayoutParams(layoutParams);
    }

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString(webSettings.getUserAgentString()
                + " ###" + getString(R.string.android_web_agent) + "/1.0");
        //mWebView.getSettings().setAppCacheEnabled(true);
        webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setAllowFileAccess(true);

        // performance improve
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);

        /**
         *  设置网页布局类型：
         *  1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
         *  2、LayoutAlgorithm.SINGLE_COLUMN: 适应屏幕，内容将自动缩放
         *
         */
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setDefaultTextEncodingName("utf-8"); //设置文本编码

        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.addJavascriptInterface(new AppJs(this), "AppJs");

        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgress.setVisibility(View.GONE);
                } else {
                    if (mProgress.getVisibility() == View.GONE) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                    mProgress.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    protected class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (!isNetworkAvailable()) {
                mWebView.stopLoading();
            }
        }
    }
}
