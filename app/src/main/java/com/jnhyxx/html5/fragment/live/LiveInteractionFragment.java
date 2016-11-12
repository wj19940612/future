package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
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
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.johnz.kutils.DateUtil;

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

    private List<LiveHomeChatInfo.ChatData> chatDatas;
    private InputMethodManager mInputMethodManager;

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

        mPageSize = 20;
        mHashSet = new HashSet<>();
        mListView.setOnScrollListener(this);
        getChatInfo();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTimeStamp = getTimeStamp(chatDatas);
                mPage = mPage + 1;
                getChatInfo();
                if (!Network.isNetworkAvailable() && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveOriginalData(String data) {
            super.onReceiveOriginalData(data);
            LiveHomeChatInfo.ChatData chatData = new Gson().fromJson(data, LiveHomeChatInfo.ChatData.class);
            if (chatData != null) {
                if (mHashSet.add(chatData.getCreateTime())) {
                    mLiveChatInfoAdapter.add(chatData);
                    mLiveChatInfoAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @OnClick({R.id.liveSpeak})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.liveSpeak:
                sendLiveSpeak();
                break;
        }
    }

    private void sendLiveSpeak() {
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (!mSpeakEditText.isShown()) {
            mSpeakEditText.setVisibility(View.VISIBLE);
            mLiveSpeak.setVisibility(View.GONE);
            mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            mInputMethodManager.toggleSoftInputFromWindow(mSpeakEditText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
            mInputMethodManager.showSoftInput(mSpeakEditText, InputMethodManager.SHOW_FORCED);
            mSpeakEditText.setOnEditorActionListener(mOnEditorActionListener);

            mSpeakEditText.addTextChangedListener(new ValidationWatcher() {
                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                ToastUtil.curt("enter键盘");
                if (mSpeakEditText != null && TextUtils.isEmpty(mSpeakEditText.getText().toString())) {
                    NettyClient.getInstance().sendMessage(mSpeakEditText.getText().toString());
                }
                if (mInputMethodManager.isActive(mSpeakEditText)) {
                    mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                if (mSpeakEditText.isShown()) {
                    mSpeakEditText.setVisibility(View.GONE);
                }
                if (!mLiveSpeak.isShown()) {
                    mLiveSpeak.setVisibility(View.VISIBLE);
                }
                return true;
            }
//            if (actionId == EditorInfo.IME_ACTION_GO) {
//                ToastUtil.curt("你点了软键盘'去往'按钮");
//                return true;
//            } else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                ToastUtil.curt("你点了软键盘'搜索'按钮");
//                return true;
//            } else if (actionId == EditorInfo.IME_ACTION_SEND) {
//                ToastUtil.curt("你点了软键盘'发送'按钮");
//                return true;
//            } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                ToastUtil.curt("你点了软键盘下一个按钮");
//                return true;
//            } else if (actionId == EditorInfo.IME_ACTION_NONE) {
//                ToastUtil.curt("你点击了none按钮");
//                return true;
//            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
//                ToastUtil.curt("你点击了完成按钮");
//                return true;
//            }
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
                            Log.d(TAG, "谈话内容" + liveHomeChatInfoResp.getData().toString());
                            chatDatas = liveHomeChatInfoResp.getData().getData();
                            updateCHatInfo(liveHomeChatInfoResp.getData());
                        }
                    }
                })
                .fire();
    }

    private long getTimeStamp(List<LiveHomeChatInfo.ChatData> chatDatas) {
        if (chatDatas != null && !chatDatas.isEmpty()) {
            return chatDatas.get(0).getTimeStamp();
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

        liveHomeChatInfo.sort();
        for (LiveHomeChatInfo.ChatData item : liveHomeChatInfo.getData()) {
//            if (mHashSet.add(item.getCreateTime())) {
            mLiveChatInfoAdapter.add(item);
//            }
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

                /**
                 *   DateUtils.formatDateTime(getApplicationContext(), //格式化时间，最多显示到分钟。最后参数设定显示的格式
                 System.currentTimeMillis(),
                 DateUtils.FORMAT_24HOUR|DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_TIME
                 |DateUtils.FORMAT_SHOW_YEAR|DateUtils.LENGTH_LONG|DateUtils.FORMAT_ABBREV_MONTH);

                 DateUtils.getRelativeTimeSpanString(System.currentTimeMillis()+60*4000));
                 //返回相对于当前时间的最大区间表示的字符串：几(分钟,小时,天,周,月,年)前/后

                 DateUtils.getRelativeTimeSpanString(context, long timeMillis);
                 //返回相对于当前时间的，参数时间字符串：在同一天显示时分；在不同一天，显示月日；在不同一年，显示年月日

                 DateUtils.formatDateRange(getApplicationContext(), System.currentTimeMillis(), System.currentTimeMillis() + 60 * 60 * 3000,
                 DateUtils.FORMAT_SHOW_TIME));  //返回两个时间值间的 相距 字符串
                 */

                boolean today = DateUtils.isToday(item.getCreateTime());
                if (today) {
                    Log.d(TAG, "isToday  " + today);
                    String formatElapsedTime = DateUtils.formatElapsedTime(item.getCreateTime());
                    Log.d(TAG, "formatElapsedTime  " + formatElapsedTime);
                    String dateTime = DateUtils.formatDateTime(context, item.getCreateTime(), DateUtils.FORMAT_ABBREV_ALL);
                    Log.d(TAG, "formatDateTime " + dateTime);
                    String formatDateRange = DateUtils.formatDateRange(context, item.getCreateTime(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_YEAR);
                    Log.d(TAG, "formatDateRange " + formatDateRange);
                    CharSequence relativeTimeSpanString2 = DateUtils.getRelativeTimeSpanString(item.getCreateTime());
//                    Log.d(TAG, "一参 relativeTimeSpanString2 " + relativeTimeSpanString2);
                    format = format + "  " + relativeTimeSpanString2;
                } else {
                    CharSequence relativeTimeSpanString2 = DateUtils.getRelativeTimeSpanString(item.getCreateTime());
                    format = format + "    " + relativeTimeSpanString2;
//                    Log.d(TAG, "一参 relativeTimeSpanString2 " + relativeTimeSpanString2);
                }


                // TODO: 2016/11/10 测试老师
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
                mUserStatus.setText(chatUser);
            }
        }
    }
}

