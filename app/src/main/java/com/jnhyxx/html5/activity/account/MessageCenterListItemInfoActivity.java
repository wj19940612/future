package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.msg.SysTradeMessage;
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
        mTvMessageTitle.setText(mSysTradeMessage.getPushTopic());
        mTvMessageTime.setText(DateUtil.format(mSysTradeMessage.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm"));
        mTvMessageContent.setText(mSysTradeMessage.getPushContent());

    }
}
