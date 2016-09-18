package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.WithdrawRecord;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithDrawRecordActivity extends BaseActivity {

    @BindView(R.id.withdrawRecord)
    ListView mWithdrawRecordList;

    private int mSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_record);
        ButterKnife.bind(this);
        mSize = 15;
        getWithdrawRecordList();
    }

    public void getWithdrawRecordList() {
        API.Finance.getWithdrawRecordList(WithdrawRecord.RECORD_TYPE_WITHDRAW, 0, mSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<WithdrawRecord>>, List<WithdrawRecord>>() {

                    @Override
                    public void onRespSuccess(List<WithdrawRecord> withdrawRecords) {

                    }
                }).fire();
    }

    class WithdrawRecordAdapter extends ArrayAdapter<WithdrawRecord> {

        private Context mContext;

        public WithdrawRecordAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_withdraw_record, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.saleDateMonth)
            TextView mSaleDateMonth;
            @BindView(R.id.saleDateHour)
            TextView mSaleDateHour;
            @BindView(R.id.saleStatus)
            TextView mSaleStatus;
            @BindView(R.id.saleGetMoney)
            TextView mSaleGetMoney;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(WithdrawRecord item, Context mContext) {

            }
        }
    }

}
