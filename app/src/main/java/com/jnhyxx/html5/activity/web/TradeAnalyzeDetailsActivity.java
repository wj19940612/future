package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_analyze_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Information information = (Information) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
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
