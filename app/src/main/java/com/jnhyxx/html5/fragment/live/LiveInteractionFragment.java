package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.gson.Gson;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.ChatData;
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.domain.live.LiveSpeakInfo;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.johnz.kutils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.jnhyxx.html5.activity.LiveActivity.REQUEST_CODE_LOGIN;


/**
 * Created by ${wangJie} on 2016/11/8.
 * 直播互动界面
 */

public class LiveInteractionFragment extends BaseFragment implements AbsListView.OnScrollListener {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty)
    TextView mEmpty;

    private Unbinder mBind;


    private int mPage = 0;
    private int mPageSize = 0;
    private long mTimeStamp = 0;
    private LiveChatInfoAdapter mLiveChatInfoAdapter;

    private HashSet<Long> mHashSet;


    private List<ChatData> mChatDataListInfo;

    private ArrayList<ChatData> mDataArrayList;

    private boolean isRefreshed;
    private LiveMessage.TeacherInfo mTeacherInfo;

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
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);
        mPageSize = 10;
        mHashSet = new HashSet<>();
        mDataArrayList = new ArrayList<>();
        mListView.setOnScrollListener(this);
        getChatInfo();
        setOnRefresh();
    }


    private void setOnRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefreshed) {
                    mTimeStamp = getTimeStamp(mDataArrayList);
                    mPage = mPage + 1;
                    getChatInfo();
                    if (!Network.isNetworkAvailable() && mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
                    mListView.setStackFromBottom(false);
                } else {
                    ToastUtil.curt("没有更多的数据了");
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    public void setTeacherInfo(LiveMessage.TeacherInfo teacherInfo) {
        mTeacherInfo = teacherInfo;
    }

    public void setData(String data) {
        Log.d(TAG, "新数据" + data);
        LiveSpeakInfo liveSpeakInfo = new Gson().fromJson(data, LiveSpeakInfo.class);

        if (liveSpeakInfo != null) {
            if (liveSpeakInfo.isOwner()) {
                mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                mListView.setStackFromBottom(true);
            }
            if (liveSpeakInfo.isSlience() && liveSpeakInfo.isOwner()) {
                ToastUtil.curt("您被禁言，请稍后发言");
            }
            if (!TextUtils.isEmpty(liveSpeakInfo.getMsg())) {
                if (liveSpeakInfo.isOwner() || !liveSpeakInfo.isSlience()) {
                    ChatData chatData = new ChatData(liveSpeakInfo);
                    if (chatData != null && mLiveChatInfoAdapter != null) {
                        if (mHashSet.add(chatData.getCreateTime())) {
                            mDataArrayList.add(chatData);
                            if (DateUtil.isTimeBetweenFiveMin(chatData.getCreateTime(), mDataArrayList.get(mDataArrayList.size() - 2).getCreateTime())) {
                                chatData.setMoreThanFiveMin(true);
                            }
                            mLiveChatInfoAdapter.add(chatData);
                            mLiveChatInfoAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        }
    }

    private void getChatInfo() {
        API.Live.getLiveTalk(mTimeStamp, mPage, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback<Resp<LiveHomeChatInfo>>() {

                                 @Override
                                 public void onReceive(Resp<LiveHomeChatInfo> liveHomeChatInfoResp) {
                                     if (liveHomeChatInfoResp.isSuccess()) {
                                         if (liveHomeChatInfoResp.hasData()) {

                                             mChatDataListInfo = liveHomeChatInfoResp.getData().getData();

                                             // TODO: 2016/11/15 如果不是本人，则被屏蔽或者被禁言的部分看不到
                                             Iterator<ChatData> iterator = mChatDataListInfo.iterator();
                                             while (iterator.hasNext()) {
                                                 ChatData chatData = iterator.next();
                                                 Log.d(TAG, "下载的数据" + chatData.toString() + "\n");
                                                 if (!chatData.isOwner())
                                                     if (!chatData.isNormalSpeak() || chatData.isDeleted()) {
                                                         iterator.remove();
                                                     }
                                             }

                                             mDataArrayList.addAll(0, mChatDataListInfo);
                                             updateCHatInfo(liveHomeChatInfoResp.getData());
                                         } else {
                                             if (mChatDataListInfo.size() < mPageSize) {
                                                 isRefreshed = true;
                                             }
                                         }
                                     }
                                 }
                             }
                ).fire();
    }

    private long getTimeStamp(List<ChatData> chatDatas) {
        if (chatDatas != null && !chatDatas.isEmpty()) {
            return chatDatas.get(chatDatas.size() - 1).getTimeStamp();
        }
        return 0;
    }

    //登录成功后需要清空数据，重新获取状态
    public void setLoginSuccess(boolean isLogin) {
        if (isLogin) {
            if (mLiveChatInfoAdapter != null) {
                mLiveChatInfoAdapter.clear();
            }
            if (mDataArrayList != null && !mDataArrayList.isEmpty()) {
                mDataArrayList.clear();
            }
            getChatInfo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            ToastUtil.curt("接到登录成功的回调了");
        }
    }

    private void updateCHatInfo(final LiveHomeChatInfo liveHomeChatInfo) {
        if (liveHomeChatInfo == null || liveHomeChatInfo.getData().isEmpty() || liveHomeChatInfo.getData().size() == 0) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
            return;
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mLiveChatInfoAdapter == null) {
            mLiveChatInfoAdapter = new LiveChatInfoAdapter(getActivity());
            mListView.setAdapter(mLiveChatInfoAdapter);
        }
        if (mTeacherInfo != null) {
            mLiveChatInfoAdapter.setTeacher(mTeacherInfo);
        }

        mLiveChatInfoAdapter.clear();
        if (mDataArrayList != null && !mDataArrayList.isEmpty()) {
            int dataPosition = mDataArrayList.size() - 1;
            for (int i = mDataArrayList.size(); i > 0; i--) {
                if (DateUtil.isTimeBetweenFiveMin(mDataArrayList.get(dataPosition).getCreateTime(), mDataArrayList.get(i - 1).getCreateTime())) {
                    mDataArrayList.get(i).setMoreThanFiveMin(true);
                    dataPosition = i - 1;
                }
            }
            mLiveChatInfoAdapter.addAll(mDataArrayList);
        }
        mLiveChatInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            mOnScrollListener.scroll(true);
        }
    }

    private OnScrollListener mOnScrollListener;

    public interface OnScrollListener {
        void scroll(boolean isScroll);
    }

    public void setOnScrollListener(OnScrollListener scrollListener) {
        mOnScrollListener = scrollListener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    static class LiveChatInfoAdapter extends ArrayAdapter<ChatData> {

        private Context mContext;
        private LiveMessage.TeacherInfo mTeacherInfo;

        public LiveChatInfoAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setTeacher(LiveMessage.TeacherInfo teacher) {
            mTeacherInfo = teacher;
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
            viewHolder.bindViewWithData(getItem(position), mContext, mTeacherInfo);
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
            @BindView(R.id.commonUserHeadImage)
            ImageView mCommonUserHeadImage;
            @BindView(R.id.commonUserContent)
            TextView mCommonUserContent;
            @BindView(R.id.commonUserLayout)
            RelativeLayout mCommonUserLayout;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(ChatData item, Context context, LiveMessage.TeacherInfo teacherInfo) {

                String formatTime = DateUtil.getFormatTime(item.getCreateTime());

                if (item.isMoreThanFiveMin()) {
                    mTimeBeforeHintLayout.setVisibility(View.VISIBLE);
                    mTimeBeforeHint.setText(formatTime);
                } else {
                    mTimeBeforeHintLayout.setVisibility(View.GONE);
                }

                //老师或者管理员
                if (!item.isNormalUser()) {
                    showManagerLayout();
                    setChatUserStatus(item, context, teacherInfo);
                    mContent.setText(item.getMsg());
                    //普通游客发言
                } else {
                    //自己发的言
                    if (item.isOwner()) {
                        showUserMineLayout();
                        mUserMineStatus.setText(R.string.live_type_mine);
                        mUserMineContent.setText(item.getMsg());
                        //普通游客发言
                    } else {
                        showCommonUserLayout();
                        mCommonUserStatus.setText(item.getName());
                        mCommonUserContent.setText(item.getMsg());
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

            private void setChatUserStatus(ChatData item, Context context, LiveMessage.TeacherInfo teacherInfo) {
                String chatUser = "";
                if (item.getChatType() == item.CHAT_TYPE_MANAGER) {
                    chatUser = context.getString(R.string.live_type_manager);
                } else if (item.getChatType() == item.CHAT_TYPE_TEACHER) {
                    chatUser = item.getName();


//                    Picasso.with(context).load("https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1477449444225008026.png")
//                            .transform(new CircleTransform()).into(mUserHeadImage);
                    if (teacherInfo != null && !TextUtils.isEmpty(teacherInfo.getPictureUrl())) {
                        Picasso.with(context).load(teacherInfo.getPictureUrl())
                                .transform(new CircleTransform()).into(mUserHeadImage);
                    } else {
                        Picasso.with(context).load(R.drawable.ic_live_pic_head)
                                .transform(new CircleTransform()).into(mUserHeadImage);
                    }
                }
                mUserStatus.setText(chatUser);
            }
        }
    }
}

