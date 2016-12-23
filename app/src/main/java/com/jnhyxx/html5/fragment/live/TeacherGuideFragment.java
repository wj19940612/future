package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;
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

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.johnz.kutils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/11/8.
 * 老师指导
 */

public class TeacherGuideFragment extends BaseFragment implements AbsListView.OnScrollListener {

    private static final String KEY_TEACHER_INFO = "TEACHER_INFO";

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty)
    TextView mEmpty;


    private Unbinder mBind;

    private int mPageOffset = 0;
    private int mPageSize;

    private HashSet<Long> mHashSet;
    private LiveMessage mLiveMessage;

    private LiveTeacherGuideAdapter mLiveTeacherGuideAdapter;

    private ArrayList<LiveHomeChatInfo> mDataInfoList;

    public static TeacherGuideFragment newInstance() {

        Bundle args = new Bundle();
        TeacherGuideFragment fragment = new TeacherGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_advise, container, false);
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
        setLiveViewStackFromBottom(true);
        mPageSize = 10;
        mHashSet = new HashSet<>();
        mDataInfoList = new ArrayList<>();
        mListView.setOnScrollListener(this);
        getLiveMessage();
        initSwipeRefreshLayout();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPageOffset = 0;
    }

    private void getLiveMessage() {
        API.Live.getLiveMessage().setTag(TAG)
                .setCallback(new Callback2<Resp<LiveMessage>, LiveMessage>() {
                                 @Override
                                 public void onRespSuccess(LiveMessage liveMessage) {
                                     mLiveMessage = liveMessage;
                                     getTeacherGuideIfo();

                                 }
                             }
                ).fire();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser
                && isAdded()
                && !getActivity().isFinishing()) {
            mPageOffset = 0;
            if (mDataInfoList != null) {
                mDataInfoList.clear();
            }
            getLiveMessage();
        }
    }

    private void setLiveViewStackFromBottom(boolean isStackFromBottom) {
        mListView.setStackFromBottom(isStackFromBottom);
        if (isStackFromBottom) {
            mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        } else {
            mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
        }
    }

    public void setData(LiveHomeChatInfo data) {
        if (data != null && mLiveTeacherGuideAdapter != null) {
            if (mHashSet.add(data.getCreateTime())) {
                mPageOffset++;
                mDataInfoList.add(data);
                if (mDataInfoList.size() > 2) {
                    if (DateUtil.isTimeBetweenFiveMin(data.getCreateTime(), mDataInfoList.get(mDataInfoList.size() - 2).getCreateTime())) {
                        data.setMoreThanFiveMin(true);
                    }
                }
                mLiveTeacherGuideAdapter.add(data);
                mLiveTeacherGuideAdapter.notifyDataSetChanged();

            }
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (!Network.isNetworkAvailable()) {
                    stopRefreshAnimation();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTeacherGuideIfo();
                setLiveViewStackFromBottom(false);
                if (!Network.isNetworkAvailable()) {
                    stopRefreshAnimation();
                }
            }
        });
    }

    private void getTeacherGuideIfo() {
        if (mLiveMessage == null || mLiveMessage.getTeacher() == null) {
            mEmpty.setText(R.string.there_is_no_teacher_advise);
            mListView.setEmptyView(mEmpty);
            stopRefreshAnimation();
            return;
        }
        API.Live.getTeacherGuide(mPageOffset, mPageSize, mLiveMessage.getTeacher().getTeacherAccountId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<List<LiveHomeChatInfo>>>() {

                    @Override
                    public void onReceive(Resp<List<LiveHomeChatInfo>> listResp) {
                        if (listResp.isSuccess()) {
                            if (listResp.hasData()) {

                                if (listResp.getData().size() < 6) {
                                    setLiveViewStackFromBottom(false);
                                }

                                mPageOffset = mPageOffset + mPageSize;
                                mDataInfoList.addAll(0, listResp.getData());
                                updateTeacherGuide(listResp.getData());
                                if (mPageOffset > 10) {
                                    mListView.setSelection(mPageSize - 1);
                                }
                            } else {
                                ToastUtil.curt(R.string.now_is_not_has_more_data);
                                stopRefreshAnimation();
                            }
                        } else {
                            updateTeacherGuide(listResp.getData());
                        }
                    }
                })
                .fire();
    }

    private void updateTeacherGuide(List<LiveHomeChatInfo> data) {
        if (data == null || data.isEmpty()) {
            mListView.setEmptyView(mEmpty);
            stopRefreshAnimation();
            return;
        }
        addListViewFootView(data);
    }

    private void addListViewFootView(List<LiveHomeChatInfo> data) {
        stopRefreshAnimation();

        if (mLiveTeacherGuideAdapter == null) {
            mLiveTeacherGuideAdapter = new LiveTeacherGuideAdapter(getActivity());
            mListView.setAdapter(mLiveTeacherGuideAdapter);
            if (mLiveMessage != null) {
                mLiveTeacherGuideAdapter.setTeacherInfo(mLiveMessage.getTeacher());
            }
        }
        mLiveTeacherGuideAdapter.clear();
        getTalkTimeIsThanFiveMinute();
        mLiveTeacherGuideAdapter.notifyDataSetChanged();
    }

    //判断谈话时间是否超过5分钟，如果超过，出现分割线
    private void getTalkTimeIsThanFiveMinute() {
        if (mDataInfoList != null && !mDataInfoList.isEmpty()) {
            for (int i = mDataInfoList.size(); i > 0; i--) {
                if (i < 3) break;
                if (DateUtil.isTimeBetweenFiveMin(mDataInfoList.get(i - 1).getCreateTime(), mDataInfoList.get(i - 2).getCreateTime())) {
                    mDataInfoList.get(i - 1).setMoreThanFiveMin(true);
                }
            }
            mLiveTeacherGuideAdapter.addAll(mDataInfoList);
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
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

    static class LiveTeacherGuideAdapter extends ArrayAdapter<LiveHomeChatInfo> {
        Context mContext;
        LiveMessage.TeacherInfo mTeacherInfo;

        public LiveTeacherGuideAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setTeacherInfo(LiveMessage.TeacherInfo teacherInfo) {
            this.mTeacherInfo = teacherInfo;
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
            viewHolder.bindDataWithView(getItem(position), position, mContext, mTeacherInfo);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.timeBeforeHint)
            TextView mTimeBeforeHint;
            @BindView(R.id.timeBeforeHintLayout)
            LinearLayout mTimeBeforeHintLayout;

            //老师的图片
            @BindView(R.id.ivTeacherImage)
            ImageView mIvTeacherImage;
            //老师图片父容器
            @BindView(R.id.llImageLayout)
            LinearLayout mLlImageLayout;

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

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(LiveHomeChatInfo item, int position, Context context, LiveMessage.TeacherInfo teacherInfo) {
                if (item == null) return;

                String formatTime = DateUtil.getFormatTime(item.getCreateTime());

                if (item.isMoreThanFiveMin()) {
                    mTimeBeforeHintLayout.setVisibility(View.VISIBLE);
                    mTimeBeforeHint.setText(formatTime);
                } else {
                    mTimeBeforeHintLayout.setVisibility(View.GONE);
                }

                String format = DateUtil.format(item.getCreateTime(), DateUtil.DEFAULT_FORMAT);
                CharSequence relativeTimeSpanString2 = DateUtils.getRelativeTimeSpanString(item.getCreateTime());
                format = format + "  " + relativeTimeSpanString2.toString();
                if (format.equalsIgnoreCase("0分钟前") || format.equalsIgnoreCase("0分钟后")) {
                    format = "刚刚";
                }
                if (!mManagerLayout.isShown()) {
                    mManagerLayout.setVisibility(View.VISIBLE);
                }
                setTeacherMsg(item, context);
                mUserStatus.setText(item.getName());

                if (teacherInfo != null && !TextUtils.isEmpty(teacherInfo.getPictureUrl())) {
                    Picasso.with(context).load(teacherInfo.getPictureUrl())
                            .transform(new CircleTransform()).into(mUserHeadImage);
                } else {
                    Picasso.with(context).load(R.drawable.ic_live_pic_head)
                            .transform(new CircleTransform()).into(mUserHeadImage);
                }
            }

            private void setTeacherMsg(LiveHomeChatInfo item, Context context) {
                if (item.isText()) {
                    if (mContent.getVisibility() == View.GONE) {
                        mContent.setVisibility(View.VISIBLE);
                    }
                    if (mIvTeacherImage.getVisibility() == View.VISIBLE)
                        mLlImageLayout.setVisibility(View.GONE);
                    mContent.setText(item.getMsg());

                } else {
                    Log.d("liveIn", "网址地址" + item.getMsg());
                    if (!TextUtils.isEmpty(item.getMsg())) {
                        if (mContent.getVisibility() == View.VISIBLE || mLlImageLayout.getVisibility() == View.GONE) {
                            mContent.setVisibility(View.GONE);
                            mLlImageLayout.setVisibility(View.VISIBLE);
                        }
                        Picasso.with(context).load(item.getMsg()).into(mIvTeacherImage);
                    }
                }
            }
        }
    }
}
