package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.message)
    TextView mMessage;
    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.tradeInfo)
    LinearLayout mTradeInfo;
    @BindView(R.id.tradeInfoTitle)
    TextView mTradeInfoTitle;
    @BindView(R.id.tradeInfoMessageFrom)
    TextView mTradeInfoMessageFrom;
    @BindView(R.id.tradeInfoTime)
    TextView mTradeInfoTime;


    @BindView(R.id.hint)
    TextView mHint;
    @BindView(R.id.progress)
    ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_analyze_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Information information = (Information) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        if (information.getType() == Information.TYPE_MARKET_ANALYSIS) {
            initData(information);
        } else {
            mTradeAnalyze.setVisibility(View.GONE);
            mTradeInfo.setVisibility(View.VISIBLE);
            mHint.setVisibility(View.GONE);

            mTradeInfoTitle.setText(information.getTitle());
            mTradeInfoMessageFrom.setText(getString(R.string.message_from, information.getOperator()));
            mTradeInfoTime.setText(DateUtil.format(information.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm"));
            if (!information.isH5Style()) {
                mWebView.loadData(information.getContent(), "text/html", "utf-8");
            } else {
//            mWebView.loadUrl(information.getContent());
                mWebView.setVisibility(View.GONE);
                mMessage.setVisibility(View.VISIBLE);
                mMessage.setText(information.getContent());
            }

        }
    }

    private void initData(Information information) {
        mTitle.setText(information.getTitle());
        mMessageFrom.setText(getString(R.string.message_from, information.getOperator()));
        mTime.setText(DateUtil.format(information.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm"));
        if (!information.isH5Style()) {
            mWebView.loadData(information.getContent(), "text/html", "utf-8");
        } else {
//            mWebView.loadUrl(information.getContent());
            mWebView.setVisibility(View.GONE);
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(information.getContent());
        }
    }
}
