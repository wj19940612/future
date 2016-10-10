package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.msg.SysTradeMessage;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageCenterListItemInfoActivity extends BaseActivity {
    @BindView(R.id.messageCenterInfoTitle)
    TextView mTvMessageTitle;
    @BindView(R.id.messageCenterInfoTime)
    TextView mTvMessageTime;
    @BindView(R.id.message_center_info_content)
    TextView mTvMessageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_list_item_info);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        SysTradeMessage mSysTradeMessage = (SysTradeMessage) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
//        mTvMessageTitle.setText(mSysTradeMessage.getTitle());
//        mTvMessageTime.setText(mSysTradeMessage.getUpdateDate());
//        mTvMessageContent.setText(mSysTradeMessage.getContent());
    }
}
