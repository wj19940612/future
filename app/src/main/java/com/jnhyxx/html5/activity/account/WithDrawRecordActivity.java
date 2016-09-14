package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.WithdrawRecord;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithDrawRecordActivity extends BaseActivity {

    @BindView(R.id.withdrawRecord)
    ListView mWithdrawRecordList;
    private Object withdrawRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_record);
        ButterKnife.bind(this);

        getWithdrawRecordList();
    }

    public void getWithdrawRecordList() {
//        API.Finance.getWithdrawRecord(WithdrawRecord.RECORD_TYPE_WITHDRAW,0,)
    }
}
