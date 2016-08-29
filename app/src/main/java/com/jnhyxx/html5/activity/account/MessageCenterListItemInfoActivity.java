package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.msg.SysTradeMessage;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageCenterListItemInfoActivity extends BaseActivity {
    @BindView(R.id.message_center_info_title)
    TextView tv_Message_title;
    @BindView(R.id.message_center_info_time)
    TextView tv_message_time;
    @BindView(R.id.message_center_info_content)
    TextView tv_message_content;

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
        tv_Message_title.setText(mSysTradeMessage.getTitle());
        tv_message_time.setText(mSysTradeMessage.getUpdateDate());
        tv_message_content.setText(mSysTradeMessage.getContent());
    }
}
