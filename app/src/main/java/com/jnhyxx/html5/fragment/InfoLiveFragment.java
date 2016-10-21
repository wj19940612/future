package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
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

public class InfoLiveFragment extends BaseFragment implements AbsListView.OnScrollListener {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;

    private boolean isLoad;
    private InfoLiveMessageAdapter mInfoLiveMessageAdapter;


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
        mListView.setDivider(null);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfoLiveData();
            }
        });
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
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void getInfoLiveData() {
        API.Message.findNewsByUrl(API.getInfoLiveUrl())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp>() {
                                 @Override
                                 public void onReceive(Resp resp) {
                                     if (resp.isSuccess()) {

//                                         Log.d(TAG, "直播资讯" + resp.getData().toString());
                                         ArrayList<ArrayList<String>> infoLiveMessageList = new ArrayList<>();
                                         String timeHint = "0#1#";
                                         String well = "#";
                                         String endHint = "#####0###";
                                         try {
                                             JSONArray jsonArray = new JSONArray(resp.getData().toString().replaceAll("\\\"", "\""));
                                             for (int i = 0; i < jsonArray.length(); i++) {
                                                 String data = jsonArray.optString(i);
//                                                 Log.d(TAG, "直播具体数据" + data);
                                                 String[] split = data.split(well);
                                                 ArrayList<String> strings = new ArrayList<>();
                                                 for (int j = 0; j < split.length; j++) {
                                                     if (split[j] == null || split[j].length() == 1 || TextUtils.isEmpty(split[j])) {
                                                         continue;
                                                     }
//                                                     Log.d(TAG, "分割开的数据" + split[j].toString());
                                                     strings.add(split[j]);
                                                 }

//                                                 InfoLiveMessage infoLiveMessage = new InfoLiveMessage(strings.get(0), strings.get(1), null, false);
                                                 if (!strings.toString().contains("<a") && !strings.toString().contains("</a>")) {
                                                     infoLiveMessageList.add(strings);
                                                 }
                                             }
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }

                                         setAdapter(infoLiveMessageList);
                                     } else {
                                         if (mSwipeRefreshLayout.isRefreshing()) {
                                             mSwipeRefreshLayout.setRefreshing(false);
                                         }
                                     }
                                 }
                             }

                ).fire();
    }

    private void setAdapter(ArrayList<ArrayList<String>> infoLiveMessageList) {
        if (infoLiveMessageList == null || infoLiveMessageList.isEmpty()) {
            mListView.setEmptyView(mEmptyView);
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

            String[] mSplit = null;

            StringBuffer stringBuffer = null;
            if (infoLiveMessage != null) {
                for (int i = 0; i < infoLiveMessage.size(); i++) {
                    String content = infoLiveMessage.get(1);
                    if (content.contains("<b>") || content.contains("</b>")) {
                        content = content.replace("<b>", "").replaceAll("</b>", "");
                    } else if (content.contains("<br />")) {
                        mSplit = content.split("<br />");
                        stringBuffer = new StringBuffer();
//                        Log.d(TAG, "含有换行符" + content);
                        for (String s : mSplit) {
                            stringBuffer.append(s + "\n");
                        }
                        content = stringBuffer.toString().replaceAll("<br />", "");
                    } else if (content.contains("</br>")) {
                        mSplit = content.split("</br>");
                        stringBuffer = new StringBuffer();
//                        Log.d(TAG, "含有换行符" + content);
                        for (String s : mSplit) {
                            stringBuffer.append(s + "\n");
                        }
                        content = stringBuffer.toString().replaceAll("</br>", "");
                    }
                    if (content.contains("【") && content.contains("】")) {
                        content = content.substring(content.indexOf("【"));
                        mViewHolder.mContent.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
                    } else {
                        mViewHolder.mContent.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPrimary));
                    }

                    mViewHolder.mTime.setText(infoLiveMessage.get(0));
                    mViewHolder.mContent.setText(content);

                    String messageData = infoLiveMessage.toString();
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
                        String imageUrl = "https://res.6006.com/jin10/" + infoLiveMessage.get(imageUrlPosition);
                        Log.d(TAG, "图片地址" + imageUrl);
                        Picasso.with(getContext()).load(imageUrl).into(mViewHolder.mImageHint);
                    } else {
                        mViewHolder.mImageHint.setVisibility(View.GONE);
                    }
                }

                if (infoLiveMessage.size() == 10) {
                    Log.d("55555", "size大小" + infoLiveMessage.size() + "   " + infoLiveMessage.toString());
                    if (!TextUtils.isEmpty(infoLiveMessage.get(2))) {
                        mViewHolder.mDataLayout.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(infoLiveMessage.get(2))) {
                            mViewHolder.mBeforeData.setText(getString(R.string.expect_data, infoLiveMessage.get(2)));
                        }
                        if (!TextUtils.isEmpty(infoLiveMessage.get(3))) {
                            mViewHolder.mExpectData.setText(getString(R.string.expect_data, infoLiveMessage.get(3)));
                        }
                        if (!TextUtils.isEmpty(infoLiveMessage.get(4))) {
                            mViewHolder.mRealData.setText(getString(R.string.expect_data, infoLiveMessage.get(4)));
                        }
                    }
                }
            }

            return convertView;
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

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
