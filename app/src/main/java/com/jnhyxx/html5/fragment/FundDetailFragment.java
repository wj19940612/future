package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.account.TradeDetail;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.net.ApiIndeterminate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${wangJie} on 2016/9/19.
 * 资金明细
 */

public class FundDetailFragment extends ListFragment implements ApiIndeterminate {

    private static final String TAG = "FundDetailFragment";
    //资金
    public static final String TYPE_FUND = "money";

    //流水显示条数
    private static final int mSize = 20;
    //流水起点
    private static int mOffset = 0;

    /**
     * bundle所传递的fragmentId，代表是哪一个fragment
     */
    private static final String TYPE = "fragmentItem";
    /**
     * 代表是哪一个fragment
     */
    private String mFragmentType;


    private ArrayList<TradeDetail> mTradeDetailList;
    private HashSet<Integer> mSet;
    private TradeDetail tradeDetail;

    private TextView mFooter;

    private TradeDetailAdapter mTradeDetailAdapter;


    public static FundDetailFragment newInstance(String type) {
        FundDetailFragment mFundDetailFragment = new FundDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, type);
        mFundDetailFragment.setArguments(bundle);
        return mFundDetailFragment;
    }

    public static FundDetailFragment newInstance() {
        FundDetailFragment mFundDetailFragment = new FundDetailFragment();
        return mFundDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentType = getArguments().getString(TYPE);
        }
        mTradeDetailList = new ArrayList<TradeDetail>();
    }


    public void onActivityCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSet = new HashSet<>();
        setEmptyText(getString(R.string.there_is_no_info_for_now));
//        getListView().setDivider(null);
        getTradeInfoList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getTradeInfoList() {

        API.Finance.getFundSwitchIntegral(TYPE_FUND, mOffset, mSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<List<TradeDetail>>>() {
                    @Override
                    public void onReceive(Resp<List<TradeDetail>> listResp) {

                        mTradeDetailList = (ArrayList<TradeDetail>) listResp.getData();
                        for (int i = 0; i < mTradeDetailList.size(); i++) {
                            Log.d(TAG, "资金明细查询结果" + mTradeDetailList.get(i).toString());
                        }
                        setAdapter(mTradeDetailList);
                    }
                }).fire();

    }

    private void setAdapter(ArrayList<TradeDetail> mTradeDetailLists) {
        if (mTradeDetailLists == null || mTradeDetailLists.isEmpty()) {
            return;
        }
        if (mFooter == null) {
            mFooter = new TextView(getContext());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
            mFooter.setPadding(padding, padding, padding, padding);
            mFooter.setGravity(Gravity.CENTER);
            mFooter.setText(R.string.click_to_load_more);
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffset++;
                    getTradeInfoList();
                }
            });
            getListView().addFooterView(mFooter);
        }

        if (mTradeDetailLists.size() < mSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            getListView().removeFooterView(mFooter);
        }

        if (mTradeDetailAdapter == null) {
            mTradeDetailAdapter = new TradeDetailAdapter(getContext());
            getListView().setAdapter(mTradeDetailAdapter);
        }

        for (TradeDetail item : mTradeDetailLists) {
            if (mSet.add(item.getId())) {
                mTradeDetailAdapter.add(item);
            }
        }
        mTradeDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShow(String tag) {
        setListShown(false);
    }

    @Override
    public void onDismiss(String tag) {
        setListShown(true);
    }


    class TradeDetailAdapter extends ArrayAdapter<TradeDetail> {
        Context context;

        public TradeDetailAdapter(Context context) {
            super(context, 0);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_trade_detail, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext());
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.dateYear)
            TextView mTimeYear;
            @BindView(R.id.dateHour)
            TextView mTimeHour;
            @BindView(R.id.tradeDetailDataType)
            TextView mDataType;
            @BindView(R.id.dateTypeDetail)
            TextView mTradeDetail;
            @BindView(R.id.tradeDetailGrade)
            TextView mTradeDetailMarginRemain;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(TradeDetail item, Context context) {
                String createTime = item.getCreateTime().trim();
                String[] time = createTime.split(" ");
                if (time.length == 2) {
                    mTimeYear.setText(time[0]);
                    mTimeHour.setText(time[1]);
                } else {
                    mTimeHour.setText(createTime);
                }
                mDataType.setText(String.valueOf(item.getTypeDetail()));
                mTradeDetail.setText(item.getRemark());
                mTradeDetailMarginRemain.setText(String.valueOf(item.getScoreLeft()));
            }
        }

    }
}
