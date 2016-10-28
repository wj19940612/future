package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.msg.SysTradeMessage;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.johnz.kutils.DateUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MsgListFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TYPE = "fragmentType";
    public static final int TYPE_SYSTEM = 2;
    public static final int TYPE_TRADE = 3;


    private OnMsgItemClickListener mListener;

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

    private MessageListAdapter mMessageListAdapter;
    private Set<Integer> mSet;
    private TextView mFooter;

    public static MsgListFragment newInstance(int type) {
        MsgListFragment fragment = new MsgListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMsgItemClickListener) {
            mListener = (OnMsgItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMsgItemClickListener");
        }
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
        mPageNo = 0;
        mPageSize = 10;
        mSet = new HashSet<>();
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mListView.setDivider(null);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPageNo = 0;
                requestMessageList();
                if (!Network.isNetworkAvailable() && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
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
        mListener = null;
        API.cancel(TAG);
    }

    private void requestMessageList() {
        API.Message.getMessageInfo(mType, mPageNo, mPageSize)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp<List<SysTradeMessage>>>() {
                                 @Override
                                 public void onReceive(Resp<List<SysTradeMessage>> listResp) {
                                     if (listResp.isSuccess()) {
                                         updateMessageList(listResp.getData());
                                         for (int i = 0; i < listResp.getData().size(); i++) {
                                             Log.d(TAG, "系统消息中心数据" + listResp.getData().get(i).toString());
                                         }
                                     } else {
                                         if (mSwipeRefreshLayout.isRefreshing()) {
                                             mSwipeRefreshLayout.setRefreshing(false);
                                         }
                                     }
                                 }
                             }

                ).fire();
    }

    private void updateMessageList(List<SysTradeMessage> sysTradeMessages) {
        if (sysTradeMessages == null) {
            mListView.setEmptyView(mEmpty);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
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

        if (sysTradeMessages.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mListView.removeFooterView(mFooter);
        }

        if (mMessageListAdapter == null) {
            mMessageListAdapter = new MessageListAdapter(getContext());
            mListView.setAdapter(mMessageListAdapter);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mMessageListAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        for (SysTradeMessage item : sysTradeMessages) {
            if (mSet.add(item.getId())) {
                mMessageListAdapter.add(item);
            }
        }
        mMessageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SysTradeMessage message = (SysTradeMessage) parent.getAdapter().getItem(position);
        if (mListener != null) {
            mListener.onMsgItemClick(message);
        }
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

    static class MessageListAdapter extends ArrayAdapter<SysTradeMessage> {

        public MessageListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bindingData(getItem(position), position);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.splitBlock)
            View mSplitBlock;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.updateDate)
            TextView mUpdateDate;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(SysTradeMessage item, int position) {
                if (item == null) return;
                if (position == 0) {
                    mSplitBlock.setVisibility(View.VISIBLE);
                } else {
                    mSplitBlock.setVisibility(View.GONE);
                }

                if (item.getCreateTime().isEmpty()) return;

                String systemTime = item.getCreateTime();
                if (DateUtil.isInThisYear(systemTime, DateUtil.DEFAULT_FORMAT)) {
                    systemTime = DateUtil.format(systemTime, DateUtil.DEFAULT_FORMAT, "MM/dd HH:mm");
                } else {
                    systemTime = DateUtil.format(systemTime, DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm");
                }
                mUpdateDate.setText(systemTime);
                mTitle.setText(item.getPushTopic());
            }
        }
    }

    public interface OnMsgItemClickListener {
        void onMsgItemClick(SysTradeMessage sysTradeMessage);
    }
}
