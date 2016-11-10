package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.johnz.kutils.DateUtil;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/11/8.
 * 直播互动界面
 */

public class LiveInteractionFragment extends BaseFragment implements AbsListView.OnScrollListener {
    private static final String TAG = "LiveInteractionFragment";

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;

    private TextView mFooter;

    private int mPage = 0;
    private int mPageSize = 0;
    private long mTimeStamp = 0;
    private LiveChatInfoAdapter mLiveChatInfoAdapter;

    private HashSet<Long> mHashSet;

    public static LiveInteractionFragment newInstance() {
        Bundle args = new Bundle();
        LiveInteractionFragment fragment = new LiveInteractionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPageSize = 15;
        mHashSet = new HashSet<>();
        mListView.setOnScrollListener(this);
        getChatInfo();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                mHashSet.clear();
                getChatInfo();
                if (!Network.isNetworkAvailable() && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void getChatInfo() {
        API.Live.getLiveTalk(mTimeStamp, mPage, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback<Resp<LiveHomeChatInfo>>() {

                    @Override
                    public void onReceive(Resp<LiveHomeChatInfo> liveHomeChatInfoResp) {
                        if (liveHomeChatInfoResp.isSuccess() && liveHomeChatInfoResp.hasData()) {
                            Log.d(TAG, "谈话内容" + liveHomeChatInfoResp.getData().getData().toString());
                            updateCHatInfo(liveHomeChatInfoResp.getData().getData());
                        }
                    }
                })
                .fire();
    }

    private long getTimeStamp(List<LiveHomeChatInfo.ChatData> chatDatas) {
        if (chatDatas != null && !chatDatas.isEmpty()) {
            return chatDatas.get(chatDatas.size() - 1).getTimeStamp();
        }
        return 0;
    }

    private void updateCHatInfo(final List<LiveHomeChatInfo.ChatData> chatDatas) {
        if (chatDatas == null || chatDatas.isEmpty() || chatDatas.size() == 0) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
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
                    mPage = mPage + 1;
                    mTimeStamp = getTimeStamp(chatDatas);
                    Log.d(TAG, "数据的位移标识 " + mTimeStamp);
                    getChatInfo();
                }
            });
            mListView.addFooterView(mFooter);
        }

        if (chatDatas.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mListView.removeFooterView(mFooter);
            mFooter = null;
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mLiveChatInfoAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mLiveChatInfoAdapter == null) {
            mLiveChatInfoAdapter = new LiveChatInfoAdapter(getActivity());
            mListView.setAdapter(mLiveChatInfoAdapter);
        }

        for (LiveHomeChatInfo.ChatData item : chatDatas) {
            if (mHashSet.add(item.getCreateTime())) {
                mLiveChatInfoAdapter.add(item);
            }
        }
        mLiveChatInfoAdapter.notifyDataSetChanged();
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

    static class LiveChatInfoAdapter extends ArrayAdapter<LiveHomeChatInfo.ChatData> {

        Context mContext;

        public LiveChatInfoAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_live_interaction, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindViewWithData(getItem(position), position, mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.timeBeforeHint)
            TextView mTimeBeforeHint;
            @BindView(R.id.timeBeforeHintLayout)
            LinearLayout mTimeBeforeHintLayout;

            //老师或者管理员的layout
            @BindView(R.id.userStatus)
            TextView mUserStatus;
            @BindView(R.id.timeHint)
            TextView mTimeHint;
            @BindView(R.id.userHeadImage)
            ImageView mUserHeadImage;
            @BindView(R.id.arrow)
            ImageView mArrow;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.managerLayout)
            RelativeLayout mManagerLayout;

            //自己发言的layout
            @BindView(R.id.userMineStatus)
            TextView mUserMineStatus;
            @BindView(R.id.mineTimeHint)
            TextView mMineTimeHint;
            @BindView(R.id.userMineContent)
            TextView mUserMineContent;
            @BindView(R.id.userMineArrow)
            ImageView mUserMineArrow;
            @BindView(R.id.userMineHeadImage)
            ImageView mUserMineHeadImage;
            @BindView(R.id.userMineLayout)
            RelativeLayout mUserMineLayout;

            //普通游客
            @BindView(R.id.commonUserStatus)
            TextView mCommonUserStatus;
            @BindView(R.id.commonUserTimeHint)
            TextView mCommonUserTimeHint;
            @BindView(R.id.commonUserHeadImage)
            ImageView mCommonUserHeadImage;
            @BindView(R.id.commonUserContent)
            TextView mCommonUserContent;
            @BindView(R.id.commonUserLayout)
            RelativeLayout mCommonUserLayout;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(LiveHomeChatInfo.ChatData item, int position, Context context) {

                String format = DateUtil.format(item.getCreateTime());
                // TODO: 2016/11/10 测试老师
                if (position % 3 == 2) {
                    showManagerLayout();
                    setChaterStatus(item, context);
                    mContent.setText(item.getMsg());
                    mTimeHint.setText(format);
                    return;
                }
                //老师或者管理员
                if (!item.isCommonUser()) {
                    showManagerLayout();
                    setChaterStatus(item, context);
                    mContent.setText(item.getMsg());
                    mTimeHint.setText(format);
                    //普通游客发言
                } else {
                    //自己发的言
                    if (item.isOwner()) {
                        showUserMineLayout();
                        mMineTimeHint.setText(format);
                        mUserMineStatus.setText(R.string.live_type_mine);
                        mUserMineContent.setText(item.getMsg());
                        //普通游客发言
                    } else {
                        showCommonUserLayout();
                        mCommonUserStatus.setText(item.getName());
                        mCommonUserContent.setText(item.getMsg());
                        mCommonUserTimeHint.setText(format);
                    }
                }
            }

            private void showManagerLayout() {
                if (!mManagerLayout.isShown()) {
                    mManagerLayout.setVisibility(View.VISIBLE);
                    mCommonUserLayout.setVisibility(View.GONE);
                    mUserMineLayout.setVisibility(View.GONE);
                }
            }

            private void showUserMineLayout() {
                if (!mUserMineLayout.isShown()) {
                    mManagerLayout.setVisibility(View.GONE);
                    mCommonUserLayout.setVisibility(View.GONE);
                    mUserMineLayout.setVisibility(View.VISIBLE);
                }
            }

            private void showCommonUserLayout() {
                if (!mCommonUserLayout.isShown()) {
                    mCommonUserLayout.setVisibility(View.VISIBLE);
                    mManagerLayout.setVisibility(View.GONE);
                    mUserMineLayout.setVisibility(View.GONE);
                }
            }

            private void setChaterStatus(LiveHomeChatInfo.ChatData item, Context context) {
                String chatUser = "";
                if (item.getChatType() == item.CHAT_TYPE_MANAGER) {
                    chatUser = context.getString(R.string.live_type_manager);
                } else if (item.getChatType() == item.CHAT_TYPE_TEACHER) {
                    chatUser = context.getString(R.string.live_type_teacher);
                }
                // TODO: 2016/11/10  测试，需删除
                mUserStatus.setText(R.string.live_type_manager);
//                mUserStatus.setText(chatUser);
            }
        }
    }
}

