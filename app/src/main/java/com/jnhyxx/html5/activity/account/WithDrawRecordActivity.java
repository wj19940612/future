package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提现记录
 */
public class WithDrawRecordActivity extends BaseActivity {

    @BindView(R.id.withdrawRecord)
    ListView mWithdrawRecordList;
    @BindView(R.id.empty)
    TextView mEmpty;

    private int mSize = 0;
    private int mOffset;

    private WithdrawRecordAdapter mWithdrawRecordAdapter;
    private TextView mFooter;

    private Set<Integer> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_record);
        ButterKnife.bind(this);
        mSize = 15;
        mOffset = 0;
        mSet = new HashSet<>();
        getWithdrawRecordList();
    }

    public void getWithdrawRecordList() {
        API.Finance.getWithdrawRecordList(WithdrawRecord.RECORD_TYPE_WITHDRAW, mOffset, mSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<WithdrawRecord>>, List<WithdrawRecord>>() {

                    @Override
                    public void onRespSuccess(List<WithdrawRecord> withdrawRecords) {
                        for (int i = 0; i < withdrawRecords.size(); i++) {
                            Log.d(TAG, "提现记录 " + withdrawRecords.get(i).toString() + "\n");
                        }
                        updateInfoList(withdrawRecords);

                    }
                }).fire();
    }

    private void updateInfoList(List<WithdrawRecord> withdrawRecordList) {
        if (withdrawRecordList == null) {
            mWithdrawRecordList.setEmptyView(mEmpty);
            return;
        }
        if (mFooter == null) {
            mFooter = new TextView(WithDrawRecordActivity.this);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
            mFooter.setPadding(padding, padding, padding, padding);
            mFooter.setGravity(Gravity.CENTER);
            mFooter.setText(R.string.click_to_load_more);
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffset++;
                    getWithdrawRecordList();
                }
            });
            mWithdrawRecordList.addFooterView(mFooter);
        }

        if (withdrawRecordList.size() < mSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mWithdrawRecordList.removeFooterView(mFooter);
        }

        if (mWithdrawRecordAdapter == null) {
            mWithdrawRecordAdapter = new WithdrawRecordAdapter(WithDrawRecordActivity.this);
            mWithdrawRecordList.setAdapter(mWithdrawRecordAdapter);
        }

        for (WithdrawRecord item : withdrawRecordList) {
            if (mSet.add(item.getId())) {
                mWithdrawRecordAdapter.add(item);
            }
        }
        mWithdrawRecordAdapter.notifyDataSetChanged();
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
                String time = item.getCreateTime().trim();
                String[] date = time.split(" ");
                if (date.length == 2) {
                    mSaleDateMonth.setText(date[0]);
                    mSaleDateHour.setText(date[1]);
                }
//                mSaleGetMoney.setText(getString(R.string.withdraw_money, item.getMoney()));
                mSaleGetMoney.setText( String.valueOf(item.getMoney())+"元");
                if (item.getStatus() == WithdrawRecord.WITHDRAW_RECHARGE_SUCCESS) {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_green_primary);
                    mSaleStatus.setText(R.string.withdraw_status_success);
                    mSaleGetMoney.setTextColor(getResources().getColor(R.color.common_drop));
                    //如果提现失败或者拒绝
                } else if (item.getStatus() == WithdrawRecord.WITHDRAW_FAIL || item.getStatus() == WithdrawRecord.WITHDRAW_REFUSE) {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_red_primary);
                    mSaleStatus.setText(R.string.withdraw_status_fail);
                    mSaleGetMoney.setTextColor(getResources().getColor(R.color.common_rise_activity_sum));
                } else {
                    mSaleStatus.setBackgroundResource(R.drawable.btn_dialog_left);
                    mSaleStatus.setText(R.string.withdraw_status_auditing);
                    mSaleGetMoney.setTextColor(getResources().getColor(R.color.splitLineOverspread));
                }
            }
        }
    }

}
