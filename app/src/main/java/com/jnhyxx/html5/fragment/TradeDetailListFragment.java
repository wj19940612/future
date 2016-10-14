package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.jnhyxx.html5.domain.account.TradeDetail;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.RemarkHandleUtil;
import com.jnhyxx.html5.utils.TradeDetailRemarkUtil;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/8/30.
 */

public class TradeDetailListFragment extends BaseFragment {
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
    private Unbinder mBinder;


    @BindView(R.id.listView)
    ListView mList;
    @BindView(R.id.empty)
    TextView mEmpty;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_emptyview, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOffset = 0;
        mSet = new HashSet<>();
        getTradeInfoList();
    }

    public void getTradeInfoList() {
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
            mList.setEmptyView(mEmpty);
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
            mList.addFooterView(mFooter);
        }

        if (mTradeDetailLists.size() < mSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mList.removeFooterView(mFooter);
        }


        if (mTradeDetailAdapter == null) {
            mTradeDetailAdapter = new TradeDetailAdapter(getContext());
        }
        for (TradeDetail item : mTradeDetailLists) {
            if (mSet.add(item.getId())) {
                mTradeDetailAdapter.add(item);
            }
        }
        mList.setAdapter(mTradeDetailAdapter);
        mTradeDetailAdapter.notifyDataSetChanged();
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
                convertView = LayoutInflater.from(context).inflate(R.layout.row_trade_detail, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext(), position);
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
            @BindView(R.id.splitBlock)
            View mSplitBlock;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(TradeDetail item, Context context, int position) {
                if (position == 0) {
                    mSplitBlock.setVisibility(View.VISIBLE);
                } else {
                    mSplitBlock.setVisibility(View.GONE);
                }
                String createTime = item.getCreateTime().trim();
                String tradeDetailTime;
                if (DateUtil.isInThisYear(createTime, DateUtil.DEFAULT_FORMAT)) {
                    tradeDetailTime = DateUtil.format(createTime, DateUtil.DEFAULT_FORMAT, "MM/dd HH:mm");
                } else {
                    tradeDetailTime = DateUtil.format(createTime, DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm");
                }
                String[] time = tradeDetailTime.split(" ");
                if (time.length == 2) {
                    mTimeYear.setText(time[0]);
                    mTimeHour.setText(time[1]);

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

                if (item.getTypeDetail() > 0) {
                    mStringBuffer.append("+");
                    mDataType.setBackgroundResource(R.drawable.bg_red_primary);
//                    mTradeDetailMarginRemain.setTextColor(getResources().getColor(R.color.common_rise_activity_sum));
                    mTradeDetailMarginRemain.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));

                } else {
                    mStringBuffer.append("-");
                    mDataType.setBackgroundResource(R.drawable.bg_green_primary);
                    mTradeDetailMarginRemain.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                }
                String data = new RemarkHandleUtil().get(item.getTypeDetail());
                if (!TextUtils.isEmpty(data)) {
                    mDataType.setText(data);
                } else {
                    mTradeDetailAdapter.remove(item);
                }
//                mTradeDetail.setText(CommonMethodUtils.getRemarkInfo(data, item.getRemark()));
                /**
                 * 根据得到的key值显示文字，如果value不存在，则不显示;
                 */
//                String tradeDepict = new TradeDetailRemarkUtil().get(item.getTypeDetail());
                String tradeStatus = getTradeStatus(item);
                if (!TextUtils.isEmpty(tradeStatus)) {
                    mTradeDetail.setText(tradeStatus);
                } else {
                    mTradeDetailAdapter.remove(item);
                }


                if (TextUtils.equals(mFragmentType, TYPE_FUND)) {
                    mStringBuffer.append(FinanceUtil.formatWithScale(item.getMoney()));
                    mStringBuffer.append("元");
                } else {
                    mStringBuffer.append(FinanceUtil.formatWithScale(item.getScore()));
                    mStringBuffer.append("分");
                }
                mTradeDetailMarginRemain.setText(mStringBuffer.toString());
            }
        }
    }

    private String getTradeStatus(TradeDetail item) {
        RemarkHandleUtil mRemarkHandleUtil = new RemarkHandleUtil();
        String remark = item.getRemark();
        //第二栏显示的文字
        String result = "";
        if (item.getTypeDetail() == TradeDetail.LOGO_FEE_APPLY ||
                item.getTypeDetail() == TradeDetail.LOGO_FEE_BACK ||
                item.getTypeDetail() == TradeDetail.LOGO_MARGIN_BACK ||
                item.getTypeDetail() == TradeDetail.LOGO_MARGIN_FREEZE) {
            result = mRemarkHandleUtil.get(item.getTypeDetail()).trim();
            if (remark.contains(result)) {
                result = remark.substring(0, 2) + "(" + remark.substring(5, remark.length()) + ")";
            }


        } else if (item.getTypeDetail() == TradeDetail.LOGO_INCOME_ADD ||
                item.getTypeDetail() == TradeDetail.LOGO_INCOME_CUT) {
            result = mRemarkHandleUtil.get(item.getTypeDetail()).trim();
            if (remark.contains(result)) {
                if (remark.length() > 4) {
                    result = "(" + remark.substring(4, remark.length()) + ")";
                }
            }
        } else {
            result = new TradeDetailRemarkUtil().get(item.getTypeDetail());
        }
        return result;
    }
}