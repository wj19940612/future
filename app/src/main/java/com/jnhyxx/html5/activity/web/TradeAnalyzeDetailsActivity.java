package com.jnhyxx.html5.activity.web;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.AppJs;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.Constant;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.utils.Network.isNetworkAvailable;
import static com.jnhyxx.html5.utils.Network.registerNetworkChangeReceiver;
import static com.jnhyxx.html5.utils.Network.unregisterNetworkChangeReceiver;

/**
 * 行情分析详情
 */
public class TradeAnalyzeDetailsActivity extends BaseActivity {

    @BindView(R.id.tradeAnalyze)
    RelativeLayout mTradeAnalyze;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.messageFrom)
    TextView mMessageFrom;
    @BindView(R.id.time)
    TextView mTime;


    @BindView(R.id.tradeInfo)
    LinearLayout mTradeInfo;
    @BindView(R.id.tradeInfoTitle)
    TextView mTradeInfoTitle;
    @BindView(R.id.tradeInfoMessageFrom)
    TextView mTradeInfoMessageFrom;
    @BindView(R.id.tradeInfoTime)
    TextView mTradeInfoTime;

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.refreshButton)
    Button mRefreshButton;
    @BindView(R.id.errorPage)
    LinearLayout mErrorPage;

    @BindView(R.id.emptyView)
    View mView;

    private boolean mLoadSuccess;

    private BroadcastReceiver mNetworkChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_analyze_details);
        ButterKnife.bind(this);

        mNetworkChangeReceiver = new NetworkReceiver();
        mLoadSuccess = true;

        Intent intent = getIntent();
        Information information = (Information) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        Log.d(TAG, "资讯界面的数据" + information.toString());

        initWebView();

        //行情分析
        if (information.getType() == Information.TYPE_MARKET_ANALYSIS) {
            initMarketAnalyzeData(information);
        } else {
            initTradeMessage(information);
        }
    }

    //行情资讯
    private void initTradeMessage(Information information) {
        mTradeAnalyze.setVisibility(View.GONE);
        mTradeInfo.setVisibility(View.VISIBLE);

        mTradeInfoTitle.setText(information.getTitle());
        mTradeInfoMessageFrom.setText(getString(R.string.message_from, information.getSource()));
        mTradeInfoTime.setText(DateUtil.format(information.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm"));
        if (!information.isH5Style()) {
            setWebViewMargin();
            mView.setVisibility(View.VISIBLE);
            String s = Constant.INFO_HTML_META + "<body>" + information.getContent() + "</body>";
            mWebView.loadDataWithBaseURL(null, information.getContent(), "text/html", "utf-8", null);
        } else {
            mView.setVisibility(View.GONE);
            mWebView.loadUrl(information.getContent());
        }
    }

    private void setWebViewMargin() {
        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, defaultFontSize);
        mWebView.setLayoutParams(layoutParams);
    }

    private void initMarketAnalyzeData(Information information) {
        mTradeAnalyze.setVisibility(View.VISIBLE);
        mTradeInfo.setVisibility(View.GONE);


        mTitle.setText(information.getTitle());
        mMessageFrom.setText(getString(R.string.message_from, information.getSource()));
        mTime.setText(DateUtil.format(information.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm"));
        if (!information.isH5Style()) {
            setWebViewMargin();
            mView.setVisibility(View.VISIBLE);
            String s = Constant.INFO_HTML_META + "<body>" + information.getContent() + "</body>";
            mWebView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
        } else {
            mView.setVisibility(View.GONE);
            mWebView.loadUrl(information.getContent());
        }
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
            mLoadSuccess = true;

            if (!isNetworkAvailable()) {
                mLoadSuccess = false;
                mWebView.stopLoading();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mLoadSuccess) {
                mWebView.setVisibility(View.VISIBLE);
                mErrorPage.setVisibility(View.GONE);
            } else {
                mWebView.setVisibility(View.GONE);
                mErrorPage.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @OnClick(R.id.refreshButton)
    public void onClick() {
        mWebView.reload();
    }

    private class NetworkReceiver extends Network.NetworkChangeReceiver {

        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE && !mLoadSuccess) {
                if (mWebView != null) {
                    mWebView.reload();
                }
            }
        }
    }
}
