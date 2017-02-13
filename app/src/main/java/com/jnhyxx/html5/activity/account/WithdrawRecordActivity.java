package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.account.WithdrawRecord;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawRecordActivity extends BaseActivity implements AdapterView.OnItemClickListener {

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
        setContentView(R.layout.activity_withdraw_record);
        ButterKnife.bind(this);
        mWithdrawRecordList.setEmptyView(mEmpty);
        mSize = 10;
        mOffset = 0;
        mSet = new HashSet<>();
        getWithdrawRecordList();
        mWithdrawRecordList.setOnItemClickListener(this);
    }

    public void getWithdrawRecordList() {
        API.Finance.getWithdrawRecordList(WithdrawRecord.RECORD_TYPE_WITHDRAW, mOffset, mSize)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<WithdrawRecord>>, List<WithdrawRecord>>() {

                    @Override
                    public void onRespSuccess(List<WithdrawRecord> withdrawRecords) {
                        updateInfoList(withdrawRecords);

                    }
                }).fire();
    }

    private void updateInfoList(List<WithdrawRecord> withdrawRecordList) {
        if (withdrawRecordList == null) {
            return;
        }
        if (mFooter == null) {
            mFooter = new TextView(WithdrawRecordActivity.this);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
            mFooter.setPadding(padding, padding, padding, padding);
            mFooter.setGravity(Gravity.CENTER);
            mFooter.setText(R.string.click_to_load_more);
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffset += mSize;
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
            mWithdrawRecordAdapter = new WithdrawRecordAdapter(WithdrawRecordActivity.this);
            mWithdrawRecordList.setAdapter(mWithdrawRecordAdapter);
        }

        for (WithdrawRecord item : withdrawRecordList) {
            if (mSet.add(item.getId())) {
                mWithdrawRecordAdapter.add(item);
            }
        }
        mWithdrawRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WithdrawRecord withdrawRecord = (WithdrawRecord) parent.getAdapter().getItem(position);
        int withdrawRecordId = withdrawRecord.getId();
        Launcher.with(WithdrawRecordActivity.this, WithdrawRecordInfoActivity.class).putExtra(WithdrawRecordInfoActivity.WITHDRAW_RECORD_INFO_ID, withdrawRecordId).execute();
    }

    static class WithdrawRecordAdapter extends ArrayAdapter<WithdrawRecord> {

        public WithdrawRecordAdapter(Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_withdraw_record, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.bindingData(getItem(position), getContext());
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

            public void bindingData(WithdrawRecord item, Context context) {
                String time = item.getCreateTime().trim();
                String withdrawTime = "";
                if (DateUtil.isInThisYear(time, DateUtil.DEFAULT_FORMAT)) {
                    withdrawTime = DateUtil.format(time, DateUtil.DEFAULT_FORMAT, "MM/dd HH:mm");
                } else {
                    withdrawTime = DateUtil.format(time, DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm");
                }
                String[] date = withdrawTime.split(" ");
                if (date.length == 2) {
                    mSaleDateMonth.setText(date[0]);
                    mSaleDateHour.setText(date[1]);
                } else {
                    // TODO: 2016/9/29 做预防
                    mSaleDateMonth.setVisibility(View.GONE);
                    mSaleDateHour.setText(time.substring(0, time.indexOf(" ")));
                }
                mSaleGetMoney.setText(context.getString(R.string.withdraw_record_number, FinanceUtil.formatWithScale(item.getMoney())));
                if (item.getStatus() == WithdrawRecord.WITHDRAW_RECHARGE_SUCCESS) {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_green_primary);
                    mSaleStatus.setText(R.string.common_success);
                    mSaleGetMoney.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                    //如果提现失败或者拒绝
                } else if (item.getStatus() == WithdrawRecord.WITHDRAW_FAIL || item.getStatus() == WithdrawRecord.WITHDRAW_REFUSE) {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_red_primary);
                    mSaleStatus.setText(R.string.withdraw_status_fail);
                    mSaleGetMoney.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                    //审核通过
                } else if (item.getStatus() == WithdrawRecord.WITHDRAW_AND_RECHARGE_INITIATE) {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_gray);
                    mSaleStatus.setText(R.string.withdraw_auditing);
                } else if (item.getStatus() == WithdrawRecord.WITHDRAW_PASS) {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_gray);
                    mSaleStatus.setText(R.string.transfer);
                } else {
                    mSaleStatus.setBackgroundResource(R.drawable.bg_gray);
                    mSaleStatus.setText(R.string.withdraw_auditing);
                    mSaleGetMoney.setTextColor(ContextCompat.getColor(context, R.color.colorDisable));
                }
            }
        }
    }
}
