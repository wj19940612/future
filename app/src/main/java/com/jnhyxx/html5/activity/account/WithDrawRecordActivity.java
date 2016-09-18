package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.widget.ListView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.WithdrawRecord;
import com.jnhyxx.html5.net.API;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithDrawRecordActivity extends BaseActivity {

    @BindView(R.id.withdrawRecord)
    ListView mWithdrawRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_record);
        ButterKnife.bind(this);

        getWithdrawRecordList();
    }

    public void getWithdrawRecordList() {
        API.Finance.getWithdrawRecordInfo(WithdrawRecord.RECORD_TYPE_WITHDRAW,0,)
    }
}
