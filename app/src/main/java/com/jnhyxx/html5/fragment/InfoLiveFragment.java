package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.johnz.kutils.DateUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/10/18.
 */

public class InfoLiveFragment extends BaseFragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;

    private boolean isLoad;
    private InfoLiveMessageAdapter mInfoLiveMessageAdapter;

    private int mAutoRefreshTime;

    public static InfoLiveFragment newInstance() {
        InfoLiveFragment mInfoLiveFragment = new InfoLiveFragment();
        return mInfoLiveFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setEmptyView(mEmptyView);
        mListView.setDivider(null);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getInfoLiveData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isAdded() && !getActivity().isFinishing() && !isLoad) {
            getInfoLiveData();
            isLoad = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onResume() {
        super.onResume();
        mAutoRefreshTime = 30;
        startScheduleJob(1000);
    }

    @Override
    public void onTimeUp(int count) {
        mAutoRefreshTime--;
        if (mAutoRefreshTime == 0) {
            stopScheduleJob();
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        getInfoLiveData();
        if (!Network.isNetworkAvailable() ) {
            stopRefreshAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    private void getInfoLiveData() {
        API.Message.findNewsByUrl(API.getInfoLiveUrl())
                .setTag(TAG)
                .setCallback(new Callback<Resp>() {
                                 @Override
                                 public void onReceive(Resp resp) {
                                     if (resp.isSuccess() && resp.hasData()) {
                                         getInfoLiveData(resp);
                                     } else {
                                         stopRefreshAnimation();
                                     }
                                 }
                             }

                ).fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getInfoLiveData(Resp resp) {
        mAutoRefreshTime = 30;
        ArrayList<ArrayList<String>> infoLiveMessageList = new ArrayList<>();
        String well = "#";
        try {
            JSONArray jsonArray = new JSONArray(resp.getData().toString().replaceAll("\\\"", "\""));
            for (int i = 0; i < jsonArray.length(); i++) {
                String data = jsonArray.optString(i);
                Log.d(TAG, "直播具体数据" + data);
                String[] split = data.split(well);
                ArrayList<String> strings = new ArrayList<>();
                for (int j = 0; j < split.length; j++) {
                    if (split[j] == null || TextUtils.isEmpty(split[j])) {
                        continue;
                    }
                    Log.d(TAG, "分割开的数据" + split[j].toString());
                    strings.add(split[j]);
                }
                if (!strings.toString().contains("<a") && !strings.toString().contains("</a>")) {
                    infoLiveMessageList.add(strings);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setAdapter(infoLiveMessageList);
    }

    private void setAdapter(ArrayList<ArrayList<String>> infoLiveMessageList) {
        if (infoLiveMessageList == null ) {
            return;
        }
        if (mInfoLiveMessageAdapter == null) {
            mInfoLiveMessageAdapter = new InfoLiveMessageAdapter(getActivity());
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            mInfoLiveMessageAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mInfoLiveMessageAdapter.addAll(infoLiveMessageList);
        mListView.setAdapter(mInfoLiveMessageAdapter);
        mInfoLiveMessageAdapter.notifyDataSetChanged();
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


    class InfoLiveMessageAdapter extends ArrayAdapter<ArrayList<String>> {

        Context mContext;

        public InfoLiveMessageAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_info_live, null);
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            ArrayList<String> infoLiveMessage = getItem(position);
            Log.d(TAG, "大小" + infoLiveMessage.size() + "\n直播的具体数据" + infoLiveMessage.toString());

            if (infoLiveMessage != null && !infoLiveMessage.isEmpty()) {
                String content = getContent(mViewHolder, infoLiveMessage);
                setTime(mViewHolder, infoLiveMessage);
                changeTxtColor(mViewHolder, infoLiveMessage);
                mViewHolder.mContent.setText(content);
                String messageData = infoLiveMessage.toString();
                handleImage(mViewHolder, infoLiveMessage, messageData);
                setSpecialContent(mViewHolder, infoLiveMessage);
            }
            return convertView;
        }

        private void changeTxtColor(ViewHolder mViewHolder, ArrayList<String> infoLiveMessage) {
            if (infoLiveMessage.get(1).equalsIgnoreCase("0")) {
                mViewHolder.mContent.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            } else if (infoLiveMessage.get(1).equalsIgnoreCase("1")) {
                mViewHolder.mContent.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPrimary));
            }
        }

        private void setTime(ViewHolder mViewHolder, ArrayList<String> infoLiveMessage) {
            String time = infoLiveMessage.get(2);
            long stringToDate = DateUtil.getStringToDate(time);
//            time = StrFormatter.getTimeHint(time);
            time = DateUtils.getRelativeTimeSpanString(stringToDate).toString();
            if (time.equalsIgnoreCase("0分钟前")) {
                time = "刚刚";
            }
            mViewHolder.mTime.setText(time);
        }

        //特殊的显示,比如含有预期值,实际值等数据的特殊数据
        private void setSpecialContent(ViewHolder mViewHolder, ArrayList<String> infoLiveMessage) {
            if (infoLiveMessage.size() >= 11) {
                Log.d("55555", "size大小" + infoLiveMessage.size() + "   " + infoLiveMessage.toString());
                mViewHolder.mDataLayout.setVisibility(View.VISIBLE);
                mViewHolder.mStarImage.setVisibility(View.VISIBLE);
                mViewHolder.mTextHint.setVisibility(View.VISIBLE);
                mViewHolder.mOrganizeMarket.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(infoLiveMessage.get(3))) {
                    if (!TextUtils.isEmpty(infoLiveMessage.get(3))) {
                        mViewHolder.mBeforeData.setText(getString(R.string.before_data, infoLiveMessage.get(3)));
                    }
                    if (!TextUtils.isEmpty(infoLiveMessage.get(4))) {
                        mViewHolder.mExpectData.setText(getString(R.string.expect_data, infoLiveMessage.get(4)));
                    }
                    if (!TextUtils.isEmpty(infoLiveMessage.get(5))) {
                        mViewHolder.mRealData.setText(getString(R.string.real_data, infoLiveMessage.get(5)));
                    }
                }
                String messageLiveStarUrl = API.Message.getMessageLiveStarUrl(infoLiveMessage.get(6));
                if (!TextUtils.isEmpty(messageLiveStarUrl)) {
                    Picasso.with(mContext).load(messageLiveStarUrl).into(mViewHolder.mStarImage);
                }

                String organizeMarkUrl = API.Message.getOrganizeMarkUrl(infoLiveMessage.get(9));
                if (!TextUtils.isEmpty(organizeMarkUrl)) {
                    Picasso.with(mContext).load(organizeMarkUrl).into(mViewHolder.mOrganizeMarket);
                }
                mViewHolder.mContent.setText(infoLiveMessage.get(2));
                String time = DateUtils.getRelativeTimeSpanString(DateUtil.getStringToDate(infoLiveMessage.get(8))).toString();
                if (time.equalsIgnoreCase("0分钟前")) {
                    time = "刚刚";
                }
                mViewHolder.mTime.setText(time);
                mViewHolder.mTextHint.setText(infoLiveMessage.get(7));

            } else {
                mViewHolder.mOrganizeMarket.setVisibility(View.GONE);
                mViewHolder.mDataLayout.setVisibility(View.GONE);
                mViewHolder.mStarImage.setVisibility(View.GONE);
                mViewHolder.mTextHint.setVisibility(View.GONE);
            }
        }

        private void handleImage(ViewHolder mViewHolder, ArrayList<String> infoLiveMessage, String messageData) {
            if (messageData.contains(".png") || messageData.contains(".jpg") || messageData.contains(".jpeg")) {
                int imageUrlPosition = 0;
                for (int j = 0; j < infoLiveMessage.size(); j++) {
                    String s = infoLiveMessage.get(j);
                    if (s.contains(".png") || s.contains(".jpg") || s.contains(".jpeg")) {
                        imageUrlPosition = j;
                        break;
                    }
                }
                mViewHolder.mImageHint.setVisibility(View.VISIBLE);
                String imageUrl = API.Message.getMessageLiveInfoUrl() + infoLiveMessage.get(imageUrlPosition);
                Log.d(TAG, "图片地址" + imageUrl);
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.with(getContext()).load(imageUrl).into(mViewHolder.mImageHint);
                }
            } else {
                mViewHolder.mImageHint.setVisibility(View.GONE);
            }
        }

        private String getContent(ViewHolder mViewHolder, ArrayList<String> infoLiveMessage) {
            String content = infoLiveMessage.get(3);

            if (content.contains("<font ")) {
                content = content.substring(content.indexOf(">"));
            }
            content = content.replaceAll("<br\\s*/?>", "\r\n");
            content = content.replaceAll("<b>|</b>|</font>|>|<br/>|</br", "");
            if (content.contains("【") && content.contains("】")) {
                content = content.substring(content.indexOf("【"));
            }

            return content;
        }


        class ViewHolder {
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.beforeData)
            TextView mBeforeData;
            @BindView(R.id.expectData)
            TextView mExpectData;
            @BindView(R.id.realData)
            TextView mRealData;
            @BindView(R.id.dataLayout)
            LinearLayout mDataLayout;
            @BindView(R.id.imageHint)
            ImageView mImageHint;
            @BindView(R.id.organizeMarket)
            ImageView mOrganizeMarket;
            @BindView(R.id.starImage)
            ImageView mStarImage;
            @BindView(R.id.TextHint)
            TextView mTextHint;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
