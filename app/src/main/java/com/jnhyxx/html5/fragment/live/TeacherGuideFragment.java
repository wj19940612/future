package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.jnhyxx.html5.domain.live.ChatData;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.domain.live.LiveTeacherGuideInfo;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.johnz.kutils.DateUtil;

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
    @BindView(R.id.liveSpeak)
    ImageView mLiveSpeak;


    private Unbinder mBind;

    private int mPage = 0;
    private int mPageSize;

    private HashSet<Long> mHashSet;
    private LiveMessage mLiveMessage;

    private LiveTeacherGuideAdapter mLiveTeacherGuideAdapter;

    private ArrayList<ChatData> mDataInfos;

    public static TeacherGuideFragment newInstance() {

        Bundle args = new Bundle();
        TeacherGuideFragment fragment = new TeacherGuideFragment();
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
        mLiveSpeak.setVisibility(View.GONE);
        mPageSize = 5;
        mHashSet = new HashSet<>();
        mDataInfos = new ArrayList<>();
        mListView.setOnScrollListener(this);
        getLiveMessage();
        initSwipeRefreshLayout();
    }

    private void getLiveMessage() {
        API.Live.getLiveMessage().setTag(TAG)
                .setCallback(new Callback2<Resp<LiveMessage>, LiveMessage>() {
                                 @Override
                                 public void onRespSuccess(LiveMessage liveMessage) {
                                     mLiveMessage = liveMessage;
                                     Log.d(TAG, "直播单数据" + mLiveMessage);
                                     getTeacherGuideIfo();

                                 }
                             }
                ).fire();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            getLiveMessage();
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage++;
                getTeacherGuideIfo();
                if (!mSwipeRefreshLayout.isRefreshing() && Network.isNetworkAvailable()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {

        }
    };

    private void getTeacherGuideIfo() {
        if (mLiveMessage == null || mLiveMessage.getTeacher() == null) return;
        API.Live.getTeacherGuide(mPage, mPageSize, mLiveMessage.getTeacher().getTeacherAccountId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<LiveTeacherGuideInfo>>() {

                    @Override
                    public void onReceive(Resp<LiveTeacherGuideInfo> liveTeacherGuideInfoResp) {
                        if (liveTeacherGuideInfoResp.isSuccess() && liveTeacherGuideInfoResp.hasData()) {
                            mDataInfos.addAll(0, liveTeacherGuideInfoResp.getData().getData());
                            updateTeacherGuide(liveTeacherGuideInfoResp.getData().getData());
                        }
                    }
                })
                .fire();
    }

    private void updateTeacherGuide(List<ChatData> data) {
        if (data == null || data.isEmpty()) {
            mEmpty.setText("老师暂未发出指令");
            mListView.setEmptyView(mEmpty);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            return;
        }
        addListViewFootView(data);
    }

    private void addListViewFootView(List<ChatData> data) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
//            mLiveTeacherGuideAdapter.clear();
        }

        if (mLiveTeacherGuideAdapter == null) {
            mLiveTeacherGuideAdapter = new LiveTeacherGuideAdapter(getActivity());
            mListView.setAdapter(mLiveTeacherGuideAdapter);
        }
//        for (LiveTeacherGuideInfo.DataInfo dataInfo : data) {
//            mHashSet.add(dataInfo.getCreateTime());
//            mLiveTeacherGuideAdapter.add(dataInfo);
//        }
        mLiveTeacherGuideAdapter.clear();
        if (mDataInfos != null && !mDataInfos.isEmpty()) {
            mLiveTeacherGuideAdapter.addAll(mDataInfos);
        }
        mLiveTeacherGuideAdapter.notifyDataSetChanged();
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

    static class LiveTeacherGuideAdapter extends ArrayAdapter<ChatData> {
        Context mContext;

        public LiveTeacherGuideAdapter(Context context) {
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
            viewHolder.bindDataWithView(getItem(position), position, mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.timeBeforeHint)
            TextView mTimeBeforeHint;
            @BindView(R.id.timeBeforeHintLayout)
            LinearLayout mTimeBeforeHintLayout;
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

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(ChatData item, int position, Context context) {
                if (item == null) return;
                if (!mManagerLayout.isShown()) {
                    mManagerLayout.setVisibility(View.VISIBLE);
                }
                String format = DateUtil.format(item.getCreateTime());
//                if (item.isTeacherGuide()) {
//                }
                mTimeHint.setText(format);
                mUserStatus.setText(item.getName());
                mContent.setText(item.getMsg());
            }
        }
    }
}
