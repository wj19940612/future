package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.DateUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/10/10.
 */

public class TradeHintListFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private static final String TYPE = "fragmentType";
    public static final int TYPE_SYSTEM = 2;
    public static final int TYPE_TRADE = 3;
    private int mType;


    private int mPageNo;
    private int mPageSize;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private Unbinder mBinder;

    private TradeListAdapter mTradeListAdapter;
    private Set<String> mSet;
    private TextView mFooter;

    private boolean isLoad = false;

    public static TradeHintListFragment newInstance(int type) {
        TradeHintListFragment fragment = new TradeHintListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_emptyview, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEmpty.setText(R.string.now_is_not_has_trade_remind);
        mListView.setEmptyView(mEmpty);

        mPageNo = 0;
        mPageSize = 10;
        mSet = new HashSet<>();

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mListView.setDivider(null);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNo = 0;
                mSet.clear();
                requestMessageList();
            }
        });
        requestMessageList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        API.cancel(TAG);
    }

    public void requestMessageList() {
        API.Message.getMessageInfo(mType, mPageNo, mPageSize)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp<List<SysMessage>>>() {
                                 @Override
                                 public void onReceive(Resp<List<SysMessage>> listResp) {
                                     if (listResp.isSuccess()) {
                                         updateMessageList(listResp.getData());
                                     } else {
                                         stopRefreshAnimation();
                                     }
                                 }

                                 @Override
                                 public void onFailure(VolleyError volleyError) {
                                     super.onFailure(volleyError);
                                     stopRefreshAnimation();
                                 }
                             }
                ).fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateMessageList(List<SysMessage> sysMessages) {
        if (sysMessages == null) {
            stopRefreshAnimation();
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
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mPageNo++;
                    requestMessageList();
                }
            });
            mListView.addFooterView(mFooter);
        }

        if (sysMessages.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mListView.removeFooterView(mFooter);
        }

        if (mTradeListAdapter == null) {
            mTradeListAdapter = new TradeListAdapter(getContext());
            mListView.setAdapter(mTradeListAdapter);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mTradeListAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        for (SysMessage item : sysMessages) {
            if (mSet.add(item.getId())) {
                mTradeListAdapter.add(item);
            }
        }
        mTradeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SysMessage message = (SysMessage) parent.getAdapter().getItem(position);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    static class TradeListAdapter extends ArrayAdapter<SysMessage> {

        public TradeListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TradeViewHolder mTradeViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_trade_hint, null);
                mTradeViewHolder = new TradeViewHolder(convertView);
                convertView.setTag(mTradeViewHolder);
            } else {
                mTradeViewHolder = (TradeViewHolder) convertView.getTag();
            }
            mTradeViewHolder.bindingData(getItem(position), position);
            return convertView;
        }

        class TradeViewHolder {
            @BindView(R.id.tradeStatusHint)
            ImageView mTradeStatusHint;
            @BindView(R.id.tradeStatus)
            TextView mTradeStatus;
            @BindView(R.id.tradeTime)
            TextView mTradeTime;
            @BindView(R.id.tradeHintContent)
            TextView mTradeHintContent;
            @BindView(R.id.splitBlock)
            View mView;

            TradeViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(SysMessage item, int position) {
                if (item == null) return;
                if (position == 0) {
                    mView.setVisibility(View.VISIBLE);
                } else {
                    mView.setVisibility(View.GONE);
                }
                setTradeTime(item);
                mTradeStatus.setText(item.getPushTopic());
                setTradeStatus(item);
                mTradeHintContent.setText(item.getPushContent());
            }

            private void setTradeStatus(SysMessage item) {
                if (item.isTradeStatus()) {
                    if (item.isSuccess()) {
                        setSuccessImage();
                    } else {
                        mTradeStatusHint.setImageResource(R.drawable.ic_trade_warn_list_icon_fail);
                    }
                } else {
                    setOrderStatus(item);
                }
            }

            private void setSuccessImage() {
                int successImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getContext().getResources().getDisplayMetrics());
                ViewGroup.LayoutParams layoutParams = mTradeStatusHint.getLayoutParams();
                layoutParams.width = successImageSize;
                layoutParams.height = successImageSize;
                mTradeStatusHint.setLayoutParams(layoutParams);
                mTradeStatusHint.setImageResource(R.drawable.ic_common_toast_succeed);
            }

            private void setOrderStatus(SysMessage item) {
                if (item.isSuccess()) {
                    mTradeStatusHint.setImageResource(R.drawable.ic_trade_warn_list_icon_good);
                } else {
                    mTradeStatusHint.setImageResource(R.drawable.ic_trade_warn_list_icon_bad);
                }
            }

            private void setTradeTime(SysMessage item) {
                String tradeTime = item.getCreateTime();
                if (TextUtils.isEmpty(tradeTime)) return;
                if (DateUtil.isInThisYear(tradeTime, DateUtil.DEFAULT_FORMAT)) {
                    tradeTime = DateUtil.format(tradeTime, DateUtil.DEFAULT_FORMAT, "MM/dd HH:mm");
                } else {
                    tradeTime = DateUtil.format(tradeTime, DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm");
                }
                mTradeTime.setText(tradeTime);
            }
        }
    }
}
