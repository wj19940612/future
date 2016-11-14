package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.domain.live.ChatData;
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.domain.live.LiveSpeakInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/11/8.
 * 直播互动界面
 */

public class LiveInteractionFragment extends BaseFragment implements AbsListView.OnScrollListener {
    private static final String TAG = "LiveInteractionFragment";

    private static final int REQUEST_CODE_LOGIN = 583;

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.liveSpeak)
    ImageView mLiveSpeak;
    @BindView(R.id.speakEditText)
    EditText mSpeakEditText;

    private Unbinder mBind;

    private TextView mFooter;

    private int mPage = 0;
    private int mPageSize = 0;
    private long mTimeStamp = 0;
    private LiveChatInfoAdapter mLiveChatInfoAdapter;

    private HashSet<Long> mHashSet;

    private List<ChatData> chatDatas;
    private InputMethodManager mInputMethodManager;

    private ArrayList<ChatData> mDataArrayList;

    private boolean isRefreshed;

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
                } else {
                    ToastUtil.curt("没有更多的数据了");
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    public void setData(String data) {
        Log.d("newData", "新数据" + data);
        LiveSpeakInfo liveSpeakInfo = new Gson().fromJson(data, LiveSpeakInfo.class);
        ChatData chatData = new ChatData(liveSpeakInfo);
        if (chatData != null) {
            mLiveChatInfoAdapter.add(chatData);
            mDataArrayList.add(0, chatData);
            mLiveChatInfoAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.liveSpeak})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.liveSpeak:
                sendLiveSpeak();
                break;
        }
    }

    private void sendLiveSpeak() {
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!mSpeakEditText.isShown()) {
            mSpeakEditText.setVisibility(View.VISIBLE);
            mLiveSpeak.setVisibility(View.GONE);
        }
        mSpeakEditText.setFocusable(true);
        mSpeakEditText.setFocusableInTouchMode(true);
        mSpeakEditText.requestFocus();

        boolean b = mInputMethodManager.isActive(mSpeakEditText);
        if (b) {
//            mInputMethodManager.toggleSoftInputFromWindow(mSpeakEditText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
            mInputMethodManager.showSoftInput(mSpeakEditText, InputMethodManager.SHOW_FORCED);
        } else {
            if (mSpeakEditText != null && mSpeakEditText.isShown()) {
                mSpeakEditText.setVisibility(View.GONE);
            }
        }

        mSpeakEditText.setOnEditorActionListener(mOnEditorActionListener);
    }

    private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (LocalUser.getUser().isLogin()) {
                    if (mSpeakEditText != null && !TextUtils.isEmpty(mSpeakEditText.getText().toString())) {
                        NettyClient.getInstance().sendMessage(mSpeakEditText.getText().toString());
                    }
                    if (mInputMethodManager.isActive(mSpeakEditText)) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    if (mSpeakEditText.isShown()) {
                        mSpeakEditText.setText("");
                        mSpeakEditText.setVisibility(View.GONE);
                    }
                    if (!mLiveSpeak.isShown()) {
                        mLiveSpeak.setVisibility(View.VISIBLE);
                    }
                    return true;
                } else {
                    Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQUEST_CODE_LOGIN);
                }
            }
            return false;
        }
    };

    private void getChatInfo() {
        API.Live.getLiveTalk(mTimeStamp, mPage, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback<Resp<LiveHomeChatInfo>>() {

                    @Override
                    public void onReceive(Resp<LiveHomeChatInfo> liveHomeChatInfoResp) {
                        if (liveHomeChatInfoResp.isSuccess() && liveHomeChatInfoResp.hasData()) {
                            chatDatas = liveHomeChatInfoResp.getData().getData();
                            Log.d("dataLive", "== page" + mPage + " 数据大小 " + chatDatas.size());
                            mDataArrayList.addAll(0, chatDatas);
                            updateCHatInfo(liveHomeChatInfoResp.getData());
                            if (chatDatas.size() < mPageSize) {
                                isRefreshed = true;
                            }
                        }
                    }
                })
                .fire();
    }

    private long getTimeStamp(List<ChatData> chatDatas) {
        if (chatDatas != null && !chatDatas.isEmpty()) {
            return chatDatas.get(chatDatas.size() - 1).getTimeStamp();
        }
        return 0;
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

//        liveHomeChatInfo.sort();
//        for (LiveHomeChatInfo.ChatData item : liveHomeChatInfo.getData()) {
////            if (mHashSet.add(item.getCreateTime())) {
//            mLiveChatInfoAdapter.add(item);
////            }
//        }
        mLiveChatInfoAdapter.clear();
        if (mDataArrayList != null && !mDataArrayList.isEmpty()) {
            mLiveChatInfoAdapter.addAll(mDataArrayList);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == getActivity().RESULT_OK) {
            if (mSpeakEditText != null && !TextUtils.isEmpty(mSpeakEditText.getText().toString())) {
                NettyClient.getInstance().sendMessage(mSpeakEditText.getText().toString());
            }
        }
    }

    static class LiveChatInfoAdapter extends ArrayAdapter<ChatData> {

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

            public void bindViewWithData(ChatData item, int position, Context context) {

                String format = DateUtil.format(item.getCreateTime());

                boolean today = DateUtils.isToday(item.getCreateTime());
                if (today) {
                    String formatElapsedTime = DateUtils.formatElapsedTime(item.getCreateTime());
                    String dateTime = DateUtils.formatDateTime(context, item.getCreateTime(), DateUtils.FORMAT_ABBREV_ALL);
                    String formatDateRange = DateUtils.formatDateRange(context, item.getCreateTime(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_YEAR);
                    CharSequence relativeTimeSpanString2 = DateUtils.getRelativeTimeSpanString(item.getCreateTime());
                    format = format + "  " + relativeTimeSpanString2;
                } else {
                    CharSequence relativeTimeSpanString2 = DateUtils.getRelativeTimeSpanString(item.getCreateTime());
                    format = format + "    " + relativeTimeSpanString2;
                }


                //老师或者管理员
                if (!item.isNormalUser()) {
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

            private void setChaterStatus(ChatData item, Context context) {
                String chatUser = "";
                if (item.getChatType() == item.CHAT_TYPE_MANAGER) {
                    chatUser = context.getString(R.string.live_type_manager);
                } else if (item.getChatType() == item.CHAT_TYPE_TEACHER) {
                    chatUser = context.getString(R.string.live_type_teacher);
                }
                mUserStatus.setText(chatUser);
            }
        }
    }
}

