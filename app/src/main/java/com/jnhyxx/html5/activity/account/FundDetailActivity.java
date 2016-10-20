package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
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
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.finance.FundFlowItem;
import com.jnhyxx.html5.domain.finance.FundInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.StrUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FundDetailActivity extends BaseActivity {

    public static final String EX_IS_CASH = "isCash";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.frozenDeposit)
    TextView mFrozenDeposit;
    @BindView(R.id.profitLossList)
    ListView mProfitLossList;
    @BindView(R.id.emptyView)
    TextView mEmptyView;

    private FundFlowListAdapter mAdapter;
    private TextView mFooter;

    private boolean mIsCash;
    private int mPageNo;
    private int mPageSize;
    private Set<Integer> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_detail);
        ButterKnife.bind(this);

        mSet = new HashSet<>();
        mPageNo = 1;
        mPageSize = 10;
        mProfitLossList.setEmptyView(mEmptyView);

        determinePageType(getIntent());

        requestFlowList();
    }

    private void requestFlowList() {
        if (mIsCash) {
            API.Finance.getCashFlowList(LocalUser.getUser().getToken(), mPageNo, mPageSize)
                    .setCallback(new Callback2<Resp<List<FundFlowItem>>, List<FundFlowItem>>() {
                        @Override
                        public void onRespSuccess(List<FundFlowItem> fundFlowItems) {
                            updateFlowList(fundFlowItems);
                        }
                    }).setIndeterminate(this).setTag(TAG).fire();
        } else {
            API.Finance.getScoreFlowList(LocalUser.getUser().getToken(), mPageNo, mPageSize)
                    .setCallback(new Callback2<Resp<List<FundFlowItem>>, List<FundFlowItem>>() {
                        @Override
                        public void onRespSuccess(List<FundFlowItem> fundFlowItems) {
                            updateFlowList(fundFlowItems);
                        }
                    }).setIndeterminate(this).setTag(TAG).fire();
        }
    }

    private void updateFlowList(List<FundFlowItem> fundFlowItems) {
        if (fundFlowItems == null) return;

        if (mFooter == null) {
            mFooter = new TextView(this);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
            mFooter.setPadding(padding, padding, padding, padding);
            mFooter.setGravity(Gravity.CENTER);
            mFooter.setText(R.string.click_to_load_more);
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPageNo++;
                    requestFlowList();
                }
            });
            mProfitLossList.addFooterView(mFooter);
        }

        if (fundFlowItems.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mProfitLossList.removeFooterView(mFooter);
        }

        if (mAdapter == null) {
            mAdapter = new FundFlowListAdapter(this);
            mProfitLossList.setAdapter(mAdapter);
        }
        for (FundFlowItem item : fundFlowItems) {
            if (mSet.add(item.getId())) {
                mAdapter.add(item);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void determinePageType(Intent intent) {
        mIsCash = intent.getBooleanExtra(EX_IS_CASH, false);
        FundInfo fundInfo = (FundInfo) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        updateFrozenDepositView(fundInfo);
        mTitleBar.setTitle(mIsCash ? R.string.fund_detail : R.string.score_detail);
    }

    private void updateFrozenDepositView(FundInfo fundInfo) {
        String frozen = FinanceUtil.formatWithScale(fundInfo.getCurCashScore());
        if (mIsCash) {
            frozen = FinanceUtil.formatWithScale(fundInfo.getCurCashFund());
        }
        mFrozenDeposit.setText(StrUtil.mergeTextWithRatioColor(
                frozen, Unit.GOLD, 0.5f, Color.GRAY));
    }

     class FundFlowListAdapter extends ArrayAdapter<FundFlowItem> {

        public FundFlowListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_fund_flow, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bindingData(getItem(position), getContext().getResources());
            return convertView;
        }

         class ViewHolder {
            @BindView(R.id.date)
            TextView mDate;
            @BindView(R.id.introduce)
            TextView mIntroduce;
            @BindView(R.id.flowAmount)
            TextView mFlowAmount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(FundFlowItem item, Resources resources) {
                mDate.setText(item.getFormattedCreateDate());
                mIntroduce.setText(item.getIntro());
                String flowAmount;
                if (item.getType() > 0) {
                    mFlowAmount.setTextColor(getColor(R.color.greenPrimary));
                    flowAmount = "+" + item.getCurflowAmt();
                } else {
                    mFlowAmount.setTextColor(resources.getColor(R.color.greenPrimary));
                    flowAmount = "-" + item.getCurflowAmt();
                }
                mFlowAmount.setText(flowAmount);
            }
        }
    }
}
