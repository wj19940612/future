package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageCenterListItemInfoActivity extends BaseActivity {


    @BindView(R.id.messageTitle)
    TextView mTvMessageTitle;
    @BindView(R.id.time)
    TextView mTvMessageTime;
    @BindView(R.id.messageContent)
    TextView mTvMessageContent;
    @BindView(R.id.webView)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_list_item_info);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        boolean isUpdateNotice = intent.getBooleanExtra(Launcher.EX_PAYLOAD_1, false);

        SysMessage mSysMessage = (SysMessage) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        mTvMessageTitle.setText(mSysMessage.getPushTopic());
        mTvMessageTime.setText(DateUtil.format(mSysMessage.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm:ss"));
        if (isUpdateNotice) {
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadDataWithBaseURL(null, mSysMessage.getPushMsg(), "text/html", "utf-8", null);
            return;
        } else {
            mWebView.setVisibility(View.GONE);
        }
        String content = "\t\t\t\t" + mSysMessage.getPushMsg().replaceAll("<p>|</p>", "\r\n");
        mTvMessageContent.setText(content);

    }
}
