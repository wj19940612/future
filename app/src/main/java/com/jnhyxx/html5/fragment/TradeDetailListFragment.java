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
import com.jnhyxx.html5.domain.Information;
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
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/8/30.
 */

public class TradeDetailListFragment extends ListFragment implements ApiIndeterminate {
    private static final String TAG = "TradeDetailListFragment";

    //积分
    public static final int TYPE_INTEGRAL = 378;
    //资金
    public static final int TYPE_FUND = 646;

    //流水显示条数
    private static final int mSize = 15;
    //流水起点
    private static int mOffset = 1;

    /**
     * bundle所传递的fragmentId，代表是哪一个fragment
     */
    private static final String FRAGMENT_ID = "fragmentItem";
    /**
     * 代表是哪一个fragment
     */
    private int mFragmentItem;


    private String mType;
    private ArrayList<TradeDetail> mTradeDetailList;
    private HashSet<Integer> mSet;
    private TradeDetail tradeDetail;

    private TextView mFooter;

    private TradeDetailAdapter mTradeDetailAdapter;


    public static TradeDetailListFragment newInstance(int fragmentItem) {
        TradeDetailListFragment mTradeDetailListFragment = new TradeDetailListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_ID, fragmentItem);
        mTradeDetailListFragment.setArguments(bundle);
        return mTradeDetailListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentItem = getArguments().getInt(FRAGMENT_ID, -1);
        }
        mTradeDetailList = new ArrayList<TradeDetail>();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSet = new HashSet<>();
        setEmptyText(getString(R.string.there_is_no_info_for_now));
        getListView().setDivider(null);
        getTradeInfoList();
    }


    public void getTradeInfoList() {
        if (mFragmentItem == TradeDetailListFragment.TYPE_FUND) {
            mType = "money";
        } else if (mFragmentItem == TYPE_INTEGRAL) {
            mType = "score";
        }

        API.Finance.getFundSwitchIntegral(mType, mOffset, mSize).setTag(TAG).setIndeterminate(this).setCallback(new Callback<Resp<List<TradeDetail>>>() {
            @Override
            public void onReceive(Resp<List<TradeDetail>> listResp) {
                mTradeDetailList = (ArrayList<TradeDetail>) listResp.getData();
                for (int i = 0; i < mTradeDetailList.size(); i++) {
                    Log.d(TAG, "积分或资金明细查询结果" + mTradeDetailList.get(i).toString());
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
            setListAdapter(mTradeDetailAdapter);
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trade_detail, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext());
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tvTradeDetailYear)
            TextView timeYear;
            @BindView(R.id.tvTradeDetailHour)
            TextView timeHour;
            @BindView(R.id.tvDataType)
            TextView dataType;
            @BindView(R.id.tvTradeDetailType)
            TextView tradeDetail;
            @BindView(R.id.tradeDetailGrade)
            TextView tradeDetailMarginRemain;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(TradeDetail item, Context context) {

            }
        }
    }
}