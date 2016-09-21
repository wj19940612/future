package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
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
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.RemarkHandleUtil;
import com.johnz.kutils.net.ApiIndeterminate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/30.
 */

public class TradeDetailListFragment extends ListFragment implements ApiIndeterminate {
    private static final String TAG = "TradeDetailListFragment";

    //积分
    public static final String TYPE_INTEGRAL = "score";
    //资金
    public static final String TYPE_FUND = "money";

    //流水显示条数
    private static final int mSize = 15;
    //流水起点
    private static int mOffset;

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

    boolean isLoaded;

    public static TradeDetailListFragment newInstance(String type) {
        TradeDetailListFragment mTradeDetailListFragment = new TradeDetailListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, type);
        mTradeDetailListFragment.setArguments(bundle);
        return mTradeDetailListFragment;
    }

    public static TradeDetailListFragment newInstance() {
        return new TradeDetailListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentType = getArguments().getString(TYPE);
        }
        mTradeDetailList = new ArrayList<TradeDetail>();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOffset = 0;
        mSet = new HashSet<>();
        setEmptyText(getString(R.string.there_is_no_info_for_now));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isAdded() && getUserVisibleHint() && !isLoaded) {
            getTradeInfoList();
            isLoaded = true;
        }
    }

    public void getTradeInfoList() {
        Log.d(TAG, "所选的fragment " + mFragmentType);
//        if (TextUtils.equals(mFragmentType, TYPE_FUND)) {
//            API.Finance.getFundSwitchIntegral(TYPE_FUND, mOffset, mSize)
//                    .setTag(TAG)
//                    .setIndeterminate(this)
//                    .setCallback(new Callback<Resp<List<TradeDetail>>>() {
//                        @Override
//                        public void onReceive(Resp<List<TradeDetail>> listResp) {
//
//                            mTradeDetailList = (ArrayList<TradeDetail>) listResp.getData();
//                            for (int i = 0; i < mTradeDetailList.size(); i++) {
//                                Log.d(TAG, "资金明细查询结果" + mTradeDetailList.get(i).toString());
//                            }
//                            setAdapter(mTradeDetailList);
//                        }
//                    }).fire();
//        } else if (TextUtils.equals(mFragmentType, TYPE_INTEGRAL)) {
//            API.Finance.getFundSwitchIntegral(TYPE_INTEGRAL, mOffset, mSize)
//                    .setTag(TAG).setIndeterminate(this)
//                    .setCallback(new Callback<Resp<List<TradeDetail>>>() {
//                        @Override
//                        public void onReceive(Resp<List<TradeDetail>> listResp) {
//                            mTradeDetailList = (ArrayList<TradeDetail>) listResp.getData();
//                            for (int i = 0; i < mTradeDetailList.size(); i++) {
//                                Log.d(TAG, "积分明细查询结果" + mTradeDetailList.get(i).toString());
//                            }
//                            setAdapter(mTradeDetailList);
//                        }
//                    }).fire();
//        }
        if (TextUtils.isEmpty(mFragmentType)) return;
        API.Finance.getFundSwitchIntegral(mFragmentType, mOffset, mSize)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<List<TradeDetail>>>() {
                    @Override
                    public void onReceive(Resp<List<TradeDetail>> listResp) {
                        mTradeDetailList = (ArrayList<TradeDetail>) listResp.getData();
                        for (int i = 0; i < mTradeDetailList.size(); i++) {
                            Log.d(TAG, "交易明细查询结果" + mTradeDetailList.get(i).toString());
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
//                    mOffset++;
                    mOffset = mOffset + 10;
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

    public class TradeDetailAdapter extends ArrayAdapter<TradeDetail> {
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
                    // TODO: 2016/9/21 未做转换的  返回的是时间格式  2016-09-21 14:11"50   转换成 09/21
                    mTimeYear.setText(time[0]);
                    mTimeHour.setText(time[1]);
//                    String yearDate = time[0];
//                    if (yearDate.contains("-")) {
//                        Log.d(TAG, "时间中含有-");
//                        yearDate = yearDate.substring(5, yearDate.length());
//                        yearDate = yearDate.replace("-", "/");
//                    }
//                    mTimeHour.setText(yearDate);
//
//                    String hourDate = time[1];
//                    if (hourDate.contains(":")) {
//                        hourDate = hourDate.substring(0, hourDate.lastIndexOf(":"));
//                    }
//                    mTimeHour.setText(hourDate);

                } else {
                    mTimeHour.setText(createTime);
                }

                /**
                 * 最右侧数据，如果是资金  最后为元;
                 *            如果是积分  最后是分
                 *            如果是正数  最前面是+
                 *            如果是负数  最前面是-
                 */
                StringBuffer mStringBuffer = new StringBuffer();

                if (item.getType() > 0) {
                    mStringBuffer.append("+");
                    mDataType.setBackgroundResource(R.drawable.bg_red_primary);
                    mTradeDetailMarginRemain.setTextColor(getResources().getColor(R.color.common_rise_activity_sum));
                } else {
                    mStringBuffer.append("-");
                    mDataType.setBackgroundResource(R.drawable.bg_green_primary);
                    mTradeDetailMarginRemain.setTextColor(getResources().getColor(R.color.common_drop));
                }
                String data = new RemarkHandleUtil().get(item.getTypeDetail());
                mDataType.setText(data);
                mTradeDetail.setText(CommonMethodUtils.getRemarkInfo(data, item.getRemark()));
                if (TextUtils.equals(mFragmentType, TYPE_FUND)) {
                    mStringBuffer.append(String.valueOf(item.getMoney()));
                    mStringBuffer.append("元");
                } else {
                    mStringBuffer.append(String.valueOf(item.getScore()));
                    mStringBuffer.append("分");
                }
                mTradeDetailMarginRemain.setText(mStringBuffer.toString());
            }
        }
    }
}